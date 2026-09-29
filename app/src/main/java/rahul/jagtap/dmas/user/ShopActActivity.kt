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
import rahul.jagtap.dmas.databinding.ActivityShopActBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class ShopActActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    private var aadharFrontPhotoFileName: String = ""
    private var aadharFrontPhotoDownloadUrl: String = ""
    private var aadharBackPhotoFileName: String = ""
    private var aadharBackPhotoDownloadUrl: String = ""
    private var panPhotoFileName: String = ""
    private var panPhotoDownloadUrl: String = ""
    private var lightPhotoFileName: String = ""
    private var lightPhotoDownloadUrl: String = ""
    private var ownerPhotoFileName: String = ""
    private var ownerPhotoDownloadUrl: String = ""
    private var signPhotoFileName: String = ""
    private var signPhotoDownloadUrl: String = ""
    private var oldShopActPhotoFileName: String = ""
    private var oldShopActPhotoDownloadUrl: String = ""
    private var partnerPhotoFileName: String = ""
    private var partnerPhotoDownloadUrl: String = ""
    private var partnerSignPhotoFileName: String = ""
    private var partnerSignPhotoDownloadUrl: String = ""
    //    private var boardPhotoFileName: String = ""
    //    private var boardPhotoDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private val TAG = ShopActActivity::class.java.simpleName
    var imageType = -1 // 1 - aadhar f, 2 - aadhar b, 3 - pan, 4 - light, 5 - sign, 6 - sign, 7 - board, 8 - payment sc
    var aadharFrontPhotoUri: Uri? = null
    var aadharBackPhotoUri: Uri? = null
    var panPhotoUri: Uri? = null
    var lightPhotoUri: Uri? = null
    var ownerPhotoUri: Uri? = null
    var signPhotoUri: Uri? = null
    var oldShopActPhotoUri: Uri? = null
    var partnerPhotoUri: Uri? = null
    var partnerSignPhotoUri: Uri? = null
    //    var boardPhotoUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    var cpd: ProgressDialog? = null
    private var strDate: String? = ""
    private lateinit var easyImage: EasyImage
    private var strTypeToShow: String? = ""
    private var strType: String? = ""
    lateinit var binding: ActivityShopActBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopActBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilType, binding.tilApplicantName, binding.tilBusinessName, binding.tilBusinessAddress, binding.tilBusinessStartDate, binding.tilBusinessInfo, binding.tilOwnerName, binding.tilOwnerMobileNo, binding.tilOwnerEmail, binding.tilAadharFPhoto, binding.tilAadharBPhoto, binding.tilPanPhoto, binding.tilLightBillPhoto, binding.tilOwnerPhoto, binding.tilSignPhoto, binding.tilOldShopActPhoto, binding.tilPartnerPhoto, binding.tilPartnerSign, binding.tilBoardPhoto, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "शॉप ऍक्ट"
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
                ?: "").putExtra("title", "शॉप ऍक्ट"))
        }
        typeMap = intent.extras?.getSerializable("hashMap") as HashMap<String, HashMap<String, String>>
        val list = ArrayList<String>()
        typeMap?.forEach {
            if (it.value["type_title"] != "0") list.add(it.value["type_title"].toString())
        }
        if (list.size == 0) {
            binding.tilType.gone()
            binding.tilBusinessName.gone()
            binding.tilBusinessAddress.gone()
            binding.tilBusinessStartDate.gone()
            binding.tilBusinessInfo.gone()
            binding.tilOwnerName.gone()
            binding.tilOwnerMobileNo.gone()
            binding.tilOwnerEmail.gone()
            binding.tilAadharFPhoto.gone()
            binding.tilAadharBPhoto.gone()
            binding.tilPanPhoto.gone()
            binding.tilLightBillPhoto.gone()
            binding.tilOwnerPhoto.gone()
            binding.tilSignPhoto.gone()
            binding.tilOldShopActPhoto.gone()
            binding.tilPartnerPhoto.gone()
            binding.tilPartnerSign.gone()
            binding.tilBoardPhoto.gone()
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
                            binding.tilPartnerPhoto?.gone()
                            binding.tilPartnerSign?.gone()
                            binding.tilOldShopActPhoto?.gone()
                        }
                        TYPE_DURUSTI -> {
                            strType = TYPE_DURUSTI
                            binding.tilPartnerPhoto?.gone()
                            binding.tilPartnerSign?.gone()
                            binding.tilOldShopActPhoto?.visible()
                        }
                        TYPE_PARTNERSHIP_SHOP_ACT -> {
                            strType = TYPE_PARTNERSHIP_SHOP_ACT
                            binding.tilPartnerPhoto?.visible()
                            binding.tilPartnerSign?.visible()
                            binding.tilOldShopActPhoto?.gone()
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
//            list.add("पार्टनरशिप शॉप ऍक्ट")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    applyType(position)
                }
            }.show()
        }
        binding.btnSubmit?.setOnClickListener {
            val strApplicantName = binding.etApplicantName.text.toString()
            val strBusinessName = binding.etBusinessName.text.toString()
            val strBusinessAddress = binding.etBusinessAddress.text.toString()
            val strBusinessStartDate = binding.etBusinessStartDate.text.toString()
            val strBusinessInfo = binding.etBusinessInfo.text.toString() //            val strOwnerName = binding.etOwnerName.text.toString()
            val strOwnerMobileNo = binding.etOwnerMobileNo.text.toString()
            val strOwnerEmail = binding.etOwnerEmail.text.toString()
            if (TextUtils.isEmpty(strTypeToShow)) {
                binding.tilType?.error = binding.tilType.hint.toString()
                binding.etType?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strApplicantName)) {
                binding.tilApplicantName?.error = "कस्टमरचे नाव टाका"
                binding.etApplicantName?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilBusinessName.visibility == View.VISIBLE && TextUtils.isEmpty(strBusinessName)) {
                binding.tilBusinessName?.error = "व्यवसायाचे नाव टाका"
                binding.etBusinessName?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilBusinessAddress.visibility == View.VISIBLE && TextUtils.isEmpty(strBusinessAddress)) {
                binding.tilBusinessAddress?.error = "व्यवसाय पत्ता टाका"
                binding.etBusinessAddress?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strBusinessStartDate)) {
                binding.tilBusinessStartDate?.error = "व्यवसाय सुरू करण्याची तारीख निवडा"
                binding.etBusinessStartDate?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strBusinessInfo)) {
                binding.tilBusinessInfo?.error = "व्यवसायाची संपूर्ण माहिती द्या"
                binding.etBusinessInfo?.requestFocus()
                return@setOnClickListener
            } //            if (TextUtils.isEmpty(strOwnerName)) {
            //                binding.tilOwnerName?.error = "मालकाचे नाव टाका"
            //                binding.etOwnerName?.requestFocus()
            //                return@setOnClickListener
            //            }
            if (TextUtils.isEmpty(strOwnerMobileNo)) {
                binding.tilOwnerMobileNo?.error = "मालकाचा मोबाईल नंबर टाका"
                binding.etOwnerMobileNo?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strOwnerEmail)) {
                binding.tilOwnerEmail?.error = "मालकाचा ईमेल आयडी टाका"
                binding.etOwnerEmail?.requestFocus()
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
            if (panPhotoUri == null) {
                binding.tilPanPhoto?.error = "पॅन कार्ड फोटो निवडा"
                binding.etPanPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (lightPhotoUri == null) {
                binding.tilLightBillPhoto?.error = binding.tilLightBillPhoto.hint.toString()
                binding.etLightBillPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (ownerPhotoUri == null) {
                binding.tilOwnerPhoto?.error = "मालकाचा फोटो निवडा"
                binding.etOwnerPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (signPhotoUri == null) {
                binding.tilSignPhoto?.error = "स्वाक्षरी फोटो निवडा"
                binding.etSignPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (strType.equals(TYPE_DURUSTI) && oldShopActPhotoUri == null) {
                binding.tilOldShopActPhoto?.error = binding.tilOldShopActPhoto.hint.toString()
                binding.etOldShopActPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (strType.equals(TYPE_PARTNERSHIP_SHOP_ACT) && partnerPhotoUri == null) {
                binding.tilPartnerPhoto?.error = binding.tilPartnerPhoto.hint.toString()
                binding.etPartnerPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (strType.equals(TYPE_PARTNERSHIP_SHOP_ACT) && partnerSignPhotoUri == null) {
                binding.tilPartnerSign?.error = binding.tilPartnerSign.hint.toString()
                binding.etPartnerSign?.requestFocus()
                return@setOnClickListener
            } //            if (boardPhotoUri == null) {
            //                binding.tilBoardPhoto?.error = "बोर्ड फोटो निवडा"
            //                binding.etBoardPhoto?.requestFocus()
            //                return@setOnClickListener
            //            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = "पेमेंट स्क्रीनशॉट निवडा"
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            }
            uploadAadharFPhoto(aadharFrontPhotoUri!!)
        }
        binding.etBusinessStartDate?.setOnClickListener {
            val mMaxDate = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(mContext!!, { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val date = "" + dayOfMonth + "/" + (month + 1) + "/" + year
                strDate = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
                binding.etBusinessStartDate?.setText(date)
            }, mMaxDate[Calendar.YEAR], mMaxDate[Calendar.MONTH], mMaxDate[Calendar.DAY_OF_MONTH])
            datePickerDialog.show()
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
        binding.etLightBillPhoto?.setOnClickListener {
            imageType = 4
            choosePhotoWithPermissions()
        }
        binding.etOwnerPhoto?.setOnClickListener {
            imageType = 5
            choosePhotoWithPermissions()
        }
        binding.etSignPhoto?.setOnClickListener {
            imageType = 6
            choosePhotoWithPermissions()
        } //        binding.etBoardPhoto?.setOnClickListener {
        //            imageType = 7
        //            choosePhotoWithPermissions()
        //        }
        binding.etPaymentScreenshot?.setOnClickListener {
            imageType = 8
            choosePhotoWithPermissions()
        }
        binding.etOldShopActPhoto?.setOnClickListener {
            imageType = 9
            choosePhotoWithPermissions()
        }
        binding.etPartnerPhoto?.setOnClickListener {
            imageType = 10
            choosePhotoWithPermissions()
        }
        binding.etPartnerSign?.setOnClickListener {
            imageType = 11
            choosePhotoWithPermissions()
        }
        binding.btnPayByUpiId?.setOnClickListener {
            startActivity(Intent(mContext, PayByUpiActivity::class.java))
        }
    }

    private fun uploadAadharFPhoto(fileUri: Uri) {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("shop_acts").child(aadharFrontPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    aadharFrontPhotoDownloadUrl = uri.toString()
                    aadharBackPhotoUri?.let { it1 -> uploadAadharBPhoto(it1) }
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

    private fun uploadAadharBPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharBackPhotoFileName = "aadhar_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("shop_acts").child(aadharBackPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    aadharBackPhotoDownloadUrl = uri.toString()
                    panPhotoUri?.let { it1 -> uploadPanPhoto(it1) }
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

    private fun uploadPanPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        panPhotoFileName = "pan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("shop_acts").child(panPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    panPhotoDownloadUrl = uri.toString()
                    lightPhotoUri?.let { it1 -> uploadLightPhoto(it1) }
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

    private fun uploadLightPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        lightPhotoFileName = "light_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("shop_acts").child(lightPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    lightPhotoDownloadUrl = uri.toString()
                    ownerPhotoUri?.let { it1 -> uploadOwnerPhoto(it1) }
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

    private fun uploadOwnerPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        ownerPhotoFileName = "owner_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("shop_acts").child(ownerPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    ownerPhotoDownloadUrl = uri.toString()
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
        }.addOnProgressListener { //displaying the upload progress
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("shop_acts").child(signPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    signPhotoDownloadUrl = uri.toString() //                    boardPhotoUri?.let { it1 -> uploadBoardPhoto(it1) }
                    if (strType.equals(TYPE_DURUSTI)) {
                        oldShopActPhotoUri?.let { it1 -> uploadOldShopActPhoto(it1) }
                    } else if (strType.equals(TYPE_PARTNERSHIP_SHOP_ACT)) {
                        partnerPhotoUri?.let { it1 -> uploadPartnerPhoto(it1) }
                    } else {
                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
                    }
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

    private fun uploadOldShopActPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        oldShopActPhotoFileName = "old_shop_act_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("shop_acts").child(oldShopActPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    oldShopActPhotoDownloadUrl = uri.toString() //                    boardPhotoUri?.let { it1 -> uploadBoardPhoto(it1) }
                    if (strType.equals(TYPE_PARTNERSHIP_SHOP_ACT)) {
                        partnerPhotoUri?.let { it1 -> uploadPartnerPhoto(it1) }
                    } else {
                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
                    }
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

    private fun uploadPartnerPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        partnerPhotoFileName = "partner_photo_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("shop_acts").child(partnerPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    partnerPhotoDownloadUrl = uri.toString() //                    boardPhotoUri?.let { it1 -> uploadBoardPhoto(it1) }
                    partnerSignPhotoUri?.let { it1 -> uploadPartnerSignPhoto(it1) }
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

    private fun uploadPartnerSignPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        partnerSignPhotoFileName = "partner_sign_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("shop_acts").child(partnerSignPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    partnerSignPhotoDownloadUrl = uri.toString() //                    boardPhotoUri?.let { it1 -> uploadBoardPhoto(it1) }
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

    //    private fun uploadBoardPhoto(fileUri: Uri) {
    //        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
    //        cpd = ProgressDialog(mContext)
    //        cpd?.setCancelable(false)
    //        cpd?.show()
    //        boardPhotoFileName = "board_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
    //        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("shop_acts").child(boardPhotoFileName)
    //        filepath.putFile(fileUri).addOnSuccessListener {
    //            try {
    //                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
    //                    boardPhotoDownloadUrl = uri.toString()
    //                    paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
    //                }.addOnFailureListener {
    //                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
    //                    it.printStackTrace()
    //                }
    //            } catch (e: java.lang.Exception) {
    //                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
    //                e.printStackTrace()
    //            }
    //        }.addOnFailureListener {
    //            if (cpd?.isShowing == true) cpd?.dismiss()
    //            it.message?.let { it1 -> toast(it1) }
    //        }.addOnProgressListener {
    //            //displaying the upload progress
    //            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
    //            cpd?.setMessage("Please wait.. ")
    //        }
    //    }

    private fun uploadPaymentScreenshot(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("shop_acts").child(paymentScreenshotFileName)
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
        billMap["applicantName"] = binding.etApplicantName.text.toString()
        billMap["type"] = strType
        billMap["typeToShow"] = strTypeToShow
        billMap["businessName"] = binding.etBusinessName.text.toString()
        billMap["businessAddress"] = binding.etBusinessAddress.text.toString()
        billMap["businessStartDate"] = strDate
        billMap["businessInfo"] = binding.etBusinessInfo.text.toString() //        billMap["ownerName"] =  binding.etOwnerName.text.toString()
        billMap["ownerMobileNo"] = binding.etOwnerMobileNo.text.toString()
        billMap["ownerEmail"] = binding.etOwnerEmail.text.toString()
        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
        billMap["panPhotoFileName"] = panPhotoFileName
        billMap["panPhotoDownloadUrl"] = panPhotoDownloadUrl
        billMap["lightPhotoFileName"] = lightPhotoFileName
        billMap["lightPhotoDownloadUrl"] = lightPhotoDownloadUrl
        billMap["ownerPhotoFileName"] = ownerPhotoFileName
        billMap["ownerPhotoDownloadUrl"] = ownerPhotoDownloadUrl
        billMap["signPhotoFileName"] = signPhotoFileName
        billMap["signPhotoDownloadUrl"] = signPhotoDownloadUrl
        billMap["oldShopActPhotoFileName"] = oldShopActPhotoFileName
        billMap["oldShopActPhotoDownloadUrl"] = oldShopActPhotoDownloadUrl
        billMap["partnerPhotoFileName"] = partnerPhotoFileName
        billMap["partnerPhotoDownloadUrl"] = partnerPhotoDownloadUrl
        billMap["partnerSignPhotoFileName"] = partnerSignPhotoFileName
        billMap["partnerSignPhotoDownloadUrl"] = partnerSignPhotoDownloadUrl
        //        billMap["boardPhotoFileName"] = boardPhotoFileName
        //        billMap["boardPhotoDownloadUrl"] = boardPhotoDownloadUrl
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("shop_acts").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/shop_acts/$pushKey"] = billMap

        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Shop Act added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_shop_act")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Shop Act added by $name")
        }

        // Create a backup record
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/shop_acts/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss() //        toast("शॉप ऍक्ट अर्ज सादर केला")
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
                                panPhotoUri = Uri.fromFile(it)
                                binding.etPanPhoto?.setText("Success") //panPhotoUri.toString())
                            }

                            4 -> {
                                lightPhotoUri = Uri.fromFile(it)
                                binding.etLightBillPhoto?.setText("Success") //lightPhotoUri.toString())
                            }

                            5 -> {
                                ownerPhotoUri = Uri.fromFile(it)
                                binding.etOwnerPhoto?.setText("Success") //ownerPhotoUri.toString())
                            }

                            6 -> {
                                signPhotoUri = Uri.fromFile(it)
                                binding.etSignPhoto?.setText("Success") //signPhotoUri.toString())
                            } //                            7 -> {
                            //                                boardPhotoUri = Uri.fromFile(it)
                            //                                etBoardPhoto?.setText(boardPhotoUri.toString())
                            //                            }
                            8 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success") //paymentScreenshotUri.toString())
                            }
                            9 -> {
                                oldShopActPhotoUri = Uri.fromFile(it)
                                binding.etOldShopActPhoto?.setText("Success") //paymentScreenshotUri.toString())
                            }
                            10 -> {
                                partnerPhotoUri = Uri.fromFile(it)
                                binding.etPartnerPhoto?.setText("Success") //paymentScreenshotUri.toString())
                            }
                            11 -> {
                                partnerSignPhotoUri = Uri.fromFile(it)
                                binding.etPartnerSign?.setText("Success") //paymentScreenshotUri.toString())
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

    companion object {
        val TYPE_NAVIN = "TYPE_NAVIN"
        val TYPE_DURUSTI = "TYPE_DURUSTI"
        val TYPE_PARTNERSHIP_SHOP_ACT = "TYPE_PARTNERSHIP_SHOP_ACT"
//        val TYPE_NAVIN = "नवीन"
//        val TYPE_DURUSTI = "दुरुस्ती"
//        val TYPE_PARTNERSHIP_SHOP_ACT = "पार्टनरशिप शॉप ऍक्ट"
    }
}
