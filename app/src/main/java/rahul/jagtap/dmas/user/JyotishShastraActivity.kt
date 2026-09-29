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
import rahul.jagtap.dmas.widget.snaptimepicker.SnapTimePickerDialog
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
import rahul.jagtap.dmas.databinding.ActivityJyotishShastraBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class JyotishShastraActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private val TAG = JyotishShastraActivity::class.java.simpleName
    var imageType = -1 // 1 - payment screenshot
    var paymentScreenshotUri: Uri? = null
    var cpd: ProgressDialog? = null
    private var strDob: String? = ""
    private var strGirlDob: String? = ""
    private lateinit var easyImage: EasyImage
    lateinit var binding: ActivityJyotishShastraBinding
    private var strTypeToShow: String? = ""
    private var strType: String? = ""
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJyotishShastraBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilServiceType, binding.tilFullName, binding.tilDob, binding.tilBirthTime, binding.tilBirthTimeAmPm, binding.tilBirthPlace, binding.tilGirlFullName, binding.tilGirlDob, binding.tilGirlBirthTime, binding.tilGirlBirthTimeAmPm, binding.tilGirlBirthPlace, binding.tilQuestion, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "ज्योतिष शास्त्रींना प्रश्न विचारा"
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
                ?: "").putExtra("title", "ज्योतिष शास्त्रींना प्रश्न विचारा"))
        }

        binding.etDob?.setOnClickListener {
            val mMaxDate = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(mContext!!, { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val date = "" + dayOfMonth + "/" + (month + 1) + "/" + year
                strDob = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
                binding.etDob?.setText(date)
            }, mMaxDate[Calendar.YEAR], mMaxDate[Calendar.MONTH], mMaxDate[Calendar.DAY_OF_MONTH])
            datePickerDialog.show()
        }
        binding.etGirlDob?.setOnClickListener {
            val mMaxDate = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(mContext!!, { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val date = "" + dayOfMonth + "/" + (month + 1) + "/" + year
                strGirlDob = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
                binding.etGirlDob?.setText(date)
            }, mMaxDate[Calendar.YEAR], mMaxDate[Calendar.MONTH], mMaxDate[Calendar.DAY_OF_MONTH])
            datePickerDialog.show()
        }
        binding.etBirthTime?.setOnClickListener {
//            val now = Calendar.getInstance()
//            val dpd = TimePickerDialog.newInstance({ view, hourOfDay, minute, second ->
//                binding.etBirthTime?.setText("$hourOfDay:$minute:$second")
//            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
//            dpd.setTitle("Select Time")
//            dpd.show(supportFragmentManager, "Timepickerdialog")
            SnapTimePickerDialog.Builder().apply {
                this.setTitle(R.string.birth_time)
                setThemeColor(R.color.colorAccent)
                setTitleColor(R.color.white)
            }.build().apply{
                setListener { hour, minute ->
                    // Do something when user selected the time
                    binding.etBirthTime?.setText("$hour:$minute")
                }
            }.show(supportFragmentManager, "Timepickerdialog")
        }
        binding.etBirthTimeAmPm.setOnClickListener {
            val list = java.util.ArrayList<String>()
            list.add("दुपारी 12 च्या अगोदर")
            list.add("दुपारी 12 च्या नंतर")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    binding.etBirthTimeAmPm?.setText(list[position])
                }
            }.show()
        }
        binding.etGirlBirthTime?.setOnClickListener {
//            val now = Calendar.getInstance()
//            val dpd = TimePickerDialog.newInstance({ view, hourOfDay, minute, second ->
//                binding.etGirlBirthTime?.setText("$hourOfDay:$minute:$second")
//            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
//            dpd.setTitle("Select Time")
//            dpd.show(supportFragmentManager, "Timepickerdialog")
            SnapTimePickerDialog.Builder().apply {
                this.setTitle(R.string.girl_birth_time)
                setThemeColor(R.color.colorAccent)
                setTitleColor(R.color.white)
            }.build().apply{
                setListener { hour, minute ->
                    // Do something when user selected the time
                    binding.etGirlBirthTime?.setText("$hour:$minute")
                }
            }.show(supportFragmentManager, "GirlTimepickerdialog")
        }
        binding.etGirlBirthTimeAmPm.setOnClickListener {
            val list = java.util.ArrayList<String>()
            list.add("दुपारी 12 च्या अगोदर")
            list.add("दुपारी 12 च्या नंतर")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    binding.etGirlBirthTimeAmPm?.setText(list[position])
                }
            }.show()
        }
        typeMap = intent.extras?.getSerializable("hashMap") as HashMap<String, HashMap<String, String>>?
        val list = ArrayList<String>()
        typeMap?.forEach {
            if (it.value["type_title"] != "0") list.add(it.value["type_title"].toString())
        }
        if (list.size == 0) {
            binding.tilServiceType.gone()
            binding.tilDob.gone()
            binding.tilBirthTime.gone()
            binding.tilBirthTimeAmPm.gone()
            binding.tilBirthPlace.gone()
            binding.tilGirlFullName.gone()
            binding.tilGirlDob.gone()
            binding.llGirlBirthTime.gone()
            binding.tilGirlBirthTime.gone()
            binding.tilGirlBirthTimeAmPm.gone()
            binding.tilGirlBirthPlace.gone()
            binding.tilQuestion.gone()
            binding.tilPaymentScreenshot.gone()
            binding.btnSubmit.gone()
        }
        binding.etServiceType?.setOnClickListener { //            val list = ArrayList<String>()
            //            list.add("वधू-वर कुंडली मिलन + ग्रह मिलन + गुण मिलन")
            //            list.add("कुंडली दोष असल्यास परिहार")
            //            list.add("मुहूर्त")
            //            list.add("महिना कसा जाईल")
            //            list.add("१-प्रश्न")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    binding.etServiceType?.setText(list[position])
                    strTypeToShow = list[position]
                    val keyFromValue = typeMap?.let { it1 -> Utils.getKeyFromNestedHashMap(it1, strTypeToShow!!) }
                    when (keyFromValue) {
                        TYPE_KUNDLI_MILAN -> {
                            strType = TYPE_KUNDLI_MILAN
                            binding.tilGirlFullName?.visible()
                            binding.tilGirlDob?.visible()
                            binding.llGirlBirthTime?.visible()
                            binding.tilGirlBirthTime?.visible()
                            binding.tilGirlBirthPlace?.visible()
                        }
                        TYPE_KUNDLI_DOSH -> {
                            strType = TYPE_KUNDLI_DOSH
                            binding.tilGirlFullName?.gone()
                            binding.tilGirlDob?.gone()
                            binding.llGirlBirthTime?.gone()
                            binding.tilGirlBirthTime?.gone()
                            binding.tilGirlBirthPlace?.gone()
                        }
                        TYPE_MUHURT -> {
                            strType = TYPE_MUHURT
                            binding.tilGirlFullName?.gone()
                            binding.tilGirlDob?.gone()
                            binding.llGirlBirthTime?.gone()
                            binding.tilGirlBirthTime?.gone()
                            binding.tilGirlBirthPlace?.gone()
                        }
                        TYPE_MONTH_ASTRO -> {
                            strType = TYPE_MONTH_ASTRO
                            binding.tilGirlFullName?.gone()
                            binding.tilGirlDob?.gone()
                            binding.llGirlBirthTime?.gone()
                            binding.tilGirlBirthTime?.gone()
                            binding.tilGirlBirthPlace?.gone()
                        }
                        TYPE_ONE_QUESTION -> {
                            strType = TYPE_ONE_QUESTION
                            binding.tilGirlFullName?.gone()
                            binding.tilGirlDob?.gone()
                            binding.llGirlBirthTime?.gone()
                            binding.tilGirlBirthTime?.gone()
                            binding.tilGirlBirthPlace?.gone()
                        }
                    }
                    selectedTypeAmount = extractAmountFromType(strTypeToShow!!) ?: ""
                    binding.btnPay.visible()
                    binding.tvPayNote.visible()
                }
            }.show()
        }

        binding.btnSubmit?.setOnClickListener {
            val strServiceType = binding.etServiceType.text.toString()
            val strFullName = binding.etFullName.text.toString()
            val strDob = binding.etDob.text.toString()
            val strBirthTime = binding.etBirthTime.text.toString()
            val strBirthTimeAmPm = binding.etBirthTimeAmPm.text.toString()
            val strBirthPlace = binding.etBirthPlace.text.toString()
            val strGirlFullName = binding.etGirlFullName.text.toString()
            val strGirlDob = binding.etGirlDob.text.toString()
            val strGirlBirthTime = binding.etGirlBirthTime.text.toString()
            val strGirlBirthTimeAmPm = binding.etGirlBirthTimeAmPm.text.toString()
            val strGirlBirthPlace = binding.etGirlBirthPlace.text.toString()
            val strQuestion = binding.etQuestion.text.toString()
            if (TextUtils.isEmpty(strServiceType)) {
                binding.tilServiceType?.error = "कोणती सुविधा पाहिजे"
                binding.etServiceType?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strFullName)) {
                binding.tilFullName?.error = "व्यक्तीचे संपूर्ण नाव"
                binding.etFullName?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strDob)) {
                binding.tilDob?.error = "व्यक्तीची जन्मतारीख"
                binding.etDob?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strBirthTime)) {
                binding.tilBirthTime?.error = "जन्म वेळ"
                binding.etBirthTime?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strBirthTimeAmPm)) {
                binding.tilBirthTimeAmPm?.error = binding.tilBirthTimeAmPm.hint.toString()
                binding.etBirthTimeAmPm?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strBirthPlace)) {
                binding.tilBirthPlace?.error = "जन्म ठिकाण – गाव तालुका जिल्हा सहित"
                binding.etBirthPlace?.requestFocus()
                return@setOnClickListener
            }
            if (strType == TYPE_KUNDLI_MILAN && TextUtils.isEmpty(strGirlFullName)) {
                binding.tilGirlFullName?.error = "नाव वधूचे / मुलीचे नाव"
                binding.etGirlFullName?.requestFocus()
                return@setOnClickListener
            }
            if (strType == TYPE_KUNDLI_MILAN && TextUtils.isEmpty(strGirlDob)) {
                binding.tilGirlDob?.error = "वधूची / मुलीची जन्मतारीख"
                binding.etGirlDob?.requestFocus()
                return@setOnClickListener
            }
            if (strType == TYPE_KUNDLI_MILAN && TextUtils.isEmpty(strGirlBirthTime)) {
                binding.tilGirlBirthTime?.error = "वधूची / मुलीची जन्म वेळ"
                binding.etGirlBirthTime?.requestFocus()
                return@setOnClickListener
            }
            if (strType == TYPE_KUNDLI_MILAN && TextUtils.isEmpty(strGirlBirthTimeAmPm)) {
                binding.tilGirlBirthTimeAmPm?.error = binding.tilGirlBirthTimeAmPm?.hint.toString()
                binding.etGirlBirthTimeAmPm?.requestFocus()
                return@setOnClickListener
            }
            if (strType == TYPE_KUNDLI_MILAN && TextUtils.isEmpty(strGirlBirthPlace)) {
                binding.tilGirlBirthPlace?.error = "वधूचे / मुलीचे जन्म ठिकाण – गाव तालुका जिल्हा सहित"
                binding.etGirlBirthPlace?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strQuestion)) {
                binding.tilQuestion?.error = "प्रश्न (३०० शब्दात)"
                binding.etQuestion?.requestFocus()
                return@setOnClickListener
            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = "पेमेंट स्कीनशॉट अपलोड करावा"
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            }
            uploadPaymentScreenshot(paymentScreenshotUri!!)
        }
        binding.etPaymentScreenshot?.setOnClickListener {
            imageType = 1
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
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success") //paymentScreenshotUri.toString())
                            }
                        }
                    }
                }
            }
        })
    }

    private fun uploadPaymentScreenshot(fileUri: Uri) {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("jyotish_shastra").child(paymentScreenshotFileName)
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
//        billMap["serviceType"] = binding.etServiceType.text.toString()
        billMap["type"] = strType
        billMap["typeToShow"] = strTypeToShow
        billMap["fullName"] = binding.etFullName.text.toString()
        billMap["dob"] = strDob
        billMap["birthtime"] = binding.etBirthTime.text.toString()
        billMap["birthtimeAmPm"] = binding.etBirthTimeAmPm.text.toString()
        billMap["birthplace"] = binding.etBirthPlace.text.toString()
        billMap["girlFullName"] = binding.etGirlFullName.text.toString()
        billMap["girlDob"] = strGirlDob
        billMap["girlBirthTime"] = binding.etGirlBirthTime.text.toString()
        billMap["girlBirthTime"] = binding.etGirlBirthTimeAmPm.text.toString()
        billMap["girlBirthPlace"] = binding.etGirlBirthPlace.text.toString()
        billMap["question"] = binding.etQuestion.text.toString()
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("jyotish_shastra").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/jyotish_shastra/$pushKey"] = billMap
        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Jyotish Shastra Prashna added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_jyotish_shastra")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Jyotish Shastra Prashna added by $name")
        }
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/jyotish_shastra/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd?.isShowing == true) cpd?.dismiss() //        toast("Request submitted successfully")
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
        val TYPE_KUNDLI_MILAN = "TYPE_KUNDLI_MILAN"
        val TYPE_KUNDLI_DOSH = "TYPE_KUNDLI_DOSH"
        val TYPE_MUHURT = "TYPE_MUHURT"
        val TYPE_MONTH_ASTRO = "TYPE_MONTH_ASTRO"
        val TYPE_ONE_QUESTION = "TYPE_ONE_QUESTION" //        वधू-वर कुंडली मिलन + ग्रह मिलन + गुण मिलन")
        //        //            list.add("कुंडली दोष असल्यास परिहार")
        //        //            list.add("मुहूर्त")
        //        //            list.add("महिना कसा जाईल")
        //        //            list.add("१-प्रश्न"
    }
}
