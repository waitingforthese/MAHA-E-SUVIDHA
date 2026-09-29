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
import rahul.jagtap.dmas.adapter.AllGovtCardListAdapter
import rahul.jagtap.dmas.admin.TextMsgActivity
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.AllGovtCard
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.AllGovtCardsActivity
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.lang.reflect.Type


class AllGovtCardListActivity : BaseActivity() {
    private var creator: User? = null

    //    private var tempFile: File? = null
    //    private var resultFileUri: Uri? = null
    //    private lateinit var resultFile: File
    var list = ArrayList<AllGovtCard>() //    private lateinit var simplyPdfDocument: SimplyPdfDocument //    var pdfDownloaded = false

    var adapter: AllGovtCardListAdapter? = null
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
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.all_govt_cards)

        val creatorJsonString = intent.getStringExtra("creatorJsonString")
        creator = Gson().fromJson<User>(creatorJsonString, User::class.java)
        val json = intent.getStringExtra("data")
        val type: Type = object : TypeToken<MutableList<AllGovtCard>>() {}.type
        val passportList: MutableList<AllGovtCard> = Gson().fromJson(json, type)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = AllGovtCardListAdapter(mContext, list)
        adapter?.itemClickListener = object : AllGovtCardListAdapter.ItemClickListener {
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
                if (!TextUtils.isEmpty(item.aadharFrontPhotoDownloadUrl)) item.aadharFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.aadharFrontPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.aadharBackPhotoDownloadUrl)) item.aadharBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.aadharBackPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.bankPassbookPhotoFileName) && !TextUtils.isEmpty(item.bankPassbookPhotoDownloadUrl)) item.bankPassbookPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.bankPassbookPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.rationFrontPhotoFileName) && !TextUtils.isEmpty(item.rationFrontPhotoDownloadUrl)) item.rationFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.rationFrontPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.rationBackPhotoFileName) && !TextUtils.isEmpty(item.rationBackPhotoDownloadUrl)) item.rationBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.rationBackPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.rationElectionFrontPhotoFileName) && !TextUtils.isEmpty(item.rationElectionFrontPhotoDownloadUrl)) item.rationElectionFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.rationElectionFrontPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.rationElectionBackPhotoFileName) && !TextUtils.isEmpty(item.rationElectionBackPhotoDownloadUrl)) item.rationElectionBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.rationElectionBackPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.panPhotoFileName) && !TextUtils.isEmpty(item.panPhotoDownloadUrl)) item.panPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.panPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.paymentScreenshotDownloadUrl)) item.paymentScreenshotDownloadUrl?.let { it1 -> downloadTask(it1, item.paymentScreenshotFileName.toString(), isCreateFolderClicked) }

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
                viewHolder?.itemView?.let { Utils.downloadScreenshot(it, window, isCreateFolderClicked, "Download/$selectedCustomerName All Govt Cards") }
                toast("Downloading...")
            }

            override fun onMarkAsUnPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "2"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child(AllGovtCardsActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child(AllGovtCardsActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                }
            }

            override fun onMarkAsPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "1"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child(AllGovtCardsActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child(AllGovtCardsActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                }
            }

            override fun onCallOrSms(phoneNo: String) {
                callOrSms(phoneNo)
            }

            override fun onCallOrSmsCreator() {
                creator?.contactNo?.let { callOrSms(it) }
            }

            override fun onPdfDownload(position: Int) {
                val item = list[position] //                downloadPdf(item)
            }
        }
        binding.recyclerView?.adapter = adapter
        if (passportList != null && passportList.size > 0) {
            list.addAll(passportList)
            notifyAdapter()
            binding.recyclerView?.visible()
            binding.tvError?.gone()
        } else {
            binding.recyclerView?.gone()
            binding.tvError?.visible()
        } //        createSimplyPdfDocument()
    }

    private fun performDelete(
        item: AllGovtCard, closing_update: String
    ) { // Delete Aadhar Front file
        if (!TextUtils.isEmpty(item.aadharFrontPhotoFileName)) {
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.aadharFrontPhotoFileName}").delete()
        }

        // Delete Aadhar Back file
        if (!TextUtils.isEmpty(item.aadharBackPhotoFileName)) {
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.aadharBackPhotoFileName}").delete()
        }

        // Delete Bank Passbook file
        if (!TextUtils.isEmpty(item.bankPassbookPhotoFileName)) {
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.bankPassbookPhotoFileName}").delete()
        }

        // Delete Ration Front file
        if (!TextUtils.isEmpty(item.rationFrontPhotoFileName)) {
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.rationFrontPhotoFileName}").delete()
        }

        // Delete Ration Back file
        if (!TextUtils.isEmpty(item.rationBackPhotoFileName)) {
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.rationBackPhotoFileName}").delete()
        }

        if (!TextUtils.isEmpty(item.rationElectionFrontPhotoFileName)) {
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.rationElectionFrontPhotoFileName}").delete()
        }

        if (!TextUtils.isEmpty(item.rationElectionBackPhotoFileName)) {
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.rationElectionBackPhotoFileName}").delete()
        }

        // Delete Pan Card file
        if (!TextUtils.isEmpty(item.panPhotoFileName)) {
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.panPhotoFileName}").delete()
        }

        // Delete Payment SC file
        if (!TextUtils.isEmpty(item.paymentScreenshotFileName)) {
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.paymentScreenshotFileName}").delete()
        }

        if (!TextUtils.isEmpty(item.attachment1FileName))
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment1FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment2FileName))
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment2FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment3FileName))
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment3FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment4FileName))
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment4FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment5FileName))
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment5FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment6FileName))
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment6FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment7FileName))
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment7FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment8FileName))
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment8FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment9FileName))
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment9FileName}").delete()
        if (!TextUtils.isEmpty(item.attachment10FileName))
            storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment10FileName}").delete()

        item.closingUpdate = closing_update

        // Update record in Backup DB
        Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
            database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child(AllGovtCardsActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
        }

        // Delete DB Record
        Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
            database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child(AllGovtCardsActivity.PATH_NAME).child(item.pushKey!!).removeValue().addOnCompleteListener {
                Log.e("OnCompleteListen: ", "delete complete success")
            }.addOnFailureListener { //                    it.printStackTrace()
                Log.e("OnFailureListen: ", "" + it.message.toString())
            }
        }

        toast("Record deleted successfully")
        startActivity(Intent(mContext, ESuvidhaListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    //    private fun downloadPdf(item: JyotishShastra) {
    //        if (pdfDownloaded) {
    //            createSimplyPdfDocument()
    //            pdfDownloaded = false
    //        }
    //        launchCoroutine({
    //            val properties = TableProperties().apply {
    //                borderColor = "#000000"
    //                borderWidth = 1
    //            }
    //
    //            val rows = LinkedList<LinkedList<Cell>>()
    //
    //            /*
    //            * This will add a table with 12 equal width column and N rows
    //            */
    //            val dividedBy = 4
    //            val widthToSet = simplyPdfDocument.usablePageWidth / dividedBy
    //            val valueColumnWidth = widthToSet + widthToSet + widthToSet
    //
    //            LinkedList<Cell>().apply {
    //                add(TextCell("सुविधा", TextProperties().apply { textSize = 5 }, widthToSet))
    //                add(TextCell(item.serviceType.toString(), TextProperties().apply { textSize = 5 }, valueColumnWidth))
    //                rows.add(this)
    //            }
    //            LinkedList<Cell>().apply {
    //                add(TextCell("व्यक्तीचे संपूर्ण नाव", TextProperties().apply { textSize = 5 }, widthToSet))
    //                add(TextCell(item.fullName.toString(), TextProperties().apply { textSize = 5 }, valueColumnWidth))
    //                rows.add(this)
    //            }
    //            LinkedList<Cell>().apply {
    //                add(TextCell("व्यक्तीची जन्मतारीख", TextProperties().apply { textSize = 5 }, widthToSet))
    //                add(TextCell(item.dob.toString(), TextProperties().apply { textSize = 5 }, valueColumnWidth))
    //                rows.add(this)
    //            }
    //            LinkedList<Cell>().apply {
    //                add(TextCell("जन्म वेळ (AM/PM)", TextProperties().apply { textSize = 5 }, widthToSet))
    //                add(TextCell(item.birthtime.toString(), TextProperties().apply { textSize = 5 }, valueColumnWidth))
    //                rows.add(this)
    //            }
    //            LinkedList<Cell>().apply {
    //                add(TextCell("जन्म ठिकाण", TextProperties().apply { textSize = 5 }, widthToSet))
    //                add(TextCell(item.birthplace.toString(), TextProperties().apply { textSize = 5 }, valueColumnWidth))
    //                rows.add(this)
    //            }
    //            LinkedList<Cell>().apply {
    //                add(TextCell("मुलीचे नाव", TextProperties().apply { textSize = 5 }, widthToSet))
    //                add(TextCell(item.girlFullName.toString(), TextProperties().apply { textSize = 5 }, valueColumnWidth))
    //                rows.add(this)
    //            }
    //            LinkedList<Cell>().apply {
    //                add(TextCell("मुलीची जन्मतारीख", TextProperties().apply { textSize = 5 }, widthToSet))
    //                add(TextCell(item.girlDob.toString(), TextProperties().apply { textSize = 5 }, valueColumnWidth))
    //                rows.add(this)
    //            }
    //            LinkedList<Cell>().apply {
    //                add(TextCell("मुलीची जन्म वेळ (AM/PM)", TextProperties().apply { textSize = 5 }, widthToSet))
    //                add(TextCell(item.girlBirthTime.toString(), TextProperties().apply { textSize = 5 }, valueColumnWidth))
    //                rows.add(this)
    //            }
    //            LinkedList<Cell>().apply {
    //                add(TextCell("मुलीचे जन्म ठिकाण", TextProperties().apply { textSize = 5 }, widthToSet))
    //                add(TextCell(item.girlBirthPlace.toString(), TextProperties().apply { textSize = 5 }, valueColumnWidth))
    //                rows.add(this)
    //            }
    //            LinkedList<Cell>().apply {
    //                add(TextCell("प्रश्न", TextProperties().apply { textSize = 5 }, widthToSet))
    //                add(TextCell(item.question.toString(), TextProperties().apply { textSize = 5 }, valueColumnWidth))
    //                rows.add(this)
    //            }
    //
    //            toast("Downloading...")
    //            simplyPdfDocument.table.draw(rows, properties)
    //
    //            simplyPdfDocument.finish()
    //
    //            withContext(Dispatchers.Main) {
    //                pdfDownloaded = true
    //                Toast.makeText(baseContext, "PDF downloaded successfully", Toast.LENGTH_SHORT).show()
    //                Log.e("file uri", resultFileUri.toString())
    //                try { // Perform operations on the document using its URI.
    //                    try {
    //                        val intent = Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(baseContext, "${BuildConfig.APPLICATION_ID}.fileprovider", resultFile))
    //                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    //                        startActivity(intent)
    //                    } catch (e: Exception) {
    //                        e.printStackTrace()
    //                    }
    //                } catch (e: Exception) {
    //                    e.printStackTrace();
    //                }
    //            }
    //        }, { coroutineContext, throwable ->
    //            throwable.printStackTrace()
    //        })
    //    }

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
                File(Environment.getExternalStorageDirectory(), "Download/$selectedCustomerName All Govt Cards")
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

    //    protected fun createSimplyPdfDocument() {
    //        val file = File(Environment.getExternalStorageDirectory(), "Download")
    //        if (!file.exists()) {
    //            file.mkdirs()
    //        }
    //        resultFile = File(file.absolutePath + File.separator.toString() + "Credit_card_${System.currentTimeMillis()}.pdf") //            simplyPdfDocument = SimplyPdf.with(baseContext, resultFile).colorMode(DocumentInfo.ColorMode.COLOR).paperSize(PrintAttributes.MediaSize.ISO_A4) //                .margin(Margin(20U, 20U, 20U, 20U)) //                .pageModifier(PageHeader(headerList)).firstPageBackgroundColor(Color.WHITE).paperOrientation(DocumentInfo.Orientation.PORTRAIT).build()
    //        resultFileUri = Uri.fromFile(resultFile)
    //        simplyPdfDocument = SimplyPdf.with(baseContext, resultFile).colorMode(DocumentInfo.ColorMode.COLOR).paperSize(PrintAttributes.MediaSize.ISO_A4) //            .margin(Margin(15U, 15U, 15U, 15U))
    //            .paperOrientation(DocumentInfo.Orientation.PORTRAIT).pageModifier(PageHeader(LinkedList<Cell>().apply {
    //                add(TextCell("Credit Card", TextProperties().apply {
    //                    textSize = 24
    //                    alignment = Layout.Alignment.ALIGN_CENTER
    //                    textColor = "#000000"
    //                }, Cell.MATCH_PARENT)) //                add(TextCell("$strSubTitle", TextProperties().apply {
    //                //                    textSize = 20
    //                //                    alignment = Layout.Alignment.ALIGN_CENTER
    //                //                    textColor = "#000000"
    //                //                }, Cell.MATCH_PARENT))
    //                //                add(TextCell("$strDescription", TextProperties().apply {
    //                //                    textSize = 17
    //                //                    alignment = Layout.Alignment.ALIGN_CENTER
    //                //                    textColor = "#000000"
    //                //                }, Cell.MATCH_PARENT))
    //            })).pageModifier(HeaderLinePageModifier()).build()
    //    }

    //    protected inner class HeaderLinePageModifier : PageModifier() {
    //        override fun render(simplyPdfDocument: SimplyPdfDocument) {
    //            simplyPdfDocument.apply {
    //                val rect = RectF(startMargin.toFloat(), pageContentHeight.toFloat(), usablePageWidth.toFloat() + endMargin, (pageContentHeight + 1).toFloat())
    //
    //                currentPage.canvas.drawRect(rect, Paint(Paint.ANTI_ALIAS_FLAG).apply {
    //                    color = Color.BLACK
    //                })
    //
    //                addContentHeight(rect.height().toInt())
    //            }
    //            simplyPdfDocument.insertEmptySpace(25)
    //        }
    //    }

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
                    // Delete Aadhar Front file
                    if (!TextUtils.isEmpty(item.aadharFrontPhotoFileName)) {
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.aadharFrontPhotoFileName}").delete()
                    }

                    // Delete Aadhar Back file
                    if (!TextUtils.isEmpty(item.aadharBackPhotoFileName)) {
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.aadharBackPhotoFileName}").delete()
                    }

                    // Delete Bank Passbook file
                    if (!TextUtils.isEmpty(item.bankPassbookPhotoFileName)) {
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.bankPassbookPhotoFileName}").delete()
                    }

                    // Delete Ration Front file
                    if (!TextUtils.isEmpty(item.rationFrontPhotoFileName)) {
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.rationFrontPhotoFileName}").delete()
                    }

                    // Delete Ration Back file
                    if (!TextUtils.isEmpty(item.rationBackPhotoFileName)) {
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.rationBackPhotoFileName}").delete()
                    }

                    if (!TextUtils.isEmpty(item.rationElectionFrontPhotoFileName)) {
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.rationElectionFrontPhotoFileName}").delete()
                    }

                    if (!TextUtils.isEmpty(item.rationElectionBackPhotoFileName)) {
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.rationElectionBackPhotoFileName}").delete()
                    }

                    // Delete Pan Card file
                    if (!TextUtils.isEmpty(item.panPhotoFileName)) {
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.panPhotoFileName}").delete()
                    }

                    // Delete Payment SC file
                    if (!TextUtils.isEmpty(item.paymentScreenshotFileName)) {
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.paymentScreenshotFileName}").delete()
                    }

                    if (!TextUtils.isEmpty(item.attachment1FileName))
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment1FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment2FileName))
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment2FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment3FileName))
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment3FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment4FileName))
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment4FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment5FileName))
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment5FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment6FileName))
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment6FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment7FileName))
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment7FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment8FileName))
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment8FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment9FileName))
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment9FileName}").delete()
                    if (!TextUtils.isEmpty(item.attachment10FileName))
                        storageRef.child("esuvidha/${item.email}/${AllGovtCardsActivity.PATH_NAME}/${item.attachment10FileName}").delete()

                    item.closingUpdate = text.toString()

                    // Update record in Backup DB
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child(AllGovtCardsActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                    }

                    // Delete DB Record
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child(AllGovtCardsActivity.PATH_NAME).child(item.pushKey!!).removeValue().addOnCompleteListener {
                            Log.e("OnCompleteListen: ", "delete complete success")
                        }.addOnFailureListener { //                    it.printStackTrace()
                            Log.e("OnFailureListen: ", "" + it.message.toString())
                        }
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
