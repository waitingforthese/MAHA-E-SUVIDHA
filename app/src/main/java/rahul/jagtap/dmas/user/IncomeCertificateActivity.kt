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
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.TextView
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
import rahul.jagtap.dmas.databinding.ActivityIncomeCertificateBinding
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class IncomeCertificateActivity : BaseActivity() {
    private var passportPhotoFileName: String = ""
    private var passportPhotoDownloadUrl: String = ""
    private var aadharFrontPhotoFileName: String = ""
    private var aadharFrontPhotoDownloadUrl: String = ""
    private var aadharBackPhotoFileName: String = ""
    private var aadharBackPhotoDownloadUrl: String = ""
    private var rationFrontPhotoFileName: String = ""
    private var rationFrontPhotoDownloadUrl: String = ""
    private var rationBackPhotoFileName: String = ""
    private var rationBackPhotoDownloadUrl: String = ""
    private var bonafideFileName: String = ""
    private var bonafideDownloadUrl: String = ""
    private var talathiResidentCertFileName: String = ""
    private var talathiResidentCertDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private val TAG = IncomeCertificateActivity::class.java.simpleName
    var imageType = -1 // 1 - passport, 2 - aadhar f, 3 - aadhar b, 4 - ration f, 5 - ratio b, 6 - bonafide,
    // 7 - company letter, 8 - sign, 9 - payment screenshot
    var passportPhotoUri: Uri? = null
    var aadharFrontPhotoUri: Uri? = null
    var aadharBackPhotoUri: Uri? = null
    var rationFrontPhotoUri: Uri? = null
    var rationBackPhotoUri: Uri? = null
    var bonafideUri: Uri? = null
    var talathiResidentCertUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    var cpd: ProgressDialog? = null
    private var strDate: String? = ""
    private lateinit var easyImage: EasyImage
    lateinit var binding: ActivityIncomeCertificateBinding
    private var selectedTypeAmount = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeCertificateBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilFullName, binding.tilFatherName, binding.tilMobileNo, binding.tilEmail, binding.tilAddress, binding.tilCertificatePurpose, binding.tilDob, binding.tilPassportPhoto, binding.tilAadharFPhoto, binding.tilAadharBPhoto, binding.tilRationCardFPhoto, binding.tilRationCardBPhoto, binding.tilBonafideLcPhoto, binding.tilTalathiResidentCertificate, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "Income Certificate"
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
        binding.tvPayNote?.setText(spanText, TextView.BufferType.SPANNABLE)
        binding.tvPayNote?.movementMethod = LinkMovementMethod.getInstance()
        binding.btnSuchna.setOnClickListener {
            startActivity(Intent(mContext, ViewSuchnaActivity::class.java).putExtra("suchna", intent.getStringExtra("suchna")
                ?: "").putExtra("title", "Income Certificate"))
        }

        binding.etDob?.setOnClickListener {
            val mMaxDate = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(mContext!!, { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val date = "" + dayOfMonth + "/" + (month + 1) + "/" + year
                strDate = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
                binding.etDob?.setText(date)
            }, mMaxDate[Calendar.YEAR], mMaxDate[Calendar.MONTH], mMaxDate[Calendar.DAY_OF_MONTH])
            datePickerDialog.show()
        }

        binding.btnSubmit?.setOnClickListener {
            val strFullName = binding.etFullName.text.toString()
            val strFatherName = binding.etFatherName.text.toString()
            val strMobileNo = binding.etMobileNo.text.toString()
            val strEmail = binding.etEmail.text.toString()
            val strAddress = binding.etAddress.text.toString()
            val strDob = binding.etDob.text.toString()
            val strCertificatePurpose = binding.etCertificatePurpose.text.toString()
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
            if (TextUtils.isEmpty(strMobileNo)) {
                binding.tilMobileNo?.error = "मोबाईल नंबर"
                binding.etMobileNo?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strEmail)) {
                binding.tilEmail?.error = "इमेल आयडी"
                binding.etEmail?.requestFocus()
                return@setOnClickListener
            }
            if (!Utils.isValidEmail(strEmail)) {
                binding.tilEmail?.error = "वैध इमेल आयडी टाका"
                binding.etEmail?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strAddress)) {
                binding.tilAddress?.error = "संपूर्ण पत्ता पिन सहित"
                binding.etAddress?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strCertificatePurpose)) {
                binding.tilCertificatePurpose?.error = "प्रमाणपत्र कशासाठी हवे त्याचा तपशील"
                binding.etCertificatePurpose?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strDob)) {
                binding.tilDob?.error = "व्यक्तीची संपूर्ण जन्मतारीख"
                binding.etDob?.requestFocus()
                return@setOnClickListener
            }
            if (passportPhotoUri == null) {
                binding.tilPassportPhoto?.error = "पासपोर्ट साईझ फोटो चा फोटो"
                binding.etPassportPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (aadharFrontPhotoUri == null) {
                binding.tilAadharFPhoto?.error = "आधार कार्ड च्या पुढील बाजूचा फोटो निवडा"
                binding.etAadharFPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (aadharBackPhotoUri == null) {
                binding.tilAadharBPhoto?.error = "आधार कार्ड च्या मागील बाजूचा फोटो निवडा"
                binding.etAadharBPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (rationFrontPhotoUri == null) {
                binding.tilRationCardFPhoto?.error = "ओरिजिनल रेशन कार्ड कार्डच्या पुढील बाजूचा फोटो"
                binding.etRationCardFPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (rationBackPhotoUri == null) {
                binding.tilRationCardBPhoto?.error = "ओरिजिनल रेशन कार्ड कार्डच्या मागील बाजूचा फोटो"
                binding.etRationCardBPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (bonafideUri == null) {
                binding.tilBonafideLcPhoto?.error = "ओरिजिनल बोनाफाईड किव्हा शाळा सोडल्याचा प्रमाणपत्र"
                binding.etBonafideLcPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (talathiResidentCertUri == null) {
                binding.tilTalathiResidentCertificate?.error = "उत्पन्न अहवाल तलाठी ग्रामसेवक"
                binding.etTalathiResidentCertificate?.requestFocus()
                return@setOnClickListener
            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = "पेमेंट स्कीनशॉट अपलोड करावा"
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            }
            uploadPassportPhoto(passportPhotoUri!!)
        }
        binding.etPassportPhoto?.setOnClickListener {
            imageType = 1
            choosePhotoWithPermissions()
        }
        binding.etAadharFPhoto?.setOnClickListener {
            imageType = 2
            choosePhotoWithPermissions()
        }
        binding.etAadharBPhoto?.setOnClickListener {
            imageType = 3
            choosePhotoWithPermissions()
        }
        binding.etRationCardFPhoto?.setOnClickListener {
            imageType = 4
            choosePhotoWithPermissions()
        }
        binding.etRationCardBPhoto?.setOnClickListener {
            imageType = 5
            choosePhotoWithPermissions()
        }
        binding.etBonafideLcPhoto?.setOnClickListener {
            imageType = 6
            choosePhotoWithPermissions()
        }
        binding.etTalathiResidentCertificate?.setOnClickListener {
            imageType = 7
            choosePhotoWithPermissions()
        }
        binding.etPaymentScreenshot?.setOnClickListener {
            imageType = 8
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
                                aadharFrontPhotoUri = Uri.fromFile(it)
                                binding.etAadharFPhoto?.setText("Success")//aadharFrontPhotoUri.toString())
                            }
                            3 -> {
                                aadharBackPhotoUri = Uri.fromFile(it)
                                binding.etAadharBPhoto?.setText("Success")//aadharBackPhotoUri.toString())
                            }
                            4 -> {
                                rationFrontPhotoUri = Uri.fromFile(it)
                                binding.etRationCardFPhoto?.setText("Success")//rationFrontPhotoUri.toString())
                            }
                            5 -> {
                                rationBackPhotoUri = Uri.fromFile(it)
                                binding.etRationCardBPhoto?.setText("Success")//rationBackPhotoUri.toString())
                            }
                            6 -> {
                                bonafideUri = Uri.fromFile(it)
                                binding.etBonafideLcPhoto?.setText("Success")//bonafideUri.toString())
                            }
                            7 -> {
                                talathiResidentCertUri = Uri.fromFile(it)
                                binding.etTalathiResidentCertificate?.setText("Success")//talathiResidentCertUri.toString())
                            }
                            8 -> {
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("income_certificates").child(passportPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    passportPhotoDownloadUrl = uri.toString()
                    aadharFrontPhotoUri?.let { it1 -> uploadAadharFrontPhoto(it1) }
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
        }.addOnProgressListener {
            //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadAadharFrontPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("income_certificates").child(aadharFrontPhotoFileName)
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
        }.addOnProgressListener {
            //displaying the upload progress
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("income_certificates").child(aadharBackPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    aadharBackPhotoDownloadUrl = uri.toString()
                    rationFrontPhotoUri?.let { it1 -> uploadRationFrontPhoto(it1) }
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
        }.addOnProgressListener {
            //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadRationFrontPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        rationFrontPhotoFileName = "ration_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("income_certificates").child(rationFrontPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    rationFrontPhotoDownloadUrl = uri.toString()
                    rationBackPhotoUri?.let { it1 -> uploadRationBackPhoto(it1) }
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
        }.addOnProgressListener {
            //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadRationBackPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        rationBackPhotoFileName = "ration_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("income_certificates").child(rationBackPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    rationBackPhotoDownloadUrl = uri.toString()
                    bonafideUri?.let { it1 -> uploadBonafidePhoto(it1) }
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
        }.addOnProgressListener {
            //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadBonafidePhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        bonafideFileName = "bonafide_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("income_certificates").child(bonafideFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    bonafideDownloadUrl = uri.toString()
                    talathiResidentCertUri?.let { it1 -> uploadTalathiResidentCertPhoto(it1) }
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
        }.addOnProgressListener {
            //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadTalathiResidentCertPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        talathiResidentCertFileName = "talathi_income_report_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("income_certificates").child(talathiResidentCertFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    talathiResidentCertDownloadUrl = uri.toString()
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
        }.addOnProgressListener {
            //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadPaymentScreenshot(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("income_certificates").child(paymentScreenshotFileName)
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
        billMap["fullName"] = binding.etFullName.text.toString()
        billMap["fatherName"] = binding.etFatherName.text.toString()
        billMap["mobileNo"] = binding.etMobileNo.text.toString()
        billMap["userEmail"] = binding.etEmail.text.toString()
        billMap["address"] = binding.etAddress.text.toString()
        billMap["certificatePurpose"] = binding.etCertificatePurpose.text.toString()
        billMap["dob"] = strDate
        billMap["passportPhotoFileName"] = passportPhotoFileName
        billMap["passportPhotoDownloadUrl"] = passportPhotoDownloadUrl
        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
        billMap["rationFrontPhotoFileName"] = rationFrontPhotoFileName
        billMap["rationFrontPhotoDownloadUrl"] = rationFrontPhotoDownloadUrl
        billMap["rationBackPhotoFileName"] = rationBackPhotoFileName
        billMap["rationBackPhotoDownloadUrl"] = rationBackPhotoDownloadUrl
        billMap["bonafideFileName"] = bonafideFileName
        billMap["bonafideDownloadUrl"] = bonafideDownloadUrl
        billMap["talathiResidentCertFileName"] = talathiResidentCertFileName
        billMap["talathiResidentCertDownloadUrl"] = talathiResidentCertDownloadUrl
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("income_certificates").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/income_certificates/$pushKey"] = billMap
        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Income Certificate added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_income_certificate")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Income Certificate added by $name")
        }
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/income_certificates/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd?.isShowing == true) cpd?.dismiss()
//        toast("Income Certificate created successfully")
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

    companion object {}
}
