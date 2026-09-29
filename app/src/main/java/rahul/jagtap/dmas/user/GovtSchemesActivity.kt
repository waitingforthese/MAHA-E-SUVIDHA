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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import rahul.jagtap.dmas.extensions.longToast
import rahul.jagtap.dmas.extensions.toast
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityGovtSchemesBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.text.SimpleDateFormat
import java.util.*

class GovtSchemesActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
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
//    private var aadharFrontPhotoFileName: String = ""
//    private var aadharFrontPhotoDownloadUrl: String = ""
//    private var aadharBackPhotoFileName: String = ""
//    private var aadharBackPhotoDownloadUrl: String = ""
//    private var panPhotoFileName: String = ""
//    private var panPhotoDownloadUrl: String = ""
//    private var passbookPhotoFileName: String = ""
//    private var passbookPhotoDownloadUrl: String = ""
//    private var rationFrontPhotoFileName: String = ""
//    private var rationFrontPhotoDownloadUrl: String = ""
//    private var rationBackPhotoFileName: String = ""
//    private var rationBackPhotoDownloadUrl: String = ""
//    private var signPhotoFileName: String = ""
//    private var signPhotoDownloadUrl: String = ""
//    private var passportPhotoFileName: String = ""
//    private var passportPhotoDownloadUrl: String = ""
//    private var schoolLCFileName: String = ""
//    private var schoolLCDownloadUrl: String = ""
//    private var birthCertFileName: String = ""
//    private var birthCertDownloadUrl: String = ""
//    private var incomeCertFileName: String = ""
//    private var incomeCertDownloadUrl: String = ""
//    private var castCertFileName: String = ""
//    private var castCertDownloadUrl: String = ""
//    private var projectReportFileName: String = ""
//    private var projectReportDownloadUrl: String = ""
//    private var paymentScreenshotFileName: String = ""
//    private var paymentScreenshotDownloadUrl: String = ""
    private val TAG = GovtSchemesActivity::class.java.simpleName
    var imageType = -1 // 1 - aadhar f, 2 - aadhar b, 3 - pan, 4 - passbook, 5 - payment screenshot
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
//    var aadharFrontPhotoUri: Uri? = null
//    var aadharBackPhotoUri: Uri? = null
//    var panPhotoUri: Uri? = null
//    var passbookPhotoUri: Uri? = null
//    var rationFrontPhotoUri: Uri? = null
//    var rationBackPhotoUri: Uri? = null
//    var signPhotoUri: Uri? = null
//    var passportPhotoUri: Uri? = null
//    var schoolLCUri: Uri? = null
//    var birthCertUri: Uri? = null
//    var incomeCertUri: Uri? = null
//    var castCertUri: Uri? = null
//    var projectReportUri: Uri? = null
//    var paymentScreenshotUri: Uri? = null
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    var cpd: ProgressDialog? = null
    private lateinit var easyImage: EasyImage
    private var strTypeToShow: String? = ""
    private var strType: String? = ""
    lateinit var binding: ActivityGovtSchemesBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null
    var strDatebox = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGovtSchemesBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_govt_schemes)
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
                ?: "").putExtra("title", getString(R.string.txt_govt_schemes)))
        }
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
            binding.btnSubmit.gone()
        }
        /** Selects the subtype at [position] (shared by the picker dialog and grid deep-links). */
        fun applyType(position: Int) {

                    binding.etServiceType?.setText(typeList[position])
                    binding.tilServiceType.helperText = null
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
//                    when (keyFromValue) {
//                        TYPE_NEW_SCHEME_1 -> {
//                            strType = TYPE_NEW_SCHEME_1
//                            binding.etSchoolLC.gone()
//                            binding.etBirthCert.gone()
//                            binding.etIncomeCert.gone()
//                            binding.etCastCert.gone()
//                            binding.etProjectReport.gone()
//                        }
//                        TYPE_CMEGP -> {
//                            strType = TYPE_CMEGP
//                            binding.etSchoolLC.visible()
//                            binding.etBirthCert.visible()
//                            binding.etIncomeCert.gone()
//                            binding.etCastCert.gone()
//                            binding.etProjectReport.gone()
//                        }
//                        TYPE_NEW_SCHEME_2 -> {
//                            strType = TYPE_NEW_SCHEME_2
//                            binding.etSchoolLC.visible()
//                            binding.etBirthCert.gone()
//                            binding.etIncomeCert.gone()
//                            binding.etCastCert.gone()
//                            binding.etProjectReport.gone()
//                        }
//                        TYPE_ANNASAHEB_PATIL -> {
//                            strType = TYPE_ANNASAHEB_PATIL
//                            binding.etSchoolLC.visible()
//                            binding.etBirthCert.gone()
//                            binding.etIncomeCert.visible()
//                            binding.etCastCert.gone()
//                            binding.etProjectReport.gone()
//                        }
//                        TYPE_OBC_MAHAMANDAL -> {
//                            strType = TYPE_OBC_MAHAMANDAL
//                            binding.etSchoolLC.visible()
//                            binding.etBirthCert.gone()
//                            binding.etIncomeCert.gone()
//                            binding.etCastCert.visible()
//                            binding.etProjectReport.visible()
//                        }
//                        TYPE_CAST_MAHAMANDAL -> {
//                            strType = TYPE_CAST_MAHAMANDAL
//                            binding.etSchoolLC.visible()
//                            binding.etBirthCert.gone()
//                            binding.etIncomeCert.gone()
//                            binding.etCastCert.visible()
//                            binding.etProjectReport.visible()
//                        }
//                        TYPE_TYPE_THREE -> {
//                            strType = TYPE_TYPE_THREE
//                            binding.etSchoolLC.visible()
//                            binding.etBirthCert.gone()
//                            binding.etIncomeCert.gone()
//                            binding.etCastCert.gone()
//                            binding.etProjectReport.gone()
//                        }
//                    }
                    selectedTypeAmount = extractAmountFromType(strTypeToShow!!) ?: ""
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
//            list.add("नवीन आलेली योजना - 1")
//            list.add("सी एम ई जी पी (मुख्यमंत्री रोजगार निर्मिती कार्यक्रम)")
//            list.add("नवीन आलेली योजना - 2")
//            list.add("अण्णासाहेब पाटील व्याज परतावा योजना")
//            list.add("OBC MAHAMANDAL")
//            list.add("CAST MAHAMANDAL")
//            list.add("3")
            MaterialDialog.Builder(mContext!!).items(typeList).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    applyType(position)
                }
            }.show()
        }

        binding.btnSubmit?.setOnClickListener {
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
                binding.tilServiceType?.error = binding.tilServiceType?.hint.toString()
                binding.etServiceType?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittext1.visibility == View.VISIBLE && TextUtils.isEmpty(strEdittext1)) {
                binding.tilEdittext1?.error = binding.tilEdittext1.hint.toString()
                binding.edittext1?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittext2.visibility == View.VISIBLE && TextUtils.isEmpty(strEdittext2)) {
                binding.tilEdittext2?.error = binding.tilEdittext2.hint.toString()
                binding.edittext2?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittext3.visibility == View.VISIBLE && TextUtils.isEmpty(strEdittext3)) {
                binding.tilEdittext3?.error = binding.tilEdittext3.hint.toString()
                binding.edittext3?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittext4.visibility == View.VISIBLE && TextUtils.isEmpty(strEdittext4)) {
                binding.tilEdittext4?.error = binding.tilEdittext4.hint.toString()
                binding.edittext4?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittext5.visibility == View.VISIBLE && TextUtils.isEmpty(strEdittext5)) {
                binding.tilEdittext5?.error = binding.tilEdittext5.hint.toString()
                binding.edittext5?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextDate1.visibility == View.VISIBLE && TextUtils.isEmpty(strEdittextDate1)) {
                binding.tilEdittextDate1?.error = binding.tilEdittextDate1.hint.toString()
                binding.edittextDate1?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto1.visibility == View.VISIBLE && attachment1Uri == null) {
                binding.tilEdittextPhoto1?.error = binding.tilEdittextPhoto1.hint.toString()
                binding.edittextPhoto1?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto2.visibility == View.VISIBLE && attachment2Uri == null){
                binding.tilEdittextPhoto2?.error = binding.tilEdittextPhoto2.hint.toString()
                binding.edittextPhoto2?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto3.visibility == View.VISIBLE && attachment3Uri == null){
                binding.tilEdittextPhoto3?.error = binding.tilEdittextPhoto3.hint.toString()
                binding.edittextPhoto3?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto4.visibility == View.VISIBLE && attachment4Uri == null){
                binding.tilEdittextPhoto4?.error = binding.tilEdittextPhoto4.hint.toString()
                binding.edittextPhoto4?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto5.visibility == View.VISIBLE && attachment5Uri == null){
                binding.tilEdittextPhoto5?.error = binding.tilEdittextPhoto5.hint.toString()
                binding.edittextPhoto5?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto6.visibility == View.VISIBLE && attachment6Uri == null){
                binding.tilEdittextPhoto6?.error = binding.tilEdittextPhoto6.hint.toString()
                binding.edittextPhoto6?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto7.visibility == View.VISIBLE && attachment7Uri == null){
                binding.tilEdittextPhoto7?.error = binding.tilEdittextPhoto7.hint.toString()
                binding.edittextPhoto7?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto8.visibility == View.VISIBLE && attachment8Uri == null){
                binding.tilEdittextPhoto8?.error = binding.tilEdittextPhoto8.hint.toString()
                binding.edittextPhoto8?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto9.visibility == View.VISIBLE && attachment9Uri == null){
                binding.tilEdittextPhoto9?.error = binding.tilEdittextPhoto9.hint.toString()
                binding.edittextPhoto9?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilEdittextPhoto10.visibility == View.VISIBLE && attachment10Uri == null){
                binding.tilEdittextPhoto10?.error = binding.tilEdittextPhoto10.hint.toString()
                binding.edittextPhoto10?.requestFocus()
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
//            uploadAadharFrontPhoto(aadharFrontPhotoUri!!)
        }
//        binding.etOwnerCast?.setOnClickListener {
//            val list = listOf("SC", "ST", "OBC", "General")
//            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, _: View?, position: Int, _: CharSequence ->
//                run {
//                    dialog?.dismiss()
//                    binding.etOwnerCast.setText(list[position])
//                }
//            }.show()
//        }
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
//        binding.etAadharFPhoto?.setOnClickListener {
//            imageType = 1
//            choosePhotoWithPermissions()
//        }
//        binding.etAadharBPhoto?.setOnClickListener {
//            imageType = 2
//            choosePhotoWithPermissions()
//        }
//        binding.etPanPhoto?.setOnClickListener {
//            imageType = 3
//            choosePhotoWithPermissions()
//        }
//        binding.etPassbookPhoto?.setOnClickListener {
//            imageType = 4
//            choosePhotoWithPermissions()
//        }
//        binding.etRationFPhoto?.setOnClickListener {
//            imageType = 5
//            choosePhotoWithPermissions()
//        }
//        binding.etRationBPhoto?.setOnClickListener {
//            imageType = 6
//            choosePhotoWithPermissions()
//        }
//        binding.etSignPhoto?.setOnClickListener {
//            imageType = 7
//            choosePhotoWithPermissions()
//        }
//        binding.etPassportPhoto?.setOnClickListener {
//            imageType = 8
//            choosePhotoWithPermissions()
//        }
//        binding.etSchoolLC?.setOnClickListener {
//            imageType = 9
//            choosePhotoWithPermissions()
//        }
//        binding.etBirthCert?.setOnClickListener {
//            imageType = 10
//            choosePhotoWithPermissions()
//        }
//        binding.etIncomeCert?.setOnClickListener {
//            imageType = 11
//            choosePhotoWithPermissions()
//        }
//        binding.etPaymentScreenshot?.setOnClickListener {
//            imageType = 12
//            choosePhotoWithPermissions()
//        }
//        binding.etCastCert?.setOnClickListener {
//            imageType = 13
//            choosePhotoWithPermissions()
//        }
//        binding.etProjectReport?.setOnClickListener {
//            imageType = 14
//            choosePhotoWithPermissions()
//        }
    }

//    private fun uploadAadharFrontPhoto(fileUri: Uri) {
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(aadharFrontPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    aadharFrontPhotoDownloadUrl = uri.toString()
//                    aadharBackPhotoUri?.let { it1 -> uploadAadharBackPhoto(it1) }
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadAadharBackPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        aadharBackPhotoFileName = "aadhar_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(aadharBackPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    aadharBackPhotoDownloadUrl = uri.toString()
//                    panPhotoUri?.let { it1 -> uploadPanPhoto(it1) }
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadPanPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        panPhotoFileName = "pan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(panPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    panPhotoDownloadUrl = uri.toString()
//                    passbookPhotoUri?.let { it1 -> uploadPassbookPhoto(it1) }
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadPassbookPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        passbookPhotoFileName = "passbook_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(passbookPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    passbookPhotoDownloadUrl = uri.toString()
//                    rationFrontPhotoUri?.let { it1 -> uploadRationFrontPhoto(it1) }
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadRationFrontPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        rationFrontPhotoFileName = "ration_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(rationFrontPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    rationFrontPhotoDownloadUrl = uri.toString()
//                    rationBackPhotoUri?.let { it1 -> uploadRationBackPhoto(it1) }
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadRationBackPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        rationBackPhotoFileName = "ration_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(rationBackPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    rationBackPhotoDownloadUrl = uri.toString()
//                    signPhotoUri?.let { it1 -> uploadSignPhoto(it1) }
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//    private fun uploadSignPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        signPhotoFileName = "sign_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(signPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    signPhotoDownloadUrl = uri.toString()
//                    passportPhotoUri?.let { it1 -> uploadPassportPhoto(it1) }
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadPassportPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        passportPhotoFileName = "passport_photo_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(passportPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    passportPhotoDownloadUrl = uri.toString()
//                    if (schoolLCUri != null) {
//                        schoolLCUri?.let { it1 -> uploadSchoolLCPhoto(it1) }
//                    } else if (birthCertUri != null) {
//                        birthCertUri?.let { it1 -> uploadBirthCert(it1) }
//                    } else if (incomeCertUri != null) {
//                        incomeCertUri?.let { it1 -> uploadIncomeCert(it1) }
//                    } else {
//                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
//                    }
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadSchoolLCPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        schoolLCFileName = "school_lc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(schoolLCFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    schoolLCDownloadUrl = uri.toString()
//                    if (birthCertUri != null) {
//                        birthCertUri?.let { it1 -> uploadBirthCert(it1) }
//                    } else if (incomeCertUri != null) {
//                        incomeCertUri?.let { it1 -> uploadIncomeCert(it1) }
//                    } else {
//                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
//                    }
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadBirthCert(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        birthCertFileName = "birth_cert_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(birthCertFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    birthCertDownloadUrl = uri.toString()
//                    if (incomeCertUri != null) {
//                        incomeCertUri?.let { it1 -> uploadIncomeCert(it1) }
//                    } else {
//                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
//                    }
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadIncomeCert(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        incomeCertFileName = "income_cert_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(incomeCertFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    incomeCertDownloadUrl = uri.toString()
//                    if (binding.etCastCert.visibility == View.VISIBLE && castCertUri != null)
//                        castCertUri?.let { it1 -> uploadCastCert(it1) }
//                    else
//                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadCastCert(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        castCertFileName = "cast_cert_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(castCertFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    castCertDownloadUrl = uri.toString()
//                    projectReportUri?.let { it1 -> uploadProjectReport(it1) }
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadProjectReport(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        projectReportFileName = "project_report_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(projectReportFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    projectReportDownloadUrl = uri.toString()
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
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadPaymentScreenshot(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(paymentScreenshotFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    paymentScreenshotDownloadUrl = uri.toString()
//                    createDbRecord()
//                }.addOnFailureListener {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
//                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                e.printStackTrace()
//            }
//        }.addOnFailureListener {
//            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener {
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
//        billMap["serviceType"] = binding.etServiceType.text.toString()
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
            val notificationItem = NotificationItem(message = "New Govt Scheme Application added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_$PATH_NAME")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Govt Scheme Application added by $name")
        }

        // Create a backup record
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/$PATH_NAME/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss() //        toast("शासकीय योजना अर्ज सादर केला")
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

//                            11 -> {
//                                incomeCertUri = Uri.fromFile(it)
//                                binding.etIncomeCert?.setText("Success")//incomeCertUri.toString())
//                            }
//
//                            12 -> {
//                                paymentScreenshotUri = Uri.fromFile(it)
//                                binding.etPaymentScreenshot?.setText("Success")//paymentScreenshotUri.toString())
//                            }
                        }
                    }
                }
            }
        })
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

    companion object {
        var PATH_NAME = "govt_schemes"
        val TYPE_NEW_SCHEME_1 = "TYPE_NEW_SCHEME_1"
        val TYPE_NEW_SCHEME_2 = "TYPE_NEW_SCHEME_2"
        val TYPE_CMEGP = "TYPE_CMEGP"
        val TYPE_ANNASAHEB_PATIL = "TYPE_ANNASAHEB_PATIL"
        val TYPE_OBC_MAHAMANDAL = "TYPE_OBC_MAHAMANDAL"
        val TYPE_CAST_MAHAMANDAL = "TYPE_CAST_MAHAMANDAL"
        val TYPE_TYPE_THREE = "TYPE_TYPE_THREE"
//        val TYPE_NEW_SCHEME_1 = "नवीन आलेली योजना - 1"
//        val TYPE_NEW_SCHEME_2 = "सी एम ई जी पी (मुख्यमंत्री रोजगार निर्मिती कार्यक्रम)"
//        val TYPE_CMEGP = "नवीन आलेली योजना - 2"
//        val TYPE_ANNASAHEB_PATIL = "अण्णासाहेब पाटील व्याज परतावा योजना"
//        val TYPE_OBC_MAHAMANDAL = "OBC MAHAMANDAL"
//        val TYPE_CAST_MAHAMANDAL = "CAST MAHAMANDAL"
//        val TYPE_TYPE_THREE = "3"
    }
}
