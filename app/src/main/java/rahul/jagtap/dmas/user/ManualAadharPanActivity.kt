package rahul.jagtap.dmas.user

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
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
import rahul.jagtap.dmas.databinding.ActivityAadharPanCardBinding
import rahul.jagtap.dmas.databinding.ActivityAadharPanLinkBinding
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class ManualAadharPanActivity : BaseActivity() {
    private var photoFileName: String = ""
    private var photoDownloadUrl: String = ""
    private var signPhotoFileName: String = ""
    private var signPhotoDownloadUrl: String = ""
    private val TAG = ManualAadharPanActivity::class.java.simpleName
    var imageType = -1 // 1 - photo, 2 - sign
    var photoUri: Uri? = null
    var signPhotoUri: Uri? = null
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    var cpd: ProgressDialog? = null
    private lateinit var easyImage: EasyImage
    lateinit var binding: ActivityAadharPanCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAadharPanCardBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilApplicantName, binding.tilPhoto, binding.tilSignPhoto, binding.tilAadharNo, binding.tilAddress, binding.tilPanNo, binding.tilFatherName)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_manual_aadhar_pan_card)
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
                ?: "").putExtra("title", getString(R.string.txt_manual_aadhar_pan_card)))
        }

        binding.btnSubmit?.setOnClickListener {
            val strAadharNo = binding.etAadharNo.text.toString()
            val strAddress = binding.etAddress.text.toString()
            val strPanNo = binding.etPanNo.text.toString()
            val strFatherName = binding.etFatherName.text.toString()
            val strApplicantName = binding.etApplicantName.text.toString()
            if (TextUtils.isEmpty(strApplicantName)) {
                binding.tilApplicantName?.error = "Enter Applicant's Name"
                binding.etApplicantName?.requestFocus()
                return@setOnClickListener
            }
            if (photoUri == null) {
                binding.tilPhoto?.error = "Select Photo"
                binding.etPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (signPhotoUri == null) {
                binding.tilSignPhoto?.error = "Select Signature Photo"
                binding.etSignPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strAadharNo)) {
                binding.tilAadharNo?.error = "Enter Aadhar Number"
                binding.etAadharNo?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strAddress)) {
                binding.tilAddress?.error = "Enter Address"
                binding.etAddress?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strPanNo)) {
                binding.tilPanNo?.error = "Enter PAN Number"
                binding.etPanNo?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strFatherName)) {
                binding.tilFatherName?.error = "Enter Father Name"
                binding.etFatherName?.requestFocus()
                return@setOnClickListener
            }
            uploadPhoto(photoUri!!)
        }
        binding.etPhoto?.setOnClickListener {
            imageType = 1
            choosePhotoWithPermissions()
        }
        binding.etSignPhoto?.setOnClickListener {
            imageType = 2
            choosePhotoWithPermissions()
        }
    }

    private fun uploadPhoto(fileUri: Uri) {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        photoFileName = "light_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("manual_aadhar_pan_card").child(photoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    photoDownloadUrl = uri.toString()
                    signPhotoUri?.let { it1 -> uploadSignPhoto(it1) }
                }.addOnFailureListener {
                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
                    it.printStackTrace()
                }
            } catch (e: java.lang.Exception) {
                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
                e.printStackTrace()
            }
        }.addOnFailureListener {
            if (cpd?.isShowing == true) cpd?.dismiss()
            it.message?.let { it1 -> toast(it1) }
        }.addOnProgressListener {
            //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadSignPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        signPhotoFileName = "sign_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("manual_aadhar_pan_card").child(signPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    signPhotoDownloadUrl = uri.toString()
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
            if (cpd?.isShowing == true) cpd?.dismiss()
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
        billMap["applicantName"] =  binding.etApplicantName.text.toString()
        billMap["photoFileName"] = photoFileName
        billMap["photoDownloadUrl"] = photoDownloadUrl
        billMap["signFileName"] = signPhotoFileName
        billMap["signDownloadUrl"] = signPhotoDownloadUrl
        billMap["aadhar_number"] = binding.etAadharNo.text.toString()
        billMap["address"] = binding.etAddress.text.toString()
        billMap["pan_number"] = binding.etPanNo.text.toString()
        billMap["father_name"] = binding.etFatherName.text.toString()
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("manual_aadhar_pan_card").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/manual_aadhar_pan_card/$pushKey"] = billMap

        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Manual Aadhar Pan Card added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_manual_aadhar_pan_card")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Manual Aadhar Pan Card added by $name")
        }

        // Create a backup record
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/manual_aadhar_pan_card/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        toast("Manual Aadhar Pan card created successfully")
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
                                photoUri = Uri.fromFile(it)
                                binding.etPhoto.setText("Success")//photoUri.toString())
                            }
                            2 -> {
                                signPhotoUri = Uri.fromFile(it)
                                binding.etSignPhoto.setText("Success")//signPhotoUri.toString())
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
