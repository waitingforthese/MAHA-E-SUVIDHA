package rahul.jagtap.dmas.user

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.DatePicker
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import rahul.jagtap.dmas.extensions.longToast
import rahul.jagtap.dmas.extensions.toast
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityRailwayTicketBookingBinding
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class RailwayTicketBookingActivity : BaseActivity() {
    private var strDate: String = ""
    private val TAG = RailwayTicketBookingActivity::class.java.simpleName
    var imageType = -1 // 1 - aadhar
    var aadharPhotoUri: Uri? = null
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private var aadharPhotoFileName: String = ""
    private var aadharPhotoDownloadUrl: String = ""
    var cpd: ProgressDialog? = null
    private lateinit var easyImage: EasyImage
    lateinit var binding: ActivityRailwayTicketBookingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRailwayTicketBookingBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilFrom, binding.tilDestination, binding.tilDate, binding.tilName, binding.tilAge, binding.tilMobileNo, binding.tilAddress, binding.tilAadharPhoto)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_railway_ticket_booking)
        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid
        username = app?.preferences?.loggedInUser?.username
        name = app?.preferences?.loggedInUser?.name
        easyImage = EasyImage.Builder(this)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(false) // Setting to true will cause taken pictures to show up in the device gallery, DEFAULT false
            .setCopyImagesToPublicGalleryFolder(false)
            .build()
        binding.btnSuchna.setOnClickListener {
            startActivity(Intent(mContext, ViewSuchnaActivity::class.java).putExtra("suchna", intent.getStringExtra("suchna")
                ?: "").putExtra("title", getString(R.string.txt_railway_ticket_booking)))
        }

        binding.btnSubmit?.setOnClickListener {
            val strFrom = binding.etFrom.text.toString()
            val strDestination = binding.etDestination.text.toString()
            val strDate = binding.etDate.text.toString()
            val strName = binding.etName.text.toString()
            val strAge = binding.etAge.text.toString()
            val strMobileNo = binding.etMobileNo.text.toString()
            val strAddress = binding.etAddress.text.toString()
            if (TextUtils.isEmpty(strFrom)) {
                binding.tilFrom?.error = "Enter From"
                binding.etFrom?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strDestination)) {
                binding.tilDestination?.error = "Enter Destination"
                binding.etDestination?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strDate)) {
                binding.tilDate?.error = "Select Date"
                binding.etDate?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strName)) {
                binding.tilName?.error = "Enter Name"
                binding.etName?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strAge)) {
                binding.tilAge?.error = "Enter Age"
                binding.etAge?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strMobileNo)) {
                binding.tilMobileNo?.error = "Enter Mobile Number"
                binding.etMobileNo?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strAddress)) {
                binding.tilAddress?.error = "Enter Address"
                binding.etAddress?.requestFocus()
                return@setOnClickListener
            }
            if (aadharPhotoUri == null) {
                binding.tilAadharPhoto?.error = "Select Aadhar Card Photo"
                binding.etAadharPhoto?.requestFocus()
                return@setOnClickListener
            }
            uploadAadharPhoto(aadharPhotoUri!!)
        }
        binding.etDate?.setOnClickListener {
            val mMaxDate = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(mContext!!, { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val date = "" + dayOfMonth + "/" + (month + 1) + "/" + year
                strDate = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
                binding.etDate.setText(date)
            }, mMaxDate[Calendar.YEAR], mMaxDate[Calendar.MONTH], mMaxDate[Calendar.DAY_OF_MONTH])
            datePickerDialog.show()
        }
        binding.etAadharPhoto?.setOnClickListener {
            imageType = 1
            choosePhotoWithPermissions()
        }
    }

    private fun uploadAadharPhoto(fileUri: Uri) {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharPhotoFileName = "board_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("railway_ticket_booking").child(aadharPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    aadharPhotoDownloadUrl = uri.toString()
                    createDbRecord()
                }.addOnFailureListener {
                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
                    it.printStackTrace()
                }
            } catch (e: java.lang.Exception) {
                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
                e.printStackTrace()
            }
        }.addOnFailureListener {
            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
            it.message?.let { it1 -> toast(it1) }
        }.addOnProgressListener {
            //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun createDbRecord() {
        val createdDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        val billMap = HashMap<String, Any?>()
        billMap["email"] = email
        billMap["createdBy"] = name
        billMap["createdByUserName"] = username
        billMap["uid"] = uid
        billMap["createdDateTime"] = createdDateTime
        billMap["aadharPhotoFileName"] = aadharPhotoFileName
        billMap["aadharPhotoDownloadUrl"] = aadharPhotoDownloadUrl
        billMap["rail_booking_from"] = binding.etFrom.text.toString()
        billMap["rail_booking_destination"] = binding.etDestination.text.toString()
        billMap["rail_booking_date"] = strDate
        billMap["rail_booking_name"] = binding.etName.text.toString()
        billMap["rail_booking_age"] = binding.etAge.text.toString()
        billMap["rail_booking_mobileNo"] = binding.etMobileNo.text.toString()
        billMap["rail_booking_address"] = binding.etAddress.text.toString()
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("railway_ticket_booking").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/railway_ticket_booking/$pushKey"] = billMap

        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Railway ticket booking added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_railway_ticket_booking")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Railway ticket booking added by $name")
        }

        // Create a backup record
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/railway_ticket_booking/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        toast("Request submitted successfully")
        Utils.showDialog(mContext, "तुमची सुविधा विनंती जतन करण्यात आली आहे. रीसिट \"पाठविलेल्या सुविधा\" फोल्डर बटन मध्ये तपासा.", false) { dialog, which ->
            run {
                dialog.dismiss()
                finish()
            }
        }
    }

    fun getTodayDate(): String {
        return SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).format(Date())
    }

    @SuppressLint("CheckResult")
    private fun choosePhotoWithPermissions() {
//        rxPermissions?.requestEachCombined(Manifest.permission.CAMERA)?.subscribe {
//            when {
//                it.granted -> {
//                    // get picture
                    easyImage.openChooser(this)
//                }
//                it.shouldShowRequestPermissionRationale -> {
//                    // At least one denied permission without ask never again
//                }
//                else -> {
//                    // At least one denied permission with ask never again
//                    // Need to go to the settings
//                }
//            }
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                longToast(getString(R.string.txt_try_later))
            }

            override fun onCanceled(source: MediaSource) {}
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                //                val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
                //                val fileName = AccountingServicesActivity.dateFormatForPhoto.format(System.currentTimeMillis()) + ".jpg"
                //                val image = File("$dir/$fileName")

                lifecycleScope.launch {
                    val compressedImageFile = mContext?.let { Compressor.compress(it, imageFiles[0].file) }
                    Log.e(TAG, "${compressedImageFile?.path}")
                    compressedImageFile?.let {
                        when (imageType) {
                            1 -> {
                                aadharPhotoUri = Uri.fromFile(it)
                                binding.etAadharPhoto.setText("Success")//aadharPhotoUri.toString())
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Utils.hideSoftKeyboard(this)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {}
}
