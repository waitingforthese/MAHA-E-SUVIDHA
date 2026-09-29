package rahul.jagtap.dmas.user

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
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
import rahul.jagtap.dmas.PayByUpiActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityEditPanCardAadharCardBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class EditPanOrAadharCardActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    private var oldPanCardPhotoFileName: String = ""
    private var oldPanCardPhotoDownloadUrl: String = ""
    private var oldAadharCardPhotoFileName: String = ""
    private var oldAadharCardPhotoDownloadUrl: String = ""
    private var passportPhotoFileName: String = ""
    private var passportPhotoDownloadUrl: String = ""

    //    private var aadharFrontPhotoFileName: String = ""
    //    private var aadharFrontPhotoDownloadUrl: String = ""
    //    private var aadharBackPhotoFileName: String = ""
    //    private var aadharBackPhotoDownloadUrl: String = ""
    //    private var signFileName: String = ""
    //    private var signDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private val TAG = EditPanOrAadharCardActivity::class.java.simpleName
    var imageType = -1 // 1 - passport photo, 2 - payment screenshot
    var oldPanCardPhotoUri: Uri? = null
    var oldAadharCardPhotoUri: Uri? = null
    var passportPhotoUri: Uri? = null

    //    var aadharFrontPhotoUri: Uri? = null
    //    var aadharBackPhotoUri: Uri? = null
    //    var singPhotoUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    var cpd: ProgressDialog? = null
    private var strDate: String? = ""
    private lateinit var easyImage: EasyImage
    lateinit var binding: ActivityEditPanCardAadharCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPanCardAadharCardBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilPanOrAadharCard, binding.tilPanNo, binding.tilFullName, binding.tilFatherName, binding.tilDob, binding.tilOldAadharPhoto, binding.tilOldPanPhoto, binding.tilPassportPhoto, binding.tilAadharNo, binding.tilAddress, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "फोटो शॉप पँण कार्ड / आधार कार्ड"
        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid
        username = app?.preferences?.loggedInUser?.username
        name = app?.preferences?.loggedInUser?.name
        easyImage = EasyImage.Builder(this).setChooserType(ChooserType.CAMERA_AND_GALLERY).allowMultiple(false) // Setting to true will cause taken pictures to show up in the device gallery, DEFAULT false
            .setCopyImagesToPublicGalleryFolder(false).build()
        binding.btnPay.setOnClickListener {
            payUsingUPI(selectedTypeAmount)
        }
        binding.tvPayNote?.setText(spanText, TextView.BufferType.SPANNABLE)
        binding.tvPayNote?.movementMethod = LinkMovementMethod.getInstance()
        binding.btnSuchna.setOnClickListener {
            startActivity(Intent(mContext, ViewSuchnaActivity::class.java).putExtra("suchna", intent.getStringExtra("suchna")
                ?: "").putExtra("title", "फोटो शॉप पँण कार्ड / आधार कार्ड"))
        }

        binding.btnSubmit?.setOnClickListener {
            val strPanOrAadharCard = binding.etPanOrAadharCard.text.toString()
            val strPanNo = binding.etPanNo.text.toString()
            val strFullName = binding.etFullName.text.toString()
            val strFatherName = binding.etFatherName.text.toString()
            val strDob = binding.etDob.text.toString()
            val strAadharNo = binding.etAadharNo.text.toString()
            val strAddress = binding.etAddress.text.toString()
            if (TextUtils.isEmpty(strPanOrAadharCard)) {
                binding.tilPanOrAadharCard?.error = "काय एडीट करायचे? (पँण कार्ड / आधार कार्ड)"
                binding.etPanOrAadharCard?.requestFocus()
                return@setOnClickListener
            }
            if (strPanOrAadharCard.equals("पँण कार्ड", true) && TextUtils.isEmpty(strPanNo)) {
                binding.tilPanNo?.error = "पँण कार्ड नंबर"
                binding.etPanNo?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strFullName)) {
                binding.tilFullName?.error = "व्यक्तीचे संपूर्ण नाव"
                binding.etFullName?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strFatherName)) {
                binding.tilFatherName?.error = "वडीलांचे संपूर्ण नाव"
                binding.etFatherName?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strDob)) {
                binding.tilDob?.error = "जन्मतारीख"
                binding.etDob?.requestFocus()
                return@setOnClickListener
            }
            if (strPanOrAadharCard.equals("आधार कार्ड", true) && oldAadharCardPhotoUri == null) {
                binding.tilOldAadharPhoto?.error = binding.tilOldAadharPhoto?.hint.toString()
                binding.etOldAadharPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (strPanOrAadharCard.equals("पँण कार्ड", true) && oldPanCardPhotoUri == null) {
                binding.tilOldPanPhoto?.error = binding.tilOldPanPhoto?.hint.toString()
                binding.etOldPanPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (passportPhotoUri == null) {
                binding.tilPassportPhoto?.error = "पासपोर्ट साईझ फोटो चा फोटो"
                binding.etPassportPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (strPanOrAadharCard.equals("आधार कार्ड", true) && TextUtils.isEmpty(strAadharNo)) {
                binding.tilAadharNo?.error = "आधार कार्ड नंबर"
                binding.etAadharNo?.requestFocus()
                return@setOnClickListener
            }
            if (strPanOrAadharCard.equals("आधार कार्ड", true) && TextUtils.isEmpty(strAddress)) {
                binding.tilAddress?.error = "संपूर्ण पत्ता पिन सहित"
                binding.etAddress?.requestFocus()
                return@setOnClickListener
            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = "पेमेंट स्कीनशॉट अपलोड करावा"
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            } //            uploadOldPanCardPhoto()
            passportPhotoUri?.let { it1 -> uploadPassportPhoto(it1) }
        }
        binding.etDob?.setOnClickListener {
            val mMaxDate = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(mContext!!, { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val date = "" + dayOfMonth + "/" + (month + 1) + "/" + year
                strDate = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
                binding.etDob.setText(date)
            }, mMaxDate[Calendar.YEAR], mMaxDate[Calendar.MONTH], mMaxDate[Calendar.DAY_OF_MONTH])
            datePickerDialog.show()
        }
        binding.btnPayByUpiId?.setOnClickListener {
            startActivity(Intent(mContext, PayByUpiActivity::class.java))
        }
        binding.etPanOrAadharCard?.setOnClickListener {
            val list = ArrayList<String>()
            list.add("पँण कार्ड")
            list.add("आधार कार्ड")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    binding.etPanOrAadharCard?.setText(list[position])
                    if (position == 0) {
                        binding.tilPanNo?.visible()
                        binding.tilOldPanPhoto?.visible()
                        binding.tilOldAadharPhoto?.gone()
                        binding.tilAadharNo?.gone()
                        binding.tilAddress?.gone()
                    } else if (position == 1) {
                        binding.tilPanNo?.gone()
                        binding.tilOldPanPhoto?.gone()
                        binding.tilOldAadharPhoto?.visible()
                        binding.tilAadharNo?.visible()
                        binding.tilAddress?.visible()
                    }
//                    selectedTypeAmount = extractAmountFromType(strTypeToShow) ?: ""
//                    binding.btnPay.visible()
//                    binding.tvPayNote.visible()
                }
            }.show()
        }
        binding.etPassportPhoto?.setOnClickListener {
            imageType = 1
            choosePhotoWithPermissions()
        }
        binding.etOldPanPhoto?.setOnClickListener {
            imageType = 2
            choosePhotoWithPermissions()
        }
        binding.etOldAadharPhoto?.setOnClickListener {
            imageType = 3
            choosePhotoWithPermissions()
        }
        binding.etPaymentScreenshot?.setOnClickListener {
            imageType = 4
            choosePhotoWithPermissions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                longToast(getString(R.string.txt_try_later))
            }

            override fun onCanceled(source: MediaSource) {}
            override fun onMediaFilesPicked(
                imageFiles: Array<MediaFile>, source: MediaSource
            ) { //                val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
                //                val fileName = AccountingServicesActivity.dateFormatForPhoto.format(System.currentTimeMillis()) + ".jpg"
                //                val image = File("$dir/$fileName")

                lifecycleScope.launch {
                    val compressedImageFile = mContext?.let { Compressor.compress(it, imageFiles[0].file) }
                    Log.e(TAG, "${compressedImageFile?.path}")
                    compressedImageFile?.let {
                        when (imageType) {
                            1 -> {
                                passportPhotoUri = Uri.fromFile(it)
                                binding.etPassportPhoto?.setText("Success")//passportPhotoUri.toString())
                            }

                            2 -> {
                                oldPanCardPhotoUri = Uri.fromFile(it)
                                binding.etOldPanPhoto?.setText("Success")//oldPanCardPhotoUri.toString())
                            }

                            3 -> {
                                oldAadharCardPhotoUri = Uri.fromFile(it)
                                binding.etOldAadharPhoto?.setText("Success")//oldAadharCardPhotoUri.toString())
                            }

                            4 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success")//paymentScreenshotUri.toString())
                            }
                        }
                    }
                }
            }
        })
    }

    private fun uploadPassportPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        passportPhotoFileName = "passport_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("edit_pan_aadhar_cards").child(passportPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    passportPhotoDownloadUrl = uri.toString()
                    if (binding.etPanOrAadharCard.text.toString().equals("पँण कार्ड", true)) {
                        uploadOldPanPhoto(oldPanCardPhotoUri)
                    } else if (binding.etPanOrAadharCard.text.toString().equals("आधार कार्ड", true)) {
                        uploadOldAadharPhoto(oldAadharCardPhotoUri)
                    }
                }.addOnFailureListener {
                    if (cpd?.isShowing == true) cpd?.dismiss()
                    it.printStackTrace()
                }
            } catch (e: java.lang.Exception) {
                if (cpd?.isShowing == true) cpd?.dismiss()
                e.printStackTrace()
            }
        }.addOnFailureListener {
            if (cpd?.isShowing == true) cpd?.dismiss()
            it.message?.let { it1 -> toast(it1) }
        }.addOnProgressListener { //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadOldAadharPhoto(fileUri: Uri?) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        oldAadharCardPhotoFileName = "old_aadhar_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("edit_pan_aadhar_cards").child(oldAadharCardPhotoFileName)
        if (fileUri != null) {
            filepath.putFile(fileUri).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        oldAadharCardPhotoDownloadUrl = uri.toString()
                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
                    }.addOnFailureListener {
                        if (cpd?.isShowing == true) cpd?.dismiss()
                        it.printStackTrace()
                    }
                } catch (e: java.lang.Exception) {
                    if (cpd?.isShowing == true) cpd?.dismiss()
                    e.printStackTrace()
                }
            }.addOnFailureListener {
                if (cpd?.isShowing == true) cpd?.dismiss()
                it.message?.let { it1 -> toast(it1) }
            }.addOnProgressListener { //displaying the upload progress
                val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
                cpd?.setMessage("Please wait.. ")
            }
        }
    }

    private fun uploadOldPanPhoto(fileUri: Uri?) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        oldPanCardPhotoFileName = "old_pan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("edit_pan_aadhar_cards").child(oldPanCardPhotoFileName)
        if (fileUri != null) {
            filepath.putFile(fileUri).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        oldPanCardPhotoDownloadUrl = uri.toString()
                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
                    }.addOnFailureListener {
                        if (cpd?.isShowing == true) cpd?.dismiss()
                        it.printStackTrace()
                    }
                } catch (e: java.lang.Exception) {
                    if (cpd?.isShowing == true) cpd?.dismiss()
                    e.printStackTrace()
                }
            }.addOnFailureListener {
                if (cpd?.isShowing == true) cpd?.dismiss()
                it.message?.let { it1 -> toast(it1) }
            }.addOnProgressListener { //displaying the upload progress
                val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
                cpd?.setMessage("Please wait.. ")
            }
        }
    }

    private fun uploadPaymentScreenshot(fileUri: Uri?) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("edit_pan_aadhar_cards").child(paymentScreenshotFileName)
        if (fileUri != null) {
            filepath.putFile(fileUri).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        paymentScreenshotDownloadUrl = uri.toString()
                        createDbRecord()
                    }.addOnFailureListener {
                        if (cpd?.isShowing == true) cpd?.dismiss()
                        it.printStackTrace()
                    }
                } catch (e: java.lang.Exception) {
                    if (cpd?.isShowing == true) cpd?.dismiss()
                    e.printStackTrace()
                }
            }.addOnFailureListener {
                if (cpd?.isShowing == true) cpd?.dismiss()
                it.message?.let { it1 -> toast(it1) }
            }.addOnProgressListener { //displaying the upload progress
                val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
                cpd?.setMessage("Please wait.. ")
            }
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
        billMap["panOrAadharCard"] = binding.etPanOrAadharCard.text.toString()
        billMap["panNo"] = binding.etPanNo.text.toString()
        billMap["fullName"] = binding.etFullName.text.toString()
        billMap["fatherName"] = binding.etFatherName.text.toString()
        billMap["dob"] = strDate
        billMap["aadharNo"] = binding.etAadharNo.text.toString()
        billMap["address"] = binding.etAddress.text.toString()
        billMap["passportPhotoFileName"] = passportPhotoFileName
        billMap["passportPhotoDownloadUrl"] = passportPhotoDownloadUrl
        billMap["oldPanCardPhotoFileName"] = oldPanCardPhotoFileName
        billMap["oldPanCardPhotoDownloadUrl"] = oldPanCardPhotoDownloadUrl
        billMap["oldAadharCardPhotoFileName"] = oldAadharCardPhotoFileName
        billMap["oldAadharCardPhotoDownloadUrl"] = oldAadharCardPhotoDownloadUrl
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("edit_pan_aadhar_cards").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/edit_pan_aadhar_cards/$pushKey"] = billMap
        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New EDIT पँण कार्ड / आधार कार्ड added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "edit_pan_aadhar_card")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New EDIT पँण कार्ड / आधार कार्ड added by $name")
        }
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/edit_pan_aadhar_cards/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd?.isShowing == true) cpd?.dismiss()
//        toast("Edit अर्ज सादर केला")
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

    @SuppressLint("CheckResult")
    private fun choosePhotoWithPermissions() {
//        rxPermissions?.requestEachCombined(Manifest.permission.CAMERA)?.subscribe {
//            when {
//                it.granted -> { // get picture
                    easyImage.openChooser(this)
//                }
//
//                it.shouldShowRequestPermissionRationale -> { // At least one denied permission without ask never again
//                }
//
//                else -> { // At least one denied permission with ask never again
//                    // Need to go to the settings
//                }
//            }
//        }
    }

    companion object {}
}
