package rahul.jagtap.dmas.user

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.PayByUpiActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityPanCardBinding
import rahul.jagtap.dmas.extensions.copyToClipboard
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.longToast
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.text.SimpleDateFormat
import java.util.*

class PanCardActivity : BaseActivity() {
//    private var age: Int = 19
//    private var oldPanCardPhotoFileName: String = ""
//    private var oldPanCardPhotoDownloadUrl: String = ""
//    private var passportPhotoFileName: String = ""
//    private var passportPhotoDownloadUrl: String = ""
//    private var aadharFrontPhotoFileName: String = ""
//    private var aadharFrontPhotoDownloadUrl: String = ""
//    private var aadharBackPhotoFileName: String = ""
//    private var aadharBackPhotoDownloadUrl: String = ""
//    private var aadharFatherFrontPhotoFileName: String = ""
//    private var aadharFatherFrontPhotoDownloadUrl: String = ""
//    private var aadharFatherBackPhotoFileName: String = ""
//    private var aadharFatherBackPhotoDownloadUrl: String = ""
//    private var signFileName: String = ""
//    private var signDownloadUrl: String = ""
//    private var paymentScreenshotFileName: String = ""
//    private var paymentScreenshotDownloadUrl: String = ""
//    private var marriageCertFileName: String = ""
//    private var marriageCertDownloadUrl: String = ""
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private val TAG = PanCardActivity::class.java.simpleName
    var imageType = -1 // 1 - old pan card, 2 - passport, 3 - aadhar f, 4 - aadhar b, 5 - sign, 6 - payment screenshot
//    var oldPanCardPhotoUri: Uri? = null
//    var passportPhotoUri: Uri? = null
//    var aadharFrontPhotoUri: Uri? = null
//    var aadharBackPhotoUri: Uri? = null
//    var aadharFatherFrontPhotoUri: Uri? = null
//    var aadharFatherBackPhotoUri: Uri? = null
//    var singPhotoUri: Uri? = null
//    var paymentScreenshotUri: Uri? = null
//    var marriageCertUri: Uri? = null
    var cpd: ProgressDialog? = null
    private var attachment1FileName: String = ""
    private var attachment1DownloadUrl: String = ""
    private var attachment2FileName: String = ""
    private var attachment2DownloadUrl: String = ""
    private var attachment3FileName: String = ""
    private var attachment3DownloadUrl: String = ""
    private var attachment4FileName: String = ""
    private var attachment4DownloadUrl: String = ""
    private var attachment5FileName: String = ""
    private var attachment5DownloadUrl: String = ""
    private var attachment6FileName: String = ""
    private var attachment6DownloadUrl: String = ""
    private var attachment7FileName: String = ""
    private var attachment7DownloadUrl: String = ""
    private var attachment8FileName: String = ""
    private var attachment8DownloadUrl: String = ""
    private var attachment9FileName: String = ""
    private var attachment9DownloadUrl: String = ""
    private var attachment10FileName: String = ""
    private var attachment10DownloadUrl: String = ""
    var attachment1Uri: Uri? = null
    var attachment2Uri: Uri? = null
    var attachment3Uri: Uri? = null
    var attachment4Uri: Uri? = null
    var attachment5Uri: Uri? = null
    var attachment6Uri: Uri? = null
    var attachment7Uri: Uri? = null
    var attachment8Uri: Uri? = null
    var attachment9Uri: Uri? = null
    var attachment10Uri: Uri? = null
    var strDatebox = ""
    private var strDate: String? = ""
//    private var strPanType: String? = ""
    private var strType: String? = ""
    private var strTypeToShow: String = ""
    private var selectedTypeAmount = ""
    private lateinit var easyImage: EasyImage
    lateinit var binding: ActivityPanCardBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanCardBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_pan_card)
        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid
        username = app?.preferences?.loggedInUser?.username
        name = app?.preferences?.loggedInUser?.name
        easyImage = EasyImage.Builder(this).setChooserType(ChooserType.CAMERA_AND_GALLERY).allowMultiple(false) // Setting to true will cause taken pictures to show up in the device gallery, DEFAULT false
            .setCopyImagesToPublicGalleryFolder(false).build() //        binding.tvInstructions.fromHtml(intent.getStringExtra("suchna") ?: "")
        binding.btnPay.setOnClickListener {
            payUsingUPI(selectedTypeAmount)
        }
        binding.btnSuchna.setOnClickListener {
            startActivity(Intent(mContext, ViewSuchnaActivity::class.java).putExtra("suchna", intent.getStringExtra("suchna")
                ?: "").putExtra("title", getString(R.string.txt_pan_card)))
        }

        binding.btnSubmit?.setOnClickListener {
//            val strApplicantName = binding.etApplicantName.text.toString()
//            val strType = binding.etType.text.toString()
//            val strFatherName = binding.etFatherName.text.toString()
//            val strDob = binding.etDob.text.toString()
//            val strMobileNo = binding.etMobileNo.text.toString()
//            if (TextUtils.isEmpty(strType)) {
//                binding.etType?.error = "प्रकार निवडा"
//                binding.etType?.requestFocus()
//                return@setOnClickListener
//            }
//            if (TextUtils.isEmpty(strApplicantName)) {
//                binding.etApplicantName?.error = "कस्टमरचे नाव टाका"
//                binding.etApplicantName?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etFatherName.visibility == View.VISIBLE && TextUtils.isEmpty(strFatherName)) {
//                binding.etFatherName?.error = "वडिलांचे पूर्ण नाव टाका"
//                binding.etFatherName?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etDob.visibility == View.VISIBLE && TextUtils.isEmpty(strDob)) {
//                binding.etDob?.error = "जन्मतारीख निवडा"
//                binding.etDob?.requestFocus()
//                return@setOnClickListener
//            }
//            if (TextUtils.isEmpty(strMobileNo)) {
//                binding.etMobileNo?.error = "मोबाईल नंबर टाका"
//                binding.etMobileNo?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etPassportPhoto?.visibility == View.VISIBLE && passportPhotoUri == null) {
//                binding.etPassportPhoto?.error = "पासपोर्ट साइज फोटो निवडा"
//                binding.etPassportPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (strPanType == TYPE_DURUSTI && oldPanCardPhotoUri == null) {
//                binding.etOldPanCardPhoto?.error = "जुने पॅन कार्ड फोटो निवडा"
//                binding.etOldPanCardPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (aadharFrontPhotoUri == null) {
//                binding.etAadharFrontPhoto?.error = "आधार कार्ड पुढील फोटो"
//                binding.etAadharFrontPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (aadharBackPhotoUri == null) {
//                binding.etAadharBackPhoto?.error = "आधार कार्ड मागील फोटो"
//                binding.etAadharBackPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etDob.visibility == View.VISIBLE && age < 18 && aadharFatherFrontPhotoUri == null) {
//                binding.etAadharFatherFrontPhoto?.error = "वडिलांचे आधार कार्ड पुढील फोटो"
//                binding.etAadharFatherFrontPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etDob.visibility == View.VISIBLE && age < 18 && aadharFatherBackPhotoUri == null) {
//                binding.etAadharFatherBackPhoto?.error = "वडिलांचे आधार कार्ड मागील फोटो"
//                binding.etAadharFatherBackPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etSignPhoto?.visibility == View.VISIBLE && singPhotoUri == null) {
//                binding.etSignPhoto?.error = "स्वाक्षरी फोटो निवडा"
//                binding.etSignPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etMarriageCert?.visibility == View.VISIBLE && marriageCertUri == null) {
//                binding.etMarriageCert?.error = binding.etMarriageCert.hint.toString()
//                binding.etMarriageCert?.requestFocus()
//                return@setOnClickListener
//            }
//            if (paymentScreenshotUri == null) {
//                binding.etPaymentScreenshot?.error = "पेमेंट स्क्रीनशॉट निवडा"
//                binding.etPaymentScreenshot?.requestFocus()
//                return@setOnClickListener
//            }
//            uploadOldPanCardPhoto()
            val strServiceType = binding.etServiceType.text.toString()
            val strEdittext1 = binding.edittext1.text.toString()
            val strEdittext2 = binding.edittext2.text.toString()
            val strEdittext3 = binding.edittext3.text.toString()
            val strEdittext4 = binding.edittext4.text.toString()
            val strEdittext5 = binding.edittext5.text.toString()
            val strEdittextDate1 = binding.edittextDate1.text.toString()
            // TextInputLayout errors persist until cleared (unlike the old widget), so reset them each attempt.
            listOf(
                binding.tilServiceType, binding.tilEdittext1, binding.tilEdittext2,
                binding.tilEdittext3, binding.tilEdittext4, binding.tilEdittext5,
                binding.tilEdittextDate1, binding.tilEdittextPhoto1, binding.tilEdittextPhoto2,
                binding.tilEdittextPhoto3, binding.tilEdittextPhoto4, binding.tilEdittextPhoto5,
                binding.tilEdittextPhoto6, binding.tilEdittextPhoto7, binding.tilEdittextPhoto8,
                binding.tilEdittextPhoto9, binding.tilEdittextPhoto10
            ).forEach { it.error = null }
            if (TextUtils.isEmpty(strServiceType)) {
                binding.tilServiceType.error = binding.tilServiceType.hint.toString()
                binding.etServiceType.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittext1.visibility == View.VISIBLE && TextUtils.isEmpty(strEdittext1)) {
                binding.tilEdittext1.error = binding.tilEdittext1.hint.toString()
                binding.edittext1.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittext2.visibility == View.VISIBLE && TextUtils.isEmpty(strEdittext2)) {
                binding.tilEdittext2.error = binding.tilEdittext2.hint.toString()
                binding.edittext2.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittext3.visibility == View.VISIBLE && TextUtils.isEmpty(strEdittext3)) {
                binding.tilEdittext3.error = binding.tilEdittext3.hint.toString()
                binding.edittext3.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittext4.visibility == View.VISIBLE && TextUtils.isEmpty(strEdittext4)) {
                binding.tilEdittext4.error = binding.tilEdittext4.hint.toString()
                binding.edittext4.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittext5.visibility == View.VISIBLE && TextUtils.isEmpty(strEdittext5)) {
                binding.tilEdittext5.error = binding.tilEdittext5.hint.toString()
                binding.edittext5.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextDate1.visibility == View.VISIBLE && TextUtils.isEmpty(strEdittextDate1)) {
                binding.tilEdittextDate1.error = binding.tilEdittextDate1.hint.toString()
                binding.edittextDate1.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto1.visibility == View.VISIBLE && attachment1Uri == null) {
                binding.tilEdittextPhoto1.error = binding.tilEdittextPhoto1.hint.toString()
                binding.edittextPhoto1.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto2.visibility == View.VISIBLE && attachment2Uri == null){
                binding.tilEdittextPhoto2.error = binding.tilEdittextPhoto2.hint.toString()
                binding.edittextPhoto2.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto3.visibility == View.VISIBLE && attachment3Uri == null){
                binding.tilEdittextPhoto3.error = binding.tilEdittextPhoto3.hint.toString()
                binding.edittextPhoto3.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto4.visibility == View.VISIBLE && attachment4Uri == null){
                binding.tilEdittextPhoto4.error = binding.tilEdittextPhoto4.hint.toString()
                binding.edittextPhoto4.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto5.visibility == View.VISIBLE && attachment5Uri == null){
                binding.tilEdittextPhoto5.error = binding.tilEdittextPhoto5.hint.toString()
                binding.edittextPhoto5.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto6.visibility == View.VISIBLE && attachment6Uri == null){
                binding.tilEdittextPhoto6.error = binding.tilEdittextPhoto6.hint.toString()
                binding.edittextPhoto6.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto7.visibility == View.VISIBLE && attachment7Uri == null){
                binding.tilEdittextPhoto7.error = binding.tilEdittextPhoto7.hint.toString()
                binding.edittextPhoto7.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto8.visibility == View.VISIBLE && attachment8Uri == null){
                binding.tilEdittextPhoto8.error = binding.tilEdittextPhoto8.hint.toString()
                binding.edittextPhoto8.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto9.visibility == View.VISIBLE && attachment9Uri == null){
                binding.tilEdittextPhoto9.error = binding.tilEdittextPhoto9.hint.toString()
                binding.edittextPhoto9.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto10.visibility == View.VISIBLE && attachment10Uri == null){
                binding.tilEdittextPhoto10.error = binding.tilEdittextPhoto10.hint.toString()
                binding.edittextPhoto10.requestFocus()
                return@setOnClickListener
            }
            val uriMap = mapOf(
                "attachment1" to attachment1Uri,
                "attachment2" to attachment2Uri,
                "attachment3" to attachment3Uri,
                "attachment4" to attachment4Uri,
                "attachment5" to attachment5Uri,
                "attachment6" to attachment6Uri,
                "attachment7" to attachment7Uri,
                "attachment8" to attachment8Uri,
                "attachment9" to attachment9Uri,
                "attachment10" to attachment10Uri,
            )
            uploadAllImagesAndCreateRecord(uriMap)
        }
//        binding.btnPayByUpiId?.setOnClickListener {
//            startActivity(Intent(mContext, PayByUpiActivity::class.java))
//        }
//        binding.etDob?.setOnClickListener {
//            val mMaxDate = Calendar.getInstance() //            var mMaxYear = mMaxDate[Calendar.YEAR] //            mMaxYear -= 18 //            mMaxDate[Calendar.YEAR] = mMaxYear
//            val datePickerDialog = DatePickerDialog(mContext!!, { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
//                val date = "" + dayOfMonth + "/" + (month + 1) + "/" + year
//                strDate = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
//                binding.etDob.setText(date)
//                val birthdateStr = "${String.format("%02d", dayOfMonth)}-${String.format("%02d", month + 1)}-$year"
//                val df = SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH)
//                val birthdate = df.parse(birthdateStr)
//                age = calculateAge(birthdate)
//                if (age < 18) {
//                    binding.etAadharFatherFrontPhoto?.visible()
//                    binding.etAadharFatherBackPhoto?.visible()
//                } else {
//                    binding.etAadharFatherFrontPhoto?.gone()
//                    binding.etAadharFatherBackPhoto?.gone()
//                }
//            }, mMaxDate[Calendar.YEAR], mMaxDate[Calendar.MONTH], mMaxDate[Calendar.DAY_OF_MONTH])
//            datePickerDialog.show()
//        }
//        typeMap = intent.extras?.getSerializable("hashMap") as HashMap<String, HashMap<String, String>>
//        val list = ArrayList<String>()
//        typeMap?.forEach {
//            if (it.value["type_title"] != "0") list.add(it.value["type_title"].toString())
//        }
        binding.edittextDate1.setOnClickListener {
            val mMaxDate = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(mContext!!, { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val date = "" + dayOfMonth + "/" + (month + 1) + "/" + year
                strDatebox = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
                binding.edittextDate1.setText(date)
            }, mMaxDate[Calendar.YEAR], mMaxDate[Calendar.MONTH], mMaxDate[Calendar.DAY_OF_MONTH])
            datePickerDialog.show()
        }
        typeMap = intent.extras?.getSerializable("hashMap") as HashMap<String, HashMap<String, String>>
        val typeHashMapList = ArrayList<HashMap<String, String>>()
        val typeList = ArrayList<String>()
        typeMap?.forEach {
            if (it.value["type_title"] != "0") {
                typeHashMapList.add(it.value)
                typeList.add(it.value["type_title"].toString())
            }
        }
        if (typeList.size == 0) {
            binding.tilServiceType.gone()
            binding.tilEdittext1.gone()
            binding.tilEdittext2.gone()
            binding.tilEdittext3.gone()
            binding.tilEdittext4.gone()
            binding.tilEdittext5.gone()
            binding.tilEdittextDate1.gone()
            binding.tilEdittextPhoto1.gone()
            binding.tilEdittextPhoto2.gone()
            binding.tilEdittextPhoto3.gone()
            binding.tilEdittextPhoto4.gone()
            binding.tilEdittextPhoto5.gone()
            binding.tilEdittextPhoto6.gone()
            binding.tilEdittextPhoto7.gone()
            binding.tilEdittextPhoto8.gone()
            binding.tilEdittextPhoto9.gone()
            binding.tilEdittextPhoto10.gone()
//            binding.etType.gone()
//            binding.etMobileNo.gone()
//            binding.etPassportPhoto.gone()
//            binding.etOldPanCardPhoto.gone()
//            binding.etAadharFrontPhoto.gone()
//            binding.etAadharBackPhoto.gone()
//            binding.etAadharFatherFrontPhoto.gone()
//            binding.etAadharFatherBackPhoto.gone()
//            binding.etSignPhoto.gone()
//            binding.etMarriageCert.gone()
//            binding.etPaymentScreenshot.gone()
            binding.btnSubmit.gone()
        }
        /** Selects the subtype at [position] (shared by the picker dialog and grid deep-links). */
        fun applyType(position: Int) {

                    binding.etServiceType.setText(typeList[position])
                    strTypeToShow = typeList[position]
                    val selectedTypeHashMap = typeHashMapList[position]
                    val keyFromValue = typeMap?.let { it1 -> Utils.getKeyFromNestedHashMap(it1, strTypeToShow!!) }
                    strType = keyFromValue
                    binding.tilEdittext1.visibility = if (selectedTypeHashMap["textbox1"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittext1.hint = selectedTypeHashMap["textbox1"]
                    binding.tilEdittext2.visibility = if (selectedTypeHashMap["textbox2"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittext2.hint = selectedTypeHashMap["textbox2"]
                    binding.tilEdittext3.visibility = if (selectedTypeHashMap["textbox3"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittext3.hint = selectedTypeHashMap["textbox3"]
                    binding.tilEdittext4.visibility = if (selectedTypeHashMap["textbox4"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittext4.hint = selectedTypeHashMap["textbox4"]
                    binding.tilEdittext5.visibility = if (selectedTypeHashMap["textbox5"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittext5.hint = selectedTypeHashMap["textbox5"]
                    binding.tilEdittextDate1.visibility = if (selectedTypeHashMap["datebox"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittextDate1.hint = selectedTypeHashMap["datebox"]
                    binding.tilEdittextPhoto1.visibility = if (selectedTypeHashMap["attachment1"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittextPhoto1.hint = selectedTypeHashMap["attachment1"]
                    binding.tilEdittextPhoto2.visibility = if (selectedTypeHashMap["attachment2"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittextPhoto2.hint = selectedTypeHashMap["attachment2"]
                    binding.tilEdittextPhoto3.visibility = if (selectedTypeHashMap["attachment3"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittextPhoto3.hint = selectedTypeHashMap["attachment3"]
                    binding.tilEdittextPhoto4.visibility = if (selectedTypeHashMap["attachment4"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittextPhoto4.hint = selectedTypeHashMap["attachment4"]
                    binding.tilEdittextPhoto5.visibility = if (selectedTypeHashMap["attachment5"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittextPhoto5.hint = selectedTypeHashMap["attachment5"]
                    binding.tilEdittextPhoto6.visibility = if (selectedTypeHashMap["attachment6"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittextPhoto6.hint = selectedTypeHashMap["attachment6"]
                    binding.tilEdittextPhoto7.visibility = if (selectedTypeHashMap["attachment7"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittextPhoto7.hint = selectedTypeHashMap["attachment7"]
                    binding.tilEdittextPhoto8.visibility = if (selectedTypeHashMap["attachment8"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittextPhoto8.hint = selectedTypeHashMap["attachment8"]
                    binding.tilEdittextPhoto9.visibility = if (selectedTypeHashMap["attachment9"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittextPhoto9.hint = selectedTypeHashMap["attachment9"]
                    binding.tilEdittextPhoto10.visibility = if (selectedTypeHashMap["attachment10"] == "0") View.GONE else View.VISIBLE
                    binding.tilEdittextPhoto10.hint = selectedTypeHashMap["attachment10"]
//                    val keyFromValue = typeMap?.let { it1 -> Utils.getKeyFromNestedHashMap(it1, strTypeToShow!!) }
//                    when (keyFromValue) {
//                        TYPE_NAVIN -> {
//                            strPanType = TYPE_NAVIN
//                            binding.etOldPanCardPhoto?.gone()
//                            binding.etSignPhoto?.visible()
//                            binding.etPassportPhoto?.visible()
//                            binding.etFatherName.visible()
//                            binding.etDob.visible()
//                            binding.etMarriageCert.gone()
//                        }
//                        TYPE_DURUSTI -> {
//                            strPanType = TYPE_DURUSTI
//                            binding.etOldPanCardPhoto?.visible()
//                            binding.etSignPhoto?.visible()
//                            binding.etPassportPhoto?.visible()
//                            binding.etFatherName.visible()
//                            binding.etDob.gone()
//                            binding.etMarriageCert.gone()
//                        }
//                        TYPE_GET_LOST_PAN -> {
//                            strPanType = TYPE_GET_LOST_PAN
//                            binding.etOldPanCardPhoto?.visible()
//                            binding.etSignPhoto?.visible()
//                            binding.etPassportPhoto?.visible()
//                            binding.etFatherName.visible()
//                            binding.etDob.gone()
//                            binding.etMarriageCert.gone()
//                        }
//                        TYPE_FIND_LOST_PAN -> { // हरवलेले पॅन कार्ड चा फक्त नंबर शोधणे
//                            strPanType = TYPE_FIND_LOST_PAN
//                            binding.etFatherName.gone()
//                            binding.etDob.visible()
//                            binding.etOldPanCardPhoto?.gone()
//                            binding.etSignPhoto?.gone()
//                            binding.etPassportPhoto?.gone()
//                            binding.etMarriageCert.gone()
//                        }
//                        TYPE_MARRIED_WOMAN_DURUSTI -> {
//                            strPanType = TYPE_MARRIED_WOMAN_DURUSTI
//                            binding.etOldPanCardPhoto?.visible()
//                            binding.etSignPhoto?.visible()
//                            binding.etPassportPhoto?.visible()
//                            binding.etFatherName.visible()
//                            binding.etDob.gone()
//                            binding.etMarriageCert.visible()
//                        }
//                        TYPE_TYPE_ONE -> {
//                            strPanType = TYPE_TYPE_ONE
//                            binding.etOldPanCardPhoto?.visible()
//                            binding.etSignPhoto?.visible()
//                            binding.etPassportPhoto?.visible()
//                            binding.etFatherName.visible()
//                            binding.etDob.gone()
//                            binding.etMarriageCert.gone()
//                        }
//                        TYPE_URGENT_NAVIN -> { // 3 अर्जंट नवीन पॅन (आधार ओटीपी येईल, आधार चा फोटो येईल) 3 तासात प्रिंट
//                            strPanType = TYPE_URGENT_NAVIN
//                            binding.etOldPanCardPhoto?.gone()
//                            binding.etSignPhoto?.gone()
//                            binding.etPassportPhoto?.gone()
//                            binding.etFatherName.visible()
//                            binding.etDob.gone()
//                            binding.etMarriageCert.gone()
//                        }
//                    }
//                    selectedTypeAmount = extractAmountFromType(strTypeToShow) ?: ""
                    binding.btnPay.visible()
                    binding.tvPayNote.visible()
                
        }
        val preselectKey = intent.getStringExtra(EsuvidhaServiceRegistry.EXTRA_PRESELECT_SUBTYPE)
        if (!preselectKey.isNullOrEmpty()) {
            val preTitle = typeMap?.get(preselectKey)?.get("type_title")
            val preIdx = if (!preTitle.isNullOrEmpty()) typeList.indexOf(preTitle) else -1
            if (preIdx >= 0) applyType(preIdx)
        }
        binding.etServiceType?.setOnClickListener {
//            val list = ArrayList<String>()
//            list.add("नवीन पॅन कार्ड (सही, नवीन फोटो सहित)")
//            list.add("पॅन दुरुस्ती")
//            list.add("हरवलेले पॅन मागविणे (दुरुस्ती नाही)")
//            list.add("अर्जंट नवीन पॅन (आधार ओटीपी येईल, आधार चा फोटो येईल) 3 तासात प्रिंट")
//            list.add("हरवलेले पॅन कार्ड चा फक्त नंबर शोधणे")
//            list.add("लग्न झालेल्या स्त्री चे पँण कार्ड दुरुस्ती")
//            list.add("1")
            MaterialDialog.Builder(mContext!!).items(typeList).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    applyType(position)
                }
            }.show()
        }
        binding.edittextPhoto1.setOnClickListener { choosePhotoWithPermissions(1) }
        binding.edittextPhoto2.setOnClickListener { choosePhotoWithPermissions(2) }
        binding.edittextPhoto3.setOnClickListener { choosePhotoWithPermissions(3) }
        binding.edittextPhoto4.setOnClickListener { choosePhotoWithPermissions(4) }
        binding.edittextPhoto5.setOnClickListener { choosePhotoWithPermissions(5) }
        binding.edittextPhoto6.setOnClickListener { choosePhotoWithPermissions(6) }
        binding.edittextPhoto7.setOnClickListener { choosePhotoWithPermissions(7) }
        binding.edittextPhoto8.setOnClickListener { choosePhotoWithPermissions(8) }
        binding.edittextPhoto9.setOnClickListener { choosePhotoWithPermissions(9) }
        binding.edittextPhoto10.setOnClickListener { choosePhotoWithPermissions(10) }
//        binding.etOldPanCardPhoto?.setOnClickListener {
//            imageType = 1
//            choosePhotoWithPermissions()
//        }
//        binding.etPassportPhoto?.setOnClickListener {
//            imageType = 2
//            choosePhotoWithPermissions()
//        }
//        binding.etAadharFrontPhoto?.setOnClickListener {
//            imageType = 3
//            choosePhotoWithPermissions()
//        }
//        binding.etAadharBackPhoto?.setOnClickListener {
//            imageType = 4
//            choosePhotoWithPermissions()
//        }
//        binding.etAadharFatherFrontPhoto?.setOnClickListener {
//            imageType = 5
//            choosePhotoWithPermissions()
//        }
//        binding.etAadharFatherBackPhoto?.setOnClickListener {
//            imageType = 6
//            choosePhotoWithPermissions()
//        }
//        binding.etSignPhoto?.setOnClickListener {
//            imageType = 7
//            choosePhotoWithPermissions()
//        }
//        binding.etPaymentScreenshot?.setOnClickListener {
//            imageType = 8
//            choosePhotoWithPermissions()
//        }
//        binding.etMarriageCert?.setOnClickListener {
//            imageType = 9
//            choosePhotoWithPermissions()
//        }
        binding.tvPayNote?.setText(spanText, TextView.BufferType.SPANNABLE)
        binding.tvPayNote?.movementMethod = LinkMovementMethod.getInstance()
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
            ) {
                lifecycleScope.launch {
                    val compressedImageFile = mContext?.let { Compressor.compress(it, imageFiles[0].file) }
                    Log.e(TAG, "${compressedImageFile?.path}")
                    compressedImageFile?.let {
                        when (imageType) {
//                            1 -> {
//                                oldPanCardPhotoUri = Uri.fromFile(it)
//                                binding.etOldPanCardPhoto?.setText("Success") //oldPanCardPhotoUri.toString())
//                            }
//
//                            2 -> {
//                                passportPhotoUri = Uri.fromFile(it)
//                                binding.etPassportPhoto?.setText("Success") //passportPhotoUri.toString())
//                            }
//
//                            3 -> {
//                                aadharFrontPhotoUri = Uri.fromFile(it)
//                                binding.etAadharFrontPhoto?.setText("Success") //aadharFrontPhotoUri.toString())
//                            }
//
//                            4 -> {
//                                aadharBackPhotoUri = Uri.fromFile(it)
//                                binding.etAadharBackPhoto?.setText("Success") //aadharBackPhotoUri.toString())
//                            }
//
//                            5 -> {
//                                aadharFatherFrontPhotoUri = Uri.fromFile(it)
//                                binding.etAadharFatherFrontPhoto?.setText("Success") //aadharFatherFrontPhotoUri.toString())
//                            }
//
//                            6 -> {
//                                aadharFatherBackPhotoUri = Uri.fromFile(it)
//                                binding.etAadharFatherBackPhoto?.setText("Success") //aadharFatherBackPhotoUri.toString())
//                            }
//
//                            7 -> {
//                                singPhotoUri = Uri.fromFile(it)
//                                binding.etSignPhoto?.setText("Success") //singPhotoUri.toString())
//                            }
//
//                            8 -> {
//                                paymentScreenshotUri = Uri.fromFile(it)
//                                binding.etPaymentScreenshot?.setText("Success") //paymentScreenshotUri.toString())
//                            }
//
//                            9 -> {
//                                marriageCertUri = Uri.fromFile(it)
//                                binding.etMarriageCert?.setText("Success") //paymentScreenshotUri.toString())
//                            }
                            1 -> {
                                attachment1Uri = Uri.fromFile(it)
                                binding.edittextPhoto1?.setText("Success")//aadharFrontPhotoUri.toString())
                            }

                            2 -> {
                                attachment2Uri = Uri.fromFile(it)
                                binding.edittextPhoto2?.setText("Success")//aadharBackPhotoUri.toString())
                            }

                            3 -> {
                                attachment3Uri = Uri.fromFile(it)
                                binding.edittextPhoto3?.setText("Success")//panPhotoUri.toString())
                            }

                            4 -> {
                                attachment4Uri = Uri.fromFile(it)
                                binding.edittextPhoto4?.setText("Success")//passbookPhotoUri.toString())
                            }

                            5 -> {
                                attachment5Uri = Uri.fromFile(it)
                                binding.edittextPhoto5?.setText("Success")//rationFrontPhotoUri.toString())
                            }

                            6 -> {
                                attachment6Uri = Uri.fromFile(it)
                                binding.edittextPhoto6?.setText("Success")//rationBackPhotoUri.toString())
                            }

                            7 -> {
                                attachment7Uri = Uri.fromFile(it)
                                binding.edittextPhoto7?.setText("Success")//signPhotoUri.toString())
                            }

                            8 -> {
                                attachment8Uri = Uri.fromFile(it)
                                binding.edittextPhoto8?.setText("Success")//passportPhotoUri.toString())
                            }

                            9 -> {
                                attachment9Uri = Uri.fromFile(it)
                                binding.edittextPhoto9?.setText("Success")//schoolLCUri.toString())
                            }

                            10 -> {
                                attachment10Uri = Uri.fromFile(it)
                                binding.edittextPhoto10?.setText("Success")//birthCertUri.toString())
                            }
                        }
                    }
                }
            }
        })
    }

//    private fun uploadOldPanCardPhoto() {
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        if (oldPanCardPhotoUri != null) {
//            oldPanCardPhotoFileName = "old_pan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//            val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("pan_cards").child(oldPanCardPhotoFileName)
//            filepath.putFile(oldPanCardPhotoUri!!).addOnSuccessListener {
//                try {
//                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                        oldPanCardPhotoDownloadUrl = uri.toString()
//                        if (binding.etPassportPhoto?.visibility == View.VISIBLE) passportPhotoUri?.let { it1 -> uploadPassportPhoto(it1) }
//                        else aadharFrontPhotoUri?.let { it1 -> uploadAadharFrontPhoto(it1) }
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
//            if (binding.etPassportPhoto?.visibility == View.VISIBLE) {
//                passportPhotoUri?.let { uploadPassportPhoto(it) }
//            } else {
//                aadharFrontPhotoUri?.let { it1 -> uploadAadharFrontPhoto(it1) }
//            }
//        }
//    }
//
//    private fun uploadPassportPhoto(fileUri: Uri) {
//        if (cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        passportPhotoFileName = "passport_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
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
//        }.addOnProgressListener { //displaying the upload progress
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
//        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
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
//        }.addOnProgressListener { //displaying the upload progress
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
//        aadharBackPhotoFileName = "aadhar_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("pan_cards").child(aadharBackPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    aadharBackPhotoDownloadUrl = uri.toString()
//                    if (binding.etDob.visibility == View.VISIBLE && age < 18) aadharFatherFrontPhotoUri?.let { it1 -> uploadAadharFatherFrontPhoto(it1) }
//                    else if (binding.etSignPhoto?.visibility == View.VISIBLE) singPhotoUri?.let { it1 -> uploadSignPhoto(it1) }
//                    else paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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
//
//    private fun uploadAadharFatherFrontPhoto(fileUri: Uri) {
//        if (cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        aadharFatherFrontPhotoFileName = "aadhar_father_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("pan_cards").child(aadharFatherFrontPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    aadharFatherFrontPhotoDownloadUrl = uri.toString()
//                    if (binding.etDob.visibility == View.VISIBLE && age < 18) aadharFatherBackPhotoUri?.let { it1 -> uploadAadharFatherBackPhoto(it1) }
//                    else if (binding.etSignPhoto?.visibility == View.VISIBLE) singPhotoUri?.let { it1 -> uploadSignPhoto(it1) }
//                    else paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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
//
//    private fun uploadAadharFatherBackPhoto(fileUri: Uri) {
//        if (cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        aadharFatherBackPhotoFileName = "aadhar_father_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("pan_cards").child(aadharFatherBackPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    aadharFatherBackPhotoDownloadUrl = uri.toString()
//                    if (binding.etSignPhoto?.visibility == View.VISIBLE) singPhotoUri?.let { it1 -> uploadSignPhoto(it1) }
//                    else paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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
//
//    private fun uploadSignPhoto(fileUri: Uri) {
//        if (cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        signFileName = "sign_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("pan_cards").child(signFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    signDownloadUrl = uri.toString()
//                    if (strPanType == TYPE_MARRIED_WOMAN_DURUSTI) {
//                        marriageCertUri?.let { it1 -> uploadMarriageCert(it1) }
//                    } else {
//                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
//                    }
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
//
//    private fun uploadMarriageCert(fileUri: Uri) {
//        if (cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        marriageCertFileName = "marriage_cert_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("pan_cards").child(marriageCertFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    marriageCertDownloadUrl = uri.toString()
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
//        }.addOnProgressListener { //displaying the upload progress
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
//        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
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
//        }.addOnProgressListener { //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }

    fun uploadAllImagesAndCreateRecord(uriMap: Map<String, Uri?>) {
        lifecycleScope.launch {
            try {
                if (cpd?.isShowing == true) cpd?.dismiss()
                cpd = ProgressDialog(mContext)
                cpd?.setCancelable(false)
                cpd?.show()

                // Filter out null or empty URIs
                val filteredMap = uriMap.filter { it.value != null && it.value.toString().isNotBlank() }

                val deferredResults = filteredMap.mapNotNull { (label, uri) ->
                    async {
                        val fileName = "${label}_${System.currentTimeMillis()}.jpg"
                        val downloadUrl = uri?.let { uploadImageToFirebase(it, fileName) }
                        label to (fileName to downloadUrl)
                    }
                }

                val resultList = deferredResults.awaitAll().toMap()

                // Assign filenames and URLs to your class-level variables
                attachment1FileName = resultList["attachment1"]?.first ?: ""
                attachment1DownloadUrl = resultList["attachment1"]?.second ?: ""
                attachment2FileName = resultList["attachment2"]?.first ?: ""
                attachment2DownloadUrl = resultList["attachment2"]?.second ?: ""
                attachment3FileName = resultList["attachment3"]?.first ?: ""
                attachment3DownloadUrl = resultList["attachment3"]?.second ?: ""
                attachment4FileName = resultList["attachment4"]?.first ?: ""
                attachment4DownloadUrl = resultList["attachment4"]?.second ?: ""
                attachment5FileName = resultList["attachment5"]?.first ?: ""
                attachment5DownloadUrl = resultList["attachment5"]?.second ?: ""
                attachment6FileName = resultList["attachment6"]?.first ?: ""
                attachment6DownloadUrl = resultList["attachment6"]?.second ?: ""
                attachment7FileName = resultList["attachment7"]?.first ?: ""
                attachment7DownloadUrl = resultList["attachment7"]?.second ?: ""
                attachment8FileName = resultList["attachment8"]?.first ?: ""
                attachment8DownloadUrl = resultList["attachment8"]?.second ?: ""
                attachment9FileName = resultList["attachment9"]?.first ?: ""
                attachment9DownloadUrl = resultList["attachment9"]?.second ?: ""
                attachment10FileName = resultList["attachment10"]?.first ?: ""
                attachment10DownloadUrl = resultList["attachment10"]?.second ?: ""

                createDbRecord()

            } catch (e: Exception) {
                e.printStackTrace()
                toast("Upload failed: ${e.message}")
            } finally {
                if (cpd?.isShowing == true) cpd?.dismiss()
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
//        billMap["applicantName"] = binding.etApplicantName.text.toString()
//        billMap["fatherName"] = binding.etFatherName.text.toString()
//        billMap["dob"] = strDate
//        billMap["type"] = strPanType
//        billMap["typeToShow"] = strTypeToShow
//        billMap["mobileNo"] = binding.etMobileNo.text.toString()
//        billMap["oldPanCardPhotoFileName"] = oldPanCardPhotoFileName
//        billMap["oldPanCardPhotoDownloadUrl"] = oldPanCardPhotoDownloadUrl
//        billMap["passportPhotoFileName"] = passportPhotoFileName
//        billMap["passportPhotoDownloadUrl"] = passportPhotoDownloadUrl
//        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
//        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
//        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
//        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
//        billMap["aadharFatherFrontPhotoFileName"] = aadharFatherFrontPhotoFileName
//        billMap["aadharFatherFrontPhotoDownloadUrl"] = aadharFatherFrontPhotoDownloadUrl
//        billMap["aadharFatherBackPhotoFileName"] = aadharFatherBackPhotoFileName
//        billMap["aadharFatherBackPhotoDownloadUrl"] = aadharFatherBackPhotoDownloadUrl
//        billMap["signFileName"] = signFileName
//        billMap["signDownloadUrl"] = signDownloadUrl
//        billMap["marriageCertFileName"] = marriageCertFileName
//        billMap["marriageCertDownloadUrl"] = marriageCertDownloadUrl
//        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
//        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["type"] = strType
        billMap["typeToShow"] = strTypeToShow
        billMap["textbox1"] = binding.edittext1.text.toString()
        billMap["textbox1Hint"] = binding.tilEdittext1.hint.toString()
        billMap["textbox2"] = binding.edittext2.text.toString()
        billMap["textbox2Hint"] = binding.tilEdittext2.hint.toString()
        billMap["textbox3"] = binding.edittext3.text.toString()
        billMap["textbox3Hint"] = binding.tilEdittext3.hint.toString()
        billMap["textbox4"] = binding.edittext4.text.toString()
        billMap["textbox4Hint"] = binding.tilEdittext4.hint.toString()
        billMap["textbox5"] = binding.edittext5.text.toString()
        billMap["textbox5Hint"] = binding.tilEdittext5.hint.toString()
        billMap["datebox"] = strDatebox
        billMap["dateboxHint"] = binding.edittextDate1.text.toString()
        billMap["attachment1Hint"] = binding.tilEdittextPhoto1.hint.toString()
        billMap["attachment1FileName"] = attachment1FileName
        billMap["attachment1DownloadUrl"] = attachment1DownloadUrl
        billMap["attachment2Hint"] = binding.tilEdittextPhoto2.hint.toString()
        billMap["attachment2FileName"] = attachment2FileName
        billMap["attachment2DownloadUrl"] = attachment2DownloadUrl
        billMap["attachment3Hint"] = binding.tilEdittextPhoto3.hint.toString()
        billMap["attachment3FileName"] = attachment3FileName
        billMap["attachment3DownloadUrl"] = attachment3DownloadUrl
        billMap["attachment4Hint"] = binding.tilEdittextPhoto4.hint.toString()
        billMap["attachment4FileName"] = attachment4FileName
        billMap["attachment4DownloadUrl"] = attachment4DownloadUrl
        billMap["attachment5Hint"] = binding.tilEdittextPhoto5.hint.toString()
        billMap["attachment5FileName"] = attachment5FileName
        billMap["attachment5DownloadUrl"] = attachment5DownloadUrl
        billMap["attachment6Hint"] = binding.tilEdittextPhoto6.hint.toString()
        billMap["attachment6FileName"] = attachment6FileName
        billMap["attachment6DownloadUrl"] = attachment6DownloadUrl
        billMap["attachment7Hint"] = binding.tilEdittextPhoto7.hint.toString()
        billMap["attachment7FileName"] = attachment7FileName
        billMap["attachment7DownloadUrl"] = attachment7DownloadUrl
        billMap["attachment8Hint"] = binding.tilEdittextPhoto8.hint.toString()
        billMap["attachment8FileName"] = attachment8FileName
        billMap["attachment8DownloadUrl"] = attachment8DownloadUrl
        billMap["attachment9Hint"] = binding.tilEdittextPhoto9.hint.toString()
        billMap["attachment9FileName"] = attachment9FileName
        billMap["attachment9DownloadUrl"] = attachment9DownloadUrl
        billMap["attachment10Hint"] = binding.tilEdittextPhoto10.hint.toString()
        billMap["attachment10FileName"] = attachment10FileName
        billMap["attachment10DownloadUrl"] = attachment10DownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("pan_cards").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/pan_cards/$pushKey"] = billMap
        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Pan Card added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_pan_card")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Pan Card added by $name")
        }
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/pan_cards/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd?.isShowing == true) cpd?.dismiss() //        toast("पॅन कार्ड अर्ज सादर केला")
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

    suspend fun uploadImageToFirebase(fileUri: Uri, fileName: String): String = withContext(Dispatchers.IO) {
        val storagePath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(FarmerPolicyActivity.PATH_NAME).child(fileName)
        val uploadTask = storagePath.putFile(fileUri).await()
        return@withContext storagePath.downloadUrl.await().toString()
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

    fun calculateAge(birthdate: Date?): Int {
        val birth = Calendar.getInstance()
        if (birthdate != null) {
            birth.time = birthdate
        }
        val today = Calendar.getInstance()
        var yearDifference = (today[Calendar.YEAR] - birth[Calendar.YEAR])
        if (today[Calendar.MONTH] < birth[Calendar.MONTH]) {
            yearDifference--
        } else {
            if (today[Calendar.MONTH] == birth[Calendar.MONTH] && today[Calendar.DAY_OF_MONTH] < birth[Calendar.DAY_OF_MONTH]) {
                yearDifference--
            }
        }
        return yearDifference
    }

    companion object {
        val TYPE_DURUSTI = "TYPE_DURUSTI"
        val TYPE_GET_LOST_PAN = "TYPE_GET_LOST_PAN"
        val TYPE_TYPE_ONE = "TYPE_TYPE_ONE"
        val TYPE_MARRIED_WOMAN_DURUSTI = "TYPE_MARRIED_WOMAN_DURUSTI"
        val TYPE_NAVIN = "TYPE_NAVIN"
        val TYPE_URGENT_NAVIN = "TYPE_URGENT_NAVIN"
        val TYPE_FIND_LOST_PAN = "TYPE_FIND_LOST_PAN"
//        val TYPE_DURUSTI = "दुरुस्ती"
//        val TYPE_GET_LOST_PAN = "हरवलेले पॅन मागविणे"
//        val TYPE_TYPE_ONE = "1"
//        val TYPE_MARRIED_WOMAN_DURUSTI = "married woman दुरुस्ती"
//        val TYPE_NAVIN = "नवीन"
//        val TYPE_URGENT_NAVIN = "अर्जंट नवीन"
//        val TYPE_FIND_LOST_PAN = "हरवलेले पॅन नंबर शोधणे"
    }
}
