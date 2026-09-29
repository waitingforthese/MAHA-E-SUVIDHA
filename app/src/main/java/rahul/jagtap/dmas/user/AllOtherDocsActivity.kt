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
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.longToast
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityAllOtherDocsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class AllOtherDocsActivity : BaseActivity() {
    private var applicantPhotoFileName: String = ""
    private var applicantPhotoDownloadUrl: String = ""
    private var aadharFrontPhotoFileName: String = ""
    private var aadharFrontPhotoDownloadUrl: String = ""
    private var aadharBackPhotoFileName: String = ""
    private var aadharBackPhotoDownloadUrl: String = ""
    private var rationFrontPhotoFileName: String = ""
    private var rationFrontPhotoDownloadUrl: String = ""
    private var rationBackPhotoFileName: String = ""
    private var rationBackPhotoDownloadUrl: String = ""
    private var applicantSLCFileName: String = ""
    private var applicantSLCDownloadUrl: String = ""
    private var applicantCastCertXeroxFileName: String = ""
    private var applicantCastCertXeroxDownloadUrl: String = ""
    private var tehsildarIncomeCertFileName: String = ""
    private var tehsildarIncomeCertDownloadUrl: String = ""
    private var fatherSLCFileName: String = ""
    private var fatherSLCDownloadUrl: String = ""
    private var grandpaSLCFileName: String = ""
    private var grandpaSLCDownloadUrl: String = ""
    private var otherImpFileName: String = ""
    private var otherImpDownloadUrl: String = ""
    private var vanshavalFileName: String = ""
    private var vanshavalDownloadUrl: String = ""
    private var vanshavalPersonCastCertFileName: String = ""
    private var vanshavalPersonCastCertDownloadUrl: String = ""
    private var localEnquiryReportFileName: String = ""
    private var localEnquiryReportDownloadUrl: String = ""
    private var applicantSignSpecimenFileName: String = ""
    private var applicantSignSpecimenDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""

    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private val TAG = AllOtherDocsActivity::class.java.simpleName
    var imageType = -1

    var applicantPhotoUri: Uri? = null
    var aadharFrontPhotoUri: Uri? = null
    var aadharBackPhotoUri: Uri? = null
    var rationFrontPhotoUri: Uri? = null
    var rationBackPhotoUri: Uri? = null
    var applicantSLCUri: Uri? = null
    var applicantCastCertXeroxUri: Uri? = null
    var tehsildarIncomeCertUri: Uri? = null
    var fatherSLCUri: Uri? = null
    var grandpaSLCUri: Uri? = null
    var otherImpUri: Uri? = null
    var vanshavalUri: Uri? = null
    var vanshavalPersonCastCertUri: Uri? = null
    var localEnquiryReportUri: Uri? = null
    var applicantSignSpecimenUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    private var selectedTypeAmount = ""
    var cpd: ProgressDialog? = null
    private lateinit var easyImage: EasyImage
    lateinit var binding: ActivityAllOtherDocsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllOtherDocsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilServiceType, binding.tilApplicantName, binding.tilMobileNo, binding.tilApplicantPhoto, binding.tilGatNumber, binding.tilAadharFPhoto, binding.tilAadharBPhoto, binding.tilRationCardFPhoto, binding.tilRationCardBPhoto, binding.tilApplicantSchoolLC, binding.tilApplicantCastCertXerox, binding.tilTehsildarIncomeCert, binding.tilFatherSchoolLC, binding.tilGrandpaSchoolLC, binding.tilOtherImp, binding.tilVanshaval, binding.tilVanshavalPersonCastCert, binding.tilLocalEnquiryReport, binding.tilApplicantSignSpecimen, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.all_other_docs)
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
                ?: "").putExtra("title", getString(R.string.all_other_docs)))
        }

        binding.etServiceType?.setOnClickListener {
            val list = ArrayList<String>()
            list.add("शेतकरी / अल्पभुधारक दाखला")
            list.add("नॉनक्रिमीलेयर")
            list.add("जातीचा दाखला")
            list.add("EWS State/Central List")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    binding.etServiceType?.setText(list[position])
                    when (position) {
                        0 -> {
                            binding.tilGatNumber?.visible()
                            binding.tilApplicantSchoolLC?.visible()
                            binding.tilApplicantCastCertXerox?.gone()
                            binding.tilTehsildarIncomeCert?.gone()
                            binding.tilFatherSchoolLC?.gone()
                            binding.tilGrandpaSchoolLC?.gone()
                            binding.tvCastDocInstructions?.gone()
                            binding.tilOtherImp?.visible()
                            binding.tilVanshaval?.gone()
                            binding.tilVanshavalPersonCastCert?.gone()
                            binding.tilLocalEnquiryReport?.gone()
                        }

                        1 -> {
                            binding.tilGatNumber?.gone()
                            binding.tilApplicantSchoolLC?.visible()
                            binding.tilApplicantCastCertXerox?.visible()
                            binding.tilTehsildarIncomeCert?.visible()
                            binding.tilFatherSchoolLC?.gone()
                            binding.tilGrandpaSchoolLC?.gone()
                            binding.tvCastDocInstructions?.gone()
                            binding.tilOtherImp?.visible()
                            binding.tilVanshaval?.gone()
                            binding.tilVanshavalPersonCastCert?.gone()
                            binding.tilLocalEnquiryReport?.gone()
                        }

                        2 -> {
                            binding.tilGatNumber?.gone()
                            binding.tilApplicantSchoolLC?.visible()
                            binding.tilApplicantCastCertXerox?.gone()
                            binding.tilTehsildarIncomeCert?.gone()
                            binding.tilFatherSchoolLC?.gone()
                            binding.tilGrandpaSchoolLC?.gone()
                            binding.tvCastDocInstructions?.visible()
                            binding.tilOtherImp?.gone()
                            binding.tilVanshaval?.visible()
                            binding.tilVanshavalPersonCastCert?.visible()
                            binding.tilLocalEnquiryReport?.visible()
                        }

                        3 -> {
                            binding.tilGatNumber?.gone()
                            binding.tilApplicantSchoolLC?.gone()
                            binding.tilApplicantCastCertXerox?.gone()
                            binding.tilTehsildarIncomeCert?.visible()
                            binding.tilFatherSchoolLC?.visible()
                            binding.tilGrandpaSchoolLC?.visible()
                            binding.tvCastDocInstructions?.gone()
                            binding.tilOtherImp?.visible()
                            binding.tilVanshaval?.gone()
                            binding.tilVanshavalPersonCastCert?.gone()
                            binding.tilLocalEnquiryReport?.gone()
                        }
                    }
//                    selectedTypeAmount = extractAmountFromType(strTypeToShow) ?: ""
                    binding.btnPay.visible()
                    binding.tvPayNote.visible()
                }
            }.show()
        }

        binding.btnSubmit?.setOnClickListener {
            val strServiceType = binding.etServiceType.text.toString()
            val strFullName = binding.etApplicantName.text.toString()
            val strMobileNo = binding.etMobileNo.text.toString()
            val strGatNumber = binding.etGatNumber.text.toString()
            if (TextUtils.isEmpty(strServiceType)) {
                binding.tilServiceType?.error = binding.tilServiceType?.hint.toString()
                binding.etServiceType?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strFullName)) {
                binding.tilApplicantName?.error = binding.tilApplicantName.hint.toString()
                binding.etApplicantName?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strMobileNo)) {
                binding.tilMobileNo?.error = binding.tilMobileNo.hint.toString()
                binding.etMobileNo?.requestFocus()
                return@setOnClickListener
            }
            if (applicantPhotoUri == null) {
                binding.tilApplicantPhoto?.error = binding.tilApplicantPhoto.hint.toString()
                binding.etApplicantPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (strServiceType == "शेतकरी / अल्पभुधारक दाखला" && TextUtils.isEmpty(strGatNumber)) {
                binding.tilGatNumber?.error = binding.tilGatNumber.hint.toString()
                binding.etGatNumber?.requestFocus()
                return@setOnClickListener
            }
            if (aadharFrontPhotoUri == null) {
                binding.tilAadharFPhoto?.error = binding.tilAadharFPhoto.hint.toString()
                binding.etAadharFPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (aadharBackPhotoUri == null) {
                binding.tilAadharBPhoto?.error = binding.tilAadharBPhoto.hint.toString()
                binding.etAadharBPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (rationFrontPhotoUri == null) {
                binding.tilRationCardFPhoto?.error = binding.tilRationCardFPhoto.hint.toString()
                binding.etRationCardFPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (rationBackPhotoUri == null) {
                binding.tilRationCardBPhoto?.error = binding.tilRationCardBPhoto.hint.toString()
                binding.etRationCardBPhoto?.requestFocus()
                return@setOnClickListener
            } //  "शेतकरी / अल्पभुधारक दाखला"
            //  "नॉनक्रिमीलेयर"
            //            "जातीचा दाखला"
            //            "EWS State/Central List"
            if ((strServiceType == "शेतकरी / अल्पभुधारक दाखला" || strServiceType == "नॉनक्रिमीलेयर" || strServiceType == "जातीचा दाखला") && applicantSLCUri == null) {
                binding.tilApplicantSchoolLC?.error = binding.tilApplicantSchoolLC.hint.toString()
                binding.etApplicantSchoolLC?.requestFocus()
                return@setOnClickListener
            }
            if ((strServiceType == "नॉनक्रिमीलेयर") && applicantCastCertXeroxUri == null) {
                binding.tilApplicantCastCertXerox?.error = binding.tilApplicantCastCertXerox.hint.toString()
                binding.etApplicantCastCertXerox?.requestFocus()
                return@setOnClickListener
            }
            if ((strServiceType == "नॉनक्रिमीलेयर" || strServiceType == "EWS State/Central List") && tehsildarIncomeCertUri == null) {
                binding.tilTehsildarIncomeCert?.error = binding.tilTehsildarIncomeCert.hint.toString()
                binding.etTehsildarIncomeCert?.requestFocus()
                return@setOnClickListener
            }
            if (strServiceType == "EWS State/Central List" && fatherSLCUri == null) {
                binding.tilFatherSchoolLC?.error = binding.tilFatherSchoolLC.hint.toString()
                binding.etFatherSchoolLC?.requestFocus()
                return@setOnClickListener
            }
            if (strServiceType == "EWS State/Central List" && grandpaSLCUri == null) {
                binding.tilGrandpaSchoolLC?.error = binding.tilGrandpaSchoolLC.hint.toString()
                binding.etGrandpaSchoolLC?.requestFocus()
                return@setOnClickListener
            }
            if ((strServiceType == "शेतकरी / अल्पभुधारक दाखला" || strServiceType == "नॉनक्रिमीलेयर" || strServiceType == "EWS State/Central List") && otherImpUri == null) {
                binding.tilOtherImp?.error = binding.tilOtherImp.hint.toString()
                binding.etOtherImp?.requestFocus()
                return@setOnClickListener
            }
            if (strServiceType == "जातीचा दाखला" && vanshavalUri == null) {
                binding.tilVanshaval?.error = binding.tilVanshaval.hint.toString()
                binding.etVanshaval?.requestFocus()
                return@setOnClickListener
            }
            if (strServiceType == "जातीचा दाखला" && vanshavalPersonCastCertUri == null) {
                binding.tilVanshavalPersonCastCert?.error = binding.tilVanshavalPersonCastCert.hint.toString()
                binding.etVanshavalPersonCastCert?.requestFocus()
                return@setOnClickListener
            }
            if (strServiceType == "जातीचा दाखला" && localEnquiryReportUri == null) {
                binding.tilLocalEnquiryReport?.error = binding.tilLocalEnquiryReport.hint.toString()
                binding.etLocalEnquiryReport?.requestFocus()
                return@setOnClickListener
            }
            if (applicantSignSpecimenUri == null) {
                binding.tilApplicantSignSpecimen?.error = binding.tilApplicantSignSpecimen.hint.toString()
                binding.etApplicantSignSpecimen?.requestFocus()
                return@setOnClickListener
            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = binding.tilPaymentScreenshot.hint.toString()
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            }
            uploadApplicantPhoto(applicantPhotoUri!!)
        }
        binding.etApplicantPhoto?.setOnClickListener { choosePhotoWithPermissions(1) }
        binding.etAadharFPhoto?.setOnClickListener { choosePhotoWithPermissions(2) }
        binding.etAadharBPhoto?.setOnClickListener { choosePhotoWithPermissions(3) }
        binding.etRationCardFPhoto?.setOnClickListener { choosePhotoWithPermissions(4) }
        binding.etRationCardBPhoto?.setOnClickListener { choosePhotoWithPermissions(5) }
        binding.etApplicantSchoolLC?.setOnClickListener { choosePhotoWithPermissions(6) }
        binding.etApplicantCastCertXerox?.setOnClickListener { choosePhotoWithPermissions(7) }
        binding.etTehsildarIncomeCert?.setOnClickListener { choosePhotoWithPermissions(8) }
        binding.etFatherSchoolLC?.setOnClickListener { choosePhotoWithPermissions(9) }
        binding.etGrandpaSchoolLC?.setOnClickListener { choosePhotoWithPermissions(10) }
        binding.etOtherImp?.setOnClickListener { choosePhotoWithPermissions(11) }
        binding.etVanshaval?.setOnClickListener { choosePhotoWithPermissions(12) }
        binding.etVanshavalPersonCastCert?.setOnClickListener { choosePhotoWithPermissions(13) }
        binding.etLocalEnquiryReport?.setOnClickListener { choosePhotoWithPermissions(14) }
        binding.etApplicantSignSpecimen?.setOnClickListener { choosePhotoWithPermissions(15) }
        binding.etPaymentScreenshot?.setOnClickListener { choosePhotoWithPermissions(16) }
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
                                applicantPhotoUri = Uri.fromFile(it)
                                binding.etApplicantPhoto?.setText("Success")//applicantPhotoUri.toString())
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
                                applicantSLCUri = Uri.fromFile(it)
                                binding.etApplicantSchoolLC?.setText("Success")//applicantSLCUri.toString())
                            }

                            7 -> {
                                applicantCastCertXeroxUri = Uri.fromFile(it)
                                binding.etApplicantCastCertXerox?.setText("Success")//applicantCastCertXeroxUri.toString())
                            }

                            8 -> {
                                tehsildarIncomeCertUri = Uri.fromFile(it)
                                binding.etTehsildarIncomeCert?.setText("Success")//tehsildarIncomeCertUri.toString())
                            }

                            9 -> {
                                fatherSLCUri = Uri.fromFile(it)
                                binding.etFatherSchoolLC?.setText("Success")//fatherSLCUri.toString())
                            }

                            10 -> {
                                grandpaSLCUri = Uri.fromFile(it)
                                binding.etGrandpaSchoolLC?.setText("Success")//grandpaSLCUri.toString())
                            }

                            11 -> {
                                otherImpUri = Uri.fromFile(it)
                                binding.etOtherImp?.setText("Success")//otherImpUri.toString())
                            }

                            12 -> {
                                vanshavalUri = Uri.fromFile(it)
                                binding.etVanshaval?.setText("Success")//vanshavalUri.toString())
                            }

                            13 -> {
                                vanshavalPersonCastCertUri = Uri.fromFile(it)
                                binding.etVanshavalPersonCastCert?.setText("Success")//vanshavalPersonCastCertUri.toString())
                            }

                            14 -> {
                                localEnquiryReportUri = Uri.fromFile(it)
                                binding.etLocalEnquiryReport?.setText("Success")//localEnquiryReportUri.toString())
                            }

                            15 -> {
                                applicantSignSpecimenUri = Uri.fromFile(it)
                                binding.etApplicantSignSpecimen?.setText("Success")//applicantSignSpecimenUri.toString())
                            }

                            16 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success")//paymentScreenshotUri.toString())
                            }
                        }
                    }
                }
            }
        })
    }

    private fun uploadApplicantPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        applicantPhotoFileName = "applicant_photo_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(applicantPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    applicantPhotoDownloadUrl = uri.toString()
                    aadharFrontPhotoUri?.let { it1 -> uploadAadharFPhoto(it1) }
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

    private fun uploadAadharFPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(aadharFrontPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
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
                    rationFrontPhotoUri?.let { it1 -> uploadRationFPhoto(it1) }
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

    private fun uploadRationFPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        rationFrontPhotoFileName = "ration_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(rationFrontPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    rationFrontPhotoDownloadUrl = uri.toString()
                    rationBackPhotoUri?.let { it1 -> uploadRationBPhoto(it1) }
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

    private fun uploadRationBPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        rationBackPhotoFileName = "ration_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(rationBackPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    rationBackPhotoDownloadUrl = uri.toString()
                    if (applicantSLCUri != null) applicantSLCUri?.let { it1 -> uploadApplicantSLC(it1) }
                    else if (applicantCastCertXeroxUri != null) applicantCastCertXeroxUri?.let { it1 -> uploadApplicantCastCertXerox(it1) }
                    else if (tehsildarIncomeCertUri != null) tehsildarIncomeCertUri?.let { it1 -> uploadTehsildarIncomeCert(it1) }
                    else if (fatherSLCUri != null) fatherSLCUri?.let { it1 -> uploadFatherSLC(it1) }
                    else if (grandpaSLCUri != null) grandpaSLCUri?.let { it1 -> uploadGrandpaSLC(it1) }
                    else if (otherImpUri != null) otherImpUri?.let { it1 -> uploadOtherImp(it1) }
                    else paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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

    private fun uploadApplicantSLC(fileUri: Uri) {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        applicantSLCFileName = "applicant_slc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(applicantSLCFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    applicantSLCDownloadUrl = uri.toString()
                    if (applicantCastCertXeroxUri != null) applicantCastCertXeroxUri?.let { it1 -> uploadApplicantCastCertXerox(it1) }
                    else if (tehsildarIncomeCertUri != null) tehsildarIncomeCertUri?.let { it1 -> uploadTehsildarIncomeCert(it1) }
                    else if (fatherSLCUri != null) fatherSLCUri?.let { it1 -> uploadFatherSLC(it1) }
                    else if (grandpaSLCUri != null) grandpaSLCUri?.let { it1 -> uploadGrandpaSLC(it1) }
                    else if (otherImpUri != null) otherImpUri?.let { it1 -> uploadOtherImp(it1) }
                    else if (vanshavalUri != null) vanshavalUri?.let { it1 -> uploadVanshaval(it1) }
                    else if (vanshavalPersonCastCertUri != null) vanshavalPersonCastCertUri?.let { it1 -> uploadVanshavalPersonCastCert(it1) }
                    else if (localEnquiryReportUri != null) localEnquiryReportUri?.let { it1 -> uploadLocalEnquiryReport(it1) }
                    else applicantSignSpecimenUri?.let { it1 -> uploadApplicantSignSpecimen(it1) }
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            } catch (e: java.lang.Exception) {
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

    private fun uploadApplicantCastCertXerox(fileUri: Uri) {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        applicantCastCertXeroxFileName = "applicant_cast_cert_xerox_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(applicantCastCertXeroxFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    applicantCastCertXeroxDownloadUrl = uri.toString()
                    if (tehsildarIncomeCertUri != null) tehsildarIncomeCertUri?.let { it1 -> uploadTehsildarIncomeCert(it1) }
                    else if (fatherSLCUri != null) fatherSLCUri?.let { it1 -> uploadFatherSLC(it1) }
                    else if (grandpaSLCUri != null) grandpaSLCUri?.let { it1 -> uploadGrandpaSLC(it1) }
                    else if (otherImpUri != null) otherImpUri?.let { it1 -> uploadOtherImp(it1) }
                    else if (vanshavalUri != null) vanshavalUri?.let { it1 -> uploadVanshaval(it1) }
                    else if (vanshavalPersonCastCertUri != null) vanshavalPersonCastCertUri?.let { it1 -> uploadVanshavalPersonCastCert(it1) }
                    else if (localEnquiryReportUri != null) localEnquiryReportUri?.let { it1 -> uploadLocalEnquiryReport(it1) }
                    else applicantSignSpecimenUri?.let { it1 -> uploadApplicantSignSpecimen(it1) }
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            } catch (e: java.lang.Exception) {
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

    private fun uploadTehsildarIncomeCert(fileUri: Uri) {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        tehsildarIncomeCertFileName = "tehsildar_income_cert_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(tehsildarIncomeCertFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    tehsildarIncomeCertDownloadUrl = uri.toString()
                    if (fatherSLCUri != null) fatherSLCUri?.let { it1 -> uploadFatherSLC(it1) }
                    else if (grandpaSLCUri != null) grandpaSLCUri?.let { it1 -> uploadGrandpaSLC(it1) }
                    else if (otherImpUri != null) otherImpUri?.let { it1 -> uploadOtherImp(it1) }
                    else if (vanshavalUri != null) vanshavalUri?.let { it1 -> uploadVanshaval(it1) }
                    else if (vanshavalPersonCastCertUri != null) vanshavalPersonCastCertUri?.let { it1 -> uploadVanshavalPersonCastCert(it1) }
                    else if (localEnquiryReportUri != null) localEnquiryReportUri?.let { it1 -> uploadLocalEnquiryReport(it1) }
                    else applicantSignSpecimenUri?.let { it1 -> uploadApplicantSignSpecimen(it1) }
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            } catch (e: java.lang.Exception) {
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

    private fun uploadFatherSLC(fileUri: Uri) {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        fatherSLCFileName = "father_slc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(fatherSLCFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    fatherSLCDownloadUrl = uri.toString()
                    if (grandpaSLCUri != null) grandpaSLCUri?.let { it1 -> uploadGrandpaSLC(it1) }
                    else if (otherImpUri != null) otherImpUri?.let { it1 -> uploadOtherImp(it1) }
                    else if (vanshavalUri != null) vanshavalUri?.let { it1 -> uploadVanshaval(it1) }
                    else if (vanshavalPersonCastCertUri != null) vanshavalPersonCastCertUri?.let { it1 -> uploadVanshavalPersonCastCert(it1) }
                    else if (localEnquiryReportUri != null) localEnquiryReportUri?.let { it1 -> uploadLocalEnquiryReport(it1) }
                    else applicantSignSpecimenUri?.let { it1 -> uploadApplicantSignSpecimen(it1) }
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            } catch (e: java.lang.Exception) {
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

    private fun uploadGrandpaSLC(fileUri: Uri) {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        grandpaSLCFileName = "grandpa_slc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(grandpaSLCFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    grandpaSLCDownloadUrl = uri.toString()
                    if (otherImpUri != null) otherImpUri?.let { it1 -> uploadOtherImp(it1) }
                    else if (vanshavalUri != null) vanshavalUri?.let { it1 -> uploadVanshaval(it1) }
                    else if (vanshavalPersonCastCertUri != null) vanshavalPersonCastCertUri?.let { it1 -> uploadVanshavalPersonCastCert(it1) }
                    else if (localEnquiryReportUri != null) localEnquiryReportUri?.let { it1 -> uploadLocalEnquiryReport(it1) }
                    else applicantSignSpecimenUri?.let { it1 -> uploadApplicantSignSpecimen(it1) }
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            } catch (e: java.lang.Exception) {
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

    private fun uploadOtherImp(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        otherImpFileName = "other_imp_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(otherImpFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    otherImpDownloadUrl = uri.toString()
                    if (vanshavalUri != null) vanshavalUri?.let { it1 -> uploadVanshaval(it1) }
                    else if (vanshavalPersonCastCertUri != null) vanshavalPersonCastCertUri?.let { it1 -> uploadVanshavalPersonCastCert(it1) }
                    else if (localEnquiryReportUri != null) localEnquiryReportUri?.let { it1 -> uploadLocalEnquiryReport(it1) }
                    else applicantSignSpecimenUri?.let { it1 -> uploadApplicantSignSpecimen(it1) }
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

    private fun uploadVanshaval(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        vanshavalFileName = "vanshaval_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(vanshavalFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    vanshavalDownloadUrl = uri.toString()
                    if (vanshavalPersonCastCertUri != null) vanshavalPersonCastCertUri?.let { it1 -> uploadVanshavalPersonCastCert(it1) }
                    else if (localEnquiryReportUri != null) localEnquiryReportUri?.let { it1 -> uploadLocalEnquiryReport(it1) }
                    else applicantSignSpecimenUri?.let { it1 -> uploadApplicantSignSpecimen(it1) }
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

    private fun uploadVanshavalPersonCastCert(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        vanshavalPersonCastCertFileName = "vanshaval_person_cast_cert_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(vanshavalPersonCastCertFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    vanshavalPersonCastCertDownloadUrl = uri.toString()
                    if (localEnquiryReportUri != null) localEnquiryReportUri?.let { it1 -> uploadLocalEnquiryReport(it1) }
                    else applicantSignSpecimenUri?.let { it1 -> uploadApplicantSignSpecimen(it1) }
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

    private fun uploadLocalEnquiryReport(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        localEnquiryReportFileName = "local_enquiry_report_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(localEnquiryReportFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    localEnquiryReportDownloadUrl = uri.toString()
                    applicantSignSpecimenUri?.let { it1 -> uploadApplicantSignSpecimen(it1) }
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

    private fun uploadApplicantSignSpecimen(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        applicantSignSpecimenFileName = "applicant_sign_specimen_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(applicantSignSpecimenFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    applicantSignSpecimenDownloadUrl = uri.toString()
                    paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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

    private fun uploadPaymentScreenshot(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
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

    private fun createDbRecord() {
        val createdDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        val billMap = HashMap<String, Any?>()
        billMap["email"] = email
        billMap["createdBy"] = name
        billMap["createdByUserName"] = username
        billMap["uid"] = uid
        billMap["createdDateTime"] = createdDateTime
        billMap["serviceType"] = binding.etServiceType.text.toString()
        billMap["applicantName"] = binding.etApplicantName.text.toString()
        billMap["mobileNo"] = binding.etMobileNo.text.toString()
        billMap["applicantPhotoFileName"] = applicantPhotoFileName
        billMap["applicantPhotoDownloadUrl"] = applicantPhotoDownloadUrl
        billMap["gatNumber"] = binding.etGatNumber.text.toString()
        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
        billMap["rationFrontPhotoFileName"] = rationFrontPhotoFileName
        billMap["rationFrontPhotoDownloadUrl"] = rationFrontPhotoDownloadUrl
        billMap["rationBackPhotoFileName"] = rationBackPhotoFileName
        billMap["rationBackPhotoDownloadUrl"] = rationBackPhotoDownloadUrl
        billMap["applicantSLCFileName"] = applicantSLCFileName
        billMap["applicantSLCDownloadUrl"] = applicantSLCDownloadUrl
        billMap["applicantCastCertXeroxFileName"] = applicantCastCertXeroxFileName
        billMap["applicantCastCertXeroxDownloadUrl"] = applicantCastCertXeroxDownloadUrl
        billMap["tehsildarIncomeCertFileName"] = tehsildarIncomeCertFileName
        billMap["tehsildarIncomeCertDownloadUrl"] = tehsildarIncomeCertDownloadUrl
        billMap["fatherSLCFileName"] = fatherSLCFileName
        billMap["fatherSLCDownloadUrl"] = fatherSLCDownloadUrl
        billMap["grandpaSLCFileName"] = grandpaSLCFileName
        billMap["grandpaSLCDownloadUrl"] = grandpaSLCDownloadUrl
        billMap["otherImpFileName"] = otherImpFileName
        billMap["otherImpDownloadUrl"] = otherImpDownloadUrl
        billMap["vanshavalFileName"] = vanshavalFileName
        billMap["vanshavalDownloadUrl"] = vanshavalDownloadUrl
        billMap["vanshavalPersonCastCertFileName"] = vanshavalPersonCastCertFileName
        billMap["vanshavalPersonCastCertDownloadUrl"] = vanshavalPersonCastCertDownloadUrl
        billMap["localEnquiryReportFileName"] = localEnquiryReportFileName
        billMap["localEnquiryReportDownloadUrl"] = localEnquiryReportDownloadUrl
        billMap["applicantSignSpecimenFileName"] = applicantSignSpecimenFileName
        billMap["applicantSignSpecimenDownloadUrl"] = applicantSignSpecimenDownloadUrl
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
            val notificationItem = NotificationItem(message = "New Other Card added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_$PATH_NAME")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Other Card added by $name")
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
////                    EasyImage.openChooserWithGallery(this, "", AccountingServicesActivity.REQUEST_AVATAR_CODE)
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
        var PATH_NAME = "all_other_docs"
    }
}
