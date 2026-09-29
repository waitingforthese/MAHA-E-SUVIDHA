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
import rahul.jagtap.dmas.PayByUpiActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityProvidentFundBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class ProvidentFundActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    private var aadharFrontPhotoFileName: String = ""
    private var aadharFrontPhotoDownloadUrl: String = ""
    private var aadharBackPhotoFileName: String = ""
    private var aadharBackPhotoDownloadUrl: String = ""
    private var panPhotoFileName: String = ""
    private var panPhotoDownloadUrl: String = ""
    private var passbookPhotoFileName: String = ""
    private var passbookPhotoDownloadUrl: String = ""

    //    private var passportPhotoFileName: String = ""
    //    private var passportPhotoDownloadUrl: String = ""
    //    private var passportNomineePhotoFileName: String = ""
    //    private var passportNomineePhotoDownloadUrl: String = ""
    //    private var aadharNomineePhotoFileName: String = ""
    //    private var aadharNomineePhotoDownloadUrl: String = ""
    //    private var nomineePhotoFileName: String = ""
    //    private var nomineePhotoDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private val TAG = ProvidentFundActivity::class.java.simpleName
    var imageType = -1 // 1 - aadhar f, 2 - aadhar b, 3 - pan, 4 - passbook, 5 - passport, 6 - passport n, 7 - aadhar n, 8 - photo n, 9 - payment screenshot
    var aadharFrontPhotoUri: Uri? = null
    var aadharBackPhotoUri: Uri? = null
    var panPhotoUri: Uri? = null
    var passbookPhotoUri: Uri? = null

    //    var passportPhotoUri: Uri? = null
    //    var passportNomineePhotoUri: Uri? = null
    //    var aadharNomineePhotoUri: Uri? = null
    //    var nomineePhotoUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    var cpd: ProgressDialog? = null
    private lateinit var easyImage: EasyImage
    private var strTypeToShow: String? = ""
    private var strType: String? = ""
    lateinit var binding: ActivityProvidentFundBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProvidentFundBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilType, binding.tilApplicantName, binding.tilPersonName, binding.tilPersonMobileNo, binding.tilPersonEmail, binding.tilUanNumber, binding.tilPassword, binding.tilAadharFPhoto, binding.tilAadharBPhoto, binding.tilPanPhoto, binding.tilPassbookPhoto, binding.tilPassportPhoto, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_provident_fund)
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
                ?: "").putExtra("title", getString(R.string.txt_provident_fund)))
        }
        typeMap = intent.extras?.getSerializable("hashMap") as HashMap<String, HashMap<String, String>>
        val list = ArrayList<String>()
        typeMap?.forEach {
            if (it.value["type_title"] != "0") list.add(it.value["type_title"].toString())
        }
        if (list.size == 0) {
            binding.tilType.gone()
            binding.tilUanNumber.gone()
            binding.tilPassword.gone()
            binding.tilAadharFPhoto.gone()
            binding.tilAadharBPhoto.gone()
            binding.tilPanPhoto.gone()
            binding.tilPassbookPhoto.gone()
            binding.tilPassportPhoto.gone()
            binding.tilPaymentScreenshot.gone()
            binding.btnSubmit.gone()
        }
        /** Selects the subtype at [position] (shared by the picker dialog and grid deep-links). */
        fun applyType(position: Int) {

                    binding.etType.setText(list[position])
                    strTypeToShow = list[position]
                    val keyFromValue = typeMap?.let { it1 -> Utils.getKeyFromNestedHashMap(it1, strTypeToShow!!) }
                    when (keyFromValue) {
                        TYPE_CHECK_PF -> {
                            strType = TYPE_CHECK_PF //                            binding.tilUanNumber?.visible()
                            //                            binding.tilPersonMobileNo?.visible()
                            binding.tilPassword?.visible()
                            binding.tilPanPhoto?.gone()
                            binding.tilPassbookPhoto?.gone()
                            binding.tilAadharFPhoto?.gone()
                            binding.tilAadharBPhoto?.gone()
                        }

                        TYPE_UAN_ACTIVATE -> {
                            strType = TYPE_UAN_ACTIVATE //                            binding.tilUanNumber?.visible()
                            //                            binding.tilPersonMobileNo?.visible()
                            binding.tilPassword?.gone()
                            binding.tilPanPhoto?.gone()
                            binding.tilPassbookPhoto?.gone()
                            binding.tilAadharFPhoto?.visible()
                            binding.tilAadharBPhoto?.visible()
                        }

                        TYPE_CREATE_PASSWORD -> {
                            strType = TYPE_CREATE_PASSWORD //                            binding.tilUanNumber?.visible()
                            //                            binding.tilPersonMobileNo?.visible()
                            binding.tilPassword?.gone()
                            binding.tilPanPhoto?.gone()
                            binding.tilPassbookPhoto?.gone()
                            binding.tilAadharFPhoto?.gone()
                            binding.tilAadharBPhoto?.gone()
                        }

                        TYPE_KYC -> {
                            strType = TYPE_KYC //                            binding.tilUanNumber?.visible()
                            //                            binding.tilPersonMobileNo?.visible()
                            binding.tilPassword?.visible()
                            binding.tilPanPhoto?.visible()
                            binding.tilPassbookPhoto?.visible()
                            binding.tilAadharFPhoto?.visible()
                            binding.tilAadharBPhoto?.visible()
                        }

                        TYPE_PF_TRANSFER -> {
                            strType = TYPE_PF_TRANSFER //                            binding.tilUanNumber?.visible()
                            //                            binding.tilPersonMobileNo?.visible()
                            binding.tilPassword?.visible()
                            binding.tilPanPhoto?.gone()
                            binding.tilPassbookPhoto?.gone()
                            binding.tilAadharFPhoto?.gone()
                            binding.tilAadharBPhoto?.gone()
                        }

                        TYPE_MARK_EXIT -> {
                            strType = TYPE_MARK_EXIT //                            binding.tilUanNumber?.visible()
                            //                            binding.tilPersonMobileNo?.visible()
                            binding.tilPassword?.visible()
                            binding.tilPanPhoto?.gone()
                            binding.tilPassbookPhoto?.gone()
                            binding.tilAadharFPhoto?.gone()
                            binding.tilAadharBPhoto?.gone()
                        }

                        TYPE_FILL_WITHDRAWL_FORM -> {
                            strType = TYPE_FILL_WITHDRAWL_FORM //                            binding.tilUanNumber?.visible()
                            //                            binding.tilPersonMobileNo?.visible()
                            binding.tilPassword?.visible()
                            binding.tilPanPhoto?.gone()
                            binding.tilPassbookPhoto?.visible()
                            binding.tilAadharFPhoto?.gone()
                            binding.tilAadharBPhoto?.gone()
                        } //                        7 -> {
                        //                            strType = TYPE_TYPE_ONE
                        //                            binding.tilUanNumber?.gone()
                        //                            binding.tilPersonMobileNo?.gone()
                        //                            binding.tilPassword?.gone()
                        //                            binding.tilPanPhoto?.gone()
                        //                            binding.tilPassbookPhoto?.gone()
                        //                            binding.tilAadharFPhoto?.gone()
                        //                            binding.tilAadharBPhoto?.gone()
                        //                        }
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
        binding.etType?.setOnClickListener { //            val list = ArrayList<String>()
            //            list.add("पी एफ चेक करणे")
            //            list.add("यु ए न अक्तीवेत")
            //            list.add("पासवर्ड तयार करणे")
            //            list.add("के वाय सी")
            //            list.add("जुन्या कम्पनीचा पी एफ चालू कंपनी मध्ये ट्रान्स्फर करणे")
            //            list.add("एक्झिट मार्क करणे")
            //            list.add("विद्रोल फोर्म भरणे") //            list.add("1")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    applyType(position)
                }
            }.show()
        }
        binding.btnSubmit?.setOnClickListener {
            val strApplicantName = binding.etApplicantName.text.toString() //            val strPersonName = binding.etPersonName.text.toString()
            val strPersonMobileNo = binding.etPersonMobileNo.text.toString() //            val strPersonEmail = binding.etPersonEmail.text.toString()
            val strUanNumber = binding.etUanNumber.text.toString()
            val strPassword = binding.etPassword.text.toString()
            if (TextUtils.isEmpty(strTypeToShow)) {
                binding.tilType?.error = binding.tilType.hint.toString()
                binding.etType?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strApplicantName)) {
                binding.tilApplicantName?.error = binding.tilApplicantName?.hint.toString()
                binding.etApplicantName?.requestFocus()
                return@setOnClickListener
            } //            if (TextUtils.isEmpty(strPersonName)) {
            //                binding.tilPersonName?.error = binding.tilPersonName?.hint.toString()
            //                binding.etPersonName?.requestFocus()
            //                return@setOnClickListener
            //            }
            if (TextUtils.isEmpty(strPersonMobileNo)) {
                binding.tilPersonMobileNo?.error = binding.tilPersonMobileNo?.hint.toString()
                binding.etPersonMobileNo?.requestFocus()
                return@setOnClickListener
            } //            if (TextUtils.isEmpty(strPersonEmail)) {
            //                binding.tilPersonEmail?.error = binding.tilPersonEmail?.hint.toString()
            //                binding.etPersonEmail?.requestFocus()
            //                return@setOnClickListener
            //            }
            if (TextUtils.isEmpty(strUanNumber)) {
                binding.tilUanNumber?.error = binding.tilUanNumber?.hint.toString()
                binding.etUanNumber?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilPassword.visibility == View.VISIBLE && TextUtils.isEmpty(strPassword)) {
                binding.tilPassword?.error = binding.tilPassword?.hint.toString()
                binding.etPassword?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilAadharFPhoto.visibility == View.VISIBLE && aadharFrontPhotoUri == null) {
                binding.tilAadharFPhoto?.error = binding.tilAadharFPhoto?.hint.toString()
                binding.etAadharFPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilAadharBPhoto.visibility == View.VISIBLE && aadharBackPhotoUri == null) {
                binding.tilAadharBPhoto?.error = binding.tilAadharBPhoto?.hint.toString()
                binding.etAadharBPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilPanPhoto.visibility == View.VISIBLE && panPhotoUri == null) {
                binding.tilPanPhoto?.error = binding.tilPanPhoto?.hint.toString()
                binding.etPanPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilPassbookPhoto.visibility == View.VISIBLE && passbookPhotoUri == null) {
                binding.tilPassbookPhoto?.error = binding.tilPassbookPhoto?.hint.toString()
                binding.etPassbookPhoto?.requestFocus()
                return@setOnClickListener
            } //            if (passportPhotoUri == null) {
            //                etPassportPhoto?.error = etPassportPhoto?.hint.toString()
            //                etPassportPhoto?.requestFocus()
            //                return@setOnClickListener
            //            }
            //            if (passportNomineePhotoUri == null) {
            //                etPassportNomineePhoto?.error = "Select Passport Photo(Nominee)"
            //                etPassportNomineePhoto?.requestFocus()
            //                return@setOnClickListener
            //            }
            //            if (aadharNomineePhotoUri == null) {
            //                etAadharNomineePhoto?.error = "Select Aadhar Card Photo(Nominee)"
            //                etAadharNomineePhoto?.requestFocus()
            //                return@setOnClickListener
            //            }
            //            if (nomineePhotoUri == null) {
            //                etNomineePhoto?.error = "Select Photo(Nominee)"
            //                etNomineePhoto?.requestFocus()
            //                return@setOnClickListener
            //            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = binding.tilPaymentScreenshot?.hint.toString()
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            }

            //            uploadAadharFPhoto(aadharFrontPhotoUri!!)
            if (strType.equals(TYPE_CHECK_PF) || strType.equals(TYPE_CREATE_PASSWORD) || strType.equals(TYPE_PF_TRANSFER) || strType.equals(TYPE_MARK_EXIT)) {
                uploadPanPhoto(paymentScreenshotUri!!)
            } else if (strType.equals(TYPE_UAN_ACTIVATE)) {
                uploadAadharFPhoto(aadharFrontPhotoUri!!)
            } else if (strType.equals(TYPE_KYC)) {
                uploadPassbookPhoto(passbookPhotoUri!!)
            } else if (strType.equals(TYPE_FILL_WITHDRAWL_FORM)) {
                uploadPassbookPhoto(passbookPhotoUri!!)
            }
        }
        binding.btnPayByUpiId?.setOnClickListener {
            startActivity(Intent(mContext, PayByUpiActivity::class.java))
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
        } //        etPassportPhoto?.setOnClickListener {
        //            imageType = 5
        //            choosePhotoWithPermissions()
        //        }
        //        etPassportNomineePhoto?.setOnClickListener {
        //            imageType = 6
        //            choosePhotoWithPermissions()
        //        }
        //        etAadharNomineePhoto?.setOnClickListener {
        //            imageType = 7
        //            choosePhotoWithPermissions()
        //        }
        //        etNomineePhoto?.setOnClickListener {
        //            imageType = 8
        //            choosePhotoWithPermissions()
        //        }
        binding.etPaymentScreenshot?.setOnClickListener {
            imageType = 6
            choosePhotoWithPermissions()
        }
    }

    private fun uploadAadharFPhoto(fileUri: Uri) {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("provident_fund").child(aadharFrontPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
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
    }

    private fun uploadAadharBPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharBackPhotoFileName = "aadhar_back_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("provident_fund").child(aadharBackPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    aadharBackPhotoDownloadUrl = uri.toString()
                    if (binding.tilPanPhoto.visibility == View.VISIBLE && panPhotoUri != null) {
                        panPhotoUri?.let { it1 -> uploadPanPhoto(it1) }
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

    private fun uploadPanPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        panPhotoFileName = "pan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("provident_fund").child(panPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    panPhotoDownloadUrl = uri.toString()
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

    private fun uploadPassbookPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        passbookPhotoFileName = "passbook_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("provident_fund").child(passbookPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    passbookPhotoDownloadUrl = uri.toString()
                    if (binding.tilAadharFPhoto.visibility == View.VISIBLE && aadharFrontPhotoUri != null) {
                        aadharFrontPhotoUri?.let { it1 -> uploadAadharFPhoto(it1) } //                    passportPhotoUri?.let { it1 -> uploadPassportPhoto(it1) }
                    } else {
                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) } //                    passportPhotoUri?.let { it1 -> uploadPassportPhoto(it1) }
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

    //    private fun uploadPassportPhoto(fileUri: Uri) {
    //        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
    //        cpd = ProgressDialog(mContext)
    //        cpd?.setCancelable(false)
    //        cpd?.show()
    //        passportPhotoFileName = "passport_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
    //        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("provident_fund").child(passportPhotoFileName)
    //        filepath.putFile(fileUri).addOnSuccessListener {
    //            try {
    //                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
    //                    passportPhotoDownloadUrl = uri.toString()
    ////                    when {
    ////                        passportNomineePhotoUri != null -> passportNomineePhotoUri?.let { it1 -> uploadPassportNomineePhoto(it1) }
    ////                        aadharNomineePhotoUri != null -> aadharNomineePhotoUri?.let { it1 -> uploadAadharNomineePhoto(it1) }
    ////                        nomineePhotoUri != null -> nomineePhotoUri?.let { it1 -> uploadNomineePhoto(it1) }
    //                        paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
    ////                    }
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

    //    private fun uploadPassportNomineePhoto(fileUri: Uri) {
    //        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
    //        cpd = ProgressDialog(mContext)
    //        cpd?.setCancelable(false)
    //        cpd?.show()
    //        passportNomineePhotoFileName = "passport_nominee_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
    //        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("provident_fund").child(passportNomineePhotoFileName)
    //        filepath.putFile(fileUri).addOnSuccessListener {
    //            try {
    //                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
    //                    passportNomineePhotoDownloadUrl = uri.toString()
    //                    when {
    //                        aadharNomineePhotoUri != null -> aadharNomineePhotoUri?.let { it1 -> uploadAadharNomineePhoto(it1) }
    //                        nomineePhotoUri != null -> nomineePhotoUri?.let { it1 -> uploadNomineePhoto(it1) }
    //                        paymentScreenshotUri != null -> paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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
    //        }.addOnProgressListener {
    //            //displaying the upload progress
    //            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
    //            cpd?.setMessage("Please wait.. ")
    //        }
    //    }
    //
    //    private fun uploadAadharNomineePhoto(fileUri: Uri) {
    //        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
    //        cpd = ProgressDialog(mContext)
    //        cpd?.setCancelable(false)
    //        cpd?.show()
    //        aadharNomineePhotoFileName = "aadhar_nominee_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
    //        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("provident_fund").child(aadharNomineePhotoFileName)
    //        filepath.putFile(fileUri).addOnSuccessListener {
    //            try {
    //                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
    //                    aadharNomineePhotoDownloadUrl = uri.toString()
    //                    when {
    //                        nomineePhotoUri != null -> nomineePhotoUri?.let { it1 -> uploadNomineePhoto(it1) }
    //                        paymentScreenshotUri != null -> paymentScreenshotUri?.let { it1 -> uploadPaymentScreenshot(it1) }
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
    //        }.addOnProgressListener {
    //            //displaying the upload progress
    //            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
    //            cpd?.setMessage("Please wait.. ")
    //        }
    //    }
    //
    //    private fun uploadNomineePhoto(fileUri: Uri) {
    //        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
    //        cpd = ProgressDialog(mContext)
    //        cpd?.setCancelable(false)
    //        cpd?.show()
    //        nomineePhotoFileName = "nominee_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
    //        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("provident_fund").child(nomineePhotoFileName)
    //        filepath.putFile(fileUri).addOnSuccessListener {
    //            try {
    //                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
    //                    nomineePhotoDownloadUrl = uri.toString()
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

    private fun uploadPaymentScreenshot(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("provident_fund").child(paymentScreenshotFileName)
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
        billMap["applicantName"] = binding.etApplicantName.text.toString()
        billMap["type"] = strType
        billMap["typeToShow"] = strTypeToShow //        billMap["personName"] = binding.etPersonName.text.toString()
        billMap["personMobileNo"] = binding.etPersonMobileNo.text.toString()
        billMap["personEmail"] = binding.etPersonEmail.text.toString()
        billMap["uanNumber"] = binding.etUanNumber.text.toString()
        billMap["password"] = binding.etPassword.text.toString()
        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
        billMap["panPhotoFileName"] = panPhotoFileName
        billMap["panPhotoDownloadUrl"] = panPhotoDownloadUrl
        billMap["passbookPhotoFileName"] = passbookPhotoFileName
        billMap["passbookPhotoDownloadUrl"] = passbookPhotoDownloadUrl //        billMap["passportPhotoFileName"] = passportPhotoFileName
        //        billMap["passportPhotoDownloadUrl"] = passportPhotoDownloadUrl
        //        billMap["passportNomineePhotoFileName"] = passportNomineePhotoFileName
        //        billMap["passportNomineePhotoDownloadUrl"] = passportNomineePhotoDownloadUrl
        //        billMap["aadharNomineePhotoFileName"] = aadharNomineePhotoFileName
        //        billMap["aadharNomineePhotoDownloadUrl"] = aadharNomineePhotoDownloadUrl
        //        billMap["nomineePhotoFileName"] = nomineePhotoFileName
        //        billMap["nomineePhotoDownloadUrl"] = nomineePhotoDownloadUrl
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("provident_fund").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/provident_fund/$pushKey"] = billMap

        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New PF added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_provident_fund")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New PF added by $name")
        } // Create a backup record
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/provident_fund/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd?.isShowing == true) cpd?.dismiss() //        toast("PF created successfully")
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
                                binding.etAadharFPhoto?.setText("Success")
                            }
                            2 -> {
                                aadharBackPhotoUri = Uri.fromFile(it)
                                binding.etAadharBPhoto?.setText("Success")
                            }
                            3 -> {
                                panPhotoUri = Uri.fromFile(it)
                                binding.etPanPhoto?.setText("Success") //panPhotoUri.toString())
                            }
                            4 -> {
                                passbookPhotoUri = Uri.fromFile(it)
                                binding.etPassbookPhoto?.setText("Success") //passbookPhotoUri.toString())
                            } //                            5 -> {
                            //                                passportPhotoUri = Uri.fromFile(it)
                            //                                etPassportPhoto?.setText(passportPhotoUri.toString())
                            //                            }
                            //                            6 -> {
                            //                                passportNomineePhotoUri = Uri.fromFile(it)
                            //                                etPassportNomineePhoto?.setText(passportNomineePhotoUri.toString())
                            //                            }
                            //                            7 -> {
                            //                                aadharNomineePhotoUri = Uri.fromFile(it)
                            //                                etAadharNomineePhoto?.setText(aadharNomineePhotoUri.toString())
                            //                            }
                            //                            8 -> {
                            //                                nomineePhotoUri = Uri.fromFile(it)
                            //                                etNomineePhoto?.setText(nomineePhotoUri.toString())
                            //                            }
                            6 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success") //paymentScreenshotUri.toString())
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
        val TYPE_CHECK_PF = "TYPE_CHECK_PF"
        val TYPE_UAN_ACTIVATE = "TYPE_UAN_ACTIVATE"
        val TYPE_CREATE_PASSWORD = "TYPE_CREATE_PASSWORD"
        val TYPE_KYC = "TYPE_KYC"
        val TYPE_PF_TRANSFER = "TYPE_PF_TRANSFER"
        val TYPE_MARK_EXIT = "TYPE_MARK_EXIT"
        val TYPE_FILL_WITHDRAWL_FORM = "TYPE_FILL_WITHDRAWL_FORM" //        val TYPE_CHECK_PF = "पी एफ चेक करणे"
        //        val TYPE_UAN_ACTIVATE = "यु ए न ऍक्टिवेट"
        //        val TYPE_CREATE_PASSWORD = "पासवर्ड तयार करणे"
        //        val TYPE_KYC = "के वाय सी"
        //        val TYPE_PF_TRANSFER = "जुन्या कम्पनीचा पी एफ चालू कंपनी मध्ये ट्रान्स्फर करणे"
        //        val TYPE_MARK_EXIT = "एक्झिट मार्क करणे"
        //        val TYPE_FILL_WITHDRAWL_FORM = "विद्रोल फोर्म भरणे" //        val TYPE_TYPE_ONE = "1"
    }
}
