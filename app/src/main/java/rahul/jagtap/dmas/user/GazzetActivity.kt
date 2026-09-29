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
import rahul.jagtap.dmas.databinding.ActivityGazzetBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class GazzetActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
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
    private var signPhotoFileName: String = ""
    private var signPhotoDownloadUrl: String = ""
    private var stampHundredPhotoFileName: String = ""
    private var stampHundredPhotoDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private val TAG = GazzetActivity::class.java.simpleName
    var imageType = -1 // 1 - passport, 2 - aadhar f, 3 - aadhar b, 4 - ration f, 5 - ratio b, 6 - bonafide,
    // 7 -  sign, 8 - payment screenshot
    var passportPhotoUri: Uri? = null
    var aadharFrontPhotoUri: Uri? = null
    var aadharBackPhotoUri: Uri? = null
    var rationFrontPhotoUri: Uri? = null
    var rationBackPhotoUri: Uri? = null
    var bonafideUri: Uri? = null
    var signPhotoUri: Uri? = null
    var stampHundredPhotoUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    var cpd: ProgressDialog? = null
    private var strDate: String? = ""
    private lateinit var easyImage: EasyImage
    private var strCategoryToShow: String? = ""
    private var strCategory: String? = ""
    lateinit var binding: ActivityGazzetBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGazzetBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilCategory, binding.tilFullName, binding.tilFatherName, binding.tilMobileNo, binding.tilEmail, binding.tilAddress, binding.tilCertificatePurpose, binding.tilPassportPhoto, binding.tilSignPhoto, binding.tilAadharFPhoto, binding.tilAadharBPhoto, binding.tilRationCardFPhoto, binding.tilRationCardBPhoto, binding.tilBonafideLcPhoto, binding.tilStampHundred, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "गँझेट / राजपत्र - नावात बदल"
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
                ?: "").putExtra("title", "गँझेट"))
        }
        typeMap = intent.extras?.getSerializable("hashMap") as HashMap<String, HashMap<String, String>>
        val list = ArrayList<String>()
        typeMap?.forEach {
            if (it.value["type_title"] != "0") list.add(it.value["type_title"].toString())
        }
        if (list.size == 0) {
            binding.tilCategory.gone()
            binding.tilFatherName.gone()
            binding.tilMobileNo.gone()
            binding.tilEmail.gone()
            binding.tilAddress.gone()
            binding.tilCertificatePurpose.gone()
            binding.tilPassportPhoto.gone()
            binding.tilSignPhoto.gone()
            binding.tilAadharFPhoto.gone()
            binding.tilAadharBPhoto.gone()
            binding.tilRationCardFPhoto.gone()
            binding.tilRationCardBPhoto.gone()
            binding.tilBonafideLcPhoto.gone()
            binding.tilStampHundred.gone()
            binding.tilPaymentScreenshot.gone()
            binding.btnSubmit.gone()
        }
        /** Selects the subtype at [position] (shared by the picker dialog and grid deep-links). */
        fun applyType(position: Int) {

                    binding.etCategory?.setText(list[position])
                    strCategoryToShow = list[position]
                    val keyFromValue = typeMap?.let { it1 -> Utils.getKeyFromNestedHashMap(it1, strCategoryToShow!!) }
                    when (keyFromValue) {
                        TYPE_OPEN -> strCategory = TYPE_OPEN
                        TYPE_SC -> strCategory = TYPE_SC
                        TYPE_OBC -> strCategory = TYPE_OBC
                        TYPE_MINOR -> strCategory = TYPE_MINOR
                    }
                    selectedTypeAmount = extractAmountFromType(strCategoryToShow!!) ?: ""
                    binding.btnPay.visible()
                    binding.tvPayNote.visible()
                
        }
        val preselectKey = intent.getStringExtra(EsuvidhaServiceRegistry.EXTRA_PRESELECT_SUBTYPE)
        if (!preselectKey.isNullOrEmpty()) {
            val preTitle = typeMap?.get(preselectKey)?.get("type_title")
            val preIdx = if (!preTitle.isNullOrEmpty()) list.indexOf(preTitle) else -1
            if (preIdx >= 0) applyType(preIdx)
        }
        binding.etCategory?.setOnClickListener {
//            val list = ArrayList<String>()
//            list.add("ओपन केटेगरी")
//            list.add("बँकवर्ड केटेगरी")
//            list.add("ओ बी सी केटेगरी")
//            list.add("अल्पवयीन अर्जदार")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    applyType(position)
                }
            }.show()
        }

        binding.btnSubmit?.setOnClickListener {
            val strFullName = binding.etFullName.text.toString()
            val strFatherName = binding.etFatherName.text.toString()
            val strMobileNo = binding.etMobileNo.text.toString()
            val strEmail = binding.etEmail.text.toString()
            val strAddress = binding.etAddress.text.toString()
            val strCertificatePurpose = binding.etCertificatePurpose.text.toString()
            val strCategory = binding.etCategory.text.toString()
            if (TextUtils.isEmpty(strCategory)) {
                binding.tilCategory?.error = binding.tilCategory.hint.toString()
                binding.etCategory?.requestFocus()
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
            if (passportPhotoUri == null) {
                binding.tilPassportPhoto?.error = "पासपोर्ट साईझ फोटो चा फोटो"
                binding.etPassportPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (signPhotoUri == null) {
                binding.tilSignPhoto?.error = "सही चा नमुना कोऱ्या कागदावर"
                binding.etSignPhoto?.requestFocus()
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
                binding.tilBonafideLcPhoto?.error = "ओरिजिनल बोनाफाईड किव्हा LC किव्हा SSC/HSC प्रमाणपत्र"
                binding.etBonafideLcPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (stampHundredPhotoUri == null) {
                binding.tilStampHundred?.error = binding.tilStampHundred.hint.toString()
                binding.etStampHundred?.requestFocus()
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
        binding.etSignPhoto?.setOnClickListener {
            imageType = 7
            choosePhotoWithPermissions()
        }
        binding.etPaymentScreenshot?.setOnClickListener {
            imageType = 8
            choosePhotoWithPermissions()
        }
        binding.etStampHundred?.setOnClickListener {
            imageType = 9
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
                                binding.etPassportPhoto?.setText("Success") //passportPhotoUri.toString())
                            }
                            2 -> {
                                aadharFrontPhotoUri = Uri.fromFile(it)
                                binding.etAadharFPhoto?.setText("Success") //aadharFrontPhotoUri.toString())
                            }
                            3 -> {
                                aadharBackPhotoUri = Uri.fromFile(it)
                                binding.etAadharBPhoto?.setText("Success") //aadharBackPhotoUri.toString())
                            }
                            4 -> {
                                rationFrontPhotoUri = Uri.fromFile(it)
                                binding.etRationCardFPhoto?.setText("Success") //rationFrontPhotoUri.toString())
                            }
                            5 -> {
                                rationBackPhotoUri = Uri.fromFile(it)
                                binding.etRationCardBPhoto?.setText("Success") //rationBackPhotoUri.toString())
                            }
                            6 -> {
                                bonafideUri = Uri.fromFile(it)
                                binding.etBonafideLcPhoto?.setText("Success") //bonafideUri.toString())
                            }
                            7 -> {
                                signPhotoUri = Uri.fromFile(it)
                                binding.etSignPhoto?.setText("Success") //signPhotoUri.toString())
                            }
                            8 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success") //paymentScreenshotUri.toString())
                            }
                            9 -> {
                                stampHundredPhotoUri = Uri.fromFile(it)
                                binding.etStampHundred?.setText("Success") //paymentScreenshotUri.toString())
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("gazzets").child(passportPhotoFileName)
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
        }.addOnProgressListener { //displaying the upload progress
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("gazzets").child(aadharFrontPhotoFileName)
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("gazzets").child(aadharBackPhotoFileName)
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
        }.addOnProgressListener { //displaying the upload progress
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("gazzets").child(rationFrontPhotoFileName)
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
        }.addOnProgressListener { //displaying the upload progress
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("gazzets").child(rationBackPhotoFileName)
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
        }.addOnProgressListener { //displaying the upload progress
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("gazzets").child(bonafideFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    bonafideDownloadUrl = uri.toString()
                    signPhotoUri?.let { it1 -> uploadSignPhoto(it1) }
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

    private fun uploadSignPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        signPhotoFileName = "sign_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("gazzets").child(signPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    signPhotoDownloadUrl = uri.toString()
                    stampHundredPhotoUri?.let { it1 -> uploadStampHundredPhoto(it1) }
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

    private fun uploadStampHundredPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        stampHundredPhotoFileName = "stamp_hundred_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("gazzets").child(stampHundredPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    stampHundredPhotoDownloadUrl = uri.toString()
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("gazzets").child(paymentScreenshotFileName)
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
        billMap["fullName"] = binding.etFullName.text.toString()
        billMap["fatherName"] = binding.etFatherName.text.toString()
        billMap["mobileNo"] = binding.etMobileNo.text.toString()
        billMap["userEmail"] = binding.etEmail.text.toString()
        billMap["address"] = binding.etAddress.text.toString()
        billMap["certificatePurpose"] = binding.etCertificatePurpose.text.toString()
        billMap["category"] = strCategory
        billMap["categoryToShow"] = strCategoryToShow
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
        billMap["signPhotoFileName"] = signPhotoFileName
        billMap["signPhotoDownloadUrl"] = signPhotoDownloadUrl
        billMap["stampHundredPhotoFileName"] = stampHundredPhotoFileName
        billMap["stampHundredPhotoDownloadUrl"] = stampHundredPhotoDownloadUrl
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("gazzets").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/gazzets/$pushKey"] = billMap
        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Gazzet added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_gazzet")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Gazzet added by $name")
        }
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/gazzets/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd?.isShowing == true) cpd?.dismiss() //        toast("Gazzet created successfully")
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

    companion object {
        val TYPE_OPEN = "TYPE_OPEN"
        val TYPE_SC = "TYPE_SC"
        val TYPE_OBC = "TYPE_OBC"
        val TYPE_MINOR = "TYPE_MINOR"
//        val TYPE_OPEN = "ओपन केटेगरी"
//        val TYPE_SC = "बँकवर्ड केटेगरी"
//        val TYPE_OBC = "ओ बी सी केटेगरी"
//        val TYPE_MINOR = "अल्पवयीन अर्जदार"
    }
}
