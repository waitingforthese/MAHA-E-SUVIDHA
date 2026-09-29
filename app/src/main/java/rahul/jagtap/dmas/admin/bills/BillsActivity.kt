package rahul.jagtap.dmas.admin.bills

import android.app.DownloadManager
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.BillListAdapter
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.Bill
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class BillsActivity : BaseActivity() {
    private lateinit var hashMap: HashMap<String, Bill>
    var list = ArrayList<Bill>()
    var adapter: BillListAdapter? = null

    private lateinit var binding: ActivityViewBillsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)

        hashMap = intent.getSerializableExtra("billData") as HashMap<String, Bill>

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_accounting_services)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = BillListAdapter(mContext, list)
        binding.recyclerView?.adapter = adapter

        val billList = hashMap?.values
        if (billList != null && billList.size > 0) {
            list.addAll(billList)
            notifyAdapter()
            binding.recyclerView?.visible()
            binding.tvError?.gone()
        } else {
            binding.recyclerView?.gone()
            binding.tvError?.visible()
        }
        adapter?.itemClickListener = object : BillListAdapter.ItemClickListener {
            override fun onDeleteClick(position: Int) {
                val selPanCard = list[position]

                storageRef.child("bills/${selPanCard.email}/${selPanCard.fileName}").delete()

                // Delete DB Record
                Utils.dmyHmsTodmy(selPanCard.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.BILLS_TABLE).child(formattedDate)
                        .child(selPanCard.uid!!)
                        .child(selPanCard.pushKey!!)
                        .removeValue()
                }

                toast("Record deleted successfully")
                startActivity(Intent(mContext, BillDatesActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }
    }

    private fun notifyAdapter() {
        binding.progressBar?.gone()
        adapter?.notifyDataSetChanged()
        if (list.size == 0) {
            binding.tvError?.visible()
        } else {
            binding.tvError?.gone()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_download_all, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_download -> {
                list.forEach { item ->
                    item?.downloadUrl?.let { downloadTask(it) }
                }
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

    companion object {

    }
}
