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
import rahul.jagtap.dmas.databinding.ActivityElectionCardBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class ElectionCardActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    private var passportPhotoFileName: String = ""
    private var passportPhotoDownloadUrl: String = ""
    private var aadharFrontPhotoFileName: String = ""
    private var aadharFrontPhotoDownloadUrl: String = ""
    private var aadharBackPhotoFileName: String = ""
    private var aadharBackPhotoDownloadUrl: String = ""
    private var electionCardFrontPhotoFileName: String = ""
    private var electionCardFrontPhotoDownloadUrl: String = ""
    private var electionCardBackPhotoFileName: String = ""
    private var electionCardBackPhotoDownloadUrl: String = ""
    private var oldElectionCardFrontPhotoFileName: String = ""
    private var oldElectionCardFrontPhotoDownloadUrl: String = ""
    private var oldElectionCardBackPhotoFileName: String = ""
    private var oldElectionCardBackPhotoDownloadUrl: String = ""
//    private var correctionProofPhotoFileName: String = ""
//    private var correctionProofPhotoDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private val TAG = ElectionCardActivity::class.java.simpleName
    var imageType = -1 // 1 - passport, 2 - aadhar f, 3 - aadhar b, 4 - election f, 5 - election b, 6 - old election f, 7 - old election b, 8 - correction proof, 9 - payment screenshot
    var passportPhotoUri: Uri? = null
    var aadharFrontPhotoUri: Uri? = null
    var aadharBackPhotoUri: Uri? = null
    var electionCardFrontPhotoUri: Uri? = null
    var electionCardBackPhotoUri: Uri? = null
    var oldElectionCardFrontPhotoUri: Uri? = null
    var oldElectionCardBackPhotoUri: Uri? = null
//    var correctionProofPhotoUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    var cpd: ProgressDialog? = null
    private lateinit var easyImage: EasyImage
    private var strTypeToShow: String? = ""
    private var strType: String? = ""
    lateinit var binding: ActivityElectionCardBinding
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElectionCardBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilType, binding.tilApplicantName, binding.tilFullName, binding.tilMobileNo, binding.tilEmail, binding.tilVidhansabha, binding.tilPassportPhoto, binding.tilAadharFPhoto, binding.tilAadharBPhoto, binding.tilElectionCardFPhoto, binding.tilElectionCardBPhoto, binding.tilOldElectionFPhoto, binding.tilOldElectionBPhoto, binding.tilCorrectionProof, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "इलेक्शन कार्ड"
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
                ?: "").putExtra("title", "इलेक्शन कार्ड"))
        }

        binding.btnSubmit?.setOnClickListener {
            val strApplicantName = binding.etApplicantName.text.toString()
            val strType = binding.etType.text.toString() //            val strFullName = binding.etFullName.text.toString()
            val strMobileNo = binding.etMobileNo.text.toString()
            val strVidhansabha = binding.etVidhansabha.text.toString()
            if (TextUtils.isEmpty(strType)) {
                binding.tilType?.error = binding.tilType?.hint.toString()
                binding.etType?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strApplicantName)) {
                binding.tilApplicantName?.error = "कस्टमरचे नाव टाका"
                binding.etApplicantName?.requestFocus()
                return@setOnClickListener
            }
            //            if (TextUtils.isEmpty(strFullName)) {
            //                etFullName?.error = etFullName?.hint.toString()
            //                etFullName?.requestFocus()
            //                return@setOnClickListener
            //            }
            if (TextUtils.isEmpty(strMobileNo)) {
                binding.tilMobileNo?.error = binding.tilMobileNo?.hint.toString()
                binding.etMobileNo?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strVidhansabha)) {
                binding.tilVidhansabha?.error = binding.tilVidhansabha?.hint.toString()
                binding.etVidhansabha?.requestFocus()
                return@setOnClickListener
            }
            if (passportPhotoUri == null) {
                binding.tilPassportPhoto?.error = binding.tilPassportPhoto?.hint.toString()
                binding.etPassportPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (aadharFrontPhotoUri == null) {
                binding.tilAadharFPhoto?.error = binding.tilAadharFPhoto?.hint.toString()
                binding.etAadharFPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (aadharBackPhotoUri == null) {
                binding.tilAadharBPhoto?.error = binding.tilAadharBPhoto?.hint.toString()
                binding.etAadharBPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilElectionCardFPhoto?.visibility == View.VISIBLE && electionCardFrontPhotoUri == null) {
                binding.tilElectionCardFPhoto?.error = binding.tilElectionCardFPhoto?.hint.toString()
                binding.etElectionCardFPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilElectionCardBPhoto?.visibility == View.VISIBLE && electionCardBackPhotoUri == null) {
                binding.tilElectionCardBPhoto?.error = binding.tilElectionCardBPhoto?.hint.toString()
                binding.etElectionCardBPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilOldElectionFPhoto?.visibility == View.VISIBLE && oldElectionCardFrontPhotoUri == null) {
                binding.tilOldElectionFPhoto?.error = binding.tilOldElectionFPhoto?.hint.toString()
                binding.etOldElectionFPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (binding.tilOldElectionBPhoto?.visibility == View.VISIBLE && oldElectionCardBackPhotoUri == null) {
                binding.tilOldElectionBPhoto?.error = binding.tilOldElectionBPhoto?.hint.toString()
                binding.etOldElectionBPhoto?.requestFocus()
                return@setOnClickListener
            } //            if (etCorrectionProof?.visibility == View.VISIBLE && correctionProofPhotoUri == null) {
            //                etCorrectionProof?.error = etCorrectionProof?.hint.toString()
            //                etCorrectionProof?.requestFocus()
            //                return@setOnClickListener
            //            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = binding.tilPaymentScreenshot?.hint.toString()
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            }
            uploadPassportPhoto()
        }
        binding.btnPayByUpiId?.setOnClickListener {
            startActivity(Intent(mContext, PayByUpiActivity::class.java))
        }
        typeMap = intent.extras?.getSerializable("hashMap") as HashMap<String, HashMap<String, String>>
        val list = ArrayList<String>()
        typeMap?.forEach {
            if (it.value["type_title"] != "0") list.add(it.value["type_title"].toString())
        }
        if (list.size == 0) {
            binding.tilType.gone()
            binding.tilMobileNo.gone()
            binding.tilEmail.gone()
            binding.tilVidhansabha.gone()
            binding.tilPassportPhoto.gone()
            binding.tilAadharFPhoto.gone()
            binding.tilAadharBPhoto.gone()
            binding.tilElectionCardFPhoto.gone()
            binding.tilElectionCardBPhoto.gone()
            binding.tilOldElectionFPhoto.gone()
            binding.tilOldElectionBPhoto.gone()
            binding.tilCorrectionProof.gone()
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
                            binding.tilElectionCardFPhoto?.visible()
                            binding.tilElectionCardBPhoto?.visible()
                            binding.tilOldElectionFPhoto?.gone()
                            binding.tilOldElectionBPhoto?.gone() //                            binding.tilCorrectionProof?.gone()
                        }
                        TYPE_DURUSTI -> {
                            strType = TYPE_DURUSTI
                            binding.tilElectionCardFPhoto?.gone()
                            binding.tilElectionCardBPhoto?.gone()
                            binding.tilOldElectionFPhoto?.visible()
                            binding.tilOldElectionBPhoto?.visible() //                            binding.tilCorrectionProof?.visible()
                        }
                        TYPE_LOST -> {
                            strType = TYPE_LOST
                            binding.tilElectionCardFPhoto?.gone()
                            binding.tilElectionCardBPhoto?.gone()
                            binding.tilOldElectionFPhoto?.visible()
                            binding.tilOldElectionBPhoto?.visible() //                            etCorrectionProof?.gone()
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
//            list.add("हरविलेले")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    applyType(position)
                }
            }.show()
        }
        binding.etPassportPhoto?.setOnClickListener {
            imageType = 1
            choosePhotoWithPermissions()
        }
        binding.etAadharFPhoto?.setOnClickListener {
            imageType = 2
            choosePhotoWithPermissions()
        }
        binding.etAadharBPhoto?.setOnClickListener {
            imageType = 3
            choosePhotoWithPermissions()
        }
        binding.etElectionCardFPhoto?.setOnClickListener {
            imageType = 4
            choosePhotoWithPermissions()
        }
        binding.etElectionCardBPhoto?.setOnClickListener {
            imageType = 5
            choosePhotoWithPermissions()
        }
        binding.etOldElectionFPhoto?.setOnClickListener {
            imageType = 6
            choosePhotoWithPermissions()
        }
        binding.etOldElectionBPhoto?.setOnClickListener {
            imageType = 7
            choosePhotoWithPermissions()
        }
        binding.etCorrectionProof?.setOnClickListener {
            imageType = 8
            choosePhotoWithPermissions()
        }
        binding.etPaymentScreenshot?.setOnClickListener {
            imageType = 9
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
                                passportPhotoUri = Uri.fromFile(it)
                                binding.etPassportPhoto?.setText("Success")//passportPhotoUri.toString())
                            }

                            2 -> {
                                aadharFrontPhotoUri = Uri.fromFile(it)
                                binding.etAadharFPhoto?.setText("Success")//aadharFrontPhotoUri.toString())
                            }

                            3 -> {
                                aadharBackPhotoUri = Uri.fromFile(it)
                                binding.etAadharBPhoto?.setText("Success")//aadharBackPhotoUri.toString())
                            }

                            4 -> {
                                electionCardFrontPhotoUri = Uri.fromFile(it)
                                binding.etElectionCardFPhoto?.setText("Success")//electionCardFrontPhotoUri.toString())
                            }

                            5 -> {
                                electionCardBackPhotoUri = Uri.fromFile(it)
                                binding.etElectionCardBPhoto?.setText("Success")//electionCardBackPhotoUri.toString())
                            }

                            6 -> {
                                oldElectionCardFrontPhotoUri = Uri.fromFile(it)
                                binding.etOldElectionFPhoto?.setText("Success")//oldElectionCardFrontPhotoUri.toString())
                            }

                            7 -> {
                                oldElectionCardBackPhotoUri = Uri.fromFile(it)
                                binding.etOldElectionBPhoto?.setText("Success")//oldElectionCardBackPhotoUri.toString())
                            }

//                            8 -> {
//                                correctionProofPhotoUri = Uri.fromFile(it)
//                                etCorrectionProof?.setText("Success")//correctionProofPhotoUri.toString())
//                            }

                            9 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success")//paymentScreenshotUri.toString())
                            }
                        }
                    }
                }
            }
        })
    }

    private fun uploadPassportPhoto() {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        if (passportPhotoUri != null) {
            passportPhotoFileName = "passport_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
            val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("election_cards").child(passportPhotoFileName)
            filepath.putFile(passportPhotoUri!!).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        passportPhotoDownloadUrl = uri.toString()
                        uploadAadharFrontPhoto()
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
        } else {
            if (cpd?.isShowing == true) cpd?.dismiss()
            uploadAadharFrontPhoto()
        }
    }

    private fun uploadAadharFrontPhoto() {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharFrontPhotoFileName = "aadhar_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("election_cards").child(aadharFrontPhotoFileName)
        aadharFrontPhotoUri?.let {
            filepath.putFile(it).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        aadharFrontPhotoDownloadUrl = uri.toString()
                        uploadAadharBackPhoto()
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
    }

    private fun uploadAadharBackPhoto() {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        aadharBackPhotoFileName = "aadhar_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("election_cards").child(aadharBackPhotoFileName)
        aadharBackPhotoUri?.let {
            filepath.putFile(it).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        aadharBackPhotoDownloadUrl = uri.toString()
                        if (binding.tilElectionCardFPhoto.visibility == View.VISIBLE) {
                            uploadElectionCardFrontPhoto()
                        } else {
                            uploadOldElectionCardFrontPhoto()
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
    }

    private fun uploadElectionCardFrontPhoto() {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        if (electionCardFrontPhotoUri != null) {
            electionCardFrontPhotoFileName = "election_card_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
            val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("election_cards").child(electionCardFrontPhotoFileName)
            filepath.putFile(electionCardFrontPhotoUri!!).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        electionCardFrontPhotoDownloadUrl = uri.toString()
                        uploadElectionCardBackPhoto()
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
        } else {
            if (cpd?.isShowing == true) cpd?.dismiss()
            uploadElectionCardBackPhoto()
        }
    }

    private fun uploadElectionCardBackPhoto() {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        if (electionCardBackPhotoUri != null) {
            electionCardBackPhotoFileName = "election_card_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
            val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("election_cards").child(electionCardBackPhotoFileName)
            filepath.putFile(electionCardBackPhotoUri!!).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        electionCardBackPhotoDownloadUrl = uri.toString()
                        uploadOldElectionCardFrontPhoto()
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
        } else {
            if (cpd?.isShowing == true) cpd?.dismiss()
            uploadOldElectionCardFrontPhoto()
        }
    }

    private fun uploadOldElectionCardFrontPhoto() {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        if (oldElectionCardFrontPhotoUri != null) {
            oldElectionCardFrontPhotoFileName = "old_election_card_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
            val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("election_cards").child(oldElectionCardFrontPhotoFileName)
            filepath.putFile(oldElectionCardFrontPhotoUri!!).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        oldElectionCardFrontPhotoDownloadUrl = uri.toString()
                        uploadOldElectionCardBackPhoto()
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
        } else {
            if (cpd?.isShowing == true) cpd?.dismiss()
            uploadOldElectionCardBackPhoto()
        }
    }

    private fun uploadOldElectionCardBackPhoto() {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        if (oldElectionCardBackPhotoUri != null) {
            oldElectionCardBackPhotoFileName = "old_election_card_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
            val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("election_cards").child(oldElectionCardBackPhotoFileName)
            filepath.putFile(oldElectionCardBackPhotoUri!!).addOnSuccessListener {
                try {
                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                        oldElectionCardBackPhotoDownloadUrl = uri.toString() //                        uploadCorrectionProofPhoto()
                        uploadPaymentScreenshot()
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
        } else {
            if (cpd?.isShowing == true) cpd?.dismiss() //            uploadCorrectionProofPhoto()
            uploadPaymentScreenshot()
        }
    }

    //    private fun uploadCorrectionProofPhoto() {
    //        if (cpd?.isShowing == true) cpd?.dismiss()
    //        cpd = ProgressDialog(mContext)
    //        cpd?.setCancelable(false)
    //        cpd?.show()
    //            if (correctionProofPhotoUri != null) {
    //            correctionProofPhotoFileName = "correction_proof_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
    //            val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("election_cards").child(correctionProofPhotoFileName)
    //            filepath.putFile(correctionProofPhotoUri!!).addOnSuccessListener {
    //                try {
    //                    filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
    //                        correctionProofPhotoDownloadUrl = uri.toString()
    //                        uploadPaymentScreenshot()
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
    //            uploadPaymentScreenshot()
    //        }
    //    }

    private fun uploadPaymentScreenshot() {
        if (cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        paymentScreenshotFileName = "payment_sc_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("election_cards").child(paymentScreenshotFileName)
        paymentScreenshotUri?.let {
            filepath.putFile(it).addOnSuccessListener {
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
    }

    private fun createDbRecord() {
        val createdDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        val billMap = HashMap<String, Any?>()
        billMap["email"] = email
        billMap["createdBy"] = name
        billMap["createdByUserName"] = username
        billMap["uid"] = uid
        billMap["createdDateTime"] = createdDateTime
        billMap["applicantName"] = binding.etApplicantName.text.toString() //        billMap["fullName"] = binding.etFullName.text.toString()
        billMap["customerEmail"] = binding.etEmail.text.toString()
        billMap["type"] = strType
        billMap["typeToShow"] = strTypeToShow
        billMap["mobileNo"] = binding.etMobileNo.text.toString()
        billMap["vidhansabha"] = binding.etVidhansabha.text.toString()
        billMap["passportPhotoFileName"] = passportPhotoFileName
        billMap["passportPhotoDownloadUrl"] = passportPhotoDownloadUrl
        billMap["aadharFrontPhotoFileName"] = aadharFrontPhotoFileName
        billMap["aadharFrontPhotoDownloadUrl"] = aadharFrontPhotoDownloadUrl
        billMap["aadharBackPhotoFileName"] = aadharBackPhotoFileName
        billMap["aadharBackPhotoDownloadUrl"] = aadharBackPhotoDownloadUrl
        billMap["electionCardFrontPhotoFileName"] = electionCardFrontPhotoFileName
        billMap["electionCardFrontPhotoDownloadUrl"] = electionCardFrontPhotoDownloadUrl
        billMap["electionCardBackPhotoFileName"] = electionCardBackPhotoFileName
        billMap["electionCardBackPhotoDownloadUrl"] = electionCardBackPhotoDownloadUrl
        billMap["oldElectionCardFrontPhotoFileName"] = oldElectionCardFrontPhotoFileName
        billMap["oldElectionCardFrontPhotoDownloadUrl"] = oldElectionCardFrontPhotoDownloadUrl
        billMap["oldElectionCardBackPhotoFileName"] = oldElectionCardBackPhotoFileName
        billMap["oldElectionCardBackPhotoDownloadUrl"] = oldElectionCardBackPhotoDownloadUrl //        billMap["correctionProofPhotoFileName"] = correctionProofPhotoFileName
        //        billMap["correctionProofPhotoDownloadUrl"] = correctionProofPhotoDownloadUrl
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("election_cards").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/election_cards/$pushKey"] = billMap
        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Election Card added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_election_card")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Election Card added by $name")
        }
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/election_cards/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd?.isShowing == true) cpd?.dismiss() //        toast("इलेक्शन कार्ड अर्ज सादर केला")
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
        val TYPE_DURUSTI = "TYPE_DURUSTI"
        val TYPE_NAVIN = "TYPE_NAVIN"
        val TYPE_LOST = "TYPE_LOST"
//        val TYPE_DURUSTI = "दुरुस्ती"
//        val TYPE_NAVIN = "नवीन"
//        val TYPE_LOST = "हरविलेले"
    }
}
