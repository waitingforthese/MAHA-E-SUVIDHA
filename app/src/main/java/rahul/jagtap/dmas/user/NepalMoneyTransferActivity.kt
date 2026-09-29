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
import android.view.WindowManager
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
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
import rahul.jagtap.dmas.databinding.ActivityNepalMoneyTransBinding
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class NepalMoneyTransferActivity : BaseActivity() {
    private var selectedTypeAmount: String = ""
    private var nepaleseCardFrontPhotoFileName: String = ""
    private var nepaleseCardFrontPhotoDownloadUrl: String = ""
    private var nepaleseCardBackPhotoFileName: String = ""
    private var nepaleseCardBackPhotoDownloadUrl: String = ""
    private var paymentScreenshotFileName: String = ""
    private var paymentScreenshotDownloadUrl: String = ""
    private val TAG = NepalMoneyTransferActivity::class.java.simpleName
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    var cpd: ProgressDialog? = null
    var imageType = -1 // 1 - nepalese card f, 2 - nepalese card b, 3 - payment screenshot
    var nepaleseCardFrontPhotoUri: Uri? = null
    var nepaleseCardBackPhotoUri: Uri? = null
    var paymentScreenshotUri: Uri? = null
    private lateinit var easyImage: EasyImage
    lateinit var binding: ActivityNepalMoneyTransBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNepalMoneyTransBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilSenderName, binding.tilReceiverName, binding.tilReceiverMobile, binding.tilReceiverAddress, binding.tilNepaleseCardFPhoto, binding.tilNepaleseCardBPhoto, binding.tilPaymentScreenshot)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_nepal_money_transfer)
        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid
        username = app?.preferences?.loggedInUser?.username
        name = app?.preferences?.loggedInUser?.name
        easyImage = EasyImage.Builder(this)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(false) // Setting to true will cause taken pictures to show up in the device gallery, DEFAULT false
            .setCopyImagesToPublicGalleryFolder(false)
            .build()
        binding.btnPay.setOnClickListener {
            payUsingUPI(selectedTypeAmount)
        }
        binding.tvPayNote?.setText(spanText, TextView.BufferType.SPANNABLE)
        binding.tvPayNote?.movementMethod = LinkMovementMethod.getInstance()
        binding.btnSuchna.setOnClickListener {
            startActivity(Intent(mContext, ViewSuchnaActivity::class.java).putExtra("suchna", intent.getStringExtra("suchna")
                ?: "").putExtra("title", getString(R.string.txt_nepal_money_transfer)))
        }

        binding.btnSubmit?.setOnClickListener {
            val strSenderName = binding.etSenderName.text.toString()
            val strReceiverName = binding.etReceiverName.text.toString()
            val strReceiverMobile = binding.etReceiverMobile.text.toString()
            val strReceiverAddress = binding.etReceiverAddress.text.toString()
            if (TextUtils.isEmpty(strSenderName)) {
                binding.tilSenderName?.error = "Enter Sender Name"
                binding.etSenderName?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strReceiverName)) {
                binding.tilReceiverName?.error = "Enter Receiver Name"
                binding.etReceiverName?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strReceiverMobile)) {
                binding.tilReceiverMobile?.error = "Enter Receiver Mobile Number"
                binding.etReceiverMobile?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strReceiverAddress)) {
                binding.tilReceiverAddress?.error = "Enter Receiver Address"
                binding.etReceiverAddress?.requestFocus()
                return@setOnClickListener
            }
            if (nepaleseCardFrontPhotoUri == null) {
                binding.tilNepaleseCardFPhoto?.error = "Select Nepalese Card Front Photo"
                binding.etNepaleseCardFPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (nepaleseCardBackPhotoUri == null) {
                binding.tilNepaleseCardBPhoto?.error = "Select Nepalese Card Back Photo"
                binding.etNepaleseCardBPhoto?.requestFocus()
                return@setOnClickListener
            }
            if (paymentScreenshotUri == null) {
                binding.tilPaymentScreenshot?.error = "Select Payment Screenshot"
                binding.etPaymentScreenshot?.requestFocus()
                return@setOnClickListener
            }
            uploadNepaleseCardFPhoto(nepaleseCardFrontPhotoUri!!)
        }
        binding.btnPayByUpiId?.setOnClickListener {
            startActivity(Intent(mContext, PayByUpiActivity::class.java))
        }
        binding.etNepaleseCardFPhoto?.setOnClickListener {
            imageType = 1
            choosePhotoWithPermissions()
        }
        binding.etNepaleseCardBPhoto?.setOnClickListener {
            imageType = 2
            choosePhotoWithPermissions()
        }
        binding.etPaymentScreenshot?.setOnClickListener {
            imageType = 3
            choosePhotoWithPermissions()
        }
    }

    private fun uploadNepaleseCardFPhoto(fileUri: Uri) {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        nepaleseCardFrontPhotoFileName = "nepalese_card_front_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("nepal_money_transfer").child(nepaleseCardFrontPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    nepaleseCardFrontPhotoDownloadUrl = uri.toString()
                    nepaleseCardBackPhotoUri?.let { it1 -> uploadNepaleseCardBPhoto(it1) }
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
        }.addOnProgressListener {
            //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun uploadNepaleseCardBPhoto(fileUri: Uri) {
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        nepaleseCardBackPhotoFileName = "nepalese_card_back_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("nepal_money_transfer").child(nepaleseCardBackPhotoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    nepaleseCardBackPhotoDownloadUrl = uri.toString()
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
        }.addOnProgressListener {
            //displaying the upload progress
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
        val filepath = storageRef.child(Utils.ESUVIDHA_TABLE).child(email!!).child("nepal_money_transfer").child(paymentScreenshotFileName)
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
        }.addOnProgressListener {
            //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
        }
    }

    private fun createDbRecord() {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        val createdDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        val billMap = HashMap<String, Any?>()
        billMap["email"] = email
        billMap["createdBy"] = name
        billMap["createdByUserName"] = username
        billMap["uid"] = uid
        billMap["createdDateTime"] = createdDateTime
        billMap["sender_name"] = binding.etSenderName.text.toString()
        billMap["receiver_name"] = binding.etReceiverName.text.toString()
        billMap["receiver_mobile"] = binding.etReceiverMobile.text.toString()
        billMap["receiver_address"] = binding.etReceiverAddress.text.toString()
        billMap["nepaleseCardFrontPhotoFileName"] = nepaleseCardFrontPhotoFileName
        billMap["nepaleseCardFrontPhotoDownloadUrl"] = nepaleseCardFrontPhotoDownloadUrl
        billMap["nepaleseCardBackPhotoFileName"] = nepaleseCardBackPhotoFileName
        billMap["nepaleseCardBackPhotoDownloadUrl"] = nepaleseCardBackPhotoDownloadUrl
        billMap["paymentScreenshotFileName"] = paymentScreenshotFileName
        billMap["paymentScreenshotDownloadUrl"] = paymentScreenshotDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        val pushKey = database.child(Utils.ESUVIDHA_TABLE).child(getTodayDate()).child(uid!!).child("nepal_money_transfer").push().key
        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ESUVIDHA_TABLE}/${getTodayDate()}/${uid!!}/nepal_money_transfer/$pushKey"] = billMap

        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }
        val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
        if (notificationPushKey != null) {
            val notificationItem = NotificationItem(message = "New Nepal money transfer added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_nepal_money_transfer")
            database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
            sendNotification("New Nepal money transfer added by $name")
        }

        // Create a backup record
        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ESUVIDHA_TABLE}_backup/${getTodayDate()}/${uid!!}/nepal_money_transfer/$pushKey"] = billMap
        database.updateChildren(backupMap)
        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
//        toast("Request submitted successfully")
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
//                it.granted -> {
//                    // get picture
                    easyImage.openChooser(this)
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                longToast(getString(R.string.txt_try_later))
            }

            override fun onCanceled(source: MediaSource) {}
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                //                val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
                //                val fileName = AccountingServicesActivity.dateFormatForPhoto.format(System.currentTimeMillis()) + ".jpg"
                //                val image = File("$dir/$fileName")

                lifecycleScope.launch {
                    val compressedImageFile = mContext?.let { Compressor.compress(it, imageFiles[0].file) }
                    Log.e(TAG, "${compressedImageFile?.path}")
                    compressedImageFile?.let {
                        when (imageType) {
                            1 -> {
                                nepaleseCardFrontPhotoUri = Uri.fromFile(it)
                                binding.etNepaleseCardFPhoto?.setText("Success")//nepaleseCardFrontPhotoUri.toString())
                            }
                            2 -> {
                                nepaleseCardBackPhotoUri = Uri.fromFile(it)
                                binding.etNepaleseCardBPhoto?.setText("Success")//nepaleseCardBackPhotoUri.toString())
                            }
                            3 -> {
                                paymentScreenshotUri = Uri.fromFile(it)
                                binding.etPaymentScreenshot?.setText("Success")//paymentScreenshotUri.toString())
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

    companion object {}
}
