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
import rahul.jagtap.dmas.databinding.ActivityBusinessPanCardBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.longToast
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import androidx.core.widget.doAfterTextChanged

class BusinessPanCardActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    private var aadharFrontPhotoFileName: String = ""
    private var aadharFrontPhotoDownloadUrl: String = ""
    private var aadharBackPhotoFileName: String = ""
    private var aadharBackPhotoDownloadUrl: String = ""
    private var gpnocPhotoFileName: String = ""
    private var gpnocPhotoDownloadUrl: String = ""
    private var stampPhotoFileName: String = ""
    private var stampPhotoDownloadUrl: String = ""

    //    private var regCertPhotoFileName: String = ""
    //    private var regCertPhotoDownloadUrl: String = ""
    private var partnershipDeedPhotoFileName: String = ""
    private var partnershipDeedPhotoDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private val TAG = BusinessPanCardActivity::class.java.simpleName
    var imageType = -1 // 1 - aadhar f, 2 - aadhar b, 3 - gpnoc, 4 - stamp, 5 - reg certificate, 6 - partnership, 7 - payment screenshot
    var aadharFrontPhotoUri: Uri? = null
    var aadharBackPhotoUri: Uri? = null
    var gpnocPhotoUri: Uri? = null
    var stampPhotoUri: Uri? = null

    //    var regCertPhotoUri: Uri? = null
    var partnershipDeedUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    var cpd: ProgressDialog? = null
    private var strDate: String? = ""
    private lateinit var easyImage: EasyImage
    private var strType: String? = ""
    private var strTypeToShow: String? = ""
    lateinit var binding: ActivityBusinessPanCardBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessPanCardBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilType, binding.tilApplicantName, binding.tilFullName, binding.tilFullAddress, binding.tilStartDate, binding.tilMobileNo, binding.tilAadharFPhoto, binding.tilAadharBPhoto, binding.tilGPNOCPhoto, binding.tilStamp, binding.tilPartnershipDid, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_business_pan_card)
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
                ?: "").putExtra("title", getString(R.string.txt_business_pan_card)))
        }
        typeMap = intent.extras?.getSerializable("hashMap") as HashMap<String, HashMap<String, String>>
        val list = ArrayList<String>()
        typeMap?.forEach {
            if (it.value["type_title"] != "0") list.add(it.value["type_title"].toString())
        }
        if (list.size == 0) {
            binding.tilType.gone()
            binding.tilFullName.gone()
            binding.tilFullAddress.gone()
            binding.tilStartDate.gone()
            binding.tilMobileNo.gone()
            binding.tilAadharFPhoto.gone()
            binding.tilAadharBPhoto.gone()
            binding.tilGPNOCPhoto.gone()
            binding.tilStamp.gone()
            binding.tilPartnershipDid.gone()
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
                            binding.tilPartnershipDid.gone()
                        }

                        TYPE_PARTNERSHIP -> {
                            strType = TYPE_PARTNERSHIP
                            binding.tilPartnershipDid.visible()
                        }

                        TYPE_TYPE_THREE -> {
                            strType = TYPE_TYPE_THREE
                            binding.tilPartnershipDid.gone()
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
//            list.add("1")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    applyType(position)
                }
            }.show()
        }
        binding.btnSubmit?.setOnClickListener {
            val strApplicantName = binding.etApplicantName.text.toString()
            val strFullName = binding.etFullName.text.toString()
            val strFullAddress = binding.etFullAddress.text.toString()
            val strStartDate = binding.etStartDate.text.toString()
            val strMobileNo = binding.etMobileNo.text.toString()
            if (TextUtils.isEmpty(strType)) {
                binding.tilType?.error = binding.tilType.hint.toString()
                binding.etType?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strApplicantName)) {
                binding.tilApplicantName?.error = "व्यवसायाचे नाव टाका"
                binding.etApplicantName?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilFullName.visibility == View.VISIBLE && TextUtils.isEmpty(strFullName)) {
                binding.tilFullName?.error = "व्यवसाय / बचत गट / संस्था - संपूर्ण नाव"
                binding.etFullName?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilFullAddress.visibility == View.VISIBLE && TextUtils.isEmpty(strFullAddress)) {
                binding.tilFullAddress?.error = "व्यवसाय / बचत गट / संस्था - संपूर्ण पत्ता"
                binding.etFullAddress?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strStartDate)) {
                binding.tilStartDate?.error = "व्यवसाय / बचत गट / संस्था - चालू केल्याची तारीख"
                binding.etStartDate?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strMobileNo)) {
                binding.tilMobileNo?.error = "अध्यक्ष किव्हा चेअरमन किव्हा सचिव - एकाचा मोबाईल नंबर"
                binding.etMobileNo?.requestFocus()
                return@setOnClickListener
            }
            if (aadharFrontPhotoUri == null) {
                binding.tilAadharFPhoto?.error = binding.tilAadharFPhoto?.hint.toString()
                binding.etAadharFPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (aadharBackPhotoUri == null) {
                binding.tilAadharBPhoto?.error = binding.tilAadharBPhoto?.hint.toString()
                binding.etAadharBPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (gpnocPhotoUri == null) {
                binding.tilGPNOCPhoto?.error = binding.tilGPNOCPhoto?.hint.toString()
                binding.etGPNOCPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (stampPhotoUri == null) {
                binding.tilStamp?.error = binding.tilStamp?.hint.toString()
                binding.etStamp?.requestFocus()
                return@setOnClickListener
            } //            if (regCertPhotoUri == null) {
            //                etRegCertificate?.error = etRegCertificate?.hint.toString()
            //                etRegCertificate?.requestFocus()
            //                return@setOnClickListener
            //            }
            //            if (partnershipDeedUri == null) {
            //                etPartnershipDid?.error = etPartnershipDid?.hint.toString()
            //                etPartnershipDid?.requestFocus()
            //                return@setOnClickListener
            //            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = binding.tilPaymentScreenshot?.hint.toString()
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            }
            uploadAadharFPhoto()
        }
        binding.btnPayByUpiId?.setOnClickListener {
            startActivity(Intent(mContext, PayByUpiActivity::class.java))
        }
        binding.etStartDate?.setOnClickListener {
            val mMaxDate = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(mContext!!, { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val date = "" + dayOfMonth + "/" + (month + 1) + "/" + year
                strDate = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
                binding.etStartDate.setText(date)
            }, mMaxDate[Calendar.YEAR], mMaxDate[Calendar.MONTH], mMaxDate[Calendar.DAY_OF_MONTH])
            datePickerDialog.show()
        } //        etType?.setOnClickListener {
        //            val list = ArrayList<String>()
        //            list.add("नवीन")
        //            list.add("दुरुस्ती")
        //            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
        //                run {
        //                    dialog?.dismiss()
        //                    etType.setText(list[position])
        //                    if (list[position] == "दुरुस्ती") {
        //                        etOldPanCardPhoto?.visible()
        //                    } else {
        //                        etOldPanCardPhoto?.gone()
        //                    }
        //                }
        //            }.show()
        //        }
        binding.etAadharFPhoto?.setOnClickListener {
            imageType = 1
            choosePhotoWithPermissions()
        }
        binding.etAadharBPhoto?.setOnClickListener {
            imageType = 2
            choosePhotoWithPermissions()
        }
        binding.etGPNOCPhoto?.setOnClickListener {
            imageType = 3
            choosePhotoWithPermissions()
        }
        binding.etStamp?.setOnClickListener {
            imageType = 4
            choosePhotoWithPermissions()
        } //        etRegCertificate?.setOnClickListener {
        //            imageType = 5
        //            choosePhotoWithPermissions()
        //        }
        binding.etPartnershipDid?.setOnClickListener {
            imageType = 6
            choosePhotoWithPermissions()
        }
        binding.etPaymentScreenshot?.setOnClickListener {
            imageType = 5
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
                                aadharFrontPhotoUri = Uri.fromFile(it)
                                binding.etAadharFPhoto?.setText("Success") //aadharFrontPhotoUri.toString())
                            }

                            2 -> {
                                aadharBackPhotoUri = Uri.fromFile(it)
                                binding.etAadharBPhoto?.setText("Success") //aadharBackPhotoUri.toString())
                            }

                            3 -> {
                                gpnocPhotoUri = Uri.fromFile(it)
                                binding.etGPNOCPhoto?.setText("Success") //gpnocPhotoUri.toString())
                            }

                            4 -> {
                                stampPhotoUri = Uri.fromFile(it)
                                binding.etStamp?.setText("Success") //stampPhotoUri.toString())
                            } //                            5 -> {
                            //                                regCertPhotoUri = Uri.fromFile(it)
                            //                                etRegCertificate?.setText(regCertPhotoUri.toString())
                            //                            }
                            6 -> {
                                partnershipDeedUri = Uri.fromFile(it)
                                binding.etPartnershipDid?.setText("Success")
                            }

                            5 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success") //paymentScreenshotUri.toString())
                            }
                        }
                    }
                }
            }
        })
    }

    private fun uploadAadharFPhoto() {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        if (aadharFrontPhotoUri != null) {
            aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
            val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("business_pan_cards").child(aadharFrontPhotoFileName)
            filepath.putFile(aadharFrontPhotoUri!!).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        aadharFrontPhotoDownloadUrl = uri.toString()
                        aadharBackPhotoUri?.let { it1 -> uploadAadharBPhoto(it1) }
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
        } else {
            if (cpd?.isShowing == true) cpd?.dismiss()
            aadharBackPhotoUri?.let { uploadAadharBPhoto(it) }
        }
    }

    private fun uploadAadharBPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharBackPhotoFileName = "aadhar_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("business_pan_cards").child(aadharBackPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    aadharBackPhotoDownloadUrl = uri.toString()
                    gpnocPhotoUri?.let { it1 -> uploadGpnocPhoto(it1) }
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

    private fun uploadGpnocPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        gpnocPhotoFileName = "gpnoc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("business_pan_cards").child(gpnocPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    gpnocPhotoDownloadUrl = uri.toString()
                    stampPhotoUri?.let { it1 -> uploadStampPhoto(it1) }
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

    private fun uploadStampPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        stampPhotoFileName = "stamp_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("business_pan_cards").child(stampPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    stampPhotoDownloadUrl = uri.toString() //                    if (regCertPhotoUri != null) {
                    //                        regCertPhotoUri?.let { it1 -> uploadRegCertPhoto(it1) }
                    //                    } else
                    if (partnershipDeedUri != null) {
                        partnershipDeedUri?.let { it1 -> uploadPartnershipDeedPhoto(it1) }
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

    //    private fun uploadRegCertPhoto(fileUri: Uri) {
    //        if (cpd?.isShowing == true) cpd?.dismiss()
    //        cpd = ProgressDialog(mContext)
    //        cpd?.setCancelable(false)
    //        cpd?.show()
    //        if (regCertPhotoUri != null) {
    //            regCertPhotoFileName = "reg_cert_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
    //            val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("business_pan_cards").child(regCertPhotoFileName)
    //            filepath.putFile(fileUri).addOnSuccessListener {
    //                try {
    //                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
    //                        regCertPhotoDownloadUrl = uri.toString()
    //                        if (partnershipDeedUri != null) {
    //                            partnershipDeedUri?.let { it1 -> uploadPartnershipDeedPhoto(it1) }
    //                        } else {
    //                            paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
    //                        }
    //                    }.addOnFailureListener {
    //                        if (cpd?.isShowing == true) cpd?.dismiss()
    //                        it.printStackTrace()
    //                    }
    //                } catch (e: java.lang.Exception) {
    //                    if (cpd?.isShowing == true) cpd?.dismiss()
    //                    e.printStackTrace()
    //                }
    //            }.addOnFailureListener {
    //                if (cpd?.isShowing == true) cpd?.dismiss()
    //                it.message?.let { it1 -> toast(it1) }
    //            }.addOnProgressListener { //displaying the upload progress
    //                val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
    //                cpd?.setMessage("Please wait.. ")
    //            }
    //        } else {
    //            if (cpd?.isShowing == true) cpd?.dismiss()
    //            partnershipDeedUri?.let { uploadPartnershipDeedPhoto(it) }
    //        }
    //    }

    private fun uploadPartnershipDeedPhoto(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        if (partnershipDeedUri != null) {
            partnershipDeedPhotoFileName = "partnership_deed_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
            val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("business_pan_cards").child(partnershipDeedPhotoFileName)
            filepath.putFile(fileUri).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        partnershipDeedPhotoDownloadUrl = uri.toString()
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
        } else {
            if (cpd?.isShowing == true) cpd?.dismiss()
            paymentScreenshotUri?.let { uploadPaymentScreenshot(it) }
        }
    }

    private fun uploadPaymentScreenshot(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("business_pan_cards").child(paymentScreenshotFileName)
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
        billMap["applicantName"] = binding.etApplicantName.text.toString()
        billMap["fullName"] = binding.etFullName.text.toString()
        billMap["fullAddress"] = binding.etFullAddress.text.toString()
        billMap["startDate"] = strDate
        billMap["mobileNo"] = binding.etMobileNo.text.toString()
        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
        billMap["gpnocPhotoFileName"] = gpnocPhotoFileName
        billMap["gpnocPhotoDownloadUrl"] = gpnocPhotoDownloadUrl
        billMap["stampPhotoFileName"] = stampPhotoFileName
        billMap["stampPhotoDownloadUrl"] = stampPhotoDownloadUrl //        billMap["regCertPhotoFileName"] = regCertPhotoFileName
        //        billMap["regCertPhotoDownloadUrl"] = regCertPhotoDownloadUrl
        billMap["partnershipDeedPhotoFileName"] = partnershipDeedPhotoFileName
        billMap["partnershipDeedPhotoDownloadUrl"] = partnershipDeedPhotoDownloadUrl
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("business_pan_cards").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/business_pan_cards/$pushKey"] = billMap
        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Business Pan Card added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_business_pan_card")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Business Pan Card added by $name")
        }
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/business_pan_cards/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd?.isShowing == true) cpd?.dismiss() //        toast("व्यवसाय पॅन कार्ड अर्ज सादर केला")
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
        val TYPE_PROPRIETOR = "TYPE_PROPRIETOR"
        val TYPE_PARTNERSHIP = "TYPE_PARTNERSHIP"
        val TYPE_TYPE_THREE = "TYPE_TYPE_THREE"
//        val TYPE_PROPRIETOR = "प्रोप्रायटर"
//        val TYPE_PARTNERSHIP = "पार्टनरशिप"
//        val TYPE_TYPE_THREE = "1"
    }
}
