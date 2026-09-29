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
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.PayByUpiActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityPassportBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.longToast
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class PassportActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    //    private var oldPanCardPhotoFileName: String = ""
    //    private var oldPanCardPhotoDownloadUrl: String = ""
    //    private var passportPhotoFileName: String = ""
    //    private var passportPhotoDownloadUrl: String = ""
    private var aadharFrontPhotoFileName: String = ""
    private var aadharFrontPhotoDownloadUrl: String = ""
    private var aadharBackPhotoFileName: String = ""
    private var aadharBackPhotoDownloadUrl: String = ""
    private var panFileName: String = ""
    private var panDownloadUrl: String = ""
    private var oldPassportPhotoFileName: String = ""
    private var oldPassportPhotoDownloadUrl: String = ""
    private var oldPassportFileNoPhotoFileName: String = ""
    private var oldPassportFileNoPhotoDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private val TAG = PassportActivity::class.java.simpleName
    var imageType = -1 // 1 - payment screenshot

    //    var oldPanCardPhotoUri: Uri? = null
    //    var passportPhotoUri: Uri? = null
    var aadharFrontPhotoUri: Uri? = null
    var aadharBackPhotoUri: Uri? = null
    var panPhotoUri: Uri? = null
    var oldPassportPhotoUri: Uri? = null
    var oldPassportFileNoPhotoUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    var cpd: ProgressDialog? = null
    private var strDate: String? = ""
    private lateinit var easyImage: EasyImage
    private var strTypeToShow: String? = ""
    private var strType: String? = ""
    lateinit var binding: ActivityPassportBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassportBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilType, binding.tilApplicantName, binding.tilDob, binding.tilEmail, binding.tilMobileNo, binding.tilAadharNo, binding.tilBirthPlace, binding.tilEducation, binding.tilEmployment, binding.tilMaritalStatus, binding.tilAddress, binding.tilFatherName, binding.tilMotherName, binding.tilEmergencyName, binding.tilEmergencyMobileNo, binding.tilHusbandWifeName, binding.tilAadharFPhoto, binding.tilAadharBPhoto, binding.tilOldPassportPhoto, binding.tilOldPassportPhotoFileNo, binding.tilPanPhoto, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "पासपोर्ट (नवीन/दुरूस्ती)"
        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid
        username = app?.preferences?.loggedInUser?.username
        name = app?.preferences?.loggedInUser?.name
        easyImage = EasyImage.Builder(this)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(false) // Setting to true will cause taken pictures to show up in the device gallery, DEFAULT false
            .setCopyImagesToPublicGalleryFolder(false)
            .build()
        binding.btnPay.setOnClickListener {
            payUsingUPI(selectedTypeAmount)
        }
        binding.tvPayNote?.setText(spanText, TextView.BufferType.SPANNABLE)
        binding.tvPayNote?.movementMethod = LinkMovementMethod.getInstance()
        binding.btnSuchna.setOnClickListener {
            startActivity(Intent(mContext, ViewSuchnaActivity::class.java).putExtra("suchna", intent.getStringExtra("suchna")
                ?: "").putExtra("title", "पासपोर्ट (नवीन/दुरूस्ती)"))
        }
        typeMap = intent.extras?.getSerializable("hashMap") as HashMap<String, HashMap<String, String>>
        val list = ArrayList<String>()
        typeMap?.forEach {
            if (it.value["type_title"] != "0") list.add(it.value["type_title"].toString())
        }
        if (list.size == 0) {
            binding.tilType.gone()
            binding.tilEmail.gone()
            binding.tilMobileNo.gone()
            binding.tilAadharNo.gone()
            binding.tilBirthPlace.gone()
            binding.tilEducation.gone()
            binding.tilEmployment.gone()
            binding.tilMaritalStatus.gone()
            binding.tilAddress.gone()
            binding.tilFatherName.gone()
            binding.tilMotherName.gone()
            binding.tilEmergencyName.gone()
            binding.tilEmergencyMobileNo.gone()
            binding.tilHusbandWifeName.gone()
            binding.tilAadharFPhoto.gone()
            binding.tilAadharBPhoto.gone()
            binding.tilOldPassportPhoto.gone()
            binding.tilOldPassportPhotoFileNo.gone()
            binding.tilPanPhoto.gone()
            binding.tilPaymentScreenshot.gone()
            binding.btnSubmit.gone()
        }
        /** Selects the subtype at [position] (shared by the picker dialog and grid deep-links). */
        fun applyType(position: Int) {

                    binding.etType.setText(list[position])
                    strTypeToShow = list[position]
                    val keyFromValue = typeMap?.let { it1 -> Utils.getKeyFromNestedHashMap(it1, strTypeToShow!!) }
                    when (keyFromValue) {
                        TYPE_NAVIN -> {
                            strType = TYPE_NAVIN
                            binding.tilOldPassportPhoto?.gone()
                            binding.tilOldPassportPhotoFileNo?.gone()
                        }
                        TYPE_DURUSTI -> {
                            strType = TYPE_DURUSTI
                            binding.tilOldPassportPhoto?.visible()
                            binding.tilOldPassportPhotoFileNo?.visible()
                        }
                    }
                    selectedTypeAmount = extractAmountFromType(strTypeToShow!!) ?: ""
                    binding.btnPay.visible()
                    binding.tvPayNote.visible()
                
        }
        val preselectKey = intent.getStringExtra(EsuvidhaServiceRegistry.EXTRA_PRESELECT_SUBTYPE)
        if (!preselectKey.isNullOrEmpty()) {
            val preTitle = typeMap?.get(preselectKey)?.get("type_title")
            val preIdx = if (!preTitle.isNullOrEmpty()) list.indexOf(preTitle) else -1
            if (preIdx >= 0) applyType(preIdx)
        }
        binding.etType?.setOnClickListener {
//            val list = ArrayList<String>()
//            list.add("नवीन")
//            list.add("दुरुस्ती")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    applyType(position)
                }
            }.show()
        }

        binding.btnSubmit?.setOnClickListener {
            val strApplicantName = binding.etApplicantName.text.toString()
            val strDob = binding.etDob.text.toString()
            val strEmail = binding.etEmail.text.toString()
            val strMobileNo = binding.etMobileNo.text.toString()
            val strAadharNo = binding.etAadharNo.text.toString()
            val strBirthPlace = binding.etBirthPlace.text.toString()
            val strEducation = binding.etEducation.text.toString()
            val strEmployment = binding.etEmployment.text.toString()
            val strMaritalStatus = binding.etMaritalStatus.text.toString()
            val strAddress = binding.etAddress.text.toString()
            val strFatherName = binding.etFatherName.text.toString()
            val strMotherName = binding.etMotherName.text.toString()
            val strEmergencyName = binding.etEmergencyName.text.toString()
            val strEmergencyMobileNo = binding.etEmergencyMobileNo.text.toString()
            val strHusbandWifeName = binding.etHusbandWifeName.text.toString()
            if (TextUtils.isEmpty(strType)) {
                binding.tilType?.error = binding.tilType.hint.toString()
                binding.etType?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strApplicantName)) {
                binding.tilApplicantName?.error = "कस्टमरचे नाव"
                binding.etApplicantName?.requestFocus()
                return@setOnClickListener
            }
//            if (TextUtils.isEmpty(strDob)) {
//                etDob?.error = "अर्जदाराची जन्मतारीख"
//                etDob?.requestFocus()
//                return@setOnClickListener
//            }
            if (TextUtils.isEmpty(strEmail)) {
                binding.tilEmail?.error = "इमेल आयडी"
                binding.etEmail?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strMobileNo)) {
                binding.tilMobileNo?.error = "मोबाईल नंबर"
                binding.etMobileNo?.requestFocus()
                return@setOnClickListener
            }
//            if (TextUtils.isEmpty(strAadharNo)) {
//                etAadharNo?.error = "आधार नंबर"
//                etAadharNo?.requestFocus()
//                return@setOnClickListener
//            }
            if (TextUtils.isEmpty(strBirthPlace)) {
                binding.tilBirthPlace?.error = "जन्म ठिकाण"
                binding.etBirthPlace?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strEducation)) {
                binding.tilEducation?.error = "शिक्षण"
                binding.etEducation?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strEmployment)) {
                binding.tilEmployment?.error = "नोकरी(सरकारी /  खासगी / विद्यार्थी)"
                binding.etEmployment?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strMaritalStatus)) {
                binding.tilMaritalStatus?.error = "वैवाहिक स्थिती(सिंगल / विवाहित)"
                binding.etMaritalStatus?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strAddress)) {
                binding.tilAddress?.error = "तुमचे पोलीस स्टेशन कोणते"
                binding.etAddress?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strFatherName)) {
                binding.tilFatherName?.error = "वडीलांचे नाव"
                binding.etFatherName?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strMotherName)) {
                binding.tilMotherName?.error = "आई चे नाव"
                binding.etMotherName?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEmergencyName.visibility == View.VISIBLE && TextUtils.isEmpty(strEmergencyName)) {
                binding.tilEmergencyName?.error = "इमर्जन्सी नाव"
                binding.etEmergencyName?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strEmergencyMobileNo)) {
                binding.tilEmergencyMobileNo?.error = "इमर्जन्सी मोबाईल नंबर"
                binding.etEmergencyMobileNo?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strHusbandWifeName)) {
                binding.tilHusbandWifeName?.error = binding.tilHusbandWifeName.hint.toString()
                binding.etHusbandWifeName?.requestFocus()
                return@setOnClickListener
            }
            if (aadharFrontPhotoUri == null) {
                binding.tilAadharFPhoto?.error = "आधार कार्ड पुढील फोटो"
                binding.etAadharFPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (aadharBackPhotoUri == null) {
                binding.tilAadharBPhoto?.error = "आधार कार्ड मागील फोटो"
                binding.etAadharBPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (panPhotoUri == null) {
                binding.tilPanPhoto?.error = binding.tilPanPhoto.hint.toString()
                binding.etPanPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (strType.equals(TYPE_DURUSTI) && oldPassportPhotoUri == null) {
                binding.tilOldPassportPhoto?.error = binding.tilOldPassportPhoto.hint.toString()
                binding.etOldPassportPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (strType.equals(TYPE_DURUSTI) && oldPassportFileNoPhotoUri == null) {
                binding.tilOldPassportPhotoFileNo?.error = binding.tilOldPassportPhotoFileNo.hint.toString()
                binding.etOldPassportPhotoFileNo?.requestFocus()
                return@setOnClickListener
            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = "पेमेंट स्कीनशॉट अपलोड करावा"
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            } //            uploadOldPanCardPhoto()
            uploadAadharFrontPhoto(aadharFrontPhotoUri!!)
        }
        binding.btnPayByUpiId?.setOnClickListener {
            startActivity(Intent(mContext, PayByUpiActivity::class.java))
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
        binding.etEmployment?.setOnClickListener {
            val list = ArrayList<String>()
            list.add("सरकारी")
            list.add("खासगी")
            list.add("विद्यार्थी")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    binding.etEmployment.setText(list[position])
                }
            }.show()
        }
        binding.etMaritalStatus?.setOnClickListener {
            val list = ArrayList<String>()
            list.add("सिंगल")
            list.add("विवाहित")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    binding.etMaritalStatus.setText(list[position])
                }
            }.show()
        }
        binding.etAadharFPhoto?.setOnClickListener {
            imageType = 1
            choosePhotoWithPermissions()
        }
        binding.etAadharBPhoto?.setOnClickListener {
            imageType = 2
            choosePhotoWithPermissions()
        }
        binding.etPanPhoto?.setOnClickListener {
            imageType = 3
            choosePhotoWithPermissions()
        }
        binding.etPaymentScreenshot?.setOnClickListener {
            imageType = 4
            choosePhotoWithPermissions()
        }
        binding.etOldPassportPhoto?.setOnClickListener {
            imageType = 5
            choosePhotoWithPermissions()
        }
        binding.etOldPassportPhotoFileNo?.setOnClickListener {
            imageType = 6
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
                                aadharFrontPhotoUri = Uri.fromFile(it)
                                binding.etAadharFPhoto?.setText("Success")//aadharFrontPhotoUri.toString())
                            }
                            2 -> {
                                aadharBackPhotoUri = Uri.fromFile(it)
                                binding.etAadharBPhoto?.setText("Success")//aadharBackPhotoUri.toString())
                            }
                            3 -> {
                                panPhotoUri = Uri.fromFile(it)
                                binding.etPanPhoto?.setText("Success")//panPhotoUri.toString())
                            }
                            4 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success")//paymentScreenshotUri.toString())
                            }
                            5 -> {
                                oldPassportPhotoUri = Uri.fromFile(it)
                                binding.etOldPassportPhoto?.setText("Success")//paymentScreenshotUri.toString())
                            }
                            6 -> {
                                oldPassportFileNoPhotoUri = Uri.fromFile(it)
                                binding.etOldPassportPhotoFileNo?.setText("Success")//paymentScreenshotUri.toString())
                            }
                        }
                    }
                }
            }
        })
    }

    private fun uploadAadharFrontPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("passports").child(aadharFrontPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    aadharFrontPhotoDownloadUrl = uri.toString()
                    aadharBackPhotoUri?.let { it1 -> uploadAadharBackPhoto(it1) }
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

    private fun uploadAadharBackPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharBackPhotoFileName = "aadhar_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("passports").child(aadharBackPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    aadharBackPhotoDownloadUrl = uri.toString()
                    panPhotoUri?.let { it1 -> uploadPanPhoto(it1) }
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

    private fun uploadPanPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        panFileName = "pan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("passports").child(panFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    panDownloadUrl = uri.toString()
                    if (strType == TYPE_DURUSTI) {
                        oldPassportPhotoUri?.let { it1 -> uploadOldPassportPhoto(it1) }
                    } else {
                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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

    private fun uploadOldPassportPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        oldPassportPhotoFileName = "old_passport_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("passports").child(oldPassportPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    oldPassportPhotoDownloadUrl = uri.toString()
                    oldPassportFileNoPhotoUri?.let { it1 -> uploadOldPassportFileNoPhoto(it1) }
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

    private fun uploadOldPassportFileNoPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        oldPassportFileNoPhotoFileName = "old_passport_file_no_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("passports").child(oldPassportFileNoPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    oldPassportFileNoPhotoDownloadUrl = uri.toString()
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

    private fun uploadPaymentScreenshot(fileUri: Uri?) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("passports").child(paymentScreenshotFileName)
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
        billMap["type"] = strType
        billMap["typeToShow"] = strTypeToShow
        billMap["applicantName"] = binding.etApplicantName.text.toString()
        billMap["dob"] = strDate
        billMap["applicantEmail"] = binding.etEmail.text.toString()
        billMap["mobileNo"] = binding.etMobileNo.text.toString()
        billMap["aadharNo"] = binding.etAadharNo.text.toString()
        billMap["birthPlace"] = binding.etBirthPlace.text.toString()
        billMap["education"] = binding.etEducation.text.toString()
        billMap["employment"] = binding.etEmployment.text.toString()
        billMap["maritalStatus"] = binding.etMaritalStatus.text.toString()
        billMap["address"] = binding.etAddress.text.toString()
        billMap["fatherName"] = binding.etFatherName.text.toString()
        billMap["motherName"] = binding.etMotherName.text.toString()
        billMap["emergencyName"] = binding.etEmergencyName.text.toString()
        billMap["emergencyMobileNo"] = binding.etEmergencyMobileNo.text.toString()
        billMap["husbandWifeName"] = binding.etHusbandWifeName.text.toString()
        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
        billMap["panFileName"] = panFileName
        billMap["panDownloadUrl"] = panDownloadUrl
        billMap["oldPassportPhotoFileName"] = oldPassportPhotoFileName
        billMap["oldPassportPhotoDownloadUrl"] = oldPassportPhotoDownloadUrl
        billMap["oldPassportFileNoPhotoFileName"] = oldPassportFileNoPhotoFileName
        billMap["oldPassportFileNoPhotoDownloadUrl"] = oldPassportFileNoPhotoDownloadUrl
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("passports").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/passports/$pushKey"] = billMap
        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Passport added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_passport")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Passport added by $name")
        }
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/passports/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd?.isShowing == true) cpd?.dismiss()
//        toast("पासपोर्ट अर्ज सादर केला")
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
//                it.shouldShowRequestPermissionRationale -> { // At least one denied permission without ask never again
//                }
//                else -> { // At least one denied permission with ask never again
//                    // Need to go to the settings
//                }
//            }
//        }
    }

    companion object {
        val TYPE_DURUSTI = "TYPE_DURUSTI"
        val TYPE_NAVIN = "TYPE_NAVIN"
//        val TYPE_DURUSTI = "दुरुस्ती"
//        val TYPE_NAVIN = "नवीन"
    }
}
