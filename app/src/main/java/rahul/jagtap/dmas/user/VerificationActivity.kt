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
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.longToast
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityVerificationBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.text.SimpleDateFormat
import java.util.*

class VerificationActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    //    private var aadharFrontPhotoFileName: String = ""
    //    private var aadharFrontPhotoDownloadUrl: String = ""
    //    private var aadharBackPhotoFileName: String = ""
    //    private var aadharBackPhotoDownloadUrl: String = ""
    //    private var rationFrontPhotoFileName: String = ""
    //    private var rationFrontPhotoDownloadUrl: String = ""
    //    private var rationBackPhotoFileName: String = ""
    //    private var rationBackPhotoDownloadUrl: String = ""
    //    private var rationElectionFrontPhotoFileName: String = ""
    //    private var rationElectionFrontPhotoDownloadUrl: String = ""
    //    private var rationElectionBackPhotoFileName: String = ""
    //    private var rationElectionBackPhotoDownloadUrl: String = ""
    //    private var bankPassbookPhotoFileName: String = ""
    //    private var bankPassbookPhotoDownloadUrl: String = ""
    //    private var panPhotoFileName: String = ""
    //    private var panPhotoDownloadUrl: String = ""
    //    private var paymentScreenshotFileName: String = ""
    //    private var paymentScreenshotDownloadUrl: String = ""

    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private val TAG = VerificationActivity::class.java.simpleName
    var imageType = -1

    //    var aadharFrontPhotoUri: Uri? = null
    //    var aadharBackPhotoUri: Uri? = null
    //    var bankPassbookPhotoUri: Uri? = null
    //    var rationFrontPhotoUri: Uri? = null
    //    var rationBackPhotoUri: Uri? = null
    //    var rationElectionFrontPhotoUri: Uri? = null
    //    var rationElectionBackPhotoUri: Uri? = null
    //    var panPhotoUri: Uri? = null
    //    var paymentScreenshotUri: Uri? = null
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
    var cpd: ProgressDialog? = null
    private lateinit var easyImage: EasyImage
    private var strTypeToShow: String? = ""
    private var strType: String? = ""
    lateinit var binding: ActivityVerificationBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null
    private val typeList = ArrayList<String>()
    private val typeHashMapList = ArrayList<HashMap<String, String>>()
    private val typeKeyList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_verification)
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
                ?: "").putExtra("title", getString(R.string.txt_verification)))
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
        typeList.clear(); typeHashMapList.clear(); typeKeyList.clear()
        typeMap?.forEach {
            if (it.value["type_title"] != "0") {
                typeHashMapList.add(it.value)
                typeList.add(it.value["type_title"].toString())
                typeKeyList.add(it.key)
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
//            binding.etMobileNo.gone()
//            binding.etAadharFPhoto.gone()
//            binding.etAadharBPhoto.gone()
//            binding.etBankPassbook.gone() //            binding.etPanNo.gone()
//            binding.etEmail.gone()
//            binding.etPanPhoto.gone()
//            binding.etRationCardFPhoto.gone()
//            binding.etRationCardBPhoto.gone()
//            binding.etRationElectionCardFPhoto.gone()
//            binding.etRationElectionCardBPhoto.gone()
//            binding.etPaymentScreenshot.gone()
            binding.btnSubmit.gone()
        }
        binding.etServiceType?.setOnClickListener { //            val list = ArrayList<String>()
            //            list.add("आयुष्यमान कार्ड")
            //            list.add("श्रम कार्ड")
            //            list.add("आभा कार्ड")
            //            list.add("जन आरोग्य कार्ड")
            //            list.add("आधार कार्ड PVC")
            //            list.add("नंबर शोधणे")
            //            list.add("रेशन कार्ड मध्ये नाव वाढवणे")
            //            list.add("रेशन कार्ड मधील नाव कमी करणे")
            //            list.add("रेशन कार्ड ऑनलाईन करणे")
            //            list.add("डिजिटल रेशन कार्ड")
            MaterialDialog.Builder(mContext!!).items(typeList).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    applyType(position)
                }
            }.show()
        }

        // Deep link from the services grid: a subtype tile pre-selects its type and configures the form.
        val preselectKey = intent.getStringExtra(EsuvidhaServiceRegistry.EXTRA_PRESELECT_SUBTYPE)
        if (!preselectKey.isNullOrEmpty()) {
            val idx = typeKeyList.indexOf(preselectKey)
            if (idx >= 0) applyType(idx)
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
//            val strServiceType = binding.etServiceType.text.toString()
//            val strFullName = binding.etApplicantName.text.toString()
//            val strMobileNo = binding.etMobileNo.text.toString() //            val strPanNo = binding.etPanNo.text.toString()
//            val strEmail = binding.etEmail.text.toString()
//            if (TextUtils.isEmpty(strServiceType)) {
//                binding.tilServiceType?.error = binding.tilServiceType?.hint.toString()
//                binding.etServiceType?.requestFocus()
//                return@setOnClickListener
//            }
//            if (TextUtils.isEmpty(strFullName)) {
//                binding.etApplicantName?.error = binding.etApplicantName.hint.toString()
//                binding.etApplicantName?.requestFocus()
//                return@setOnClickListener
//            }
//            if (TextUtils.isEmpty(strMobileNo)) {
//                binding.etMobileNo?.error = binding.etMobileNo.hint.toString()
//                binding.etMobileNo?.requestFocus()
//                return@setOnClickListener
//            }
//            if (aadharFrontPhotoUri == null) {
//                binding.etAadharFPhoto?.error = binding.etAadharFPhoto.hint.toString()
//                binding.etAadharFPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (aadharBackPhotoUri == null) {
//                binding.etAadharBPhoto?.error = binding.etAadharBPhoto.hint.toString()
//                binding.etAadharBPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etBankPassbook.visibility == View.VISIBLE && bankPassbookPhotoUri == null) {
//                binding.etBankPassbook?.error = binding.etBankPassbook.hint.toString()
//                binding.etBankPassbook?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etEmail.visibility == View.VISIBLE && TextUtils.isEmpty(strEmail)) {
//                binding.etEmail?.error = binding.etEmail.hint.toString()
//                binding.etEmail?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etPanPhoto.visibility == View.VISIBLE && panPhotoUri == null) {
//                binding.etPanPhoto?.error = binding.etPanPhoto.hint.toString()
//                binding.etPanPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etRationCardFPhoto.visibility == View.VISIBLE && rationFrontPhotoUri == null) {
//                binding.etRationCardFPhoto?.error = binding.etRationCardFPhoto.hint.toString()
//                binding.etRationCardFPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etRationCardBPhoto.visibility == View.VISIBLE && rationBackPhotoUri == null) {
//                binding.etRationCardBPhoto?.error = binding.etRationCardBPhoto.hint.toString()
//                binding.etRationCardBPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etRationElectionCardFPhoto.visibility == View.VISIBLE && rationElectionFrontPhotoUri == null) {
//                binding.etRationElectionCardFPhoto?.error = binding.etRationElectionCardFPhoto.hint.toString()
//                binding.etRationElectionCardFPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (binding.etRationElectionCardBPhoto.visibility == View.VISIBLE && rationElectionBackPhotoUri == null) {
//                binding.etRationElectionCardBPhoto?.error = binding.etRationElectionCardBPhoto.hint.toString()
//                binding.etRationElectionCardBPhoto?.requestFocus()
//                return@setOnClickListener
//            }
//            if (paymentScreenshotUri == null) {
//                binding.etPaymentScreenshot?.error = binding.etPaymentScreenshot.hint.toString()
//                binding.etPaymentScreenshot?.requestFocus()
//                return@setOnClickListener
//            }
//            uploadAadharFPhoto(aadharFrontPhotoUri!!)
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
//        binding.etAadharFPhoto?.setOnClickListener { choosePhotoWithPermissions(1) }
//        binding.etAadharBPhoto?.setOnClickListener { choosePhotoWithPermissions(2) }
//        binding.etBankPassbook?.setOnClickListener { choosePhotoWithPermissions(3) }
//        binding.etRationCardFPhoto?.setOnClickListener { choosePhotoWithPermissions(4) }
//        binding.etRationCardBPhoto?.setOnClickListener { choosePhotoWithPermissions(7) }
//        binding.etRationElectionCardFPhoto?.setOnClickListener { choosePhotoWithPermissions(8) }
//        binding.etRationElectionCardBPhoto?.setOnClickListener { choosePhotoWithPermissions(9) }
//        binding.etPanPhoto?.setOnClickListener { choosePhotoWithPermissions(5) }
//        binding.etPaymentScreenshot?.setOnClickListener { choosePhotoWithPermissions(6) }
    }

    /** Selects the subtype at [position], mirroring the type-picker dialog (used by the dialog and by grid deep-links). */
    private fun applyType(position: Int) {
        binding.etServiceType?.setText(typeList[position])
        binding.tilServiceType.helperText = null
        strTypeToShow = typeList[position]
        val selectedTypeHashMap = typeHashMapList[position]
        strType = typeMap?.let { Utils.getKeyFromNestedHashMap(it, strTypeToShow!!) }
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
        binding.btnPay.visible()
        binding.tvPayNote.visible()
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
//                            1 -> {
//                                aadharFrontPhotoUri = Uri.fromFile(it)
//                                binding.etAadharFPhoto?.setText("Success") //aadharFrontPhotoUri.toString())
//                            }
//
//                            2 -> {
//                                aadharBackPhotoUri = Uri.fromFile(it)
//                                binding.etAadharBPhoto?.setText("Success") //aadharBackPhotoUri.toString())
//                            }
//
//                            3 -> {
//                                bankPassbookPhotoUri = Uri.fromFile(it)
//                                binding.etBankPassbook?.setText("Success") //bankPassbookPhotoUri.toString())
//                            }
//
//                            4 -> {
//                                rationFrontPhotoUri = Uri.fromFile(it)
//                                binding.etRationCardFPhoto?.setText("Success")
//                            }
//
//                            7 -> {
//                                rationBackPhotoUri = Uri.fromFile(it)
//                                binding.etRationCardBPhoto?.setText("Success")
//                            }
//
//                            8 -> {
//                                rationElectionFrontPhotoUri = Uri.fromFile(it)
//                                binding.etRationElectionCardFPhoto?.setText("Success")
//                            }
//
//                            9 -> {
//                                rationElectionBackPhotoUri = Uri.fromFile(it)
//                                binding.etRationElectionCardBPhoto?.setText("Success")
//                            }
//
//                            5 -> {
//                                panPhotoUri = Uri.fromFile(it)
//                                binding.etPanPhoto?.setText("Success") //panPhotoUri.toString())
//                            }
//
//                            6 -> {
//                                paymentScreenshotUri = Uri.fromFile(it)
//                                binding.etPaymentScreenshot?.setText("Success") //paymentScreenshotUri.toString())
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

//    private fun uploadAadharFPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(aadharFrontPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    aadharFrontPhotoDownloadUrl = uri.toString()
//                    aadharBackPhotoUri?.let { it1 -> uploadAadharBPhoto(it1) }
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
//        }.addOnProgressListener { //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadAadharBPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        aadharBackPhotoFileName = "aadhar_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(aadharBackPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    aadharBackPhotoDownloadUrl = uri.toString() //                    if (rationFrontPhotoUri != null)
//                    //                        rationFrontPhotoUri?.let { it1 -> uploadRationFPhoto(it1) }
//                    //                    else if (rationBackPhotoUri != null)
//                    //                        rationBackPhotoUri?.let { it1 -> uploadRationBPhoto(it1) }
//                    if (bankPassbookPhotoUri != null) bankPassbookPhotoUri?.let { it1 -> uploadBankPassbookPhoto(it1) }
//                    else if (panPhotoUri != null) panPhotoUri?.let { it1 -> uploadPanPhoto(it1) }
//                    else if (binding.etRationCardFPhoto.visibility == View.VISIBLE && rationFrontPhotoUri != null) {
//                        rationFrontPhotoUri?.let { it1 -> uploadRationFPhoto(it1) }
//                    } else paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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
//        }.addOnProgressListener { //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadBankPassbookPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        bankPassbookPhotoFileName = "bank_passbook_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(bankPassbookPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    bankPassbookPhotoDownloadUrl = uri.toString()
//                    if (binding.etRationCardFPhoto.visibility == View.VISIBLE && rationFrontPhotoUri != null) {
//                        rationFrontPhotoUri?.let { it1 -> uploadRationFPhoto(it1) }
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
//            if (cpd?.isShowing == true) cpd?.dismiss()
//            it.message?.let { it1 -> toast(it1) }
//        }.addOnProgressListener { //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadPanPhoto(fileUri: Uri) {
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        panPhotoFileName = "pan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(panPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    panPhotoDownloadUrl = uri.toString()
//                    if (binding.etRationCardFPhoto.visibility == View.VISIBLE && rationFrontPhotoUri != null) {
//                        rationFrontPhotoUri?.let { it1 -> uploadRationFPhoto(it1) }
//                    } else if (binding.etRationElectionCardFPhoto.visibility == View.VISIBLE && rationElectionFrontPhotoUri != null) {
//                        rationElectionFrontPhotoUri?.let { it1 -> uploadRationElectionFPhoto(it1) }
//                    } else {
//                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
//                    }
//                }.addOnFailureListener {
//                    it.printStackTrace()
//                }
//            } catch (e: java.lang.Exception) {
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
//    private fun uploadRationFPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        rationFrontPhotoFileName = "ration_front_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(rationFrontPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    rationFrontPhotoDownloadUrl = uri.toString()
//                    rationBackPhotoUri?.let { it1 -> uploadRationBPhoto(it1) }
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
//        }.addOnProgressListener { //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadRationBPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        rationBackPhotoFileName = "ration_back_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(rationBackPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    rationBackPhotoDownloadUrl = uri.toString()
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
//        }.addOnProgressListener { //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadRationElectionFPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        rationElectionFrontPhotoFileName = "ration_election_front_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(rationElectionFrontPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    rationElectionFrontPhotoDownloadUrl = uri.toString()
//                    rationElectionBackPhotoUri?.let { it1 -> uploadRationElectionBPhoto(it1) }
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
//        }.addOnProgressListener { //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//            cpd?.setMessage("Please wait.. ")
//        }
//    }
//
//    private fun uploadRationElectionBPhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        rationElectionBackPhotoFileName = "ration_election_back_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(rationElectionBackPhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    rationElectionBackPhotoDownloadUrl = uri.toString()
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
//        }.addOnProgressListener { //displaying the upload progress
//            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
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
        billMap["serviceType"] = strType
        billMap["serviceTypeToShow"] = strTypeToShow
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
//        billMap["applicantName"] = binding.etApplicantName.text.toString()
//        billMap["aadharLinkedMobileNo"] = binding.etMobileNo.text.toString()
//        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
//        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
//        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
//        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
//        billMap["bankPassbookPhotoFileName"] = bankPassbookPhotoFileName
//        billMap["bankPassbookPhotoDownloadUrl"] = bankPassbookPhotoDownloadUrl
//        billMap["rationFrontPhotoFileName"] = rationFrontPhotoFileName
//        billMap["rationFrontPhotoDownloadUrl"] = rationFrontPhotoDownloadUrl
//        billMap["rationBackPhotoFileName"] = rationBackPhotoFileName
//        billMap["rationBackPhotoDownloadUrl"] = rationBackPhotoDownloadUrl
//        billMap["rationElectionFrontPhotoFileName"] = rationElectionFrontPhotoFileName
//        billMap["rationElectionFrontPhotoDownloadUrl"] = rationElectionFrontPhotoDownloadUrl
//        billMap["rationElectionBackPhotoFileName"] = rationElectionBackPhotoFileName
//        billMap["rationElectionBackPhotoDownloadUrl"] = rationElectionBackPhotoDownloadUrl //        billMap["panNo"] = binding.etPanNo.text.toString()
//        billMap["customerEmail"] = binding.etEmail.text.toString()
//        billMap["panPhotoFileName"] = panPhotoFileName
//        billMap["panPhotoDownloadUrl"] = panPhotoDownloadUrl
//        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
//        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
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
            val notificationItem = NotificationItem(message = "New Verification added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_$PATH_NAME")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Verification added by $name")
        }
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/$PATH_NAME/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd?.isShowing == true) cpd?.dismiss() //        toast("Request submitted successfully")
        Utils.showDialog(mContext, "तुमची सुविधा विनंती जतन करण्यात आली आहे. रीसिट \"पाठविलेल्या सुविधा\" फोल्डर बटन मध्ये तपासा.", false) { dialog, which ->
            run {
                dialog.dismiss()
                finish()
            }
        }
    }

    fun getTodayDate(): String = SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).format(Date())

    suspend fun uploadImageToFirebase(fileUri: Uri, fileName: String): String = withContext(Dispatchers.IO) {
        val storagePath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(fileName)
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
        imageType = pos //        rxPermissions?.requestEachCombined(Manifest.permission.CAMERA)?.subscribe {
        //            when {
        //                it.granted -> { // get picture
        //                    //                    EasyImage.openChooserWithGallery(this, "", AccountingServicesActivity.REQUEST_AVATAR_CODE)
        easyImage.openChooser(this) //                }
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
        var PATH_NAME = "verification"
        val TYPE_AYUSHMAN_CARD = "TYPE_AYUSHMAN_CARD"
        val TYPE_SHRAM_CARD = "TYPE_SHRAM_CARD"
        val TYPE_AABHA_CARD = "TYPE_AABHA_CARD"
        val TYPE_JAN_AROGYA_CARD = "TYPE_JAN_AROGYA_CARD"
        val TYPE_AADHAR_CARD_PVC = "TYPE_AADHAR_CARD_PVC"
        val TYPE_FIND_NUMBER = "TYPE_FIND_NUMBER"
        val TYPE_ADD_NAME_RC = "TYPE_ADD_NAME_RC"
        val TYPE_REMOVE_NAME_RC = "TYPE_REMOVE_NAME_RC"
        val TYPE_ONLINE_RC = "TYPE_ONLINE_RC"
        val TYPE_DIGITAL_RC = "TYPE_DIGITAL_RC" //        val TYPE_AYUSHMAN_CARD = "आयुष्यमान कार्ड"
        //        val TYPE_SHRAM_CARD = "श्रम कार्ड"
        //        val TYPE_AABHA_CARD = "आभा कार्ड"
        //        val TYPE_JAN_AROGYA_CARD = "जन आरोग्य कार्ड"
        //        val TYPE_AADHAR_CARD_PVC = "आधार कार्ड PVC"
        //        val TYPE_FIND_NUMBER = "नंबर शोधणे"
        //        val TYPE_ADD_NAME_RC = "रेशन कार्ड मध्ये नाव वाढवणे"
        //        val TYPE_REMOVE_NAME_RC = "रेशन कार्ड मधील नाव कमी करणे "
        //        val TYPE_ONLINE_RC = "रेशन कार्ड ऑनलाईन करणे"
        //        val TYPE_DIGITAL_RC = "डिजिटल रेशन कार्ड"
    }
}
