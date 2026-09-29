package rahul.jagtap.dmas.user

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.admin.EditTrainingVideoActivity
import rahul.jagtap.dmas.adapter.TrainingVideoAdapter
import rahul.jagtap.dmas.databinding.ActivityTrainingVideoBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.TrainingVideo
import rahul.jagtap.dmas.model.TrainingVideoConfig
import rahul.jagtap.dmas.utils.Utils

/**
 * User-facing Training Video screen. Reads admin-configured content from training_videos/config and
 * shows a hero header + list of YouTube videos. Admins see a pencil to edit. Tapping a video opens it
 * in the YouTube app, falling back to a browser.
 */
class TrainingVideoActivity : BaseActivity() {
    private lateinit var binding: ActivityTrainingVideoBinding
    private var config: TrainingVideoConfig = TrainingVideoConfig()
    private val videos = ArrayList<TrainingVideo>()
    private lateinit var adapter: TrainingVideoAdapter
    private val isAdmin get() = app?.preferences?.loggedInUser?.isAdmin == "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingVideoBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = config.screenTitle

        adapter = TrainingVideoAdapter(mContext, videos) { openVideo(it) }
        binding.rvVideos.layoutManager = LinearLayoutManager(mContext)
        binding.rvVideos.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadConfig()
    }

    private fun loadConfig() {
        binding.progressBar.visible()
        database.child(Utils.TRAINING_VIDEOS_TABLE).child("config").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (checkIfActivityDestroying()) return
                binding.progressBar.gone()
                snapshot.getValue(TrainingVideoConfig::class.java)?.let { config = it }
                bind()
            }

            override fun onCancelled(error: DatabaseError) {
                if (checkIfActivityDestroying()) return
                binding.progressBar.gone()
                bind()
            }
        })
    }

    private fun bind() {
        binding.toolbarLayout.toolbarTitle?.text = config.screenTitle
        // Only show rows the admin marked visible AND that resolve to a real YouTube id.
        videos.clear()
        config.videos?.filter { it.visible && Utils.extractYoutubeId(it.youtubeUrl) != null }?.let { videos.addAll(it) }
        adapter.notifyDataSetChanged()
        binding.tvEmpty.visibility = if (videos.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    /** Open in the YouTube app first; fall back to a browser; toast if nothing can handle it. */
    private fun openVideo(video: TrainingVideo) {
        val id = Utils.extractYoutubeId(video.youtubeUrl)
        val rawUrl = video.youtubeUrl?.trim().orEmpty()
        if (id == null && rawUrl.isEmpty()) {
            toast("व्हिडिओ लिंक उपलब्ध नाही")
            return
        }
        if (id != null) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id")))
                return
            } catch (e: ActivityNotFoundException) {
                // YouTube app not installed — fall through to browser.
            }
        }
        val webUrl = if (id != null) "https://www.youtube.com/watch?v=$id" else rawUrl
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)))
        } catch (e: ActivityNotFoundException) {
            toast("व्हिडिओ उघडण्यासाठी अ‍ॅप सापडले नाही")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isAdmin) menuInflater.inflate(R.menu.menu_training_video_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_edit_training_video -> {
                startActivity(Intent(mContext, EditTrainingVideoActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
