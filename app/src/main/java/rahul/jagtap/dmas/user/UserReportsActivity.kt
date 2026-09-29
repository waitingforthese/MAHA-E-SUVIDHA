package rahul.jagtap.dmas.user

import android.app.DownloadManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.ReportListAdapter
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.ReportInfo
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserReportsActivity : BaseActivity() {
    private lateinit var hashMap: HashMap<String, ReportInfo>
    var list = ArrayList<ReportInfo>()
    var adapter: ReportListAdapter? = null

    private lateinit var binding: ActivityViewBillsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_reports)

        hashMap = intent.getSerializableExtra("reportsData") as HashMap<String, ReportInfo>
        adapter = ReportListAdapter(mContext, list)
        val billList = hashMap?.values?.toMutableList()

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.recyclerView?.adapter = adapter
        adapter?.itemClickListener = object : ReportListAdapter.ItemClickListener {
            override fun onDeleteClick(position: Int) {
            }

            override fun onDownloadClick(position: Int) {
                val reportInfo = billList?.get(position)
                if (!TextUtils.isEmpty(reportInfo?.reportDownloadUrl)) {
//                    val downloadManager = mContext?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
//                    val uri: Uri = Uri.parse(reportInfo.reportDownloadUrl)
//                    val request = DownloadManager.Request(uri)
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                    downloadManager?.enqueue(request)
//                    val extension: String? = reportInfo.reportFileName?.lastIndexOf(".")?.let { reportInfo.reportFileName!!.substring(it) }
//                    if (extension != null) {
//                        Log.e("extension", extension)
//                    }
                    downloadTask(reportInfo?.reportDownloadUrl.toString()) //, extension.toString()
                    toast("Downloading...")
                } else toast("File not found")
            }
        }

        if (billList != null && billList.size > 0) {
            list.addAll(billList)
            binding.recyclerView?.visible()
            binding.tvError?.gone()
        } else {
            binding.recyclerView?.gone()
            binding.tvError?.visible()
        }
    }

    @Throws(Exception::class)
    private fun downloadTask(url: String): Boolean { //extension: String
        if (!url.startsWith("http")) {
            return false
        }
        val name = "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}"//$extension"
        try {
            val file = File(Environment.getExternalStorageDirectory(), "Download")
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

    }
}
