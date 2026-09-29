package rahul.jagtap.dmas.admin

import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.databinding.ActivityEditTrainingVideoBinding
import rahul.jagtap.dmas.databinding.ItemEditTrainingVideoBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.TrainingVideo
import rahul.jagtap.dmas.model.TrainingVideoConfig
import rahul.jagtap.dmas.utils.Utils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Admin-only editor for the Training Video screen (training_videos/config, mirrored to _backup).
 * Lets the admin set the screen title and add / remove / reorder any number of YouTube videos. Rows
 * are plain views in a LinearLayout (not a RecyclerView) so the editable fields never recycle/lose text.
 */
class EditTrainingVideoActivity : BaseActivity() {
    private lateinit var binding: ActivityEditTrainingVideoBinding
    private val rows = ArrayList<ItemEditTrainingVideoBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTrainingVideoBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "ट्रेनिंग व्हिडिओ एडिट करा"

        binding.btnAdd.setOnClickListener { addRow(null) }
        binding.btnSave.setOnClickListener { save() }

        loadConfig()
    }

    private fun loadConfig() {
        binding.progressBar.visible()
        database.child(Utils.TRAINING_VIDEOS_TABLE).child("config").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (checkIfActivityDestroying()) return
                binding.progressBar.gone()
                val config = snapshot.getValue(TrainingVideoConfig::class.java) ?: TrainingVideoConfig()
                prefill(config)
            }

            override fun onCancelled(error: DatabaseError) {
                if (checkIfActivityDestroying()) return
                binding.progressBar.gone()
                prefill(TrainingVideoConfig())
            }
        })
    }

    private fun prefill(config: TrainingVideoConfig) {
        binding.etScreenTitle.setText(config.screenTitle)
        rows.clear()
        binding.llVideos.removeAllViews()
        val videos = config.videos
        if (videos.isNullOrEmpty()) {
            addRow(null) // start with one empty row so the admin has something to type into
        } else {
            videos.forEach { addRow(it) }
        }
    }

    private fun addRow(video: TrainingVideo?) {
        val row = ItemEditTrainingVideoBinding.inflate(layoutInflater, binding.llVideos, false)
        video?.let {
            row.etTitle.setText(it.title)
            row.etDesc.setText(it.description)
            row.etUrl.setText(it.youtubeUrl)
            row.etDuration.setText(it.duration)
        }
        row.ivDelete.setOnClickListener { removeRow(row) }
        row.ivMoveUp.setOnClickListener { moveRow(row, -1) }
        row.ivMoveDown.setOnClickListener { moveRow(row, +1) }
        rows.add(row)
        binding.llVideos.addView(row.root)
        refreshNumbers()
    }

    private fun removeRow(row: ItemEditTrainingVideoBinding) {
        val index = rows.indexOf(row)
        if (index < 0) return
        rows.removeAt(index)
        binding.llVideos.removeView(row.root)
        refreshNumbers()
    }

    /** Move a card one step up (delta -1) or down (delta +1), re-attaching views in the new order. */
    private fun moveRow(row: ItemEditTrainingVideoBinding, delta: Int) {
        val from = rows.indexOf(row)
        val to = from + delta
        if (from < 0 || to < 0 || to >= rows.size) return
        rows.removeAt(from)
        rows.add(to, row)
        binding.llVideos.removeAllViews()
        rows.forEach { binding.llVideos.addView(it.root) }
        refreshNumbers()
    }

    private fun refreshNumbers() {
        rows.forEachIndexed { i, row -> row.tvNumber.text = "व्हिडिओ ${i + 1}" }
    }

    private fun save() {
        val list = ArrayList<TrainingVideo>()
        for (row in rows) {
            row.tilUrl.error = null
            val url = row.etUrl.text.toString().trim()
            val title = row.etTitle.text.toString().trim()
            val desc = row.etDesc.text.toString().trim()
            val duration = row.etDuration.text.toString().trim()

            // A completely empty row is silently dropped.
            if (url.isEmpty() && title.isEmpty() && desc.isEmpty()) continue

            if (url.isEmpty()) {
                row.tilUrl.error = "YouTube लिंक टाका"
                row.etUrl.requestFocus()
                return
            }
            if (Utils.extractYoutubeId(url) == null) {
                row.tilUrl.error = "वैध YouTube लिंक टाका"
                row.etUrl.requestFocus()
                return
            }
            list.add(TrainingVideo(title = title, description = desc, youtubeUrl = url, duration = duration, visible = true))
        }

        val screenTitle = binding.etScreenTitle.text.toString().trim().ifEmpty { "ट्रेनिंग व्हिडिओ" }
        val config = TrainingVideoConfig(
            screenTitle = screenTitle,
            videos = list,
            updatedBy = app?.preferences?.loggedInUser?.name ?: "admin",
            timeStamp = System.currentTimeMillis(),
            updatedDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        )

        binding.progressBar.visible()
        val updates = hashMapOf<String, Any?>(
            "${Utils.TRAINING_VIDEOS_TABLE}/config" to config,
            "${Utils.TRAINING_VIDEOS_TABLE}_backup/config" to config
        )
        database.updateChildren(updates) { error: DatabaseError?, _: DatabaseReference? ->
            if (checkIfActivityDestroying()) return@updateChildren
            binding.progressBar.gone()
            if (error != null) {
                toast("सेव्ह करता आले नाही: ${error.message}")
            } else {
                toast("ट्रेनिंग व्हिडिओ अपडेट झाले")
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
