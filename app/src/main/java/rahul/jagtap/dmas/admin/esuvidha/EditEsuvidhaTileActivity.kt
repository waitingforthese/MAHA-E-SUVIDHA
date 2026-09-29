package rahul.jagtap.dmas.admin.esuvidha

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
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
import rahul.jagtap.dmas.databinding.ActivityEditEsuvidhaTileBinding
import rahul.jagtap.dmas.extensions.copyToClipboard
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.EsuvidhaServiceTile
import rahul.jagtap.dmas.utils.Utils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Admin-only editor for one grid tile's presentation (title / icon / fee / show-hide), stored at
 * esuvidha_grid/{itemKey} (+ backup). Blank title/fee fall back to the registry / subtype defaults;
 * order is managed from the list screen and preserved here.
 */
class EditEsuvidhaTileActivity : BaseActivity() {
    private lateinit var binding: ActivityEditEsuvidhaTileBinding
    private lateinit var easyImage: EasyImage

    private var itemKey = ""
    private var serviceKey = ""
    private var subtypeKey: String? = null
    private var defaultTitle = ""
    private var storedTitle = "" // the saved override title ("" = tracking the live default)
    private var defaultIconRes = R.drawable.ic_svc_receipt
    private var existingOrder = -1
    private var iconUrl = ""
    private var pendingIconUri: Uri? = null // picked-but-not-yet-uploaded icon; uploaded only on Save

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEsuvidhaTileBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "Edit service tile"

        easyImage = EasyImage.Builder(this)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(false)
            .setCopyImagesToPublicGalleryFolder(false)
            .build()

        itemKey = intent.getStringExtra("itemKey").orEmpty()
        serviceKey = intent.getStringExtra("serviceKey").orEmpty()
        subtypeKey = intent.getStringExtra("subtypeKey")
        defaultTitle = intent.getStringExtra("defaultTitle").orEmpty()
        defaultIconRes = intent.getIntExtra("defaultIconRes", R.drawable.ic_svc_receipt)
        binding.tvSubtitle.text = intent.getStringExtra("subtitle").orEmpty()

        val ov: EsuvidhaServiceTile? = intent.getParcelableExtra("override")
        existingOrder = ov?.order ?: -1
        iconUrl = ov?.iconUrl.orEmpty()
        storedTitle = ov?.title.orEmpty()
        // Read-only reference = the ORIGINAL subtype / registry title (tap to copy). It must ALWAYS show
        // the subtype's own name — never the admin's renamed title — so the admin can always tell which
        // subtype this tile is, even after renaming it in the grid.
        binding.etCurrentTitle.setText(defaultTitle)
        // "New title" is pre-filled with the saved override so the admin sees & can tweak the current
        // custom name; clearing it (blank) resets the tile to track the subtype's original title.
        binding.etTitle.setText(storedTitle)
        binding.etFee.setText(ov?.fee.orEmpty().filter { it.isDigit() })
        binding.swEnabled.isChecked = ov?.enabled ?: true
        renderIcon()

        binding.etCurrentTitle.setOnClickListener {
            val text = binding.etCurrentTitle.text.toString()
            if (text.isNotBlank()) text.copyToClipboard(mContext!!)
        }
        binding.btnChangeIcon.setOnClickListener { easyImage.openChooser(this) }
        binding.btnSave.setOnClickListener { save() }
    }

    private fun renderIcon() {
        val source: Any? = pendingIconUri ?: iconUrl.takeIf { it.isNotBlank() }
        if (source != null) {
            ImageViewCompat.setImageTintList(binding.ivIcon, null) // real image: no tint
            mContext?.let { Glide.with(it).load(source).placeholder(defaultIconRes).into(binding.ivIcon) }
        } else {
            ImageViewCompat.setImageTintList(binding.ivIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)))
            binding.ivIcon.setImageResource(defaultIconRes)
        }
    }

    private fun save() {
        binding.progressBar.visible()
        val pending = pendingIconUri
        if (pending != null) {
            // Upload only now (on Save) so a cancelled edit never leaves an orphan in Storage. Fixed path
            // per tile => re-uploads overwrite the same file instead of accumulating.
            val filepath = storageRef.child(Utils.ESUVIDHA_GRID_TABLE).child(itemKey).child("icon.jpg")
            filepath.putFile(pending).addOnSuccessListener {
                filepath.downloadUrl.addOnSuccessListener { uri ->
                    iconUrl = uri.toString()
                    pendingIconUri = null
                    writeTile()
                }.addOnFailureListener { onIconUploadFailed(it) }
            }.addOnFailureListener { onIconUploadFailed(it) }
        } else {
            writeTile()
        }
    }

    private fun onIconUploadFailed(e: Exception) {
        if (checkIfActivityDestroying()) return
        binding.progressBar.gone()
        Log.e("EditTile", "icon upload failed", e)
        toast("Icon upload failed: ${e.message}")
    }

    private fun writeTile() {
        val enteredTitle = binding.etTitle.text.toString().trim()
        // "New title" is pre-filled with the saved custom title, so blank now means "no custom title" —
        // reset the tile to track the subtype's original name. Typing the default also stores blank.
        val titleToStore = if (enteredTitle.isEmpty() || enteredTitle == defaultTitle.trim()) "" else enteredTitle
        val tile = EsuvidhaServiceTile(
            itemKey = itemKey,
            serviceKey = serviceKey,
            subtypeKey = subtypeKey,
            title = titleToStore,
            iconUrl = iconUrl,
            fee = binding.etFee.text.toString().trim(),
            order = existingOrder,
            enabled = binding.swEnabled.isChecked,
            updatedBy = app?.preferences?.loggedInUser?.name ?: "admin",
            timeStamp = System.currentTimeMillis(),
            updatedDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        )
        val updates = hashMapOf<String, Any?>(
            "${Utils.ESUVIDHA_GRID_TABLE}/$itemKey" to tile,
            "${Utils.ESUVIDHA_GRID_BACKUP_TABLE}/$itemKey" to tile
        )
        database.updateChildren(updates) { error: DatabaseError?, _: DatabaseReference? ->
            if (checkIfActivityDestroying()) return@updateChildren
            binding.progressBar.gone()
            if (error != null) toast("Failed to save: ${error.message}")
            else { toast("Saved"); setResult(RESULT_OK); finish() }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagePickerError(error: Throwable, source: MediaSource) { toast(getString(R.string.txt_try_later)) }
            override fun onCanceled(source: MediaSource) {}
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                lifecycleScope.launch {
                    val compressed = mContext?.let { Compressor.compress(it, imageFiles[0].file) }
                    compressed?.let {
                        pendingIconUri = Uri.fromFile(it) // preview only; uploaded on Save
                        renderIcon()
                        toast("Icon selected — tap Save to apply")
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}
