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
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.*
import rahul.jagtap.dmas.admin.TextMsgActivity
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.*
import rahul.jagtap.dmas.user.GovtSchemesActivity
import rahul.jagtap.dmas.user.GstRegistrationActivity
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.lang.reflect.Type
import java.util.*

class GovtSchemeListActivity : BaseActivity() {
    var list = ArrayList<GovtScheme>()
    private var creator: User? = null
    var adapter: GovtSchemeListAdapter? = null
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
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_govt_schemes)

        val creatorJsonString = intent.getStringExtra("creatorJsonString")
        creator = Gson().fromJson(creatorJsonString, User::class.java)
        val json = intent.getStringExtra("data")
        val type: Type = object : TypeToken<MutableList<GovtScheme>>() {}.type
        val dataList: MutableList<GovtScheme> = Gson().fromJson(json, type)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = GovtSchemeListAdapter(mContext, list)
        adapter?.itemClickListener = object : GovtSchemeListAdapter.ItemClickListener {
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
                item?.aadharFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.aadharFrontPhotoFileName.toString(), isCreateFolderClicked) }
                item?.aadharBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.aadharBackPhotoFileName.toString(), isCreateFolderClicked) }
                item?.passbookPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.passbookPhotoFileName.toString(), isCreateFolderClicked) }
                item?.panPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.panPhotoFileName.toString(), isCreateFolderClicked) }
                item?.rationFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.rationFrontPhotoFileName.toString(), isCreateFolderClicked) }
                item?.rationBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.rationBackPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item?.signPhotoDownloadUrl)) item?.signPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.signPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item?.passportPhotoDownloadUrl)) item?.passportPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.passportPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item?.schoolLCDownloadUrl)) item?.schoolLCDownloadUrl?.let { it1 -> downloadTask(it1, item?.schoolLCFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item?.birthCertDownloadUrl)) item?.birthCertDownloadUrl?.let { it1 -> downloadTask(it1, item?.birthCertFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item?.incomeCertDownloadUrl)) item?.incomeCertDownloadUrl?.let { it1 -> downloadTask(it1, item?.incomeCertFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item?.castCertDownloadUrl)) item?.castCertDownloadUrl?.let { it1 -> downloadTask(it1, item?.castCertFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item?.projectReportDownloadUrl)) item?.projectReportDownloadUrl?.let { it1 -> downloadTask(it1, item?.projectReportFileName.toString(), isCreateFolderClicked) }
                item?.paymentScreenshotDownloadUrl?.let { it1 -> downloadTask(it1, item?.paymentScreenshotFileName.toString(), isCreateFolderClicked) }
                item?.attachment1DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment1FileName.toString(), isCreateFolderClicked) }
                item?.attachment2DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment2FileName.toString(), isCreateFolderClicked) }
                item?.attachment3DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment3FileName.toString(), isCreateFolderClicked) }
                item?.attachment4DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment4FileName.toString(), isCreateFolderClicked) }
                item?.attachment5DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment5FileName.toString(), isCreateFolderClicked) }
                item?.attachment6DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment6FileName.toString(), isCreateFolderClicked) }
                item?.attachment7DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment7FileName.toString(), isCreateFolderClicked) }
                item?.attachment8DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment8FileName.toString(), isCreateFolderClicked) }
                item?.attachment9DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment9FileName.toString(), isCreateFolderClicked) }
                item?.attachment10DownloadUrl?.let { it1 -> downloadTask(it1, item?.attachment10FileName.toString(), isCreateFolderClicked) }
                // Take and download screenshot
                val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(position)
                viewHolder?.itemView?.let { Utils.downloadScreenshot(it, window, isCreateFolderClicked, "Download/$selectedCustomerName Govt Scheme") }
                toast("Downloading...")
            }

            override fun onMarkAsUnPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "2"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child(GovtSchemesActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child(GovtSchemesActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                }
            }

            override fun onMarkAsPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "1"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child(GovtSchemesActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child(GovtSchemesActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
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

        if (dataList != null && dataList.size > 0) {
            list.addAll(dataList)
            notifyAdapter()
            binding.recyclerView?.visible()
            binding.tvError?.gone()
        } else {
            binding.recyclerView?.gone()
            binding.tvError?.visible()
        }
    }

    private fun performDelete(item: GovtScheme, closing_update: String) { // Delete PAN file
        if (!TextUtils.isEmpty(item.panPhotoFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.panPhotoFileName}").delete()

        // Delete Aadhar Front file
        if (!TextUtils.isEmpty(item.aadharFrontPhotoFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.aadharFrontPhotoFileName}").delete()

        // Delete Aadhar Back file
        if (!TextUtils.isEmpty(item.aadharBackPhotoFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.aadharBackPhotoFileName}").delete()

        // Delete Ration Front file
        if (!TextUtils.isEmpty(item.rationFrontPhotoFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.rationFrontPhotoFileName}").delete()

        // Delete Ration Back file
        if (!TextUtils.isEmpty(item.rationBackPhotoFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.rationBackPhotoFileName}").delete()

        // Delete Passbook file
        if (!TextUtils.isEmpty(item.passbookPhotoFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.passbookPhotoFileName}").delete()

        if (!TextUtils.isEmpty(item.signPhotoFileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.signPhotoFileName}").delete()
        if (!TextUtils.isEmpty(item.passportPhotoFileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.passportPhotoFileName}").delete()
        if (!TextUtils.isEmpty(item.schoolLCFileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.schoolLCFileName}").delete()
        if (!TextUtils.isEmpty(item.birthCertFileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.birthCertFileName}").delete()
        if (!TextUtils.isEmpty(item.incomeCertFileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.incomeCertFileName}").delete()
        if (!TextUtils.isEmpty(item.castCertFileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.castCertFileName}").delete()
        if (!TextUtils.isEmpty(item.projectReportFileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.projectReportFileName}").delete()

        // Delete Payment SC file
        if (!TextUtils.isEmpty(item.paymentScreenshotFileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.paymentScreenshotFileName}").delete()
        if (!TextUtils.isEmpty(item.attachment1FileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment1FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment2FileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment2FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment3FileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment3FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment4FileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment4FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment5FileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment5FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment6FileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment6FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment7FileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment7FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment8FileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment8FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment9FileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment9FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment10FileName))
            storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment10FileName}").delete()

        item.closingUpdate = closing_update

        // Update record in Backup DB
        Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
            database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child(GovtSchemesActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
        }

        // Delete DB Record
        Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
            database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child(GovtSchemesActivity.PATH_NAME).child(item.pushKey!!).removeValue()
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
                File(Environment.getExternalStorageDirectory(), "Download/$selectedCustomerName Govt Scheme")
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
                    if (!TextUtils.isEmpty(item.panPhotoFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.panPhotoFileName}").delete()

                    // Delete Aadhar Front file
                    if (!TextUtils.isEmpty(item.aadharFrontPhotoFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.aadharFrontPhotoFileName}").delete()

                    // Delete Aadhar Back file
                    if (!TextUtils.isEmpty(item.aadharBackPhotoFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.aadharBackPhotoFileName}").delete()

                    // Delete Ration Front file
                    if (!TextUtils.isEmpty(item.rationFrontPhotoFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.rationFrontPhotoFileName}").delete()

                    // Delete Ration Back file
                    if (!TextUtils.isEmpty(item.rationBackPhotoFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.rationBackPhotoFileName}").delete()

                    // Delete Passbook file
                    if (!TextUtils.isEmpty(item.passbookPhotoFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.passbookPhotoFileName}").delete()

                    if (!TextUtils.isEmpty(item.signPhotoFileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.signPhotoFileName}").delete()
                    if (!TextUtils.isEmpty(item.passportPhotoFileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.passportPhotoFileName}").delete()
                    if (!TextUtils.isEmpty(item.schoolLCFileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.schoolLCFileName}").delete()
                    if (!TextUtils.isEmpty(item.birthCertFileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.birthCertFileName}").delete()
                    if (!TextUtils.isEmpty(item.incomeCertFileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.incomeCertFileName}").delete()
                    if (!TextUtils.isEmpty(item.castCertFileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.castCertFileName}").delete()
                    if (!TextUtils.isEmpty(item.projectReportFileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.projectReportFileName}").delete()

                    // Delete Payment SC file
                    if (!TextUtils.isEmpty(item.paymentScreenshotFileName)) storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.paymentScreenshotFileName}").delete()

                    if (!TextUtils.isEmpty(item.attachment1FileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment1FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment2FileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment2FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment3FileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment3FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment4FileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment4FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment5FileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment5FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment6FileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment6FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment7FileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment7FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment8FileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment8FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment9FileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment9FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment10FileName))
                        storageRef.child("esuvidha/${item.email}/${GovtSchemesActivity.PATH_NAME}/${item.attachment10FileName}").delete()

                    item.closingUpdate = text.toString()

                    // Update record in Backup DB
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child(GovtSchemesActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                    }

                    // Delete DB Record
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child(GovtSchemesActivity.PATH_NAME).child(item.pushKey!!).removeValue()
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
