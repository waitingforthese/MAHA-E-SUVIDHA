package rahul.jagtap.dmas.admin.esuvidha

import android.app.DownloadManager
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.PanCardListAdapter
import rahul.jagtap.dmas.admin.TextMsgActivity
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.PanCard
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.lang.reflect.Type


class PanCardListActivity : BaseActivity() {
    private var creator: User? = null
    var list = ArrayList<PanCard>()
    var adapter: PanCardListAdapter? = null
    var paymentStatusChanged = false
    private lateinit var binding: ActivityViewBillsBinding
    var selectedCustomerName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_pan_card)

        val creatorJsonString = intent.getStringExtra("creatorJsonString")
        creator = Gson().fromJson<User>(creatorJsonString, User::class.java)
        val json = intent.getStringExtra("data")
        val type: Type = object : TypeToken<MutableList<PanCard>>() {}.type
        val panCardList: MutableList<PanCard> = Gson().fromJson(json, type)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = PanCardListAdapter(mContext, list)
        adapter?.itemClickListener = object : PanCardListAdapter.ItemClickListener {
            override fun onDeleteClick(position: Int) {
                val item = list[position]
                val closingUpdateList = Utils.closingUpdateList
                MaterialDialog.Builder(mContext!!).items(closingUpdateList).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                    run {
                        dialog?.dismiss()
                        performDelete(item, closingUpdateList[position])
                    }
                }.show()
            }

            override fun onDownloadAll(position: Int, isCreateFolderClicked: Boolean) {
                val item = list[position]
                selectedCustomerName = if (item?.applicantName.isNullOrEmpty()) item?.textbox1.toString() else item?.applicantName.toString()
                if (!TextUtils.isEmpty(item.oldPanCardPhotoDownloadUrl)) item?.oldPanCardPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.oldPanCardPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.passportPhotoDownloadUrl)) item?.passportPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.passportPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.aadharFrontPhotoDownloadUrl)) item?.aadharFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.aadharFrontPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.aadharBackPhotoDownloadUrl)) item?.aadharBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.aadharBackPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.aadharFatherFrontPhotoDownloadUrl)) item?.aadharFatherFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.aadharFatherFrontPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.aadharFatherBackPhotoDownloadUrl)) item?.aadharFatherBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.aadharFatherBackPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.signDownloadUrl)) item?.signDownloadUrl?.let { it1 -> downloadTask(it1, item?.signFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.marriageCertDownloadUrl)) item?.marriageCertDownloadUrl?.let { it1 -> downloadTask(it1, item?.marriageCertFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.paymentScreenshotDownloadUrl)) item?.paymentScreenshotDownloadUrl?.let { it1 -> downloadTask(it1, item?.paymentScreenshotFileName.toString(), isCreateFolderClicked) }

                if (!TextUtils.isEmpty(item.attachment1DownloadUrl)) item?.attachment1DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment1FileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.attachment2DownloadUrl)) item?.attachment2DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment2FileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.attachment3DownloadUrl)) item?.attachment3DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment3FileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.attachment4DownloadUrl)) item?.attachment4DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment4FileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.attachment5DownloadUrl)) item?.attachment5DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment5FileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.attachment6DownloadUrl)) item?.attachment6DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment6FileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.attachment7DownloadUrl)) item?.attachment7DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment7FileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.attachment8DownloadUrl)) item?.attachment8DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment8FileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.attachment9DownloadUrl)) item?.attachment9DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment9FileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.attachment10DownloadUrl)) item?.attachment10DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment10FileName.toString(), isCreateFolderClicked) }
                // Take and download screenshot
                val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(position)
                viewHolder?.itemView?.let { Utils.downloadScreenshot(it, window, isCreateFolderClicked, "Download/$selectedCustomerName Pan Card") }
                toast("Downloading...")
            }

            override fun onMarkAsUnPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "2"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("pan_cards").child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("pan_cards").child(item.pushKey!!).setValue(item)
                }
            }

            override fun onMarkAsPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "1"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("pan_cards").child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("pan_cards").child(item.pushKey!!).setValue(item)
                }
            }

            override fun onCallOrSms(phoneNo: String) {
                callOrSms(phoneNo)
            }

            override fun onCallOrSmsCreator() {
                creator?.contactNo?.let { callOrSms(it) }
            }
        }
        binding.recyclerView?.adapter = adapter
        if (panCardList != null && panCardList.size > 0) {
            list.addAll(panCardList)
            notifyAdapter()
            binding.recyclerView?.visible()
            binding.tvError?.gone()
        } else {
            binding.recyclerView?.gone()
            binding.tvError?.visible()
        }
    }

    private fun performDelete(item: PanCard, closing_update: String) {
        // Delete Old Pan Card Photo file
        if (!TextUtils.isEmpty(item.oldPanCardPhotoFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.oldPanCardPhotoFileName}").delete()

        // Delete Passport file
        if (!TextUtils.isEmpty(item.passportPhotoFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.passportPhotoFileName}").delete()

        // Delete Aadhar Front file
        if (!TextUtils.isEmpty(item.aadharFrontPhotoFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.aadharFrontPhotoFileName}").delete()

        // Delete Aadhar Back file
        if (!TextUtils.isEmpty(item.aadharBackPhotoFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.aadharBackPhotoFileName}").delete()

        // Delete Aadhar Father Front file
        if (!TextUtils.isEmpty(item.aadharFatherFrontPhotoFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.aadharFatherFrontPhotoFileName}").delete()

        // Delete Aadhar Father Back file
        if (!TextUtils.isEmpty(item.aadharFatherBackPhotoFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.aadharFatherBackPhotoFileName}").delete()

        // Delete Sign file
        if (!TextUtils.isEmpty(item.signFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.signFileName}").delete()

        // Delete Marriage Cert file
        if (!TextUtils.isEmpty(item.marriageCertFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.marriageCertFileName}").delete()

        // Delete Payment SC file
        if (!TextUtils.isEmpty(item.paymentScreenshotFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.paymentScreenshotFileName}").delete()

        if (!TextUtils.isEmpty(item.attachment1FileName))
            storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment1FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment2FileName))
            storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment2FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment3FileName))
            storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment3FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment4FileName))
            storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment4FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment5FileName))
            storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment5FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment6FileName))
            storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment6FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment7FileName))
            storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment7FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment8FileName))
            storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment8FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment9FileName))
            storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment9FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment10FileName))
            storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment10FileName}").delete()

        item.closingUpdate = closing_update

        // Update record in Backup DB
        Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
            database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("pan_cards").child(item.pushKey!!).setValue(item)
        }

        // Delete DB Record
        Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
            database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("pan_cards").child(item.pushKey!!).removeValue()
        }

        toast("Record deleted successfully")
        startActivity(Intent(mContext, ESuvidhaListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
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

    @Throws(Exception::class)
    private fun downloadTask(url: String, fileName: String, isCreateFolderSelected: Boolean = false): Boolean {
        if (url.isEmpty()) return false
        if (!url.startsWith("http")) {
            return false
        }
        val name = fileName
        try {
            val directory = if (isCreateFolderSelected) {
                File(Environment.getExternalStorageDirectory(), "Download/$selectedCustomerName Pan Card")
            } else {
                File(Environment.getExternalStorageDirectory(), "DCIM/Camera")
            }
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val result = File(directory, name)
            Log.e(">>>>>", "File download path: ${result.absolutePath}")
            // Check if the file already exists
            if (result.exists()) {
                Log.d(">>>>>", "File already exists: ${result.absolutePath}")
                return false
            }
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            request.setDestinationUri(Uri.fromFile(result))
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            downloadManager.enqueue(request) //mToast(mContext, "Starting download...");
            MediaScannerConnection.scanFile(mContext, arrayOf(result.toString()), null) { path, uri -> }
        } catch (e: Exception) {
            Log.e(">>>>>", e.toString()) //mToast(this, e.toString());
            return false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Log.e("in", "onsoftback")
                Utils.hideSoftKeyboard(this)
                if (paymentStatusChanged) {
                    startActivity(Intent(mContext, ESuvidhaListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }
                finish()
                return true
            }
            R.id.action_delete_all -> {
                deleteAll()
                return true
            }
            R.id.action_text_msg -> {
                startActivity(Intent(mContext, TextMsgActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isAdmin(): Boolean = app?.preferences?.loggedInUser?.isAdmin == "1" || app?.preferences?.loggedInUser?.userType == "2"

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_all, menu)
        val item = menu?.findItem(R.id.action_delete_all)
        item?.isVisible = isAdmin()
        val itemTextMsg = menu?.findItem(R.id.action_text_msg)
        itemTextMsg?.isVisible = isAdmin()
        return true
    }

    private fun deleteAll() {
        MaterialDialog.Builder(mContext!!).items(Utils.closingUpdateList).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
            run {
                dialog?.dismiss()
                list.forEach { item ->
                    // Delete Old Pan Card Photo file
                    if (!TextUtils.isEmpty(item.oldPanCardPhotoFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.oldPanCardPhotoFileName}").delete()

                    // Delete Passport file
                    if (!TextUtils.isEmpty(item.passportPhotoFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.passportPhotoFileName}").delete()

                    // Delete Aadhar Front file
                    if (!TextUtils.isEmpty(item.aadharFrontPhotoFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.aadharFrontPhotoFileName}").delete()

                    // Delete Aadhar Back file
                    if (!TextUtils.isEmpty(item.aadharBackPhotoFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.aadharBackPhotoFileName}").delete()

                    // Delete Aadhar Father Front file
                    if (!TextUtils.isEmpty(item.aadharFatherFrontPhotoFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.aadharFatherFrontPhotoFileName}").delete()

                    // Delete Aadhar Father Back file
                    if (!TextUtils.isEmpty(item.aadharFatherBackPhotoFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.aadharFatherBackPhotoFileName}").delete()

                    // Delete Sign file
                    if (!TextUtils.isEmpty(item.signFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.signFileName}").delete()

                    // Delete Marriage Cert file
                    if (!TextUtils.isEmpty(item.marriageCertFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.marriageCertFileName}").delete()

                    // Delete Payment SC file
                    if (!TextUtils.isEmpty(item.paymentScreenshotFileName)) storageRef.child("esuvidha/${item.email}/pan_cards/${item.paymentScreenshotFileName}").delete()

                    if (!TextUtils.isEmpty(item.attachment1FileName))
                        storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment1FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment2FileName))
                        storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment2FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment3FileName))
                        storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment3FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment4FileName))
                        storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment4FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment5FileName))
                        storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment5FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment6FileName))
                        storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment6FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment7FileName))
                        storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment7FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment8FileName))
                        storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment8FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment9FileName))
                        storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment9FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment10FileName))
                        storageRef.child("esuvidha/${item.email}/pan_cards/${item.attachment10FileName}").delete()

                    item.closingUpdate = text.toString()

                    // Update record in Backup DB
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("pan_cards").child(item.pushKey!!).setValue(item)
                    }

                    // Delete DB Record
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("pan_cards").child(item.pushKey!!).removeValue()
                    }
                }
                toast("Records deleted successfully")
                startActivity(Intent(mContext, ESuvidhaListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }.show()
    }

    override fun onBackPressed() {
        Log.e("in", "onBackPressed")
        if (paymentStatusChanged) {
            startActivity(Intent(mContext, ESuvidhaListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
            return
        }
        super.onBackPressed()
    }

    companion object {

    }
}
