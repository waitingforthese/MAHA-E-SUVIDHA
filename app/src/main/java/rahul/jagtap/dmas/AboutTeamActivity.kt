package rahul.jagtap.dmas

import android.Manifest
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
import rahul.jagtap.dmas.databinding.ActivityAboutTeamBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.longToast
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.AboutTeamImage
import rahul.jagtap.dmas.utils.Utils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Locale


class AboutTeamActivity : BaseActivity() {
    private var aboutTeamImage: AboutTeamImage? = null
    private var photoFileName: String = ""
    private var photoDownloadUrl: String = ""
    var imageUri: Uri? = null
    private lateinit var easyImage: EasyImage
    var imageType = -1
    private lateinit var binding: ActivityAboutTeamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutTeamBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_about_team)
        easyImage = EasyImage.Builder(this)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(false) // Setting to true will cause taken pictures to show up in the device gallery, DEFAULT false
            .setCopyImagesToPublicGalleryFolder(false)
            .build()

        database.child(Utils.ABOUT_TEAM_IMAGE_TABLE).child("admin").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                aboutTeamImage = dataSnapshot.getValue(AboutTeamImage::class.java)
                if (aboutTeamImage != null) {
                    if (!TextUtils.isEmpty(aboutTeamImage?.photoDownloadUrl1)) mContext?.let { Glide.with(it).load(aboutTeamImage?.photoDownloadUrl1).into(binding.imageView1) }
                    if (!TextUtils.isEmpty(aboutTeamImage?.photoDownloadUrl2)) mContext?.let { Glide.with(it).load(aboutTeamImage?.photoDownloadUrl2).into(binding.imageView2) }
                }
            }
        })
        if (app?.preferences?.loggedInUser?.isAdmin == "1") {
            binding.btnAddChangeImage1?.visible()
            binding.btnAddChangeImage2?.visible()
        } else {
            binding.btnAddChangeImage1?.gone()
            binding.btnAddChangeImage2?.gone()
        }
        binding.btnAddChangeImage1?.setOnClickListener {
            imageType = 1
            choosePhotoWithPermissions()
        }
        binding.btnAddChangeImage2?.setOnClickListener {
            imageType = 2
            choosePhotoWithPermissions()
        }
    }

    @SuppressLint("CheckResult")
    private fun choosePhotoWithPermissions() {
//        rxPermissions?.requestEachCombined(Manifest.permission.CAMERA)?.subscribe {
//            when {
//                it.granted -> { // get picture
                    easyImage.openChooser(this)
//                }
//                it.shouldShowRequestPermissionRationale -> { // At least one denied permission without ask never again
//                }
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
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                lifecycleScope.launch {
                    val compressedImageFile = mContext?.let { Compressor.compress(it, imageFiles[0].file) }
                    Log.e("TAG", "${compressedImageFile?.path}")
                    compressedImageFile?.let {
                        imageUri = Uri.fromFile(it)
                        Utils.showDialog(mContext, "Continue adding/changing image?", true) { dialog, which ->
                            run {
                                dialog.dismiss()
                                when (imageType) {
                                    1 -> uploadPhoto1(imageUri!!)
                                    2 -> uploadPhoto2(imageUri!!)
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun uploadPhoto1(fileUri: Uri) {
        val cpd: ProgressDialog? = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        photoFileName = "image_1_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ABOUT_TEAM_IMAGE_TABLE).child(photoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                if (cpd != null && cpd.isShowing) cpd.dismiss()
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    photoDownloadUrl = uri.toString()
                    createDbRecord(1)
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

    private fun uploadPhoto2(fileUri: Uri) {
        val cpd: ProgressDialog? = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        photoFileName = "image_2_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        val filepath = storageRef.child(Utils.ABOUT_TEAM_IMAGE_TABLE).child(photoFileName)
        filepath.putFile(fileUri).addOnSuccessListener {
            try {
                if (cpd != null && cpd.isShowing) cpd.dismiss()
                filepath.downloadUrl.addOnSuccessListener { uri: Uri ->
                    photoDownloadUrl = uri.toString()
                    createDbRecord(2)
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

    private fun createDbRecord(type: Int) {
        val createdDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        val billMap = HashMap<String, Any?>()
        billMap["createdBy"] = "admin"
        billMap["createdDateTime"] = createdDateTime
        if (type == 1) {
            billMap["photoFileName1"] = photoFileName
            billMap["photoDownloadUrl1"] = photoDownloadUrl
            if (aboutTeamImage != null) {
                billMap["photoFileName2"] = if (!TextUtils.isEmpty(aboutTeamImage?.photoFileName2)) aboutTeamImage?.photoFileName2 else ""
                billMap["photoDownloadUrl2"] = if (!TextUtils.isEmpty(aboutTeamImage?.photoDownloadUrl2)) aboutTeamImage?.photoDownloadUrl2 else ""
            }
        } else if (type == 2) {
            if (aboutTeamImage != null) {
                billMap["photoFileName1"] = if (!TextUtils.isEmpty(aboutTeamImage?.photoFileName1)) aboutTeamImage?.photoFileName1 else ""
                billMap["photoDownloadUrl1"] = if (!TextUtils.isEmpty(aboutTeamImage?.photoDownloadUrl1)) aboutTeamImage?.photoDownloadUrl1 else ""
            }
            billMap["photoFileName2"] = photoFileName
            billMap["photoDownloadUrl2"] = photoDownloadUrl
        }
        billMap["timeStamp"] = System.currentTimeMillis()

        val messageUserMap = HashMap<String, Any?>()
        messageUserMap["${Utils.ABOUT_TEAM_IMAGE_TABLE}/admin"] = billMap
        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, _: DatabaseReference? ->
            if (databaseError != null) {
                Log.e("db error", databaseError.message)
            }
        }

        val backupMap = HashMap<String, Any?>()
        backupMap["${Utils.ABOUT_TEAM_IMAGE_TABLE}_backup/admin"] = billMap
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
