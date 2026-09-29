package rahul.jagtap.dmas.user

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.ViewShopBillsActivity
import rahul.jagtap.dmas.databinding.ActivityAccountingServicesBinding
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.longToast
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val PURCHASES = "Purchase"
const val SALES = "Sales"
const val EXPENSES = "Loan Statement"
const val BANK_TRANSACTION = "Bank Statement"
const val PAYMENT = "Payment"
const val RECEIVED = "Receipt"
const val OTHER = "Other"

class AccountingServicesActivity : BaseActivity() {
    private var uid: String? = ""
    private var username: String? = ""
    private var email: String? = ""
    private var billType: String? = ""
    private var name: String? = ""
    private var shopName: String? = ""
    private val TAG = AccountingServicesActivity::class.java.simpleName
    private lateinit var easyImage: EasyImage
    lateinit var binding: ActivityAccountingServicesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountingServicesBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)

        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid
        username = app?.preferences?.loggedInUser?.username
        name = app?.preferences?.loggedInUser?.name
        shopName = app?.preferences?.loggedInUser?.shopName

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_accounting_services)
        easyImage = EasyImage.Builder(this).setChooserType(ChooserType.CAMERA_AND_GALLERY).allowMultiple(false) // Setting to true will cause taken pictures to show up in the device gallery, DEFAULT false
            .setCopyImagesToPublicGalleryFolder(false).build()
        binding.rgBillType?.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rbPurchases -> billType = PURCHASES
                R.id.rbSales -> billType = SALES
                R.id.rbExpenses -> billType = EXPENSES
                R.id.rbPayments -> billType = PAYMENT
                R.id.rbBankTransaction -> billType = BANK_TRANSACTION
                R.id.rbReceived -> billType = RECEIVED
                R.id.rbOther -> billType = OTHER
            }
            binding.btnScanBill.isEnabled = true
        }
        binding.btnViewBills?.setOnClickListener {
            mContext?.startActivity(Intent(mContext, ViewShopBillsActivity::class.java))
        }
        binding.btnScanBill?.setOnClickListener {
            if (!binding.btnScanBill.isEnabled) {
                toast(getString(R.string.txt_choose_bill_type))
                return@setOnClickListener
            }
            choosePhotoWithPermissions()
        }
    }

    @SuppressLint("CheckResult")
    private fun choosePhotoWithPermissions() {
//        rxPermissions?.requestEachCombined(Manifest.permission.CAMERA)?.subscribe {
//            when {
//                it.granted -> { // get picture
//                    //                    EasyImage.openChooserWithGallery(this, "", REQUEST_AVATAR_CODE)
                    easyImage.openChooser(this)
//                }
//
//                it.shouldShowRequestPermissionRationale -> { // At least one denied permission without ask never again
//                }
//
//                else -> { // At least one denied permission with ask never again
//                    // Need to go to the settings
//                    askPermissions()
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
//                val fileName = dateFormatForPhoto.format(System.currentTimeMillis()) + ".jpg"
//                val image = File("$dir/$fileName")

                lifecycleScope.launch {
                    val compressedImageFile = mContext?.let { Compressor.compress(it, imageFiles[0].file) }
                    Log.e(TAG, "${compressedImageFile?.path}")
                    compressedImageFile?.let { uploadFile(it) }
                }
            }
        })
    }

    private fun uploadFile(compressedImageFile: File) {
        val cpd: ProgressDialog? = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        val createdDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        val fileName = "${billType}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.BILLS_TABLE).child(email!!).child(fileName)
        filepath.putFile(Uri.fromFile(compressedImageFile)).addOnSuccessListener {
            try {
                if (cpd != null && cpd.isShowing) cpd.dismiss()
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    val downloadUrl = uri.toString()
                    val billMap = HashMap<String, Any?>()
                    billMap["email"] = email
                    billMap["billType"] = billType
                    billMap["createdBy"] = name
                    billMap["createdByUserName"] = username
                    billMap["createdByShopName"] = shopName
                    billMap["uid"] = uid
                    billMap["createdDateTime"] = createdDateTime
                    billMap["fileName"] = fileName
                    billMap["timeStamp"] = System.currentTimeMillis()
                    billMap["downloadUrl"] = downloadUrl

                    val pushKey = database.child(Utils.BILLS_TABLE).child(getTodayDate()).child(uid!!).push().key
                    billMap["pushKey"] = pushKey

                    val messageUserMap = HashMap<String, Any?>()
                    messageUserMap["${Utils.BILLS_TABLE}/${getTodayDate()}/${uid!!}/$pushKey"] = billMap

                    database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
                        if (databaseError != null) {
                            Log.e("db error", databaseError.message)
                        }
                    }
                    val notificationPushKey = database.child(Utils.NOTIFICATIONS_TABLE).push().key
                    if (notificationPushKey != null) { //                                val notificationMap = HashMap<String, Any?>()
                        //                                notificationMap["email"] = email
                        //                                notificationMap["message"] = "New bill added by $uid"
                        //                                notificationMap["uid"] = uid
                        //                                notificationMap["billType"] = billType
                        //                                notificationMap["notificationType"] = "add_bill"
                        //                                notificationMap["createdAt"] = createdDateTime
                        //                                val notificationUserMap = HashMap<String, Any?>()
                        //                                notificationUserMap["Notifications/$notificationPushKey"] =
                        //                                    notificationMap
                        //                                database.updateChildren(notificationUserMap)
                        val notificationItem = NotificationItem(message = "New bill added by $name", createdAt = createdDateTime, email = email, uid = uid, notificationType = "add_bill", billType = billType)
                        database.child(Utils.NOTIFICATIONS_TABLE).child(notificationPushKey).setValue(notificationItem)
                        sendNotification("New bill added by $name")
                    }
                    toast("Bill created successfully")
                    finish()
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }.addOnFailureListener {
            if (cpd?.isShowing == true) cpd.dismiss()
            it.message?.let { it1 -> toast(it1) }
        }.addOnProgressListener { //displaying the upload progress
            val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            cpd?.setMessage("Please wait.. ")
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

    companion object {
//        const val REQUEST_AVATAR_CODE = 123
        val dateFormatForPhoto = SimpleDateFormat("HHmmSS", Locale.ENGLISH)
    }
}
