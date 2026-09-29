package rahul.jagtap.dmas.user

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.text.Layout
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.SimplyPdfDocument
import com.wwdablu.soumya.simplypdf.composers.properties.TableProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.composers.properties.cell.Cell
import com.wwdablu.soumya.simplypdf.composers.properties.cell.TextCell
import com.wwdablu.soumya.simplypdf.document.DocumentInfo
import com.wwdablu.soumya.simplypdf.document.PageHeader
import com.wwdablu.soumya.simplypdf.document.PageModifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import rahul.jagtap.dmas.extensions.toast
import org.joda.time.DateTime
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.BuildConfig
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.DailyEntryListAdapter
import rahul.jagtap.dmas.databinding.ActivityDailyEntriesBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.DailyEntry
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


class DailyEntriesActivity : BaseActivity() {
    private var selectedMinDate: String? = ""
    private var selectedMaxDate: String? = ""
    private var dpd: DatePickerDialog? = null
    private var uid: String? = ""
    var list = ArrayList<DailyEntry>()
    var adapter: DailyEntryListAdapter? = null
    var pdfDownloaded = false
    private lateinit var simplyPdfDocument: SimplyPdfDocument
    private var resultFileUri: Uri? = null
    private lateinit var resultFile: File
    var canCreate = false
    var isDailyEntryBlocked = false
    lateinit var binding: ActivityDailyEntriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyEntriesBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_daily_entries)

        uid = app?.preferences?.loggedInUser?.uid
        isDailyEntryBlocked = app?.preferences?.loggedInUser?.isDailyEntryBlocked == "1"
        adapter = DailyEntryListAdapter(mContext, list)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.recyclerView?.adapter = adapter
        adapter?.itemClickListener = object : DailyEntryListAdapter.ItemClickListener {
            override fun onDeleteClick(position: Int) {
                Utils.showDialog(mContext, "Are you sure want to delete the record?", true) { dialog, which ->
                    run {
                        dialog.dismiss()
                        val selItem = list[position]
                        list?.removeAt(position) // Delete DB Record
                        selItem.uid?.let {
                            database.child(Utils.DAILY_ENTRY_TABLE).child(it).child(selItem.pushKey!!).removeValue()
                        }
                        updateDailyEntriesCountInUserInfo(list.size.toLong())
                        notifyAdapter()
                        toast("Record deleted successfully")
                    }
                }
            }

            override fun onEditClick(position: Int) {
                val selItem = list[position]
                startActivityForResult(Intent(mContext, AddDailyEntryActivity::class.java).putExtra("dailyEntry", selItem), 2)
            }
        }

        //        if (billList != null && billList.size > 0) {
        binding.btnCreateDailyEntry?.setOnClickListener {
            startActivityForResult(Intent(mContext, AddDailyEntryActivity::class.java), 2)
        } //        Firebase.database.getReference(Utils.DAILY_ENTRY_TABLE).child(uid!!).addValueEventListener(object : ValueEventListener {
        //            override fun onDataChange(dataSnapshot: DataSnapshot) {
        //                setData(dataSnapshot, uid!!)
        //            }
        //
        //            override fun onCancelled(dataSnapshot: DatabaseError) {
        //            }
        //        })
        setEntriesData(shouldCheckForBlock = false)
        createSimplyPdfDocument()
    }

    private fun setEntriesData(shouldCheckForBlock: Boolean) {
        binding.progressBar?.visible() //        Log.e("TAG", "onResponse: before", )
        app?.apiRequestHelper?.apiService?.getDailyEntriesByUid(uid)?.enqueue(object : Callback<ResponseBody> {
            //        app?.apiRequestHelper?.apiService?.dailyEntries?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) { //                Log.e("TAG", "onResponse: after")
                binding.progressBar?.gone()
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (json == null || json == "null") {
                        notifyAdapter()
                        return
                    } //                    val type: Type = object : TypeToken<HashMap<String, HashMap<String, DailyEntry>>?>() {}.type
                    //                    val hashMap: HashMap<String, HashMap<String, DailyEntry>> = Gson().fromJson(json, type)
                    //                    val map: HashMap<String, DailyEntry>? = hashMap[uid!!]
                    val type: Type = object : TypeToken<HashMap<String, DailyEntry>?>() {}.type
                    val map: HashMap<String, DailyEntry>? = Gson().fromJson(json, type)
                    val dailyEntries = map?.values
                    canCreate = if (dailyEntries.isNullOrEmpty()) true
                    else if (dailyEntries.size < 25) true
                    else !isDailyEntryBlocked
                    if (!dailyEntries.isNullOrEmpty()) {
                        list.clear()
                        list.addAll(dailyEntries)
                        list.sortWith { o1, o2 -> o2.date?.compareTo(o1.date!!)!! }
                        notifyAdapter()
                        binding.recyclerView?.visible()
                        binding.tvError?.gone()
                    } else {
                        binding.recyclerView?.gone()
                        binding.tvError?.visible()
                    }
                    updateDailyEntriesCountInUserInfo(dailyEntries?.size?.toLong() ?: 0)
                    if (shouldCheckForBlock && dailyEntries != null && dailyEntries.size == 25) {
                        blockDailyEntry()
                    }
                } else {
                    canCreate = true
                    Log.e("in", "fail response")
                    notifyAdapter()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.progressBar?.gone()
                Log.e("in", "failure")
                notifyAdapter()
            }
        })
    }

    private fun updateDailyEntriesCountInUserInfo(count: Long) {
        val loggedInUser = app?.preferences?.loggedInUser
        val localUser =
            User(uid = loggedInUser?.uid, name = loggedInUser?.name, username = loggedInUser?.username, shopName = loggedInUser?.shopName,
                isAdmin = loggedInUser?.isAdmin, email = loggedInUser?.email, contactNo = loggedInUser?.contactNo,
                address = loggedInUser?.address, referrer = loggedInUser?.referrer, createdAt = loggedInUser?.createdAt,
                aadharNo = loggedInUser?.aadharNo, userType = loggedInUser?.userType, isBlocked = loggedInUser?.isBlocked,
                isDailyEntryBlocked = loggedInUser?.isDailyEntryBlocked, fcmToken = loggedInUser?.fcmToken, dailyEntriesCount = count)
        loggedInUser?.uid?.let { database.child(Utils.USERS_TABLE).child(it).setValue(localUser) }
    }

    private fun blockDailyEntry() {
        val user = app?.preferences?.loggedInUser
        user?.isDailyEntryBlocked = "1"
        user?.uid?.let { database.child(Utils.USERS_TABLE).child(it).setValue(user) }
    }

    //    private fun setData(dataSnapshot: DataSnapshot, uid: String) {
    //        progressBar?.visible()
    //        for (child in dataSnapshot.children) {
    //            val dailyEntry = child.getValue(DailyEntry::class.java)
    //            dailyEntry?.let { list.add(it) }
    //            notifyAdapter()
    //        }
    //        progressBar?.gone()
    ////        notifyAdapter()
    //    }

    private fun notifyAdapter() {
        if (list != null && list.size > 0) {
            adapter?.notifyDataSetChanged()
            var bankTotal = 0
            var cashTotal = 0
            var creditTotal = 0
            var debitTotal = 0
            var total = 0
            list.forEach {
                if (it.debit_credit == "Credit") {
                    creditTotal += it.amount?.toInt()!!
                    total += it.amount?.toInt()!!
                }
                if (it.debit_credit == "Debit") {
                    debitTotal += it.amount?.toInt()!!
                    total -= it.amount?.toInt()!!
                }
                if (it.cash_bank == "Cash") {
                    if (it.debit_credit == "Credit") cashTotal += it.amount?.toInt()!!
                    if (it.debit_credit == "Debit") cashTotal -= it.amount?.toInt()!!
                }
                if (it.cash_bank == "Bank") {
                    if (it.debit_credit == "Credit") bankTotal += it.amount?.toInt()!!
                    if (it.debit_credit == "Debit") bankTotal -= it.amount?.toInt()!!
                }
            }
            binding.tvCredit?.text = "Credit: \u20B9 $creditTotal"
            binding.tvDebit?.text = "Debit: \u20B9 $debitTotal"
            binding.tvCash?.text = "Cash: \u20B9 $cashTotal"
            binding.tvBank?.text = "Bank: \u20B9 $bankTotal"
            if (total < 0) binding.tvTotal?.text = "Total Balance: \u20B9 0"
            else binding.tvTotal?.text = "Total Balance: \u20B9 $total"
            binding.llBottom?.visible()
            binding.recyclerView?.visible()
            binding.tvError?.gone()
            binding.btnCreateDailyEntry?.gone()
        } else {
            binding.tvCredit?.text = "Credit: \u20B9 0"
            binding.tvDebit?.text = "Debit: \u20B9 0"
            binding.tvTotal?.text = "Total Balance: \u20B9 0"
            binding.recyclerView?.gone()
            binding.llBottom?.gone()
            binding.tvError?.visible()
            binding.btnCreateDailyEntry?.visible()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_daily_entries, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_create -> {
                if (canCreate) startActivityForResult(Intent(mContext, AddDailyEntryActivity::class.java), 2)
                else toast("वार्षिक सबस्क्रिप्शन घेण्यासाठी एडमिन शी संपर्क करा")
                return true
            }

            R.id.action_download -> {
                downloadPdf(false)
                return true
            }

            R.id.action_download_datewise -> {
                val calendar = Calendar.getInstance()
                dpd = DatePickerDialog.newInstance({ view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int ->
                    selectedMinDate = year.toString() + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth)
                    selectedMaxDate = yearEnd.toString() + "-" + String.format("%02d", monthOfYearEnd + 1) + "-" + String.format("%02d", dayOfMonthEnd) //                    strStartDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    //                    strEndDate = dayOfMonthEnd + "/" + (monthOfYearEnd + 1) + "/" + yearEnd;
                    downloadPdf(true)
                }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH], calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
                dpd?.isThemeDark = false
                dpd?.show(fragmentManager, "DateRangePickerDialog")
                return true
            }

            R.id.action_send_to_admin -> {
                startActivity(Intent(mContext, AddDailyEntryActivity::class.java))
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

    //    fun export() {
    //        //generate data
    //        val data = StringBuilder()
    //        data.append("Time,Distance")
    //        for (i in 0..4) {
    //            data.append("$i,${i * i}".trimIndent())
    //        }
    //        try {
    //            //saving the file into device
    //            val out: FileOutputStream = openFileOutput("data.csv", Context.MODE_PRIVATE)
    //            out.write(data.toString().toByteArray())
    //            out.close()
    //
    //            //exporting
    //            val context: Context = applicationContext
    //            val filelocation = File(filesDir, "data.csv")
    //            val path: Uri = FileProvider.getUriForFile(context, "rahul.jagtap.dmas.fileprovider", filelocation)
    //            val fileIntent = Intent(Intent.ACTION_SEND)
    //            fileIntent.type = "text/csv"
    //            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data")
    //            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    //            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
    //            startActivity(Intent.createChooser(fileIntent, "Send mail"))
    //        } catch (e: Exception) {
    //            e.printStackTrace()
    //        }
    //    }

    private fun downloadPdf(isDateRange: Boolean) {
        val dailyEntryList = ArrayList<DailyEntry>()
        list.forEachIndexed { _, dailyEntry ->
            if (isDateRange) {
                if (isDateBetweenTwoDates(selectedMinDate.toString(), selectedMaxDate.toString(), dailyEntry.date.toString())) {
                    Log.e("isDateBetweenTwoDates", "true || $selectedMinDate || $selectedMaxDate || ${dailyEntry.date}")
                    dailyEntryList.add(dailyEntry)
                } else Log.e("isDateBetweenTwoDates", "false || $selectedMinDate || $selectedMaxDate || ${dailyEntry.date}")
            } else {
                dailyEntryList.add(dailyEntry)
            }
        }
        if (dailyEntryList.size == 0) {
            if (isDateRange) toast("No records not available for selected date range")
            else toast("No records found")
            return
        }
        if (pdfDownloaded) {
            createSimplyPdfDocument()
            pdfDownloaded = false
        }
        launchCoroutine({
            val properties = TableProperties().apply {
                borderColor = "#000000"
                borderWidth = 1
            }

            val rows = LinkedList<LinkedList<Cell>>()

            /*
            * This will add a table with 12 equal width column and N rows
            */
            val dividedBy = 6
            val widthToSet = simplyPdfDocument.usablePageWidth / dividedBy //            val valueColumnWidth = widthToSet + widthToSet + widthToSet
            LinkedList<Cell>().apply {
                add(TextCell("Sr No", TextProperties().apply { textSize = 5 }, (widthToSet).toInt()))
                add(TextCell("Date", TextProperties().apply { textSize = 5 }, widthToSet))
                add(TextCell("Details", TextProperties().apply { textSize = 5 }, widthToSet))
                add(TextCell("Cash/Bank", TextProperties().apply { textSize = 5 }, widthToSet))
                add(TextCell("Debit/Credit", TextProperties().apply { textSize = 5 }, widthToSet))
                add(TextCell("Amount", TextProperties().apply { textSize = 5 }, widthToSet))

                rows.add(this)
            }
            dailyEntryList.forEachIndexed { index, dailyEntry ->
                LinkedList<Cell>().apply {
                    add(TextCell((index + 1).toString(), TextProperties().apply { textSize = 5 }, (widthToSet).toInt()))
                    add(TextCell(dailyEntry.date.toString(), TextProperties().apply { textSize = 5 }, widthToSet))
                    add(TextCell(dailyEntry.details.toString(), TextProperties().apply { textSize = 5 }, widthToSet))
                    add(TextCell(dailyEntry.cash_bank.toString(), TextProperties().apply { textSize = 5 }, widthToSet))
                    add(TextCell(dailyEntry.debit_credit.toString(), TextProperties().apply { textSize = 5 }, widthToSet))
                    add(TextCell(dailyEntry.amount.toString(), TextProperties().apply { textSize = 5 }, widthToSet))

                    rows.add(this)
                }
            }
            LinkedList<Cell>().apply {
                add(TextCell(binding.tvDebit.text.toString(), TextProperties().apply {
                    textSize = 7
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, simplyPdfDocument.usablePageWidth / 4))
                add(TextCell(binding.tvCredit.text.toString(), TextProperties().apply {
                    textSize = 7
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, simplyPdfDocument.usablePageWidth / 4))
                add(TextCell(binding.tvBank.text.toString(), TextProperties().apply {
                    textSize = 7
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, simplyPdfDocument.usablePageWidth / 4))
                add(TextCell(binding.tvCash.text.toString(), TextProperties().apply {
                    textSize = 7
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, simplyPdfDocument.usablePageWidth / 4))

                rows.add(this)
            }
            val lastRowWidthToSet = simplyPdfDocument.usablePageWidth / 4 + simplyPdfDocument.usablePageWidth / 4 + simplyPdfDocument.usablePageWidth / 4 + simplyPdfDocument.usablePageWidth / 4
            LinkedList<Cell>().apply {
                add(TextCell(binding.tvTotal.text.toString(), TextProperties().apply {
                    textSize = 10
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, lastRowWidthToSet))

                rows.add(this)
            }

            toast("Downloading...")
            simplyPdfDocument.table.draw(rows, properties)

            simplyPdfDocument.finish()

            withContext(Dispatchers.Main) {
                pdfDownloaded = true
                Toast.makeText(baseContext, "PDF downloaded successfully", Toast.LENGTH_SHORT).show()
                Log.e("file uri", resultFileUri.toString())
                try { // Perform operations on the document using its URI.
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(baseContext, "${BuildConfig.APPLICATION_ID}.fileprovider", resultFile))
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
        }, { coroutineContext, throwable ->
            throwable.printStackTrace()
        })
    }

    protected fun createSimplyPdfDocument() {
        val file = File(Environment.getExternalStorageDirectory(), "Download")
        if (!file.exists()) {
            file.mkdirs()
        }
        resultFile = File(file.absolutePath + File.separator.toString() + "Daily_Entries_${System.currentTimeMillis()}.pdf") //            simplyPdfDocument = SimplyPdf.with(baseContext, resultFile).colorMode(DocumentInfo.ColorMode.COLOR).paperSize(PrintAttributes.MediaSize.ISO_A4) //                .margin(Margin(20U, 20U, 20U, 20U)) //                .pageModifier(PageHeader(headerList)).firstPageBackgroundColor(Color.WHITE).paperOrientation(DocumentInfo.Orientation.PORTRAIT).build()
        resultFileUri = Uri.fromFile(resultFile)
        simplyPdfDocument = SimplyPdf.with(baseContext, resultFile).colorMode(DocumentInfo.ColorMode.COLOR).paperSize(PrintAttributes.MediaSize.ISO_A4) //            .margin(Margin(15U, 15U, 15U, 15U))
            .paperOrientation(DocumentInfo.Orientation.PORTRAIT).pageModifier(PageHeader(LinkedList<Cell>().apply {
                add(TextCell("Daily Entries", TextProperties().apply {
                    textSize = 24
                    alignment = Layout.Alignment.ALIGN_CENTER
                    textColor = "#000000"
                }, Cell.MATCH_PARENT)) //                add(TextCell("$strSubTitle", TextProperties().apply {
                //                    textSize = 20
                //                    alignment = Layout.Alignment.ALIGN_CENTER
                //                    textColor = "#000000"
                //                }, Cell.MATCH_PARENT))
                //                add(TextCell("$strDescription", TextProperties().apply {
                //                    textSize = 17
                //                    alignment = Layout.Alignment.ALIGN_CENTER
                //                    textColor = "#000000"
                //                }, Cell.MATCH_PARENT))
            })).pageModifier(HeaderLinePageModifier()).build()
    }

    protected inner class HeaderLinePageModifier : PageModifier() {
        override fun render(simplyPdfDocument: SimplyPdfDocument) {
            simplyPdfDocument.apply {
                val rect = RectF(startMargin.toFloat(), pageContentHeight.toFloat(), usablePageWidth.toFloat() + endMargin, (pageContentHeight + 1).toFloat())

                currentPage.canvas.drawRect(rect, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.BLACK
                })

                addContentHeight(rect.height().toInt())
            }
            simplyPdfDocument.insertEmptySpace(25)
        }
    }

    public fun isDateBetweenTwoDates(date1: String, date2: String, dateToCheck: String): Boolean {
        try { // Create a SimpleDateFormat reference with different formats
            val sdf = SimpleDateFormat("yyyy-MM-dd")

            //Create the Lower and Upper Bound Date object
            val startDate: Date? = sdf.parse(date1)
            val endDate: Date? = sdf.parse(date2)

            //Create the date object to check
            val dateToValidate: Date? = sdf.parse(dateToCheck)

            // Create Joda Datetime instance using Date objects
            val dateTime1 = DateTime(startDate)
            val dateTime2 = DateTime(endDate)
            val dateTime3 = DateTime(dateToValidate)

            // compare datetime3 with datetime1 and datetime2 using the methods
            if (dateTime3.isAfter(dateTime1) && dateTime3.isBefore(dateTime2)) { //            System.out.println("""${"The date " + sdf.format(dateToValidate)} lies between the two dates """)
                return true
            } else { //            println(sdf.format(dateToValidate) + " does not lie between the two dates\n")
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            setEntriesData(shouldCheckForBlock = true)
        }
    }

    //    private fun writeCsvFile() {
    //        val csv = "data.csv"
    //        val writer = CSVWriter(FileWriter(csv))
    //
    //        //Create record
    //        val record = "4,David,Miller,Australia,30".split(",".toRegex()).toTypedArray()
    //
    //        //Write the record to file
    //        writer.writeNext(record)
    //
    //        //close the writer
    //        writer.close()
    //    }

    companion object {

    }
}
