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
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.ElectionCardListAdapter
import rahul.jagtap.dmas.admin.TextMsgActivity
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.ElectionCard
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.lang.reflect.Type


class ElectionCardListActivity : BaseActivity() {
    private var creator: User? = null
    var list = ArrayList<ElectionCard>()
    var adapter: ElectionCardListAdapter? = null
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
        binding.toolbarLayout.toolbarTitle?.text = "इलेक्शन कार्ड"

        val creatorJsonString = intent.getStringExtra("creatorJsonString")
        creator = Gson().fromJson<User>(creatorJsonString, User::class.java)
        val json = intent.getStringExtra("data")
        val type: Type = object : TypeToken<MutableList<ElectionCard>>() {}.type
        val businessPanCardList: MutableList<ElectionCard> = Gson().fromJson(json, type)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = ElectionCardListAdapter(mContext, list)
        adapter?.itemClickListener = object : ElectionCardListAdapter.ItemClickListener {
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
                selectedCustomerName = item?.applicantName.toString()
                item?.passportPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.passportPhotoFileName.toString(), isCreateFolderClicked) }
                item?.aadharFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.aadharFrontPhotoFileName.toString(), isCreateFolderClicked) }
                item?.aadharBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.aadharBackPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.electionCardFrontPhotoDownloadUrl)) item?.electionCardFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.electionCardFrontPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.electionCardBackPhotoDownloadUrl)) item?.electionCardBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.electionCardBackPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.oldElectionCardFrontPhotoDownloadUrl)) item?.oldElectionCardFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.oldElectionCardFrontPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.oldElectionCardBackPhotoDownloadUrl)) item?.oldElectionCardBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.oldElectionCardBackPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.correctionProofPhotoDownloadUrl)) item?.correctionProofPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.correctionProofPhotoFileName.toString(), isCreateFolderClicked) }
                item?.paymentScreenshotDownloadUrl?.let { it1 -> downloadTask(it1, item?.paymentScreenshotFileName.toString(), isCreateFolderClicked) }
                // Take and download screenshot
                val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(position)
                viewHolder?.itemView?.let { Utils.downloadScreenshot(it, window, isCreateFolderClicked, "Download/$selectedCustomerName Election Card") }
                toast("Downloading...")
            }

            override fun onMarkAsUnPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "2"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("election_cards").child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("election_cards").child(item.pushKey!!).setValue(item)
                }
            }

            override fun onMarkAsPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "1"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("election_cards").child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("election_cards").child(item.pushKey!!).setValue(item)
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
        if (businessPanCardList != null && businessPanCardList.size > 0) {
            list.addAll(businessPanCardList)
            notifyAdapter()
            binding.recyclerView?.visible()
            binding.tvError?.gone()
        } else {
            binding.recyclerView?.gone()
            binding.tvError?.visible()
        }
    }

    private fun performDelete(item: ElectionCard, closing_update: String) {
        // Delete Passport file
        storageRef.child("esuvidha/${item.email}/election_cards/${item.passportPhotoFileName}").delete()

        // Delete Aadhar Front file
        storageRef.child("esuvidha/${item.email}/election_cards/${item.aadharFrontPhotoFileName}").delete()

        // Delete Aadhar Back file
        storageRef.child("esuvidha/${item.email}/election_cards/${item.aadharBackPhotoFileName}").delete()

        // Delete Election Card Front file
        if (!TextUtils.isEmpty(item.electionCardFrontPhotoFileName)) storageRef.child("esuvidha/${item.email}/election_cards/${item.electionCardFrontPhotoFileName}").delete()

        // Delete Election Card Back file
        if (!TextUtils.isEmpty(item.electionCardBackPhotoFileName)) storageRef.child("esuvidha/${item.email}/election_cards/${item.electionCardBackPhotoFileName}").delete()

        // Delete Old Election Card Front file
        if (!TextUtils.isEmpty(item.oldElectionCardFrontPhotoFileName)) storageRef.child("esuvidha/${item.email}/election_cards/${item.oldElectionCardFrontPhotoFileName}").delete()

        // Delete Old Election Card Back file
        if (!TextUtils.isEmpty(item.oldElectionCardBackPhotoFileName)) storageRef.child("esuvidha/${item.email}/election_cards/${item.oldElectionCardBackPhotoFileName}").delete()

        // Delete Correction Proof Photo file
        if (!TextUtils.isEmpty(item.correctionProofPhotoFileName)) storageRef.child("esuvidha/${item.email}/election_cards/${item.correctionProofPhotoFileName}").delete()

        // Delete Payment SC file
        storageRef.child("esuvidha/${item.email}/election_cards/${item.paymentScreenshotFileName}").delete()

        item.closingUpdate = closing_update

        // Update record in Backup DB
        Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
            database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("election_cards").child(item.pushKey!!).setValue(item)
        }

        // Delete DB Record
        Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
            database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("election_cards").child(item.pushKey!!).removeValue()
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
        if (!url.startsWith("http")) {
            return false
        }
        val name = fileName
        try {
            val directory = if (isCreateFolderSelected) {
                File(Environment.getExternalStorageDirectory(), "Download/$selectedCustomerName Election Card")
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
                    // Delete Passport file
                    storageRef.child("esuvidha/${item.email}/election_cards/${item.passportPhotoFileName}").delete()

                    // Delete Aadhar Front file
                    storageRef.child("esuvidha/${item.email}/election_cards/${item.aadharFrontPhotoFileName}").delete()

                    // Delete Aadhar Back file
                    storageRef.child("esuvidha/${item.email}/election_cards/${item.aadharBackPhotoFileName}").delete()

                    // Delete Election Card Front file
                    if (!TextUtils.isEmpty(item.electionCardFrontPhotoFileName)) storageRef.child("esuvidha/${item.email}/election_cards/${item.electionCardFrontPhotoFileName}").delete()

                    // Delete Election Card Back file
                    if (!TextUtils.isEmpty(item.electionCardBackPhotoFileName)) storageRef.child("esuvidha/${item.email}/election_cards/${item.electionCardBackPhotoFileName}").delete()

                    // Delete Old Election Card Front file
                    if (!TextUtils.isEmpty(item.oldElectionCardFrontPhotoFileName)) storageRef.child("esuvidha/${item.email}/election_cards/${item.oldElectionCardFrontPhotoFileName}").delete()

                    // Delete Old Election Card Back file
                    if (!TextUtils.isEmpty(item.oldElectionCardBackPhotoFileName)) storageRef.child("esuvidha/${item.email}/election_cards/${item.oldElectionCardBackPhotoFileName}").delete()

                    // Delete Correction Proof Photo file
                    if (!TextUtils.isEmpty(item.correctionProofPhotoFileName)) storageRef.child("esuvidha/${item.email}/election_cards/${item.correctionProofPhotoFileName}").delete()

                    // Delete Payment SC file
                    storageRef.child("esuvidha/${item.email}/election_cards/${item.paymentScreenshotFileName}").delete()

                    item.closingUpdate = text.toString()

                    // Update record in Backup DB
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("election_cards").child(item.pushKey!!).setValue(item)
                    }

                    // Delete DB Record
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("election_cards").child(item.pushKey!!).removeValue()
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
