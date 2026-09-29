package rahul.jagtap.dmas

import android.app.DownloadManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.bumptech.glide.Glide
import rahul.jagtap.dmas.databinding.ActivityFullScreenImageBinding
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class FullScreenImageActivity : BaseActivity() {
    private val TAG = FullScreenImageActivity::class.java.simpleName
    var downloadUrl = ""
    lateinit var binding: ActivityFullScreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setValues()

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setValues() {
        binding.toolbarLayout.toolbarTitle?.text = intent.getStringExtra("title") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        downloadUrl = intent.getStringExtra("downloadUrl") ?: ""
        if (!TextUtils.isEmpty(imageUrl)) Glide.with(this).load(imageUrl).into(binding.imageView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_download, menu)
//        menu?.findItem(R.id.action_download)?.isVisible = TextUtils.isEmpty(intent.getStringExtra("downloadUrl"))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_download -> {
//                val downloadManager = mContext?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
//                val uri: Uri = Uri.parse(intent.getStringExtra("downloadUrl"))
//                val request = DownloadManager.Request(uri)
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                downloadManager?.enqueue(request)
                intent.getStringExtra("downloadUrl")?.let { downloadTask(it) }
                toast("Downloading...")
                return true
            }
            android.R.id.home -> {
                Utils.hideSoftKeyboard(this)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Throws(Exception::class)
    private fun downloadTask(url: String): Boolean {
        if (!url.startsWith("http")) {
            return false
        }
        val name = "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}.jpg"
        try {
            val file = File(Environment.getExternalStorageDirectory(), "DCIM/Camera")
            if (!file.exists()) {
                file.mkdirs()
            }
            val result = File(file.absolutePath + File.separator.toString() + name)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            request.setDestinationUri(Uri.fromFile(result))
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            downloadManager.enqueue(request)
            //mToast(mContext, "Starting download...");
            MediaScannerConnection.scanFile(mContext, arrayOf(result.toString()), null) { path, uri -> }
        } catch (e: Exception) {
            Log.e(">>>>>", e.toString())
            //mToast(this, e.toString());
            return false
        }
        return true
    }
}
