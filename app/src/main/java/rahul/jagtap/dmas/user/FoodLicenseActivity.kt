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
import rahul.jagtap.dmas.databinding.ActivityFoodLicenseBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class FoodLicenseActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    private var aadharFrontPhotoFileName: String = ""
    private var aadharFrontPhotoDownloadUrl: String = ""
    private var aadharBackPhotoFileName: String = ""
    private var aadharBackPhotoDownloadUrl: String = ""
    private var addressProofPhotoFileName: String = ""
    private var addressProofPhotoDownloadUrl: String = ""
    private var panPhotoFileName: String = ""
    private var panPhotoDownloadUrl: String = ""
    //    private var gpnocPhotoFileName: String = ""
    //    private var gpnocPhotoDownloadUrl: String = ""
    private var photoFileName: String = ""
    private var photoDownloadUrl: String = ""
    //    private var signPhotoFileName: String = ""
    //    private var signPhotoDownloadUrl: String = ""
    private var udyamAadharPhotoFileName: String = ""
    private var udyamAadharPhotoDownloadUrl: String = ""
    private var oldFoodLicensePhotoFileName: String = ""
    private var oldFoodLicensePhotoDownloadUrl: String = ""
    private var rcBookPhotoFileName: String = ""
    private var rcBookPhotoDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private val TAG = FoodLicenseActivity::class.java.simpleName
    var imageType = -1 // 1 - aadhar f, 2 - aadhar b, 3 - address proof, 4 - pan, 3 - gpnoc, 4 - photo, 5 - sign, 6 - payment sc
    var aadharFrontPhotoUri: Uri? = null
    var aadharBackPhotoUri: Uri? = null
    var addressProofPhotoUri: Uri? = null
    var panPhotoUri: Uri? = null
    //    var gpnocPhotoUri: Uri? = null
    var photoUri: Uri? = null
    //    var signPhotoUri: Uri? = null
    var udyamAadharPhotoUri: Uri? = null
    var oldFoodLicensePhotoUri: Uri? = null
    var rcBookPhotoUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    var cpd: ProgressDialog? = null
    private lateinit var easyImage: EasyImage
    private var strTypeToShow: String? = ""
    private var strType: String? = ""
    lateinit var binding: ActivityFoodLicenseBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodLicenseBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilType, binding.tilApplicantName, binding.tilBusinessName, binding.tilBusinessAddress, binding.tilOwnerMobileNo, binding.tilOwnerEmail, binding.tilYearsLicense, binding.tilProductSales, binding.tilAadharFPhoto, binding.tilAadharBPhoto, binding.tilAddressProofPhoto, binding.tilPanPhoto, binding.tilGPNOCPhoto, binding.tilPhoto, binding.tilSignPhoto, binding.tilUdyamAadharPhoto, binding.tilOldFoodLicensePhoto, binding.tilRCBookPhoto, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_food_license)
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
                ?: "").putExtra("title", getString(R.string.txt_food_license)))
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
            binding.tilOwnerMobileNo.gone()
            binding.tilOwnerEmail.gone()
            binding.tilYearsLicense.gone()
            binding.tilProductSales.gone()
            binding.tilAadharFPhoto.gone()
            binding.tilAadharBPhoto.gone()
            binding.tilAddressProofPhoto.gone()
            binding.tilPanPhoto.gone()
//            binding.tilGPNOCPhoto.gone()
            binding.tilPhoto.gone()
            binding.tilSignPhoto.gone()
            binding.tilUdyamAadharPhoto.gone()
            binding.tilOldFoodLicensePhoto.gone()
            binding.tilPaymentScreenshot.gone()
            binding.btnSubmit.gone()
        }
        /** Selects the subtype at [position] (shared by the picker dialog and grid deep-links). */
        fun applyType(position: Int) {

                    binding.etType.setText(list[position])
                    strTypeToShow = list[position]
                    val keyFromValue = typeMap?.let { it1 -> Utils.getKeyFromNestedHashMap(it1, strTypeToShow!!) }
                    when (keyFromValue) {
                        TYPE_1_YR -> {
                            strType = TYPE_1_YR
                            binding.tilBusinessAddress?.visible()
                            binding.tilOwnerEmail?.visible()
                            binding.tilProductSales?.visible()
                            binding.tilAadharFPhoto?.visible()
                            binding.tilAadharBPhoto?.visible()
                            binding.tilAddressProofPhoto?.visible()
                            binding.tilPanPhoto?.visible()
//                            binding.tilGPNOCPhoto?.visible()
                            binding.tilPhoto?.visible()
//                            binding.tilSignPhoto?.visible()
                            binding.tilUdyamAadharPhoto?.visible()
                            binding.tilOldFoodLicensePhoto?.gone()
                            binding.tilRCBookPhoto?.gone()
                        }

                        TYPE_2_YR -> {
                            strType = TYPE_2_YR
                            binding.tilBusinessAddress?.visible()
                            binding.tilOwnerEmail?.visible()
                            binding.tilProductSales?.visible()
                            binding.tilAadharFPhoto?.visible()
                            binding.tilAadharBPhoto?.visible()
                            binding.tilAddressProofPhoto?.visible()
                            binding.tilPanPhoto?.visible()
//                            binding.tilGPNOCPhoto?.visible()
                            binding.tilPhoto?.visible()
//                            binding.tilSignPhoto?.visible()
                            binding.tilUdyamAadharPhoto?.visible()
                            binding.tilOldFoodLicensePhoto?.gone()
                            binding.tilRCBookPhoto?.gone()
                        }

                        TYPE_3_YR -> {
                            strType = TYPE_3_YR
                            binding.tilBusinessAddress?.visible()
                            binding.tilOwnerEmail?.visible()
                            binding.tilProductSales?.visible()
                            binding.tilAadharFPhoto?.visible()
                            binding.tilAadharBPhoto?.visible()
                            binding.tilAddressProofPhoto?.visible()
                            binding.tilPanPhoto?.visible()
//                            binding.tilGPNOCPhoto?.visible()
                            binding.tilPhoto?.visible()
//                            binding.tilSignPhoto?.visible()
                            binding.tilUdyamAadharPhoto?.visible()
                            binding.tilOldFoodLicensePhoto?.gone()
                            binding.tilRCBookPhoto?.gone()
                        }

                        TYPE_4_YR -> {
                            strType = TYPE_4_YR
                            binding.tilBusinessAddress?.visible()
                            binding.tilOwnerEmail?.visible()
                            binding.tilProductSales?.visible()
                            binding.tilAadharFPhoto?.visible()
                            binding.tilAadharBPhoto?.visible()
                            binding.tilAddressProofPhoto?.visible()
                            binding.tilPanPhoto?.visible()
//                            binding.tilGPNOCPhoto?.visible()
                            binding.tilPhoto?.visible()
//                            binding.tilSignPhoto?.visible()
                            binding.tilUdyamAadharPhoto?.visible()
                            binding.tilOldFoodLicensePhoto?.gone()
                            binding.tilRCBookPhoto?.gone()
                        }
                        TYPE_5_YR -> {
                            strType = TYPE_5_YR
                            binding.tilBusinessAddress?.visible()
                            binding.tilOwnerEmail?.visible()
                            binding.tilProductSales?.visible()
                            binding.tilAadharFPhoto?.visible()
                            binding.tilAadharBPhoto?.visible()
                            binding.tilAddressProofPhoto?.visible()
                            binding.tilPanPhoto?.visible()
//                            binding.tilGPNOCPhoto?.visible()
                            binding.tilPhoto?.visible()
//                            binding.tilSignPhoto?.visible()
                            binding.tilUdyamAadharPhoto?.visible()
                            binding.tilOldFoodLicensePhoto?.gone()
                            binding.tilRCBookPhoto?.gone()
                        }
                        TYPE_RENEW -> {
                            strType = TYPE_RENEW
                            binding.tilBusinessAddress?.gone()
                            binding.tilOwnerEmail?.gone()
                            binding.tilProductSales?.gone()
                            binding.tilAadharFPhoto?.gone()
                            binding.tilAadharBPhoto?.gone()
                            binding.tilAddressProofPhoto?.gone()
                            binding.tilPanPhoto?.gone()
//                            binding.tilGPNOCPhoto?.gone()
                            binding.tilPhoto?.gone()
//                            binding.tilSignPhoto?.gone()
                            binding.tilUdyamAadharPhoto?.gone()
                            binding.tilOldFoodLicensePhoto?.visible()
                            binding.tilRCBookPhoto?.visible()
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
        binding.etType?.setOnClickListener { //            val list = ArrayList<String>()
            //            list.add("1 वर्ष")
            //            list.add("2 वर्ष")
            //            list.add("3 वर्ष")
            //            list.add("4 वर्ष")
            //            list.add("5 वर्ष")
            //            list.add("RENEW")
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
            val strBusinessAddress = binding.etBusinessAddress.text.toString() //            val strOwnerName = etOwnerName.text.toString()
            val strOwnerMobileNo = binding.etOwnerMobileNo.text.toString()
            val strOwnerEmail = binding.etOwnerEmail.text.toString()
            val strYearsLicense = binding.etYearsLicense.text.toString()
            val strProductSales = binding.etProductSales.text.toString()
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
            if (binding.tilBusinessAddress.visibility == View.VISIBLE && TextUtils.isEmpty(strBusinessAddress)) {
                binding.tilBusinessAddress?.error = "व्यवसाय पत्ता टाका"
                binding.etBusinessAddress?.requestFocus()
                return@setOnClickListener
            } //            if (TextUtils.isEmpty(strOwnerName)) {
            //                etOwnerName?.error = "मालकाचे नाव टाका"
            //                etOwnerName?.requestFocus()
            //                return@setOnClickListener
            //            }
            if (TextUtils.isEmpty(strOwnerMobileNo)) {
                binding.tilOwnerMobileNo?.error = "मालकाचा मोबाईल नंबर टाका"
                binding.etOwnerMobileNo?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilOwnerEmail.visibility == View.VISIBLE && TextUtils.isEmpty(strOwnerEmail)) {
                binding.tilOwnerEmail?.error = "मालकाचा ईमेल आयडी टाका"
                binding.etOwnerEmail?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strYearsLicense)) {
                binding.tilYearsLicense?.error = "वर्षांची संख्या टाका"
                binding.etYearsLicense?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilProductSales.visibility == View.VISIBLE && TextUtils.isEmpty(strProductSales)) {
                binding.tilProductSales?.error = "व्यवसायाची संपूर्ण माहिती द्या"
                binding.etProductSales?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilAadharFPhoto.visibility == View.VISIBLE && aadharFrontPhotoUri == null) {
                binding.tilAadharFPhoto?.error = "आधार कार्ड च्या पुढील बाजूचा फोटो निवडा"
                binding.etAadharFPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilAadharBPhoto.visibility == View.VISIBLE && aadharBackPhotoUri == null) {
                binding.tilAadharBPhoto?.error = "आधार कार्ड च्या मागील बाजूचा फोटो निवडा"
                binding.etAadharBPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilAddressProofPhoto.visibility == View.VISIBLE && addressProofPhotoUri == null) {
                binding.tilAddressProofPhoto?.error = "ऍड्रेस प्रूफ फोटो निवडा"
                binding.etAddressProofPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilPanPhoto.visibility == View.VISIBLE && panPhotoUri == null) {
                binding.tilPanPhoto?.error = "पॅन कार्ड फोटो निवडा"
                binding.etPanPhoto?.requestFocus()
                return@setOnClickListener
            } //            if (gpnocPhotoUri == null) {
            //                etGPNOCPhoto?.error = "ग्रामपंचायत ना हरकत फोटो निवडा"
            //                etGPNOCPhoto?.requestFocus()
            //                return@setOnClickListener
            //            }
            if (binding.tilPhoto.visibility == View.VISIBLE && photoUri == null) {
                binding.tilPhoto?.error = "फोटो निवडा"
                binding.etPhoto?.requestFocus()
                return@setOnClickListener
            } //            if (signPhotoUri == null) {
            //                etSignPhoto?.error = "स्वाक्षरी फोटो निवडा"
            //                etSignPhoto?.requestFocus()
            //                return@setOnClickListener
            //            }
            if (binding.tilUdyamAadharPhoto.visibility == View.VISIBLE && udyamAadharPhotoUri == null) {
                binding.tilUdyamAadharPhoto?.error = binding.tilUdyamAadharPhoto?.hint.toString()
                binding.etUdyamAadharPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (strType.equals(TYPE_RENEW) && oldFoodLicensePhotoUri == null) {
                binding.tilOldFoodLicensePhoto?.error = binding.tilOldFoodLicensePhoto.hint.toString()
                binding.etOldFoodLicensePhoto?.requestFocus()
                return@setOnClickListener
            }
            if (strType.equals(TYPE_RENEW) && rcBookPhotoUri == null) {
                binding.tilRCBookPhoto?.error = binding.tilRCBookPhoto.hint.toString()
                binding.etRCBookPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = "पेमेंट स्क्रीनशॉट निवडा"
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilAadharFPhoto.visibility == View.VISIBLE && aadharFrontPhotoUri != null) {
                uploadAadharFPhoto(aadharFrontPhotoUri!!)
            } else if (binding.tilOldFoodLicensePhoto.visibility == View.VISIBLE && strType == TYPE_RENEW) {
                uploadOldFoodLicensePhoto(oldFoodLicensePhotoUri!!)
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
        binding.etAddressProofPhoto?.setOnClickListener {
            imageType = 3
            choosePhotoWithPermissions()
        }
        binding.etPanPhoto?.setOnClickListener {
            imageType = 4
            choosePhotoWithPermissions()
        } //        binding.etGPNOCPhoto?.setOnClickListener {
        //            imageType = 5
        //            choosePhotoWithPermissions()
        //        }
        binding.etPhoto?.setOnClickListener {
            imageType = 6
            choosePhotoWithPermissions()
        } //        binding.etSignPhoto?.setOnClickListener {
        //            imageType = 7
        //            choosePhotoWithPermissions()
        //        }
        binding.etUdyamAadharPhoto?.setOnClickListener {
            imageType = 8
            choosePhotoWithPermissions()
        }
        binding.etPaymentScreenshot?.setOnClickListener {
            imageType = 9
            choosePhotoWithPermissions()
        }
        binding.etOldFoodLicensePhoto?.setOnClickListener {
            imageType = 10
            choosePhotoWithPermissions()
        }
        binding.etRCBookPhoto?.setOnClickListener {
            imageType = 11
            choosePhotoWithPermissions()
        }
    }

    private fun uploadAadharFPhoto(fileUri: Uri) {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("food_license").child(aadharFrontPhotoFileName)
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
        aadharBackPhotoFileName = "aadhar_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("food_license").child(aadharBackPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    aadharBackPhotoDownloadUrl = uri.toString()
                    addressProofPhotoUri?.let { it1 -> uploadAddressProofPhoto(it1) }
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

    private fun uploadAddressProofPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        addressProofPhotoFileName = "address_proof_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("food_license").child(addressProofPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    addressProofPhotoDownloadUrl = uri.toString()
                    panPhotoUri?.let { it1 -> uploadPanPhoto(it1) }
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("food_license").child(panPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    panPhotoDownloadUrl = uri.toString() //                    if (gpnocPhotoUri != null) {
                    //                        gpnocPhotoUri?.let { it1 -> uploadGpNocPhoto(it1) }
                    //                    } else {
                    photoUri?.let { it1 -> uploadPhoto(it1) } //                    }
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

    //    private fun uploadGpNocPhoto(fileUri: Uri) {
    //        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
    //        cpd = ProgressDialog(mContext)
    //        cpd?.setCancelable(false)
    //        cpd?.show()
    //        gpnocPhotoFileName = "gpnoc_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
    //        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("food_license").child(gpnocPhotoFileName)
    //        filepath.putFile(fileUri).addOnSuccessListener {
    //            try {
    //                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
    //                    gpnocPhotoDownloadUrl = uri.toString()
    //                    photoUri?.let { it1 -> uploadPhoto(it1) }
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

    private fun uploadPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        photoFileName = "photo_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("food_license").child(photoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    photoDownloadUrl = uri.toString() //                    signPhotoUri?.let { it1 -> uploadSignPhoto(it1) }
                    udyamAadharPhotoUri?.let { it1 -> uploadUdyamAadharPhoto(it1) }
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

    //    private fun uploadSignPhoto(fileUri: Uri) {
    //        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
    //        cpd = ProgressDialog(mContext)
    //        cpd?.setCancelable(false)
    //        cpd?.show()
    //        signPhotoFileName = "sign_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
    //        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("food_license").child(signPhotoFileName)
    //        filepath.putFile(fileUri).addOnSuccessListener {
    //            try {
    //                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
    //                    signPhotoDownloadUrl = uri.toString()
    //                    udyamAadharPhotoUri?.let { it1 -> uploadUdyamAadharPhoto(it1) }
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

    private fun uploadUdyamAadharPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        udyamAadharPhotoFileName = "udyam_aadhar_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("food_license").child(udyamAadharPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    udyamAadharPhotoDownloadUrl = uri.toString()
                    if (strType.equals(TYPE_RENEW) && oldFoodLicensePhotoUri != null) {
                        oldFoodLicensePhotoUri?.let { it1 -> uploadOldFoodLicensePhoto(it1) }
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

    private fun uploadOldFoodLicensePhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        oldFoodLicensePhotoFileName = "old_food_license_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("food_license").child(oldFoodLicensePhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    oldFoodLicensePhotoDownloadUrl = uri.toString()
                    if (strType.equals(TYPE_RENEW) && rcBookPhotoUri != null) {
                        rcBookPhotoUri?.let { it1 -> uploadRCBookPhoto(it1) }
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

    private fun uploadRCBookPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        rcBookPhotoFileName = "rc_book_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("food_license").child(rcBookPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    rcBookPhotoDownloadUrl = uri.toString()
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

    private fun uploadPaymentScreenshot(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("food_license").child(paymentScreenshotFileName)
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
        billMap["businessName"] = binding.etBusinessName.text.toString()
        billMap["businessAddress"] = binding.etBusinessAddress.text.toString() //        billMap["ownerName"] = etOwnerName.text.toString()
        billMap["ownerMobileNo"] = binding.etOwnerMobileNo.text.toString()
        billMap["ownerEmail"] = binding.etOwnerEmail.text.toString()
        billMap["yearsLicense"] = binding.etYearsLicense.text.toString()
        billMap["productSales"] = binding.etProductSales.text.toString()
        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
        billMap["addressProofPhotoFileName"] = addressProofPhotoFileName
        billMap["addressProofPhotoDownloadUrl"] = addressProofPhotoDownloadUrl
        billMap["panPhotoFileName"] = panPhotoFileName
        billMap["panPhotoDownloadUrl"] = panPhotoDownloadUrl //        billMap["gpnocPhotoFileName"] = gpnocPhotoFileName
        //        billMap["gpnocPhotoDownloadUrl"] = gpnocPhotoDownloadUrl
        billMap["photoFileName"] = photoFileName
        billMap["photoDownloadUrl"] = photoDownloadUrl //        billMap["signPhotoFileName"] = signPhotoFileName
        //        billMap["signPhotoDownloadUrl"] = signPhotoDownloadUrl
        billMap["udyamAadharPhotoFileName"] = udyamAadharPhotoFileName
        billMap["udyamAadharPhotoDownloadUrl"] = udyamAadharPhotoDownloadUrl
        billMap["oldFoodLicensePhotoFileName"] = oldFoodLicensePhotoFileName
        billMap["oldFoodLicensePhotoDownloadUrl"] = oldFoodLicensePhotoDownloadUrl
        billMap["rcBookPhotoFileName"] = rcBookPhotoFileName
        billMap["rcBookPhotoDownloadUrl"] = rcBookPhotoDownloadUrl
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("food_license").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/food_license/$pushKey"] = billMap

        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Food License added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_food_license")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Food License added by $name")
        } // Create a backup record
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/food_license/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd?.isShowing == true) cpd?.dismiss() //        toast("फूड लायसन्स अर्ज सादर केला")
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
                                addressProofPhotoUri = Uri.fromFile(it)
                                binding.etAddressProofPhoto?.setText("Success") //addressProofPhotoUri.toString())
                            }

                            4 -> {
                                panPhotoUri = Uri.fromFile(it)
                                binding.etPanPhoto?.setText("Success") //panPhotoUri.toString())
                            } //                                    5 -> {
                            //                                        gpnocPhotoUri = Uri.fromFile(it)
                            //                                        etGPNOCPhoto?.setText(gpnocPhotoUri.toString())
                            //                                    }
                            6 -> {
                                photoUri = Uri.fromFile(it)
                                binding.etPhoto?.setText("Success") //photoUri.toString())
                            } //                            7 -> {
                            //                                signPhotoUri = Uri.fromFile(it)
                            //                                etSignPhoto?.setText(signPhotoUri.toString())
                            //                            }
                            8 -> {
                                udyamAadharPhotoUri = Uri.fromFile(it)
                                binding.etUdyamAadharPhoto?.setText("Success") //udyamAadharPhotoUri.toString())
                            }

                            9 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success") //paymentScreenshotUri.toString())
                            }

                            10 -> {
                                oldFoodLicensePhotoUri = Uri.fromFile(it)
                                binding.etOldFoodLicensePhoto?.setText("Success") //paymentScreenshotUri.toString())
                            }
                            11 -> {
                                rcBookPhotoUri = Uri.fromFile(it)
                                binding.etRCBookPhoto?.setText("Success") //paymentScreenshotUri.toString())
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
        val TYPE_1_YR = "TYPE_1_YR"
        val TYPE_2_YR = "TYPE_2_YR"
        val TYPE_3_YR = "TYPE_3_YR"
        val TYPE_4_YR = "TYPE_4_YR"
        val TYPE_5_YR = "TYPE_5_YR"
        val TYPE_RENEW = "TYPE_RENEW" //        val TYPE_1_YR = "1 वर्ष"
        //        val TYPE_2_YR = "2 वर्ष"
        //        val TYPE_3_YR = "3 वर्ष"
        //        val TYPE_4_YR = "4 वर्ष"
        //        val TYPE_5_YR = "5 वर्ष"
        //        val TYPE_RENEW = "renew"
    }
}
