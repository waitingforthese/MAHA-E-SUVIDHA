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
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityFarmerPolicyBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.longToast
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class FarmerPolicyActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    private var aadharFrontPhotoFileName: String = ""
    private var aadharFrontPhotoDownloadUrl: String = ""
    private var aadharBackPhotoFileName: String = ""
    private var aadharBackPhotoDownloadUrl: String = ""
    private var land8APhotoFileName: String = ""
    private var land8APhotoDownloadUrl: String = ""
    private var panPhotoFileName: String = ""
    private var panPhotoDownloadUrl: String = ""
    private var customer8APhotoFileName: String = ""
    private var customer8APhotoDownloadUrl: String = ""
    private var other8AOtherInfoFileName: String = ""
    private var other8AOtherInfoDownloadUrl: String = ""
    private var quotationFileName: String = ""
    private var quotationDownloadUrl: String = ""
    private var castCertFileName: String = ""
    private var castCertDownloadUrl: String = ""
    private var consentLetterFileName: String = ""
    private var consentLetterDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private val TAG = FarmerPolicyActivity::class.java.simpleName
    var imageType = -1
    var aadharFrontPhotoUri: Uri? = null
    var aadharBackPhotoUri: Uri? = null
    var land8APhotoUri: Uri? = null
    var panPhotoUri: Uri? = null
    var customer8APhotoUri: Uri? = null
    var other8AOtherInfoUri: Uri? = null
    var quotationUri: Uri? = null
    var castCertUri: Uri? = null
    var consentLetterUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    var strDatebox = ""

    var cpd: ProgressDialog? = null
    private lateinit var easyImage: EasyImage
    private var strTypeToShow: String? = ""
    private var strType: String? = ""
    lateinit var binding: ActivityFarmerPolicyBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFarmerPolicyBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilServiceType, binding.tilApplicantName, binding.tilMobileNo, binding.tilSaatBaaraLinkMobileNo, binding.tilTextbox4, binding.tilTextbox5, binding.tilDatebox, binding.tilAadharFrontPhoto, binding.tilAadharBackPhoto, binding.tilLand8APhoto, binding.tilPanPhoto, binding.tilCustomer8APhoto, binding.tilOther8AOtherInfo, binding.tilQuotationPhoto, binding.tilCastCertPhoto, binding.tilConsentLetterPhoto, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle.text = getString(R.string.farmer_policy)
        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid
        username = app?.preferences?.loggedInUser?.username
        name = app?.preferences?.loggedInUser?.name
        easyImage = EasyImage.Builder(this).setChooserType(ChooserType.CAMERA_AND_GALLERY).allowMultiple(false) // Setting to true will cause taken pictures to show up in the device gallery, DEFAULT false
            .setCopyImagesToPublicGalleryFolder(false).build()
        binding.btnPay.setOnClickListener {
            payUsingUPI(selectedTypeAmount)
        }
        binding.tvPayNote.setText(spanText, TextView.BufferType.SPANNABLE)
        binding.tvPayNote.movementMethod = LinkMovementMethod.getInstance()
        binding.btnSuchna.setOnClickListener {
            startActivity(Intent(mContext, ViewSuchnaActivity::class.java).putExtra("suchna", intent.getStringExtra("suchna")
                ?: "").putExtra("title", getString(R.string.farmer_policy)))
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

        binding.etDatebox.setOnClickListener {
            val mMaxDate = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(mContext!!, { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val date = "" + dayOfMonth + "/" + (month + 1) + "/" + year
                strDatebox = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
                binding.etDatebox.setText(date)
            }, mMaxDate[Calendar.YEAR], mMaxDate[Calendar.MONTH], mMaxDate[Calendar.DAY_OF_MONTH])
            datePickerDialog.show()
        }
        if (typeList.size == 0) {
            binding.tilServiceType.gone()
            binding.tilApplicantName.gone()
            binding.tilMobileNo.gone()
            binding.tilSaatBaaraLinkMobileNo.gone()
            binding.tilTextbox4.gone()
            binding.tilTextbox5.gone()
            binding.tilDatebox.gone()
            binding.tilAadharFrontPhoto.gone()
            binding.tilAadharBackPhoto.gone()
            binding.tilLand8APhoto.gone()
            binding.tilPanPhoto.gone()
            binding.tilCustomer8APhoto.gone()
            binding.tilOther8AOtherInfo.gone()
            binding.tilQuotationPhoto.gone()
            binding.tilCastCertPhoto.gone()
            binding.tilConsentLetterPhoto.gone()
            binding.tilPaymentScreenshot.gone()
            binding.btnSubmit.gone()
        }
        /** Selects the subtype at [position] (shared by the picker dialog and grid deep-links). */
        fun applyType(position: Int) {

                    binding.etServiceType.setText(typeList[position])
                    strTypeToShow = typeList[position]
                    val selectedTypeHashMap = typeHashMapList[position]
                    val keyFromValue = typeMap?.let { it1 -> Utils.getKeyFromNestedHashMap(it1, strTypeToShow!!) }
                    strType = keyFromValue
                    binding.tilApplicantName.visibility = if (selectedTypeHashMap["customer_name"] == "0") View.GONE else View.VISIBLE
                    binding.tilApplicantName.hint = selectedTypeHashMap["customer_name"]
                    binding.tilMobileNo.visibility = if (selectedTypeHashMap["aadhar_linked_mob"] == "0") View.GONE else View.VISIBLE
                    binding.tilMobileNo.hint = selectedTypeHashMap["aadhar_linked_mob"]
                    binding.tilSaatBaaraLinkMobileNo.visibility = if (selectedTypeHashMap["7_12_linked_mob"] == "0") View.GONE else View.VISIBLE
                    binding.tilSaatBaaraLinkMobileNo.hint = selectedTypeHashMap["7_12_linked_mob"]
                    binding.tilTextbox4.visibility = if (selectedTypeHashMap["textbox4"] == "0") View.GONE else View.VISIBLE
                    binding.tilTextbox4.hint = selectedTypeHashMap["textbox4"]
                    binding.tilTextbox5.visibility = if (selectedTypeHashMap["textbox5"] == "0") View.GONE else View.VISIBLE
                    binding.tilTextbox5.hint = selectedTypeHashMap["textbox5"]
                    binding.tilDatebox.visibility = if (selectedTypeHashMap["datebox"] == "0") View.GONE else View.VISIBLE
                    binding.tilDatebox.hint = selectedTypeHashMap["datebox"]
                    binding.tilAadharFrontPhoto.visibility = if (selectedTypeHashMap["aadhar_front_photo"] == "0") View.GONE else View.VISIBLE
                    binding.tilAadharFrontPhoto.hint = selectedTypeHashMap["aadhar_front_photo"]
                    binding.tilAadharBackPhoto.visibility = if (selectedTypeHashMap["aadhar_back_photo"] == "0") View.GONE else View.VISIBLE
                    binding.tilAadharBackPhoto.hint = selectedTypeHashMap["aadhar_back_photo"]
                    binding.tilLand8APhoto.visibility = if (selectedTypeHashMap["land_8a_photo"] == "0") View.GONE else View.VISIBLE
                    binding.tilLand8APhoto.hint = selectedTypeHashMap["land_8a_photo"]
                    binding.tilPanPhoto.visibility = if (selectedTypeHashMap["pan_photo"] == "0") View.GONE else View.VISIBLE
                    binding.tilPanPhoto.hint = selectedTypeHashMap["pan_photo"]
                    binding.tilCustomer8APhoto.visibility = if (selectedTypeHashMap["customer_8a_photo"] == "0") View.GONE else View.VISIBLE
                    binding.tilCustomer8APhoto.hint = selectedTypeHashMap["customer_8a_photo"]
                    binding.tilOther8AOtherInfo.visibility = if (selectedTypeHashMap["other_8a_photo"] == "0") View.GONE else View.VISIBLE
                    binding.tilOther8AOtherInfo.hint = selectedTypeHashMap["other_8a_photo"]
                    binding.tilQuotationPhoto.visibility = if (selectedTypeHashMap["quotation_photo"] == "0") View.GONE else View.VISIBLE
                    binding.tilQuotationPhoto.hint = selectedTypeHashMap["quotation_photo"]
                    binding.tilCastCertPhoto.visibility = if (selectedTypeHashMap["cast_cert_photo"] == "0") View.GONE else View.VISIBLE
                    binding.tilCastCertPhoto.hint = selectedTypeHashMap["cast_cert_photo"]
                    binding.tilConsentLetterPhoto.visibility = if (selectedTypeHashMap["consent_letter_photo"] == "0") View.GONE else View.VISIBLE
                    binding.tilConsentLetterPhoto.hint = selectedTypeHashMap["consent_letter_photo"]
                    binding.tilPaymentScreenshot.visibility = if (selectedTypeHashMap["payment_sc_photo"] == "0") View.GONE else View.VISIBLE
                    binding.tilPaymentScreenshot.hint = selectedTypeHashMap["payment_sc_photo"]
                    //                    when (keyFromValue) {
                    //                        TYPE_PIK_VIMA -> {
                    //                            binding.tilQuotationPhoto?.gone()
                    //                            binding.tilCastCertPhoto?.gone()
                    //                            binding.tilConsentLetterPhoto?.gone()
                    //                            binding.tilLand8APhoto?.visible()
                    //                            binding.tilPanPhoto?.gone()
                    //                        }
                    //
                    //                        TYPE_HARITGRUH -> {
                    //                            binding.tilQuotationPhoto?.visible()
                    //                            binding.tilCastCertPhoto?.visible()
                    //                            binding.tilConsentLetterPhoto?.gone()
                    //                            binding.tilLand8APhoto?.visible()
                    //                            binding.tilPanPhoto?.gone()
                    //                        }
                    //
                    //                        TYPE_KANDA_CHAL -> {
                    //                            binding.tilQuotationPhoto?.visible()
                    //                            binding.tilCastCertPhoto?.gone()
                    //                            binding.tilConsentLetterPhoto?.gone()
                    //                            binding.tilLand8APhoto?.visible()
                    //                            binding.tilPanPhoto?.gone()
                    //                        }
                    //
                    //                        TYPE_THHIBAK -> {
                    //                            binding.tilQuotationPhoto?.visible()
                    //                            binding.tilCastCertPhoto?.visible()
                    //                            binding.tilConsentLetterPhoto?.visible()
                    //                            binding.tilLand8APhoto?.visible()
                    //                            binding.tilPanPhoto?.gone()
                    //                        }
                    //
                    //                        TYPE_KISAN_CC -> {
                    //                            binding.tilQuotationPhoto?.gone()
                    //                            binding.tilCastCertPhoto?.gone()
                    //                            binding.tilConsentLetterPhoto?.gone()
                    //                            binding.tilLand8APhoto?.visible()
                    //                            binding.tilPanPhoto?.gone()
                    //                        }
                    //
                    //                        TYPE_ENGINEERING -> {
                    //                            binding.tilQuotationPhoto?.visible()
                    //                            binding.tilCastCertPhoto?.visible()
                    //                            binding.tilConsentLetterPhoto?.gone()
                    //                            binding.tilLand8APhoto?.visible()
                    //                            binding.tilPanPhoto?.gone()
                    //                        }
                    //
                    //                        TYPE_SHRAM_YOGI -> {
                    //                            binding.tilQuotationPhoto?.gone()
                    //                            binding.tilCastCertPhoto?.gone()
                    //                            binding.tilConsentLetterPhoto?.gone()
                    //                            binding.tilLand8APhoto?.gone()
                    //                            binding.tilPanPhoto?.visible()
                    //                        }
                    //
                    //                        TYPE_TYPE_1 -> {
                    //                            binding.tilQuotationPhoto?.gone()
                    //                            binding.tilCastCertPhoto?.gone()
                    //                            binding.tilConsentLetterPhoto?.gone()
                    //                            binding.tilLand8APhoto?.visible()
                    //                            binding.tilPanPhoto?.gone()
                    //                        }
                    //
                    //                        TYPE_TYPE_2 -> {
                    //                            binding.tilQuotationPhoto?.gone()
                    //                            binding.tilCastCertPhoto?.gone()
                    //                            binding.tilConsentLetterPhoto?.gone()
                    //                            binding.tilLand8APhoto?.visible()
                    //                            binding.tilPanPhoto?.gone()
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
        binding.etServiceType.setOnClickListener { //            list.add("पीक विमा")
            //            list.add("हरितगृह")
            //            list.add("कांदा चाळ")
            //            list.add("ठिबक")
            //            list.add("किसान क्रेडीट कार्ड")
            //            list.add("अभियांत्रिकीकारण (औजारे)")
            //            list.add("श्रम योगी मानधन योजना")
            //            list.add("1")
            //            list.add("2")
            MaterialDialog.Builder(mContext!!).items(typeList).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    applyType(position)
                }
            }.show()
        }
        binding.btnSubmit.setOnClickListener {
            val strServiceType = binding.etServiceType.text.toString()
            val strApplicantName = binding.etApplicantName.text.toString()
            val strMobileNo = binding.etMobileNo.text.toString()
            val strSaatBaaraLinkMobileNo = binding.etSaatBaaraLinkMobileNo.text.toString()
            val strTextbox4 = binding.etTextbox4.text.toString()
            val strTextbox5 = binding.etTextbox5.text.toString()
            val strDatebox = binding.etDatebox.text.toString()
            if (TextUtils.isEmpty(strServiceType)) {
                binding.tilServiceType.error = binding.tilServiceType.hint.toString()
                binding.etServiceType.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilApplicantName.visibility == View.VISIBLE && TextUtils.isEmpty(strApplicantName)) {
                binding.tilApplicantName.error = binding.tilApplicantName.hint.toString()
                binding.etApplicantName.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilMobileNo.visibility == View.VISIBLE && TextUtils.isEmpty(strMobileNo)) {
                binding.tilMobileNo.error = binding.tilMobileNo.hint.toString()
                binding.etMobileNo.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilSaatBaaraLinkMobileNo.visibility == View.VISIBLE && TextUtils.isEmpty(strSaatBaaraLinkMobileNo)) {
                binding.tilSaatBaaraLinkMobileNo.error = binding.tilSaatBaaraLinkMobileNo.hint.toString()
                binding.etSaatBaaraLinkMobileNo.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilTextbox4.visibility == View.VISIBLE && TextUtils.isEmpty(strTextbox4)) {
                binding.tilTextbox4.error = binding.tilTextbox4.hint.toString()
                binding.etTextbox4.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilTextbox5.visibility == View.VISIBLE && TextUtils.isEmpty(strTextbox5)) {
                binding.tilTextbox5.error = binding.tilTextbox5.hint.toString()
                binding.etTextbox5.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilDatebox.visibility == View.VISIBLE && TextUtils.isEmpty(strDatebox)) {
                binding.tilDatebox.error = binding.tilDatebox.hint.toString()
                binding.etDatebox.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilAadharFrontPhoto.visibility == View.VISIBLE && aadharFrontPhotoUri == null) {
                binding.tilAadharFrontPhoto.error = binding.tilAadharFrontPhoto.hint.toString()
                binding.etAadharFrontPhoto.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilAadharBackPhoto.visibility == View.VISIBLE && aadharBackPhotoUri == null) {
                binding.tilAadharBackPhoto.error = binding.tilAadharBackPhoto.hint.toString()
                binding.etAadharBackPhoto.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilLand8APhoto.visibility == View.VISIBLE && land8APhotoUri == null) {
                binding.tilLand8APhoto.error = binding.tilLand8APhoto.hint.toString()
                binding.etLand8APhoto.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilPanPhoto.visibility == View.VISIBLE && panPhotoUri == null) {
                binding.tilPanPhoto.error = binding.tilPanPhoto.hint.toString()
                binding.etPanPhoto.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilCustomer8APhoto.visibility == View.VISIBLE && customer8APhotoUri == null) {
                binding.tilCustomer8APhoto.error = binding.tilCustomer8APhoto.hint.toString()
                binding.etCustomer8APhoto.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilOther8AOtherInfo.visibility == View.VISIBLE && other8AOtherInfoUri == null) {
                binding.tilOther8AOtherInfo.error = binding.tilOther8AOtherInfo.hint.toString()
                binding.etOther8AOtherInfo.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilQuotationPhoto.visibility == View.VISIBLE && quotationUri == null) {
                binding.tilQuotationPhoto.error = binding.tilQuotationPhoto.hint.toString()
                binding.etQuotationPhoto.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilCastCertPhoto.visibility == View.VISIBLE && castCertUri == null) {
                binding.tilCastCertPhoto.error = binding.tilCastCertPhoto.hint.toString()
                binding.etCastCertPhoto.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilConsentLetterPhoto.visibility == View.VISIBLE && consentLetterUri == null) {
                binding.tilConsentLetterPhoto.error = binding.tilConsentLetterPhoto.hint.toString()
                binding.etConsentLetterPhoto.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilPaymentScreenshot.visibility == View.VISIBLE && paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot.error = binding.tilPaymentScreenshot.hint.toString()
                binding.etPaymentScreenshot.requestFocus()
                return@setOnClickListener
            } //            uploadAadharFPhoto()
            val uriMap = mapOf(
                "aadharFrontPhoto" to aadharFrontPhotoUri,
                "aadharBackPhoto" to aadharBackPhotoUri,
                "land8APhoto" to land8APhotoUri,
                "panPhoto" to panPhotoUri,
                "customer8APhoto" to customer8APhotoUri,
                "other8APhoto" to other8AOtherInfoUri,
                "quotationPhoto" to quotationUri,
                "castCertPhoto" to castCertUri,
                "consentLetterPhoto" to consentLetterUri,
                "paymentScreenshotPhoto" to paymentScreenshotUri,
            )
            uploadAllImagesAndCreateRecord(uriMap)
        }
        binding.etAadharFrontPhoto.setOnClickListener { choosePhotoWithPermissions(1) }
        binding.etAadharBackPhoto.setOnClickListener { choosePhotoWithPermissions(2) }
        binding.etLand8APhoto.setOnClickListener { choosePhotoWithPermissions(6) }
        binding.etCustomer8APhoto.setOnClickListener { choosePhotoWithPermissions(3) }
        binding.etOther8AOtherInfo.setOnClickListener { choosePhotoWithPermissions(4) }
        binding.etPaymentScreenshot.setOnClickListener { choosePhotoWithPermissions(5) }
        binding.etQuotationPhoto.setOnClickListener { choosePhotoWithPermissions(7) }
        binding.etCastCertPhoto.setOnClickListener { choosePhotoWithPermissions(8) }
        binding.etConsentLetterPhoto.setOnClickListener { choosePhotoWithPermissions(9) }
        binding.etPanPhoto.setOnClickListener { choosePhotoWithPermissions(10) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                longToast(getString(R.string.txt_try_later))
            }

            override fun onCanceled(source: MediaSource) {}
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                lifecycleScope.launch {
                    val compressedImageFile = mContext?.let { Compressor.compress(it, imageFiles[0].file) }
                    Log.e(TAG, "${compressedImageFile?.path}")
                    compressedImageFile?.let {
                        when (imageType) {
                            1 -> {
                                aadharFrontPhotoUri = Uri.fromFile(it)
                                binding.etAadharFrontPhoto.setText("Success") //aadharFrontPhotoUri.toString())
                            }

                            2 -> {
                                aadharBackPhotoUri = Uri.fromFile(it)
                                binding.etAadharBackPhoto.setText("Success") //aadharBackPhotoUri.toString())
                            }

                            3 -> {
                                customer8APhotoUri = Uri.fromFile(it)
                                binding.etCustomer8APhoto.setText("Success") //bankPassbookPhotoUri.toString())
                            }

                            4 -> {
                                other8AOtherInfoUri = Uri.fromFile(it)
                                binding.etOther8AOtherInfo.setText("Success") //signSpecimenUri.toString())
                            }

                            5 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot.setText("Success") //paymentScreenshotUri.toString())
                            }

                            6 -> {
                                land8APhotoUri = Uri.fromFile(it)
                                binding.etLand8APhoto.setText("Success") //land8APhotoUri.toString())
                            }

                            7 -> {
                                quotationUri = Uri.fromFile(it)
                                binding.etQuotationPhoto.setText("Success") //land8APhotoUri.toString())
                            }

                            8 -> {
                                castCertUri = Uri.fromFile(it)
                                binding.etCastCertPhoto.setText("Success") //land8APhotoUri.toString())
                            }

                            9 -> {
                                consentLetterUri = Uri.fromFile(it)
                                binding.etConsentLetterPhoto.setText("Success") //land8APhotoUri.toString())
                            }

                            10 -> {
                                panPhotoUri = Uri.fromFile(it)
                                binding.etPanPhoto.setText("Success") //land8APhotoUri.toString())
                            }
                        }
                    }
                }
            }
        })
    }

//    private fun uploadAadharFPhoto() {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(aadharFrontPhotoFileName)
//        aadharFrontPhotoUri?.let {
//            filepath.putFile(it).addOnSuccessListener {
//                try {
//                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                        aadharFrontPhotoDownloadUrl = uri.toString()
//                        aadharBackPhotoUri?.let { it1 -> uploadAadharBPhoto(it1) }
//                    }.addOnFailureListener {
//                        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                        it.printStackTrace()
//                    }
//                } catch (e: java.lang.Exception) {
//                    if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//                    e.printStackTrace()
//                }
//            }.addOnFailureListener {
//                if (cpd?.isShowing == true) cpd?.dismiss()
//                it.message?.let { it1 -> toast(it1) }
//            }.addOnProgressListener { //displaying the upload progress
//                val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
//                cpd?.setMessage("Please wait.. ")
//            }
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
//                    aadharBackPhotoDownloadUrl = uri.toString()
//                    if (binding.tilPanPhoto.visibility == View.VISIBLE && panPhotoUri != null) {
//                        panPhotoUri?.let { it1 -> uploadPanPhoto(it1) }
//                    } else {
//                        land8APhotoUri?.let { it1 -> uploadLand8A(it1) }
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
//    private fun uploadLand8A(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        land8APhotoFileName = "land_8a_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(land8APhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    land8APhotoDownloadUrl = uri.toString()
//                    customer8APhotoUri?.let { it1 -> uploadCustomer8APhoto(it1) }
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
//                    customer8APhotoUri?.let { it1 -> uploadCustomer8APhoto(it1) }
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
//    private fun uploadCustomer8APhoto(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        customer8APhotoFileName = "customer_8a_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(customer8APhotoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    customer8APhotoDownloadUrl = uri.toString()
//                    other8AOtherInfoUri?.let { it1 -> uploadOther8AOtherInfo(it1) }
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
//    private fun uploadOther8AOtherInfo(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        other8AOtherInfoFileName = "other_8a_other_info_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(other8AOtherInfoFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    other8AOtherInfoDownloadUrl = uri.toString()
//                    if (binding.tilQuotationPhoto.visibility == View.VISIBLE && quotationUri != null) quotationUri?.let { it1 -> uploadQuotation(it1) }
//                    else paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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
//    private fun uploadQuotation(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        quotationFileName = "quotation_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(quotationFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    quotationDownloadUrl = uri.toString()
//                    if (binding.tilCastCertPhoto.visibility == View.VISIBLE && castCertUri != null) castCertUri?.let { it1 -> uploadCastCert(it1) }
//                    else paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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
//    private fun uploadCastCert(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        castCertFileName = "castCert_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(castCertFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    castCertDownloadUrl = uri.toString()
//                    if (binding.tilConsentLetterPhoto.visibility == View.VISIBLE && consentLetterUri != null) consentLetterUri?.let { it1 -> uploadConsentLetter(it1) }
//                    else paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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
//    private fun uploadConsentLetter(fileUri: Uri) {
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        cpd = ProgressDialog(mContext)
//        cpd?.setCancelable(false)
//        cpd?.show()
//        consentLetterFileName = "consentLetter_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
//        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child(PATH_NAME).child(consentLetterFileName)
//        filepath.putFile(fileUri).addOnSuccessListener {
//            try {
//                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
//                    consentLetterDownloadUrl = uri.toString()
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
//        if (cpd?.isShowing == true) cpd?.dismiss()
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
                aadharFrontPhotoFileName = resultList["aadharFrontPhoto"]?.first ?: ""
                aadharFrontPhotoDownloadUrl = resultList["aadharFrontPhoto"]?.second ?: ""
                aadharBackPhotoFileName = resultList["aadharBackPhoto"]?.first ?: ""
                aadharBackPhotoDownloadUrl = resultList["aadharBackPhoto"]?.second ?: ""
                land8APhotoFileName = resultList["land8APhoto"]?.first ?: ""
                land8APhotoDownloadUrl = resultList["land8APhoto"]?.second ?: ""
                panPhotoFileName = resultList["panPhoto"]?.first ?: ""
                panPhotoDownloadUrl = resultList["panPhoto"]?.second ?: ""
                customer8APhotoFileName = resultList["customer8APhoto"]?.first ?: ""
                customer8APhotoDownloadUrl = resultList["customer8APhoto"]?.second ?: ""
                other8AOtherInfoFileName = resultList["other8APhoto"]?.first ?: ""
                other8AOtherInfoDownloadUrl = resultList["other8APhoto"]?.second ?: ""
                quotationFileName = resultList["quotationPhoto"]?.first ?: ""
                quotationDownloadUrl = resultList["quotationPhoto"]?.second ?: ""
                castCertFileName = resultList["castCertPhoto"]?.first ?: ""
                castCertDownloadUrl = resultList["castCertPhoto"]?.second ?: ""
                consentLetterFileName = resultList["consentLetterPhoto"]?.first ?: ""
                consentLetterDownloadUrl = resultList["consentLetterPhoto"]?.second ?: ""
                paymentScreenshotFileName = resultList["paymentScreenshotPhoto"]?.first ?: ""
                paymentScreenshotDownloadUrl = resultList["paymentScreenshotPhoto"]?.second ?: ""

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
        billMap["createdDateTime"] = createdDateTime //        billMap["serviceType"] = binding.etServiceType.text.toString()
        billMap["type"] = strType
        billMap["typeToShow"] = strTypeToShow
        billMap["applicantName"] = binding.etApplicantName.text.toString()
        billMap["applicantNameHint"] = binding.tilApplicantName.hint.toString()
        billMap["aadharLinkedMobileNo"] = binding.etMobileNo.text.toString()
        billMap["aadharLinkedMobileNoHint"] = binding.tilMobileNo.hint.toString()
        billMap["saatBaaraLinkMobileNo"] = binding.etSaatBaaraLinkMobileNo.text.toString()
        billMap["saatBaaraLinkMobileNoHint"] = binding.tilSaatBaaraLinkMobileNo.hint.toString()
        billMap["strTextbox4"] = binding.etTextbox4.text.toString()
        billMap["strTextbox4Hint"] = binding.tilTextbox4.hint.toString()
        billMap["strTextbox5"] = binding.etTextbox5.text.toString()
        billMap["strTextbox5Hint"] = binding.tilTextbox5.hint.toString()
        billMap["strDatebox"] = strDatebox
        billMap["strDateboxHint"] = binding.tilDatebox.hint.toString()
        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
        billMap["aadharFrontPhotoHint"] = binding.tilAadharFrontPhoto.hint.toString()
        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
        billMap["aadharBackPhotoHint"] = binding.tilAadharBackPhoto.hint.toString()
        billMap["land8APhotoFileName"] = land8APhotoFileName
        billMap["land8APhotoDownloadUrl"] = land8APhotoDownloadUrl
        billMap["land8APhotoHint"] = binding.tilLand8APhoto.hint.toString()
        billMap["panPhotoFileName"] = panPhotoFileName
        billMap["panPhotoDownloadUrl"] = panPhotoDownloadUrl
        billMap["panPhotoHint"] = binding.tilPanPhoto.hint.toString()
        billMap["customer8APhotoFileName"] = customer8APhotoFileName
        billMap["customer8APhotoDownloadUrl"] = customer8APhotoDownloadUrl
        billMap["customer8APhotoHint"] = binding.tilCustomer8APhoto.hint.toString()
        billMap["other8AOtherInfoFileName"] = other8AOtherInfoFileName
        billMap["other8AOtherInfoDownloadUrl"] = other8AOtherInfoDownloadUrl
        billMap["other8AOtherInfoHint"] = binding.tilOther8AOtherInfo.hint.toString()
        billMap["quotationFileName"] = quotationFileName
        billMap["quotationDownloadUrl"] = quotationDownloadUrl
        billMap["quotationHint"] = binding.tilQuotationPhoto.hint.toString()
        billMap["castCertFileName"] = castCertFileName
        billMap["castCertDownloadUrl"] = castCertDownloadUrl
        billMap["castCertHint"] = binding.tilCastCertPhoto.hint.toString()
        billMap["consentLetterFileName"] = consentLetterFileName
        billMap["consentLetterDownloadUrl"] = consentLetterDownloadUrl
        billMap["consentLetterHint"] = binding.tilConsentLetterPhoto.hint.toString()
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["paymentScreenshotHint"] = binding.tilPaymentScreenshot.hint.toString()
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
            val notificationItem = NotificationItem(message = "New Farmer Policy(शेतकरी योजना) added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_${PATH_NAME}")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Farmer Policy(शेतकरी योजना) added by $name")
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

    companion object {
        var PATH_NAME = "farmer_policies"
        val TYPE_PIK_VIMA = "TYPE_PIK_VIMA"
        val TYPE_HARITGRUH = "TYPE_HARITGRUH"
        val TYPE_KANDA_CHAL = "TYPE_KANDA_CHAL"
        val TYPE_THHIBAK = "TYPE_THHIBAK"
        val TYPE_KISAN_CC = "TYPE_KISAN_CC"
        val TYPE_ENGINEERING = "TYPE_ENGINEERING"
        val TYPE_SHRAM_YOGI = "TYPE_SHRAM_YOGI"
        val TYPE_TYPE_1 = "TYPE_TYPE_1"
        val TYPE_TYPE_2 = "TYPE_TYPE_2" //        val TYPE_PIK_VIMA = "पीक विमा"
        //        val TYPE_HARITGRUH = "हरितगृह"
        //        val TYPE_KANDA_CHAL = "कांदा चाळ"
        //        val TYPE_THHIBAK = "ठिबक"
        //        val TYPE_KISAN_CC = "किसान क्रेडीट कार्ड"
        //        val TYPE_ENGINEERING = "अभियांत्रिकीकारण (औजारे)"
        //        val TYPE_SHRAM_YOGI = "श्रम योगी मानधन योजना"
        //        val TYPE_TYPE_1 = "1"
        //        val TYPE_TYPE_2 = "2"
    }
}
