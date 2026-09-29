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
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.BuildConfig
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.PassportListAdapter
import rahul.jagtap.dmas.admin.TextMsgActivity
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.Passport
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Type
import java.util.*


class PassportListActivity : BaseActivity() {
    private var creator: User? = null
    var list = ArrayList<Passport>()
    private var resultFileUri: Uri? = null
    private lateinit var resultFile: File
    private var workbook: Workbook? = null
    private var sheet: Sheet? = null
    private var cell: Cell? = null
    var paymentStatusChanged = false
    var adapter: PassportListAdapter? = null
    private lateinit var binding: ActivityViewBillsBinding
    var selectedCustomerName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_passport)

        val creatorJsonString = intent.getStringExtra("creatorJsonString")
        creator = Gson().fromJson<User>(creatorJsonString, User::class.java)
        val json = intent.getStringExtra("data")
        val type: Type = object : TypeToken<MutableList<Passport>>() {}.type
        val passportList: MutableList<Passport> = Gson().fromJson(json, type)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = PassportListAdapter(mContext, list)
        adapter?.itemClickListener = object : PassportListAdapter.ItemClickListener {
            override fun onDeleteClick(position: Int) {
                val selPanCard = list[position]

                val closingUpdateList = Utils.closingUpdateList
                MaterialDialog.Builder(mContext!!).items(closingUpdateList).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                    run {
                        dialog?.dismiss()
                        performDelete(selPanCard, closingUpdateList[position])
                    }
                }.show()
            }

            override fun onDownloadAll(position: Int, isCreateFolderClicked: Boolean) {
                val item = list[position]
                selectedCustomerName = item?.applicantName.toString()
//                if (!TextUtils.isEmpty(item.oldPanCardDownloadUrl)) item?.oldPanCardDownloadUrl?.let { it1 -> downloadTask(it1, item?.oldPanCardFileName.toString()) }
//                item?.passportPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.passportPhotoFileName.toString()) }
                item?.aadharFrontPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.aadharFrontPhotoFileName.toString(), isCreateFolderClicked) }
                item?.aadharBackPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.aadharBackPhotoFileName.toString(), isCreateFolderClicked) }
                item?.panDownloadUrl?.let { it1 -> downloadTask(it1, item?.panFileName.toString(), isCreateFolderClicked) }
//                item?.signDownloadUrl?.let { it1 -> downloadTask(it1, item?.signFileName.toString()) }
                if (!TextUtils.isEmpty(item.oldPassportPhotoDownloadUrl)) item?.oldPassportPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.oldPassportPhotoFileName.toString()) }
                if (!TextUtils.isEmpty(item.oldPassportFileNoPhotoDownloadUrl)) item?.oldPassportFileNoPhotoDownloadUrl?.let { it1 -> downloadTask(it1, item?.oldPassportFileNoPhotoFileName.toString()) }
                item?.paymentScreenshotDownloadUrl?.let { it1 -> downloadTask(it1, item?.paymentScreenshotFileName.toString(), isCreateFolderClicked) }
                // Take and download screenshot
                val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(position)
                viewHolder?.itemView?.let { Utils.downloadScreenshot(it, window, isCreateFolderClicked, "Download/$selectedCustomerName Passport(New/Correction)") }
                toast("Downloading...")
            }

            override fun onMarkAsUnPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "2"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("passports").child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("passports").child(item.pushKey!!).setValue(item)
                }
            }

            override fun onMarkAsPaid(position: Int) {
                paymentStatusChanged = true
                val item = list[position]
                item.paymentStatus = "1"
                adapter?.notifyItemChanged(position)
                Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                    database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("passports").child(item.pushKey!!).setValue(item)
                    database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("passports").child(item.pushKey!!).setValue(item)
                }
            }

            override fun onCallOrSms(phoneNo: String) {
                callOrSms(phoneNo)
            }

            override fun onCallOrSmsCreator() {
                creator?.contactNo?.let { callOrSms(it) }
            }

            override fun onExcelDownload(passport: Passport) {
                downloadExelFile(passport)
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
        }
    }

    private fun performDelete(selPanCard: Passport, closing_update: String) {
        //                // Delete Old Pan Card Photo file
        //                if (!TextUtils.isEmpty(selPanCard.oldPanCardFileName)) storageRef.child("esuvidha/${selPanCard.email}/pan_cards/${selPanCard.oldPanCardFileName}").delete()
        //
        //                // Delete Passport file
        //                storageRef.child("esuvidha/${selPanCard.email}/pan_cards/${selPanCard.passportPhotoFileName}").delete()
        //
        // Delete Aadhar Front file
        storageRef.child("esuvidha/${selPanCard.email}/passports/${selPanCard.aadharFrontPhotoFileName}").delete()
        //
        // Delete Aadhar Back file
        storageRef.child("esuvidha/${selPanCard.email}/passports/${selPanCard.aadharBackPhotoFileName}").delete()
        //
        // Delete Pan file
        storageRef.child("esuvidha/${selPanCard.email}/passports/${selPanCard.panFileName}").delete()
        //
        //                // Delete Sign file
        //                storageRef.child("esuvidha/${selPanCard.email}/pan_cards/${selPanCard.signFileName}").delete()

        if (!TextUtils.isEmpty(selPanCard.oldPassportPhotoFileName)) storageRef.child("esuvidha/${selPanCard.email}/pan_cards/${selPanCard.oldPassportPhotoFileName}").delete()
        if (!TextUtils.isEmpty(selPanCard.oldPassportFileNoPhotoFileName)) storageRef.child("esuvidha/${selPanCard.email}/pan_cards/${selPanCard.oldPassportFileNoPhotoFileName}").delete()

        // Delete Payment SC file
        storageRef.child("esuvidha/${selPanCard.email}/passports/${selPanCard.paymentScreenshotFileName}").delete()

        selPanCard.closingUpdate = closing_update
        // Update record in Backup DB
        Utils.dmyHmsTodmy(selPanCard.createdDateTime)?.let { formattedDate ->
            database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(selPanCard.uid!!).child("passports").child(selPanCard.pushKey!!).setValue(selPanCard)
        }

        // Delete DB Record
        Utils.dmyHmsTodmy(selPanCard.createdDateTime)?.let { formattedDate ->
            database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(selPanCard.uid!!).child("passports").child(selPanCard.pushKey!!).removeValue()
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
                File(Environment.getExternalStorageDirectory(), "Download/$selectedCustomerName Passport(New/Correction)")
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

    private fun downloadExelFile(passport: Passport) {
        toast("Downloading...")
        createExcelWorkbook(passport)
        val isExcelGenerated = storeExcelInStorage()
        if (isExcelGenerated) {
            toast("Excel file downloaded successfully")
            try {
                val uriForFile = FileProvider.getUriForFile(baseContext, "${BuildConfig.APPLICATION_ID}.fileprovider", resultFile)
                val intent = Intent(Intent.ACTION_VIEW, uriForFile)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(uriForFile, "application/vnd.ms-excel")
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Method: Generate Excel Workbook
     */
    fun createExcelWorkbook(passport: Passport) { // New Workbook
        workbook = HSSFWorkbook()
        cell = null

        // Cell style for header row
        val cellStyle = workbook?.createCellStyle()
        cellStyle?.fillForegroundColor = HSSFColor.AQUA.index
        cellStyle?.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
        cellStyle?.alignment = CellStyle.ALIGN_CENTER

        // New Sheet
        sheet = null
        sheet = workbook?.createSheet("Sheet1")

        val columnList = LinkedHashMap<String, String>()
        columnList["अर्जदाराचे नाव"] = passport.applicantName.toString()
//        columnList["अर्जदाराची जन्मतारीख"] = passport.dob.toString()
        columnList["इमेल आयडी"] = passport.applicantEmail.toString()
        columnList["मोबाईल नंबर"] = passport.mobileNo.toString()
//        columnList["आधार नंबर"] = passport.aadharNo.toString()
        columnList["जन्म ठिकाण"] = passport.birthPlace.toString()
        columnList["शिक्षण"] = passport.education.toString()
        columnList["नोकरी"] = passport.employment.toString()
        columnList["वैवाहिक स्थिती"] = passport.maritalStatus.toString()
        columnList["तुमचे पोलीस स्टेशन कोणते"] = passport.address.toString()
        columnList["वडीलांचे नाव"] = passport.fatherName.toString()
        columnList["आई चे नाव"] = passport.motherName.toString()
        columnList["इमर्जन्सी नाव"] = passport.emergencyName.toString()
        columnList["इमर्जन्सी मोबाईल नंबर"] = passport.emergencyMobileNo.toString()
        columnList["पती/पत्नीचे नाव"] = passport.husbandWifeName.toString()
        columnList["यांनी जोडले"] = passport.createdBy.toString()
        columnList["जोडलेली तारीख"] = passport.createdDateTime.toString()

        val row = sheet?.createRow(0)
        val rowData = sheet?.createRow(1)
        columnList.keys.forEachIndexed { index, s ->
            // Generate column headings
            cell = row?.createCell(index)
            cell?.setCellValue(s)
            cell?.cellStyle = cellStyle
            // Create Cells for each row
            cell = rowData?.createCell(index)
            cell?.setCellValue(columnList[s])
        }
    }

    private fun storeExcelInStorage(): Boolean {
        var isSuccess: Boolean
        val fileDirectory = File(Environment.getExternalStorageDirectory(), "Download")
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs()
        }
        resultFile = File(fileDirectory.absolutePath + File.separator.toString() + "passport_${System.currentTimeMillis()}.xls") //            simplyPdfDocument = SimplyPdf.with(baseContext, resultFile).colorMode(DocumentInfo.ColorMode.COLOR).paperSize(PrintAttributes.MediaSize.ISO_A4) //                .margin(Margin(20U, 20U, 20U, 20U)) //                .pageModifier(PageHeader(headerList)).firstPageBackgroundColor(Color.WHITE).paperOrientation(DocumentInfo.Orientation.PORTRAIT).build()
        resultFileUri = Uri.fromFile(resultFile)
        //        val file = File(this.getExternalFilesDir(null), fileName)
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(resultFile)
            workbook?.write(fileOutputStream)
            Log.e("storeExcel", "Writing file $resultFile")
            isSuccess = true
        } catch (e: IOException) {
            Log.e("storeExcel", "Error writing Exception: ", e)
            isSuccess = false
        } catch (e: Exception) {
            Log.e("storeExcel", "Failed to save file due to Exception: ", e)
            isSuccess = false
        } finally {
            try {
                fileOutputStream?.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return isSuccess
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
                    // Delete Aadhar Front file
                    storageRef.child("esuvidha/${item.email}/passports/${item.aadharFrontPhotoFileName}").delete()
                    //
                    // Delete Aadhar Back file
                    storageRef.child("esuvidha/${item.email}/passports/${item.aadharBackPhotoFileName}").delete()
                    //
                    // Delete Pan file
                    storageRef.child("esuvidha/${item.email}/passports/${item.panFileName}").delete()
                    //
                    //                // Delete Sign file
                    //                storageRef.child("esuvidha/${item.email}/pan_cards/${item.signFileName}").delete()

                    // Delete Payment SC file
                    storageRef.child("esuvidha/${item.email}/passports/${item.paymentScreenshotFileName}").delete()

                    item.closingUpdate = text.toString()
                    // Update record in Backup DB
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child("${Utils.ESUVIDHA_TABLE}_backup").child(formattedDate).child(item.uid!!).child("passports").child(item.pushKey!!).setValue(item)
                    }

                    // Delete DB Record
                    Utils.dmyHmsTodmy(item.createdDateTime)?.let { formattedDate ->
                        database.child(Utils.ESUVIDHA_TABLE).child(formattedDate).child(item.uid!!).child("passports").child(item.pushKey!!).removeValue()
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
