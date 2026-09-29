package rahul.jagtap.dmas.user

import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.databinding.ActivityProjectFundingBinding
import rahul.jagtap.dmas.utils.Utils

class ProjectFundingActivity : BaseActivity() {
//    private var oldPanCardPhotoFileName: String = ""
//    private var oldPanCardPhotoDownloadUrl: String = ""
//    private var passportPhotoFileName: String = ""
//    private var passportPhotoDownloadUrl: String = ""
//    private var aadharFrontPhotoFileName: String = ""
//    private var aadharFrontPhotoDownloadUrl: String = ""
//    private var aadharBackPhotoFileName: String = ""
//    private var aadharBackPhotoDownloadUrl: String = ""
//    private var signFileName: String = ""
//    private var signDownloadUrl: String = ""
//    private var paymentScreenshotFileName: String = ""
//    private var paymentScreenshotDownloadUrl: String = ""
//    private var email: String? = ""
//    private var username: String? = ""
//    private var uid: String? = ""
//    private var name: String? = ""
    private val TAG = ProjectFundingActivity::class.java.simpleName
//    var imageType = -1 // 1 - old pan card, 2 - passport, 3 - aadhar f, 4 - aadhar b, 5 - sign, 6 - payment screenshot
//    var oldPanCardPhotoUri: Uri? = null
//    var passportPhotoUri: Uri? = null
//    var aadharFrontPhotoUri: Uri? = null
//    var aadharBackPhotoUri: Uri? = null
//    var singPhotoUri: Uri? = null
//    var paymentScreenshotUri: Uri? = null
//    var cpd: ProgressDialog? = null
//    private var strDate: String? = ""
    lateinit var binding: ActivityProjectFundingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectFundingBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "प्रोजेक्ट लोन फंडिंग / PROJECT FUNDINING"
//        email = app?.preferences?.loggedInUser?.email
//        uid = app?.preferences?.loggedInUser?.uid
//        username = app?.preferences?.loggedInUser?.username
//        name = app?.preferences?.loggedInUser?.name

//        btnSubmit?.setOnClickListener {
//            val strApplicantName = etApplicantName.text.toString()
//            val strType = etType.text.toString()
//            val strFatherName = etFatherName.text.toString()
//            val strDob = etDob.text.toString()
//            val strMobileNo = etMobileNo.text.toString()
//            if (TextUtils.isEmpty(strApplicantName)) {
//                etApplicantName?.error = "Enter Applicant's Name"
//                etApplicantName?.requestFocus()
//                return@setOnClickListener
//            }
//            if (TextUtils.isEmpty(strType)) {
//                etType?.error = "Select Type(नवीन/दुरुस्ती)"
//                etType?.requestFocus()
//                return@setOnClickListener
//            }
//            if (TextUtils.isEmpty(strFatherName)) {
//                etFatherName?.error = "Enter Father's Full Name"
//                etFatherName?.requestFocus()
//                return@setOnClickListener
//            }
//            if (TextUtils.isEmpty(strDob)) {
//                etDob?.error = "Select date of birth"
//                etDob?.requestFocus()
//                return@setOnClickListener
//            }
//            if (TextUtils.isEmpty(strMobileNo)) {
//                etMobileNo?.error = "Enter Mobile Number"
//                etMobileNo?.requestFocus()
//                return@setOnClickListener
//            }
//            if (strType == "दुरुस्ती" && oldPanCardPhotoUri == null) {
//                etOldPanCardPhoto?.error = "Select Old Pan Card Photo"
//                etOldPanCardPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (passportPhotoUri == null) {
//                etPassportPhoto?.error = "Select Passport Photo"
//                etPassportPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (aadharFrontPhotoUri == null) {
//                etAadharFrontPhoto?.error = "Select Aadhar Card Front Photo"
//                etAadharFrontPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (aadharBackPhotoUri == null) {
//                etAadharBackPhoto?.error = "Select Aadhar Card Back Photo"
//                etAadharBackPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (singPhotoUri == null) {
//                etSignPhoto?.error = "Select Signature Photo"
//                etSignPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (paymentScreenshotUri == null) {
//                etPaymentScreenshot?.error = "Select Payment Screenshot"
//                etPaymentScreenshot?.requestFocus()
//                return@setOnClickListener
//            }
//            uploadOldPanCardPhoto()
//        }
//        etDob?.setOnClickListener {
//            val mMaxDate = Calendar.getInstance()
//            val datePickerDialog = DatePickerDialog(mContext!!, { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
//                val date = "" + dayOfMonth + "/" + (month + 1) + "/" + year
//                strDate = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
//                etDob.setText(date)
//            }, mMaxDate[Calendar.YEAR], mMaxDate[Calendar.MONTH], mMaxDate[Calendar.DAY_OF_MONTH])
//            datePickerDialog.show()
//        }
//        etType?.setOnClickListener {
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
//        etOldPanCardPhoto?.setOnClickListener {
//            imageType = 1
//            choosePhotoWithPermissions()
//        }
//        etPassportPhoto?.setOnClickListener {
//            imageType = 2
//            choosePhotoWithPermissions()
//        }
//        etAadharFrontPhoto?.setOnClickListener {
//            imageType = 3
//            choosePhotoWithPermissions()
//        }
//        etAadharBackPhoto?.setOnClickListener {
//            imageType = 4
//            choosePhotoWithPermissions()
//        }
//        etSignPhoto?.setOnClickListener {
//            imageType = 5
//            choosePhotoWithPermissions()
//        }
//        etPaymentScreenshot?.setOnClickListener {
//            imageType = 6
//            choosePhotoWithPermissions()
//        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : EasyImage.Callbacks {
//            override fun onImagePickerError(e: Exception, source: EasyImage.ImageSource, type: Int) {
//                longToast(getString(R.string.txt_try_later))
//            }
//
//            override fun onImagesPicked(imageFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
////                val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
////                val fileName = AccountingServicesActivity.dateFormatForPhoto.format(System.currentTimeMillis()) + ".jpg"
////                val image = File("$dir/$fileName")
//                when (type) {
//                    AccountingServicesActivity.REQUEST_AVATAR_CODE -> {
//                        lifecycleScope.launch {
//                            val compressedImageFile = mContext?.let { Compressor.compress(it, imageFiles[0]) }
//                            Log.e(TAG, "${compressedImageFile?.path}")
//                            compressedImageFile?.let {
//                                when (imageType) {
//                                    1 -> {
//                                        oldPanCardPhotoUri = Uri.fromFile(it)
//                                        etOldPanCardPhoto?.setText(oldPanCardPhotoUri.toString())
//                                    }
//                                    2 -> {
//                                        passportPhotoUri = Uri.fromFile(it)
//                                        etPassportPhoto?.setText(passportPhotoUri.toString())
//                                    }
//                                    3 -> {
//                                        aadharFrontPhotoUri = Uri.fromFile(it)
//                                        etAadharFrontPhoto?.setText(aadharFrontPhotoUri.toString())
//                                    }
//                                    4 -> {
//                                        aadharBackPhotoUri = Uri.fromFile(it)
//                                        etAadharBackPhoto?.setText(aadharBackPhotoUri.toString())
//                                    }
//                                    5 -> {
//                                        singPhotoUri = Uri.fromFile(it)
//                                        etSignPhoto?.setText(singPhotoUri.toString())
//                                    }
//                                    6 -> {
//                                        paymentScreenshotUri = Uri.fromFile(it)
//                                        etPaymentScreenshot?.setText(paymentScreenshotUri.toString())
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            override fun onCanceled(source: EasyImage.ImageSource, type: Int) {}
//        })
//    }
//
//    private fun uploadOldPanCardPhoto() {
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        if (oldPanCardPhotoUri != null) {
//            oldPanCardPhotoFileName = "old_pan_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.png"
//            val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("pan_cards").child(oldPanCardPhotoFileName)
//            filepath.putFile(oldPanCardPhotoUri!!).addOnSuccessListener {
//                try {
//                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                        oldPanCardPhotoDownloadUrl = uri.toString()
//                        passportPhotoUri?.let { it1 -> uploadPassportPhoto(it1) }
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
//            }.addOnProgressListener {
//                //displaying the upload progress
//                val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//                cpd?.setMessage("Please wait.. ")
//            }
//        } else {
//            if (cpd?.isShowing == true) cpd?.dismiss()
//            passportPhotoUri?.let { uploadPassportPhoto(it) }
//        }
//    }
//
//    private fun uploadPassportPhoto(fileUri: Uri) {
//        if (cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        passportPhotoFileName = "passport_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.png"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("pan_cards").child(passportPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    passportPhotoDownloadUrl = uri.toString()
//                    aadharFrontPhotoUri?.let { it1 -> uploadAadharFrontPhoto(it1) }
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
//        }.addOnProgressListener {
//            //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadAadharFrontPhoto(fileUri: Uri) {
//        if (cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.png"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("pan_cards").child(aadharFrontPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    aadharFrontPhotoDownloadUrl = uri.toString()
//                    aadharBackPhotoUri?.let { it1 -> uploadAadharBackPhoto(it1) }
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
//        }.addOnProgressListener {
//            //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadAadharBackPhoto(fileUri: Uri) {
//        if (cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        aadharBackPhotoFileName = "aadhar_back_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.png"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("pan_cards").child(aadharBackPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    aadharBackPhotoDownloadUrl = uri.toString()
//                    singPhotoUri?.let { it1 -> uploadSignPhoto(it1) }
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
//        }.addOnProgressListener {
//            //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadSignPhoto(fileUri: Uri) {
//        if (cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        signFileName = "sign_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.png"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("pan_cards").child(signFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    signDownloadUrl = uri.toString()
//                    paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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
//        }.addOnProgressListener {
//            //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadPaymentScreenshot(fileUri: Uri) {
//        if (cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.png"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("pan_cards").child(paymentScreenshotFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    paymentScreenshotDownloadUrl = uri.toString()
//                    createDbRecord()
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
//        }.addOnProgressListener {
//            //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun createDbRecord() {
//        val createdDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Date())
//        val billMap = HashMap<String, Any?>()
//        billMap["email"] = email
//        billMap["createdBy"] = name
//        billMap["createdByUserName"] = username
//        billMap["uid"] = uid
//        billMap["createdDateTime"] = createdDateTime
//        billMap["applicantName"] = etApplicantName.text.toString()
//        billMap["fatherName"] = etFatherName.text.toString()
//        billMap["dob"] = strDate
//        billMap["type"] = etType.text.toString()
//        billMap["mobileNo"] = etMobileNo.text.toString()
//        billMap["oldPanCardPhotoFileName"] = oldPanCardPhotoFileName
//        billMap["oldPanCardPhotoDownloadUrl"] = oldPanCardPhotoDownloadUrl
//        billMap["passportPhotoFileName"] = passportPhotoFileName
//        billMap["passportPhotoDownloadUrl"] = passportPhotoDownloadUrl
//        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
//        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
//        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
//        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
//        billMap["signFileName"] = signFileName
//        billMap["signDownloadUrl"] = signDownloadUrl
//        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
//        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
//        billMap["timeStamp"] = System.currentTimeMillis()
//
//        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("pan_cards").push().key
//        billMap["pushKey"] = pushKey
//
//        val messageUserMap = HashMap<String, Any?>()
//        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/pan_cards/$pushKey"] = billMap
//        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
//            if (databaseError != null) {
//                Log.e("db error", databaseError.message)
//            }
//        }
//        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
//        if (notificationPushKey != null) {
//            val notificationItem = NotificationItem(message = "New Pan Card added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_pan_card")
//            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
//            sendNotification("New Pan Card added by $name")
//        }
//        val backupMap = HashMap<String, Any?>()
//        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/pan_cards/$pushKey"] = billMap
//        database.updateChildren(backupMap)
//        if (cpd?.isShowing == true) cpd?.dismiss()
//        toast("Pan Card created successfully")
//        finish()
//    }
//
//    fun getTodayDate(): String {
//        return SimpleDateFormat("dd-MM-yy").format(Date())
//    }

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

//    @SuppressLint("CheckResult")
//    private fun choosePhotoWithPermissions() {
//        rxPermissions?.requestEachCombined(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)?.subscribe {
//            when {
//                it.granted -> {
//                    // get picture
//                    EasyImage.openChooserWithGallery(this, "", AccountingServicesActivity.REQUEST_AVATAR_CODE)
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
//    }

    companion object {}
}
