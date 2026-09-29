package rahul.jagtap.dmas

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import rahul.jagtap.dmas.databinding.ActivityTermsConditionsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.longToast
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.ImageDetails
import rahul.jagtap.dmas.model.ReferralProgram
import rahul.jagtap.dmas.utils.Utils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TermsConditionsActivity : BaseActivity() {
    private var photoFileName: String = ""
    private var photoDownloadUrl: String = ""
    var imageUri: Uri? = null
    lateinit var binding: ActivityTermsConditionsBinding
    private lateinit var easyImage: EasyImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsConditionsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_terms_and_conditions)

//        setBannerImage()
        easyImage = EasyImage.Builder(this).setChooserType(ChooserType.CAMERA_AND_GALLERY).allowMultiple(false) // Setting to true will cause taken pictures to show up in the device gallery, DEFAULT false
            .setCopyImagesToPublicGalleryFolder(false).build()

        setReferralProgramImage()
        if (app?.preferences?.loggedInUser?.isAdmin == "1") {
            binding.btnAddChangeImage?.visible()
        } else {
            binding.btnAddChangeImage?.gone()
        }
        binding.btnAddChangeImage?.setOnClickListener {
            choosePhotoWithPermissions()
        }
    }

//    private fun setBannerImage() {
//        database.child(Utils.BANNER_IMAGE_TABLE).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//            }
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val imageDetails = dataSnapshot.getValue(ImageDetails::class.java)
//                if (imageDetails != null) { // Check if the activity is being destroyed
//                    if (checkIfActivityDestroying()) return
//                    if (mContext != null && !TextUtils.isEmpty(imageDetails.imageDownloadUrl)) mContext?.let { Glide.with(it).load(imageDetails.imageDownloadUrl).into(binding.imageView) }
//                }
//            }
//        })
//    }

    private fun setReferralProgramImage() {
        database.child(Utils.REFERRAL_PROGRAM_TABLE).child("admin").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val item = dataSnapshot.getValue(ReferralProgram::class.java)
                if (item != null) {
                    if (!TextUtils.isEmpty(item.photoDownloadUrl)) mContext?.let { Glide.with(it).load(item.photoDownloadUrl).into(binding.imageView2) }
                }
            }
        })
    }


    @SuppressLint("CheckResult")
    private fun choosePhotoWithPermissions() {
        easyImage.openChooser(this)
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
                    Log.e("TAG", "${compressedImageFile?.path}")
                    compressedImageFile?.let {
                        imageUri = Uri.fromFile(it)
                        Utils.showDialog(mContext, "Continue adding/changing image?", true) { dialog, which ->
                            run {
                                dialog.dismiss()
                                uploadPhoto(imageUri!!)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun uploadPhoto(fileUri: Uri) {
        val cpd: ProgressDialog? = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        photoFileName = "image_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.REFERRAL_PROGRAM_TABLE).child(photoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                if (cpd != null && cpd.isShowing) cpd.dismiss()
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    photoDownloadUrl = uri.toString()
                    createDbRecord()
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

    private fun createDbRecord() {
        val createdDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        val billMap = HashMap<String, Any?>()
        billMap["createdBy"] = "admin"
        billMap["createdDateTime"] = createdDateTime
        billMap["photoFileName"] = photoFileName
        billMap["photoDownloadUrl"] = photoDownloadUrl
        billMap["timeStamp"] = System.currentTimeMillis()

        //        val pushKey = database.child(Utils.REFERRAL_PROGRAM_TABLE).push().key
        //        billMap["pushKey"] = pushKey

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.REFERRAL_PROGRAM_TABLE}/admin"] = billMap
        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, _: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }

        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.REFERRAL_PROGRAM_TABLE}_backup/admin"] = billMap
        database.updateChildren(backupMap)
        toast("Updated image successfully")
        finish()
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