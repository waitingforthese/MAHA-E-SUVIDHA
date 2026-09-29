package rahul.jagtap.dmas.user

import android.Manifest
import android.annotation.SuppressLint
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
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityGstRegistrationBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class GstRegistrationActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    //    अर्जदाराचे / कंपनी पँण कार्ड
    //    ऑनलाईन भाडे करार
    //    लाईट बिल
    //    भागीरारी करार इतर (पर्यायी)
    //    पेमेंट स्क्रीन शॉट
    private var aadharFrontPhotoFileName: String = ""
    private var aadharFrontPhotoDownloadUrl: String = ""
    private var aadharBackPhotoFileName: String = ""
    private var aadharBackPhotoDownloadUrl: String = ""
    private var companyPanCardPhotoFileName: String = ""
    private var companyPanCardPhotoDownloadUrl: String = ""
    private var onlineRentalAgreeFileName: String = ""
    private var onlineRentalAgreeDownloadUrl: String = ""
    private var lightBillFileName: String = ""
    private var lightBillDownloadUrl: String = ""
//    private var partnershipAgreeFileName: String = ""
//    private var partnershipAgreeDownloadUrl: String = ""
    private var udyamAadharFileName: String = ""
    private var udyamAadharDownloadUrl: String = ""
    private var partnershipDeedFileName: String = ""
    private var partnershipDeedDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private val TAG = GstRegistrationActivity::class.java.simpleName
    var imageType = -1
    var aadharFrontPhotoUri: Uri? = null
    var aadharBackPhotoUri: Uri? = null
    var companyPanCardPhotoUri: Uri? = null
    var onlineRentalAgreeUri: Uri? = null
    var lightBillUri: Uri? = null
//    var partnershipAgreeUri: Uri? = null
    var udyamAadharUri: Uri? = null
    var partnershipDeedUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    var cpd: ProgressDialog? = null
    private lateinit var easyImage: EasyImage
    private var strTypeToShow: String? = ""
    private var strType: String? = ""
    lateinit var binding: ActivityGstRegistrationBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGstRegistrationBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilType, binding.tilBusinessName, binding.tilApplicantName, binding.tilMobileNo, binding.tilEmail, binding.tilAadharFrontPhoto, binding.tilAadharBackPhoto, binding.tilCompanyPanCard, binding.tilOnlineRentalAgreement, binding.tilLightBillPhoto, binding.tilPartnershipAgreement, binding.tilUdyamAadhar, binding.tilPartnershipDeed, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "GST Registration"
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
                ?: "").putExtra("title", "GST Registration"))
        }
        typeMap = intent.extras?.getSerializable("hashMap") as HashMap<String, HashMap<String, String>>
        val list = ArrayList<String>()
        typeMap?.forEach {
            if (it.value["type_title"] != "0") list.add(it.value["type_title"].toString())
        }
        if (list.size == 0) {
            binding.tilType.gone()
            binding.tilBusinessName.gone()
            binding.tilMobileNo.gone()
            binding.tilEmail.gone()
            binding.tilAadharFrontPhoto.gone()
            binding.tilAadharBackPhoto.gone()
            binding.tilCompanyPanCard.gone()
            binding.tilOnlineRentalAgreement.gone()
            binding.tilLightBillPhoto.gone()
            binding.tilPartnershipAgreement.gone()
            binding.tilUdyamAadhar.gone()
            binding.tilPartnershipDeed.gone()
            binding.tilPaymentScreenshot.gone()
            binding.btnSubmit.gone()
        }
        /** Selects the subtype at [position] (shared by the picker dialog and grid deep-links). */
        fun applyType(position: Int) {

                    binding.etType.setText(list[position])
                    strTypeToShow = list[position]
                    val keyFromValue = typeMap?.let { it1 -> Utils.getKeyFromNestedHashMap(it1, strTypeToShow!!) }
                    when (keyFromValue) {
                        TYPE_PROPRIETOR -> {
                            strType = TYPE_PROPRIETOR
                            binding.tilPartnershipDeed?.gone()
                        }
                        TYPE_PARTNERSHIP -> {
                            strType = TYPE_PARTNERSHIP
                            binding.tilPartnershipDeed?.visible()
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
//            list.add("प्रोप्रायटर")
//            list.add("पार्टनरशिप")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    applyType(position)
                }
            }.show()
        }
        binding.btnSubmit?.setOnClickListener {
            val strBusinessName = binding.etBusinessName.text.toString()
            val strApplicantName = binding.etApplicantName.text.toString()
            val strMobileNo = binding.etMobileNo.text.toString()
            val strEmail = binding.etEmail.text.toString()
            if (TextUtils.isEmpty(strTypeToShow)) {
                binding.tilType?.error = binding.tilType.hint.toString()
                binding.etType?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strBusinessName)) {
                binding.tilBusinessName?.error = binding.tilBusinessName?.hint.toString()
                binding.etBusinessName?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strApplicantName)) {
                binding.tilApplicantName?.error = binding.tilApplicantName?.hint.toString()
                binding.etApplicantName?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strMobileNo)) {
                binding.tilMobileNo?.error = binding.tilMobileNo?.hint.toString()
                binding.etMobileNo?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strEmail)) {
                binding.tilEmail?.error = binding.tilEmail?.hint.toString()
                binding.etEmail?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strMobileNo)) {
                binding.tilMobileNo?.error = binding.tilMobileNo?.hint.toString()
                binding.etMobileNo?.requestFocus()
                return@setOnClickListener
            }
            if (aadharFrontPhotoUri == null) {
                binding.tilAadharFrontPhoto?.error = binding.tilAadharFrontPhoto?.hint.toString()
                binding.etAadharFrontPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (aadharBackPhotoUri == null) {
                binding.tilAadharBackPhoto?.error = binding.tilAadharBackPhoto?.hint.toString()
                binding.etAadharBackPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (companyPanCardPhotoUri == null) {
                binding.tilCompanyPanCard?.error = binding.tilCompanyPanCard?.hint.toString()
                binding.etCompanyPanCard?.requestFocus()
                return@setOnClickListener
            }
            if (onlineRentalAgreeUri == null) {
                binding.tilOnlineRentalAgreement?.error = binding.tilOnlineRentalAgreement?.hint.toString()
                binding.etOnlineRentalAgreement?.requestFocus()
                return@setOnClickListener
            }
            if (lightBillUri == null) {
                binding.tilLightBillPhoto?.error = binding.tilLightBillPhoto?.hint.toString()
                binding.etLightBillPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (udyamAadharUri == null) {
                binding.tilUdyamAadhar?.error = binding.tilUdyamAadhar?.hint.toString()
                binding.etUdyamAadhar?.requestFocus()
                return@setOnClickListener
            }
            if (strType.equals(TYPE_PARTNERSHIP) && partnershipDeedUri == null) {
                binding.tilPartnershipDeed?.error = binding.tilPartnershipDeed?.hint.toString()
                binding.etPartnershipDeed?.requestFocus()
                return@setOnClickListener
            } //            if (partnershipAgreeUri == null) {
            //                etPartnershipAgreement?.error = etPartnershipAgreement?.hint.toString()
            //                etPartnershipAgreement?.requestFocus()
            //                return@setOnClickListener
            //            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = binding.tilPaymentScreenshot?.hint.toString()
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            }
            uploadAadharFPhoto()
        }
        binding.etAadharFrontPhoto?.setOnClickListener { choosePhotoWithPermissions(1) }
        binding.etAadharBackPhoto?.setOnClickListener { choosePhotoWithPermissions(2) }
        binding.etCompanyPanCard?.setOnClickListener { choosePhotoWithPermissions(3) }
        binding.etOnlineRentalAgreement?.setOnClickListener { choosePhotoWithPermissions(4) }
        binding.etLightBillPhoto?.setOnClickListener { choosePhotoWithPermissions(5) }
//        binding.etPartnershipAgreement?.setOnClickListener { choosePhotoWithPermissions(6) }
        binding.etUdyamAadhar?.setOnClickListener { choosePhotoWithPermissions(7) }
        binding.etPaymentScreenshot?.setOnClickListener { choosePhotoWithPermissions(8) }
        binding.etPartnershipDeed?.setOnClickListener { choosePhotoWithPermissions(9) }
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
                                aadharFrontPhotoUri = Uri.fromFile(it)
                                binding.etAadharFrontPhoto?.setText("Success")//aadharFrontPhotoUri.toString())
                            }
                            2 -> {
                                aadharBackPhotoUri = Uri.fromFile(it)
                                binding.etAadharBackPhoto?.setText("Success")//aadharBackPhotoUri.toString())
                            }
                            3 -> {
                                companyPanCardPhotoUri = Uri.fromFile(it)
                                binding.etCompanyPanCard?.setText("Success")//companyPanCardPhotoUri.toString())
                            }
                            4 -> {
                                onlineRentalAgreeUri = Uri.fromFile(it)
                                binding.etOnlineRentalAgreement?.setText("Success")//onlineRentalAgreeUri.toString())
                            }
                            5 -> {
                                lightBillUri = Uri.fromFile(it)
                                binding.etLightBillPhoto?.setText("Success")//lightBillUri.toString())
                            }
//                            6 -> {
//                                partnershipAgreeUri = Uri.fromFile(it)
//                                etPartnershipAgreement?.setText(partnershipAgreeUri.toString())
//                            }
                            7 -> {
                                udyamAadharUri = Uri.fromFile(it)
                                binding.etUdyamAadhar?.setText("Success")//udyamAadharUri.toString())
                            }
                            8 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success")//paymentScreenshotUri.toString())
                            }
                            9 -> {
                                partnershipDeedUri = Uri.fromFile(it)
                                binding.etPartnershipDeed?.setText("Success")//paymentScreenshotUri.toString())
                            }
                        }
                    }
                }
            }
        })
    }

    private fun uploadAadharFPhoto() {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(aadharFrontPhotoFileName)
        aadharFrontPhotoUri?.let {
            filepath.putFile(it).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        aadharFrontPhotoDownloadUrl = uri.toString()
                        aadharBackPhotoUri?.let { it1 -> uploadAadharBPhoto(it1) }
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
            }.addOnProgressListener { //displaying the upload progress
                val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
                cpd?.setMessage("Please wait.. ")
            }
        }
    }

    private fun uploadAadharBPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharBackPhotoFileName = "aadhar_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(aadharBackPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    aadharBackPhotoDownloadUrl = uri.toString()
                    companyPanCardPhotoUri?.let { it1 -> uploadCompanyPanCard(it1) }
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
        }.addOnProgressListener { //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadCompanyPanCard(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        companyPanCardPhotoFileName = "company_pan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(companyPanCardPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    companyPanCardPhotoDownloadUrl = uri.toString()
                    onlineRentalAgreeUri?.let { it1 -> uploadOnlineRentalAgree(it1) }
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
        }.addOnProgressListener { //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadOnlineRentalAgree(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        onlineRentalAgreeFileName = "online_rental_agree_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(onlineRentalAgreeFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    onlineRentalAgreeDownloadUrl = uri.toString()
                    lightBillUri?.let { it1 -> uploadLightBill(it1) }
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
        }.addOnProgressListener { //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadLightBill(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        lightBillFileName = "light_bill_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(lightBillFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    lightBillDownloadUrl = uri.toString()
//                    if (partnershipAgreeUri != null) {
//                        partnershipAgreeUri?.let { it1 -> uploadPartnershipAgree(it1) }
//                    } else {
                    udyamAadharUri?.let { it1 -> uploadUdyamAadhar(it1) }
//                    }
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
        }.addOnProgressListener { //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

//    private fun uploadPartnershipAgree(fileUri: Uri) {
//        if (cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        partnershipAgreeFileName = "partnership_agree_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(partnershipAgreeFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    partnershipAgreeDownloadUrl = uri.toString()
//                    udyamAadharUri?.let { it1 -> uploadUdyamAadhar(it1) }
//                }.addOnFailureListener {
//                    if (cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener { //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }

    private fun uploadUdyamAadhar(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        udyamAadharFileName = "udyam_aadhar_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(udyamAadharFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    udyamAadharDownloadUrl = uri.toString()
                    if (strType.equals(TYPE_PARTNERSHIP) && partnershipDeedUri != null) {
                        partnershipDeedUri?.let { it1 -> uploadPartnershipDeed(it1) }
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

    private fun uploadPartnershipDeed(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        partnershipDeedFileName = "partnership_deed_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(partnershipDeedFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    partnershipDeedDownloadUrl = uri.toString()
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

    private fun uploadPaymentScreenshot(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(paymentScreenshotFileName)
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
        billMap["businessName"] = binding.etBusinessName.text.toString()
        billMap["applicantName"] = binding.etApplicantName.text.toString()
        billMap["customerMobileNo"] = binding.etMobileNo.text.toString()
        billMap["customerEmail"] = binding.etEmail.text.toString()
        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
        billMap["companyPanCardPhotoFileName"] = companyPanCardPhotoFileName
        billMap["companyPanCardPhotoDownloadUrl"] = companyPanCardPhotoDownloadUrl
        billMap["onlineRentalAgreeFileName"] = onlineRentalAgreeFileName
        billMap["onlineRentalAgreeDownloadUrl"] = onlineRentalAgreeDownloadUrl
        billMap["lightBillFileName"] = lightBillFileName
        billMap["lightBillDownloadUrl"] = lightBillDownloadUrl
//        billMap["partnershipAgreeFileName"] = partnershipAgreeFileName
//        billMap["partnershipAgreeDownloadUrl"] = partnershipAgreeDownloadUrl
        billMap["udyamAadharFileName"] = udyamAadharFileName
        billMap["udyamAadharDownloadUrl"] = udyamAadharDownloadUrl
        billMap["partnershipDeedFileName"] = partnershipDeedFileName
        billMap["partnershipDeedDownloadUrl"] = partnershipDeedDownloadUrl
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child(PATH_NAME).push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/$PATH_NAME/$pushKey"] = billMap
        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New GST Registration added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_${PATH_NAME}")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New GST Registration added by $name")
        }
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/$PATH_NAME/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd?.isShowing == true) cpd?.dismiss()
//        toast("Request submitted successfully")
        Utils.showDialog(mContext, "तुमची सुविधा विनंती जतन करण्यात आली आहे. रीसिट \"पाठविलेल्या सुविधा\" फोल्डर बटन मध्ये तपासा.", false) { dialog, which ->
            run {
                dialog.dismiss()
                finish()
            }
        }
    }

    fun getTodayDate(): String = SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).format(Date())

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
    private fun choosePhotoWithPermissions(pos: Int) {
        imageType = pos
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

    companion object {
        var PATH_NAME = "gst_regs"
        val TYPE_PROPRIETOR = "TYPE_PROPRIETOR"
        val TYPE_PARTNERSHIP = "TYPE_PARTNERSHIP"
//        val TYPE_PROPRIETOR = "प्रोप्रायटर"
//        val TYPE_PARTNERSHIP = "पार्टनरशिप"
    }
}
