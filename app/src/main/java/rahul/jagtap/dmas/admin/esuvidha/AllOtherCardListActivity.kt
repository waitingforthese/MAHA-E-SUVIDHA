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
import rahul.jagtap.dmas.adapter.AllOtherCardListAdapter
import rahul.jagtap.dmas.admin.TextMsgActivity
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.AllOtherCard
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.AllOtherDocsActivity
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.lang.reflect.Type
import java.util.*


class AllOtherCardListActivity : BaseActivity() {
    private var creator: User? = null

    var list = ArrayList<AllOtherCard>()

    var adapter: AllOtherCardListAdapter? = null
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
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.all_other_docs)

        val creatorJsonString = intent.getStringExtra("creatorJsonString")
        creator = Gson().fromJson<User>(creatorJsonString, User::class.java)
        val json = intent.getStringExtra("data")
        val type: Type = object : TypeToken<MutableList<AllOtherCard>>() {}.type
        val passportList: MutableList<AllOtherCard> = Gson().fromJson(json, type)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = AllOtherCardListAdapter(mContext, list)
        adapter?.itemClickListener = object : AllOtherCardListAdapter.ItemClickListener {
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
                item.applicantPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.applicantPhotoFileName.toString(), isCreateFolderClicked) }
                item.aadharFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.aadharFrontPhotoFileName.toString(), isCreateFolderClicked) }
                item.aadharBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.aadharBackPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.rationFrontPhotoFileName) && !TextUtils.isEmpty(item.rationFrontPhotoDownloadUrl))
                    item.rationFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.rationFrontPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.rationBackPhotoFileName) && !TextUtils.isEmpty(item.rationBackPhotoDownloadUrl))
                    item.rationBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item.rationBackPhotoFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.applicantSLCFileName) && !TextUtils.isEmpty(item.applicantSLCDownloadUrl))
                    item.applicantSLCDownloadUrl?.let { it1 -> downloadTask(it1, item.applicantSLCFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.applicantCastCertXeroxFileName) && !TextUtils.isEmpty(item.applicantCastCertXeroxDownloadUrl))
                    item.applicantCastCertXeroxDownloadUrl?.let { it1 -> downloadTask(it1, item.applicantCastCertXeroxFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.tehsildarIncomeCertFileName) && !TextUtils.isEmpty(item.tehsildarIncomeCertDownloadUrl))
                    item.tehsildarIncomeCertDownloadUrl?.let { it1 -> downloadTask(it1, item.tehsildarIncomeCertFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.fatherSLCFileName) && !TextUtils.isEmpty(item.fatherSLCDownloadUrl))
                    item.fatherSLCDownloadUrl?.let { it1 -> downloadTask(it1, item.fatherSLCFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.grandpaSLCFileName) && !TextUtils.isEmpty(item.fatherSLCDownloadUrl))
                    item.fatherSLCDownloadUrl?.let { it1 -> downloadTask(it1, item.grandpaSLCFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.otherImpFileName) && !TextUtils.isEmpty(item.otherImpDownloadUrl))
                    item.otherImpDownloadUrl?.let { it1 -> downloadTask(it1, item.otherImpFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.vanshavalFileName) && !TextUtils.isEmpty(item.vanshavalDownloadUrl))
                    item.vanshavalDownloadUrl?.let { it1 -> downloadTask(it1, item.vanshavalFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.vanshavalPersonCastCertFileName) && !TextUtils.isEmpty(item.vanshavalPersonCastCertDownloadUrl))
                    item.vanshavalPersonCastCertDownloadUrl?.let { it1 -> downloadTask(it1, item.vanshavalPersonCastCertFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.localEnquiryReportFileName) && !TextUtils.isEmpty(item.localEnquiryReportDownloadUrl))
                    item.localEnquiryReportDownloadUrl?.let { it1 -> downloadTask(it1, item.localEnquiryReportFileName.toString(), isCreateFolderClicked) }
                if (!TextUtils.isEmpty(item.applicantSignSpecimenFileName) && !TextUtils.isEmpty(item.applicantSignSpecimenDownloadUrl))
                    item.applicantSignSpecimenDownloadUrl?.let { it1 -> downloadTask(it1, item.applicantSignSpecimenFileName.toString(), isCreateFolderClicked) }
                item.paymentScreenshotDownloadUrl?.let { it1 -> downloadTask(it1, item.paymentScreenshotFileName.toString(), isCreateFolderClicked) }
                // Take and download screenshot
                val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(position)
                viewHolder?.itemView?.let { Utils.downloadScreenshot(it, window, isCreateFolderClicked, "Download/$selectedCustomerName All Other Docs") }
                toast("Downloading...")
            }

            override fun onMarkAsUnPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "2"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child(AllOtherDocsActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child(AllOtherDocsActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                }
            }

            override fun onMarkAsPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "1"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child(AllOtherDocsActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child(AllOtherDocsActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
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

    private fun performDelete(item: AllOtherCard, closing_update: String) {
        // Delete Applicant Photo file
        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.applicantPhotoFileName}").delete()

        // Delete Aadhar Front file
        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.aadharFrontPhotoFileName}").delete()

        // Delete Aadhar Back file
        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.aadharBackPhotoFileName}").delete()

        // Delete Ration Front file
        if (!TextUtils.isEmpty(item.rationFrontPhotoFileName))
            storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.rationFrontPhotoFileName}").delete()

        // Delete Ration Back file
        if (!TextUtils.isEmpty(item.rationBackPhotoFileName))
            storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.rationBackPhotoFileName}").delete()

        // Delete Applicant SLC file
        if (!TextUtils.isEmpty(item.applicantSLCFileName))
            storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.applicantSLCFileName}").delete()

        // Delete Applicant Cast Cert Xerox file
        if (!TextUtils.isEmpty(item.applicantCastCertXeroxFileName))
            storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.applicantCastCertXeroxFileName}").delete()

        // Delete Tehsildar Income Cert file
        if (!TextUtils.isEmpty(item.tehsildarIncomeCertFileName))
            storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.tehsildarIncomeCertFileName}").delete()

        // Delete Father SLC file
        if (!TextUtils.isEmpty(item.fatherSLCFileName))
            storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.fatherSLCFileName}").delete()

        // Delete Grandpa SLC file
        if (!TextUtils.isEmpty(item.grandpaSLCFileName))
            storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.grandpaSLCFileName}").delete()

        // Delete Other Imp file
        if (!TextUtils.isEmpty(item.otherImpFileName))
            storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.otherImpFileName}").delete()

        // Delete Vanshaval file
        if (!TextUtils.isEmpty(item.vanshavalFileName))
            storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.vanshavalFileName}").delete()

        // Delete Vanshaval Person Caste Cert file
        if (!TextUtils.isEmpty(item.vanshavalPersonCastCertFileName))
            storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.vanshavalPersonCastCertFileName}").delete()

        // Delete Applicant Sign Specimen file
        if (!TextUtils.isEmpty(item.applicantSignSpecimenFileName))
            storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.applicantSignSpecimenFileName}").delete()

        // Delete Local Enquiry Report file
        if (!TextUtils.isEmpty(item.localEnquiryReportFileName))
            storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.localEnquiryReportFileName}").delete()

        // Delete Payment SC file
        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.paymentScreenshotFileName}").delete()

        item.closingUpdate = closing_update

        // Update record in Backup DB
        Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
            database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child(AllOtherDocsActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
        }

        // Delete DB Record
        Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
            database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child(AllOtherDocsActivity.PATH_NAME).child(item.pushKey!!).removeValue()
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
                File(Environment.getExternalStorageDirectory(), "Download/$selectedCustomerName All Other Docs")
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
                    // Delete Applicant Photo file
                    storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.applicantPhotoFileName}").delete()

                    // Delete Aadhar Front file
                    storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.aadharFrontPhotoFileName}").delete()

                    // Delete Aadhar Back file
                    storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.aadharBackPhotoFileName}").delete()

                    // Delete Ration Front file
                    if (!TextUtils.isEmpty(item.rationFrontPhotoFileName))
                        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.rationFrontPhotoFileName}").delete()

                    // Delete Ration Back file
                    if (!TextUtils.isEmpty(item.rationBackPhotoFileName))
                        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.rationBackPhotoFileName}").delete()

                    // Delete Applicant SLC file
                    if (!TextUtils.isEmpty(item.applicantSLCFileName))
                        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.applicantSLCFileName}").delete()

                    // Delete Applicant Cast Cert Xerox file
                    if (!TextUtils.isEmpty(item.applicantCastCertXeroxFileName))
                        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.applicantCastCertXeroxFileName}").delete()

                    // Delete Tehsildar Income Cert file
                    if (!TextUtils.isEmpty(item.tehsildarIncomeCertFileName))
                        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.tehsildarIncomeCertFileName}").delete()

                    // Delete Father SLC file
                    if (!TextUtils.isEmpty(item.fatherSLCFileName))
                        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.fatherSLCFileName}").delete()

                    // Delete Grandpa SLC file
                    if (!TextUtils.isEmpty(item.grandpaSLCFileName))
                        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.grandpaSLCFileName}").delete()

                    // Delete Other Imp file
                    if (!TextUtils.isEmpty(item.otherImpFileName))
                        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.otherImpFileName}").delete()

                    // Delete Vanshaval file
                    if (!TextUtils.isEmpty(item.vanshavalFileName))
                        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.vanshavalFileName}").delete()

                    // Delete Vanshaval Person Caste Cert file
                    if (!TextUtils.isEmpty(item.vanshavalPersonCastCertFileName))
                        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.vanshavalPersonCastCertFileName}").delete()

                    // Delete Applicant Sign Specimen file
                    if (!TextUtils.isEmpty(item.applicantSignSpecimenFileName))
                        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.applicantSignSpecimenFileName}").delete()

                    // Delete Local Enquiry Report file
                    if (!TextUtils.isEmpty(item.localEnquiryReportFileName))
                        storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.localEnquiryReportFileName}").delete()

                    // Delete Payment SC file
                    storageRef.child("esuvidha/${item.email}/${AllOtherDocsActivity.PATH_NAME}/${item.paymentScreenshotFileName}").delete()

                    item.closingUpdate = text.toString()

                    // Update record in Backup DB
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child(AllOtherDocsActivity.PATH_NAME).child(item.pushKey!!).setValue(item)
                    }

                    // Delete DB Record
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child(AllOtherDocsActivity.PATH_NAME).child(item.pushKey!!).removeValue()
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
