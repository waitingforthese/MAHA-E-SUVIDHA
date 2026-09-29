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
import rahul.jagtap.dmas.databinding.ActivityUdyamAadharBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class UdyamAadharActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    private var aadharFrontPhotoFileName: String = ""
    private var aadharFrontPhotoDownloadUrl: String = ""
    private var aadharBackPhotoFileName: String = ""
    private var aadharBackPhotoDownloadUrl: String = ""
    private var panPhotoFileName: String = ""
    private var panPhotoDownloadUrl: String = ""
    private var passbookPhotoFileName: String = ""
    private var passbookPhotoDownloadUrl: String = ""
    private var oldUdyamAadharPhotoFileName: String = ""
    private var oldUdyamAadharPhotoDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private val TAG = UdyamAadharActivity::class.java.simpleName
    var imageType = -1 // 1 - aadhar f, 2 - aadhar b, 3 - pan, 4 - passbook, 5 - payment screenshot
    var aadharFrontPhotoUri: Uri? = null
    var aadharBackPhotoUri: Uri? = null
    var panPhotoUri: Uri? = null
    var passbookPhotoUri: Uri? = null
    var oldUdyamAadharPhotoUri: Uri? = null
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
    lateinit var binding: ActivityUdyamAadharBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUdyamAadharBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilType, binding.tilApplicantName, binding.tilBusinessName, binding.tilBusinessAddress, binding.tilBusinessNature, binding.tilBusinessStartDate, binding.tilOwnerName, binding.tilOwnerMobileNo, binding.tilOwnerEmail, binding.tilOwnerCast, binding.tilAadharFPhoto, binding.tilAadharBPhoto, binding.tilPanPhoto, binding.tilPassbookPhoto, binding.tilOldUdyamAadharPhoto, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_udyam_aadhar)
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
                ?: "").putExtra("title", getString(R.string.txt_udyam_aadhar)))
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
            binding.tilBusinessNature.gone()
            binding.tilBusinessStartDate.gone()
            binding.tilOwnerName.gone()
            binding.tilOwnerMobileNo.gone()
            binding.tilOwnerEmail.gone()
            binding.tilOwnerCast.gone()
            binding.tilAadharFPhoto.gone()
            binding.tilAadharBPhoto.gone()
            binding.tilPanPhoto.gone()
            binding.tilPassbookPhoto.gone()
            binding.tilOldUdyamAadharPhoto.gone()
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
                            binding.tilOldUdyamAadharPhoto?.gone()
                        }
                        TYPE_DURUSTI -> {
                            strType = TYPE_DURUSTI
                            binding.tilOldUdyamAadharPhoto?.visible()
                        }
                        TYPE_TYPE_THREE -> {
                            strType = TYPE_TYPE_THREE
                            binding.tilOldUdyamAadharPhoto?.gone()
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
            val strBusinessName = binding.etBusinessName.text.toString()
            val strBusinessAddress = binding.etBusinessAddress.text.toString()
            val strBusinessNature = binding.etBusinessNature.text.toString()
            val strBusinessStartDate = binding.etBusinessStartDate.text.toString() //            val strOwnerName = binding.etOwnerName.text.toString()
            val strOwnerMobileNo = binding.etOwnerMobileNo.text.toString()
            val strOwnerEmail = binding.etOwnerEmail.text.toString()
            val strOwnerCast = binding.etOwnerCast.text.toString()
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
            if (TextUtils.isEmpty(strBusinessName)) {
                binding.tilBusinessName?.error = "व्यवसायाचे नाव टाका"
                binding.etBusinessName?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strBusinessAddress)) {
                binding.tilBusinessAddress?.error = "व्यवसाय पत्ता टाका"
                binding.etBusinessAddress?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strBusinessNature)) {
                binding.tilBusinessNature?.error = "व्यवसायाची संपूर्ण माहिती द्या"
                binding.etBusinessNature?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strBusinessStartDate)) {
                binding.tilBusinessStartDate?.error = "व्यवसाय चालू केल्याची तारीख निवडा"
                binding.etBusinessStartDate?.requestFocus()
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
            if (TextUtils.isEmpty(strOwnerCast)) {
                binding.tilOwnerCast?.error = "मालकाची जात निवडा"
                binding.etOwnerCast?.requestFocus()
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
            if (passbookPhotoUri == null) {
                binding.tilPassbookPhoto?.error = "बँक पासबुक फोटो निवडा"
                binding.etPassbookPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (strType.equals(TYPE_DURUSTI) && oldUdyamAadharPhotoUri == null) {
                binding.tilOldUdyamAadharPhoto?.error = binding.tilOldUdyamAadharPhoto.hint.toString()
                binding.etOldUdyamAadharPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = "पेमेंट स्क्रीनशॉट निवडा"
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            }
            uploadAadharFrontPhoto(aadharFrontPhotoUri!!)
        }
        binding.btnPayByUpiId?.setOnClickListener {
            startActivity(Intent(mContext, PayByUpiActivity::class.java))
        }
        binding.etOwnerCast?.setOnClickListener {
            val list = listOf("SC", "ST", "OBC", "General")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, _: View?, position: Int, _: CharSequence ->
                run {
                    dialog?.dismiss()
                    binding.etOwnerCast.setText(list[position])
                }
            }.show()
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
        binding.etPassbookPhoto?.setOnClickListener {
            imageType = 4
            choosePhotoWithPermissions()
        }
        binding.etPaymentScreenshot?.setOnClickListener {
            imageType = 5
            choosePhotoWithPermissions()
        }
        binding.etOldUdyamAadharPhoto?.setOnClickListener {
            imageType = 6
            choosePhotoWithPermissions()
        }
    }

    private fun uploadAadharFrontPhoto(fileUri: Uri) {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("udyam_aadhar").child(aadharFrontPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    aadharFrontPhotoDownloadUrl = uri.toString()
                    aadharBackPhotoUri?.let { it1 -> uploadAadharBackPhoto(it1) }
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
        }.addOnProgressListener { //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadAadharBackPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharBackPhotoFileName = "aadhar_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("udyam_aadhar").child(aadharBackPhotoFileName)
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
            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("udyam_aadhar").child(panPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    panPhotoDownloadUrl = uri.toString()
                    passbookPhotoUri?.let { it1 -> uploadPassbookPhoto(it1) }
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
        }.addOnProgressListener { //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadPassbookPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        passbookPhotoFileName = "passbook_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("udyam_aadhar").child(passbookPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    passbookPhotoDownloadUrl = uri.toString()
                    if (strType.equals(TYPE_DURUSTI) && oldUdyamAadharPhotoUri != null) {
                        oldUdyamAadharPhotoUri?.let { it1 -> uploadOldUdyamAadharPhoto(it1) }
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
            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
            it.message?.let { it1 -> toast(it1) }
        }.addOnProgressListener { //displaying the upload progress
            //            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadOldUdyamAadharPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        oldUdyamAadharPhotoFileName = "old_udyam_aadhar_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("udyam_aadhar").child(oldUdyamAadharPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    oldUdyamAadharPhotoDownloadUrl = uri.toString()
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
            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
            it.message?.let { it1 -> toast(it1) }
        }.addOnProgressListener { //displaying the upload progress
            //            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadPaymentScreenshot(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("udyam_aadhar").child(paymentScreenshotFileName)
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
            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
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
        billMap["businessName"] = binding.etBusinessName.text.toString()
        billMap["businessAddress"] = binding.etBusinessAddress.text.toString()
        billMap["businessNature"] = binding.etBusinessNature.text.toString()
        billMap["businessStartDate"] = strDate //        billMap["ownerName"] =  binding.etOwnerName.text.toString()
        billMap["ownerMobileNo"] = binding.etOwnerMobileNo.text.toString()
        billMap["ownerEmail"] = binding.etOwnerEmail.text.toString()
        billMap["ownerCast"] = binding.etOwnerCast.text.toString()
        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
        billMap["panPhotoFileName"] = panPhotoFileName
        billMap["panPhotoDownloadUrl"] = panPhotoDownloadUrl
        billMap["passbookPhotoFileName"] = passbookPhotoFileName
        billMap["passbookPhotoDownloadUrl"] = passbookPhotoDownloadUrl
        billMap["oldUdyamAadharPhotoFileName"] = oldUdyamAadharPhotoFileName
        billMap["oldUdyamAadharPhotoDownloadUrl"] = oldUdyamAadharPhotoDownloadUrl
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("udyam_aadhar").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/udyam_aadhar/$pushKey"] = billMap

        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Udyam Aadhar added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_udyam_aadhar")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Udyam Aadhar added by $name")
        }

        // Create a backup record
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/udyam_aadhar/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss() //        toast("उद्यम आधार अर्ज सादर केला")
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
                                passbookPhotoUri = Uri.fromFile(it)
                                binding.etPassbookPhoto?.setText("Success")//passbookPhotoUri.toString())
                            }

                            5 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success")//paymentScreenshotUri.toString())
                            }

                            6 -> {
                                oldUdyamAadharPhotoUri = Uri.fromFile(it)
                                binding.etOldUdyamAadharPhoto?.setText("Success")//paymentScreenshotUri.toString())
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
        val TYPE_TYPE_THREE = "TYPE_TYPE_THREE"
//        val TYPE_NAVIN = "नवीन"
//        val TYPE_DURUSTI = "दुरुस्ती"
//        val TYPE_TYPE_THREE = "1"
    }
}
