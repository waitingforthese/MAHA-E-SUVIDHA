package rahul.jagtap.dmas.user

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.text.Layout
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import rahul.jagtap.dmas.widget.searchablemultiselectspinner.SearchableItem
import rahul.jagtap.dmas.widget.searchablemultiselectspinner.SearchableMultiSelectSpinner
import rahul.jagtap.dmas.widget.searchablemultiselectspinner.SelectionCompleteListener
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
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
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.BuildConfig
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.DayBookTableAdapter
import rahul.jagtap.dmas.databinding.ActivityDayBookBinding
import rahul.jagtap.dmas.extensions.addIfNotExists
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.CustomDayBookTableItem
import rahul.jagtap.dmas.model.DailyEntry
import rahul.jagtap.dmas.model.DayBook
import rahul.jagtap.dmas.model.DayBookTableItem
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


open class DayBookActivity : BaseActivity() {
    private lateinit var simplyPdf: SimplyPdf
    private var tempFile: File? = null
    private var resultFileUri: Uri? = null
    private lateinit var resultFile: File
    private lateinit var simplyPdfDocument: SimplyPdfDocument
    private lateinit var records: ArrayList<DayBookTableItem>
    private lateinit var filteredRecords: ArrayList<DayBookTableItem>
    private var strTitle: String? = ""

    var list = ArrayList<DailyEntry>()
    var selectedFromDate = ""
    var selectedToDate = ""
    var pdfDownloaded = false
    var hideSrNo = true
    var hideDate = false
    var hideLedger1 = false
    var hideLedger2 = false
    var hideDebit = false
    var hideCredit = false
    var hideVouchType = true
    var hideVouchNo = true

    var hideName = true
    var hideQuantity = true
    var hideLiterPrice = true
    var hideCgst = true
    var hideSgst = true
    var hideIgst = true
    var hideTaxable = true
    var isDateRangeNotApplied = true

    var adapter: DayBookTableAdapter? = null

    var ledger1ToFilterValue1 = ""
    var ledger1ToFilterValue2 = ""
    var ledger2ToFilterValue1 = ""
    var ledger2ToFilterValue2 = ""
    var selectedVouchType = ""
    var userUid = ""
    var strFromDate = ""
    var strToDate = ""
    var shouldUserCustomPdf = true
    lateinit var binding: ActivityDayBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDayBookBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_day_book)
        records = ArrayList()
        filteredRecords = ArrayList()

        userUid = intent.getStringExtra("userUid") ?: ""
        binding.tvOpenLedgerPopup?.setOnClickListener {
            showLedger2Popup()
            showLedger1Popup()
        }
        binding.tvOpenPdfPopup?.setOnClickListener {
            handlePdfClickAction()
        }
        fetchDayBookData()
    }

    private fun fetchDayBookData() {
        if (userUid.isEmpty()) return
        binding.progressBar?.visible()
        app?.apiRequestHelper?.apiService?.getDayBookDataByUid(userUid)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>, response: Response<ResponseBody>
            ) { //                Log.e("TAG", "onResponse: after")
                binding.progressBar?.gone()
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (json == null || json == "null") {
                        return
                    }

                    val dayBook = if (!TextUtils.isEmpty(json)) Gson().fromJson(json, DayBook::class.java) else null
                    setFetchedDayBookData(dayBook)
                } else {
                    Log.e("in", "fail response")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.progressBar?.gone()
            }
        })
    }

    private fun setFetchedDayBookData(dayBook: DayBook?) {
        val showDayRangePopup = intent.getBooleanExtra("showDayRangePopup", false)
        val showLedger1Popup = intent.getBooleanExtra("showLedger1Popup", false)
        val showLedger2Popup = intent.getBooleanExtra("showLedger2Popup", false)
        val showStockItemNamePopup = intent.getBooleanExtra("showStockItemNamePopup", false)
        ledger1ToFilterValue1 = intent.getStringExtra("ledger1ToFilterValue1") ?: ""
        ledger1ToFilterValue2 = intent.getStringExtra("ledger1ToFilterValue2") ?: ""
        ledger2ToFilterValue1 = intent.getStringExtra("ledger2ToFilterValue1") ?: ""
        ledger2ToFilterValue2 = intent.getStringExtra("ledger2ToFilterValue2") ?: ""
        if (!TextUtils.isEmpty(dayBook?.json)) {
            val json = dayBook?.json
            val type: Type = object : TypeToken<ArrayList<DayBookTableItem>?>() {}.type
            val dayBookTableList = Gson().fromJson<ArrayList<DayBookTableItem>>(json, type) //            val dayBookTable = Gson().fromJson(json, DayBookTable::class.java)
            if (dayBookTableList != null && dayBookTableList.size > 0) {
                strTitle = dayBook?.title
                strFromDate = dayBook?.fromDate.toString()
                strToDate = dayBook?.toDate.toString()
                binding.tvTitle?.text = title
                binding.tvSubTitle?.gone()
                binding.tvDescription?.gone()
                if (dayBookTableList != null && dayBookTableList.isNotEmpty()) {
                    createSimplyPdfDocument()
                    filteredRecords = dayBookTableList
                    records.addAll(dayBookTableList)
                    if (showStockItemNamePopup) hideName = false
                    adapter = DayBookTableAdapter(mContext, filteredRecords)
                    adapter?.itemClickListener = clickListener
                    binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                    binding.recyclerView?.adapter = adapter
                    setAllTotals()
                    binding.recyclerView?.visible()
                    binding.tvError?.gone()

                    // Add all the optional logic here
                    if (showDayRangePopup) {
                        showDateRangeFilterDialog()
                    }
                    if (!TextUtils.isEmpty(ledger1ToFilterValue1) || !TextUtils.isEmpty(ledger1ToFilterValue2) || !TextUtils.isEmpty(ledger2ToFilterValue1) || !TextUtils.isEmpty(ledger2ToFilterValue2)) {
                        val recordsToAdd = records.filter { dayBookTableItem ->
                            if (dayBookTableItem.ledger1.equals(ledger1ToFilterValue1.toLowerCase(), true)) {
                                ledger1SearchList.add(SearchableItem(dayBookTableItem.ledger1.toString(), dayBookTableItem.ledger1.toString()))
                                dayBookTableItem.isLedger1Checked = true
                            }
                            if (dayBookTableItem.ledger1.equals(ledger1ToFilterValue2.toLowerCase(), true)) {
                                ledger1SearchList.add(SearchableItem(dayBookTableItem.ledger1.toString(), dayBookTableItem.ledger1.toString()))
                                dayBookTableItem.isLedger1Checked = true
                            }
                            if (dayBookTableItem.ledger2.equals(ledger2ToFilterValue1.toLowerCase(), true)) {
                                ledger2SearchList.add(SearchableItem(dayBookTableItem.ledger2.toString(), dayBookTableItem.ledger2.toString()))
                                dayBookTableItem.isLedger2Checked = true
                            }
                            if (dayBookTableItem.ledger2.equals(ledger2ToFilterValue2.toLowerCase(), true)) {
                                ledger2SearchList.add(SearchableItem(dayBookTableItem.ledger2.toString(), dayBookTableItem.ledger2.toString()))
                                dayBookTableItem.isLedger2Checked = true
                            }
                            dayBookTableItem.ledger1.equals(ledger1ToFilterValue1.toLowerCase(), true) || dayBookTableItem.ledger1.equals(ledger1ToFilterValue2.toLowerCase(), true) || dayBookTableItem.ledger2.equals(ledger2ToFilterValue1.toLowerCase(), true) || dayBookTableItem.ledger2.equals(ledger2ToFilterValue2.toLowerCase(), true)
                        }.toMutableList()

                        filteredRecords.clear()
                        filteredRecords.addAll(recordsToAdd)
                        setFilteredData()
                    }
                    if (showLedger2Popup) {
                        showLedger2Popup()
                    }
                    if (showLedger1Popup) {
                        showLedger1Popup()
                    }
                    if (showStockItemNamePopup) {
                        showStockItemNamePopup()
                    }
                } else {
                    binding.recyclerView?.gone()
                    binding.tvError?.visible()
                }
            }
        }
    }

    private fun setAllTotals() {
        if (filteredRecords != null && filteredRecords.size > 0) {
            var creditTotal = 0.0
            var debitTotal = 0.0
            var stockQtyTotal = 0L
            var total = 0.0
            filteredRecords.forEachIndexed { index, dayBookTableItem ->
                if (shouldUserCustomPdf) {
                    if (ledger1SearchList.firstOrNull { searchableItem -> searchableItem.code == dayBookTableItem.ledger1 } != null) creditTotal += 0
                    else debitTotal += dayBookTableItem.credit?.toDouble()!!
                    if (ledger2SearchList.firstOrNull { searchableItem -> searchableItem.code == dayBookTableItem.ledger2 } != null) debitTotal += 0
                    else creditTotal += dayBookTableItem.debit?.toDouble()!!
                    if (!TextUtils.isEmpty(dayBookTableItem.stockItemQuantity)) stockQtyTotal += dayBookTableItem.stockItemQuantity?.toLong()!!
                } else {
                    if (ledger1SearchList.firstOrNull { searchableItem -> searchableItem.code == dayBookTableItem.ledger1 } != null) creditTotal += 0
                    else creditTotal += dayBookTableItem.credit?.toDouble()!!
                    if (ledger2SearchList.firstOrNull { searchableItem -> searchableItem.code == dayBookTableItem.ledger2 } != null) debitTotal += 0
                    else debitTotal += dayBookTableItem.debit?.toDouble()!!
                    if (!TextUtils.isEmpty(dayBookTableItem.stockItemQuantity)) stockQtyTotal += dayBookTableItem.stockItemQuantity?.toLong()!!
                }
            }
            total = if (debitTotal > creditTotal) debitTotal - creditTotal
            else creditTotal - debitTotal
            binding.tvCreditTotal?.text = "Credit: \u20B9 ${Utils.formatAmount(creditTotal)}"
            binding.tvDebitTotal?.text = "Debit: \u20B9 ${Utils.formatAmount(debitTotal)}"
            binding.tvStockQuantityTotal?.text = "Stock Qty: $stockQtyTotal"
            if (total < 0) binding.tvTotal?.text = "Debit Balance: \u20B9 0"
            else {
                if (debitTotal > creditTotal) binding.tvTotal?.text = "Debit Balance: \u20B9 ${Utils.formatAmount(total)}"
                else binding.tvTotal?.text = "Credit Balance: \u20B9 ${Utils.formatAmount(total)}"
            }
            binding.llBottom?.visible()
        } else {
            binding.tvCreditTotal?.text = "Credit: \u20B9 0"
            binding.tvDebitTotal?.text = "Debit: \u20B9 0"
            binding.tvTotal?.text = "Debit Balance: \u20B9 0"
            binding.tvStockQuantityTotal?.text = "Stock Qty: 0"
            binding.llBottom?.gone()
        }
    }

    private fun downloadPdf() { //        lifecycleScope.launch {
        if (pdfDownloaded) {
            createSimplyPdfDocument()
            pdfDownloaded = false
        }
        launchCoroutine({
            val mostOccurredLedger = getMostOccurredLedger() ?: ""

            val properties = TableProperties().apply {
                borderColor = "#000000"
                borderWidth = 1
            }

            val rows = LinkedList<LinkedList<Cell>>()
            val headerRows = LinkedList<LinkedList<Cell>>()
            if (strFromDate.isNotEmpty() && strToDate.isNotEmpty()) {
                LinkedList<Cell>().apply {
                    add(TextCell("AS ON - $strFromDate TO $strToDate", TextProperties().apply {
                        textSize = if (shouldUserCustomPdf) 15 else 8
                        alignment = Layout.Alignment.ALIGN_CENTER
                        textColor = "#000000"
                        typeface = Typeface.DEFAULT_BOLD
                    }, simplyPdfDocument.usablePageWidth))

                    headerRows.add(this)
                }
            }
            if (mostOccurredLedger.isNotEmpty()) {
                LinkedList<Cell>().apply {
                    add(TextCell(mostOccurredLedger, TextProperties().apply {
                        textSize = if (shouldUserCustomPdf) 15 else 8
                        alignment = Layout.Alignment.ALIGN_CENTER
                        textColor = "#000000"
                        typeface = Typeface.DEFAULT_BOLD
                    }, simplyPdfDocument.usablePageWidth))

                    headerRows.add(this)
                }
            }
            LinkedList<Cell>().apply {
                add(TextCell("Dr.", TextProperties().apply {
                    textSize = if (shouldUserCustomPdf) 15 else 8
                    alignment = Layout.Alignment.ALIGN_NORMAL
                    textColor = "#000000"
                }, simplyPdfDocument.usablePageWidth / 2))
                add(TextCell("Cr.", TextProperties().apply {
                    textSize = if (shouldUserCustomPdf) 15 else 8
                    alignment = Layout.Alignment.ALIGN_OPPOSITE
                    textColor = "#000000"
                }, simplyPdfDocument.usablePageWidth / 2))

                headerRows.add(this)
            }

            simplyPdfDocument.table.draw(headerRows, TableProperties().apply {
                borderColor = "#00000000"
            })
            var bottomTextSizeToSet = 15
            if (shouldUserCustomPdf) {
                val dividedBy = 6
                val widthToSet = simplyPdfDocument.usablePageWidth / dividedBy

                LinkedList<Cell>().apply {
                    add(TextCell("Date", TextProperties().apply {
                        textSize = 15
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    add(TextCell("Particular To", TextProperties().apply {
                        textSize = 15
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    add(TextCell("Amount", TextProperties().apply {
                        textSize = 15
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    add(TextCell("Date", TextProperties().apply {
                        textSize = 15
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    add(TextCell("Particular By", TextProperties().apply {
                        textSize = 15
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    add(TextCell("Amount", TextProperties().apply {
                        textSize = 15
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))

                    rows.add(this)
                }

                val customRecords = createCustomDayBookList(filteredRecords)

                customRecords.forEachIndexed { index, customDayBookTableItem ->
                    LinkedList<Cell>().apply {
                        add(TextCell(customDayBookTableItem.date1.toString(), TextProperties().apply { textSize = 15 }, widthToSet))
                        add(TextCell(customDayBookTableItem.ledger1.toString(), TextProperties().apply { textSize = 15 }, widthToSet))
                        add(TextCell(customDayBookTableItem.credit.toString(), TextProperties().apply { textSize = 15 }, widthToSet))
                        add(TextCell(customDayBookTableItem.date2.toString(), TextProperties().apply { textSize = 15 }, widthToSet))
                        add(TextCell(customDayBookTableItem.ledger2.toString(), TextProperties().apply { textSize = 15 }, widthToSet))
                        add(TextCell(customDayBookTableItem.debit.toString(), TextProperties().apply { textSize = 15 }, widthToSet))

                        rows.add(this)
                    }
                }
            } else {/*
            * This will add a table with 12 equal width column and N rows
            */
                var dividedBy = 0
                bottomTextSizeToSet = 8
                if (binding.tvSL.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvDate.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvLedger1.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvLedger2.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvDebit.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvCredit.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvVouchType.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvVoucherNo.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvStockItemName.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvStockItemQuantity.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvStockItemRate.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvTaxable.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvCgst.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvSgst.visibility == View.VISIBLE) dividedBy += 1
                if (binding.tvIgst.visibility == View.VISIBLE) dividedBy += 1
                val widthToSet = simplyPdfDocument.usablePageWidth / dividedBy

                LinkedList<Cell>().apply {
                    if (binding.tvSL.visibility == View.VISIBLE) add(TextCell("Sr No", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvDate.visibility == View.VISIBLE) add(TextCell("Date", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvLedger1.visibility == View.VISIBLE) add(TextCell("PERTICULAR-C", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvCredit.visibility == View.VISIBLE) add(TextCell("AMT-C", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvLedger2.visibility == View.VISIBLE) add(TextCell("PERTICULAR-D", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvDebit.visibility == View.VISIBLE) add(TextCell("AMT-D", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvVouchType.visibility == View.VISIBLE) add(TextCell("Voucher Type", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvVoucherNo.visibility == View.VISIBLE) add(TextCell("Voucher No", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvStockItemName.visibility == View.VISIBLE) add(TextCell("Stock Item Name", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvStockItemQuantity.visibility == View.VISIBLE) add(TextCell("Stock Item Quantity", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvStockItemRate.visibility == View.VISIBLE) add(TextCell("Stock Item Rate", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvTaxable.visibility == View.VISIBLE) add(TextCell("TAXABLE", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvCgst.visibility == View.VISIBLE) add(TextCell("CGST", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvSgst.visibility == View.VISIBLE) add(TextCell("SGST", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))
                    if (binding.tvIgst.visibility == View.VISIBLE) add(TextCell("IGST", TextProperties().apply {
                        textSize = 6
                        typeface = Typeface.DEFAULT_BOLD
                    }, widthToSet))

                    rows.add(this)
                }
                filteredRecords.forEachIndexed { index, dayBookTableItem ->
                    LinkedList<Cell>().apply {
                        if (binding.tvSL.visibility == View.VISIBLE) add(TextCell((index + 1).toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvDate.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.date.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvLedger1.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.ledger1.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvCredit.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.credit.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvLedger2.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.ledger2.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvDebit.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.debit.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvVouchType.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.voucherType.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvVoucherNo.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.voucherNo.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvStockItemName.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.stockItemName.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvStockItemQuantity.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.stockItemQuantity.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvStockItemRate.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.stockItemRate.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvTaxable.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.taxable.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvCgst.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.cgst.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvSgst.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.sgst.toString(), TextProperties().apply { textSize = 6 }, widthToSet))
                        if (binding.tvIgst.visibility == View.VISIBLE) add(TextCell(dayBookTableItem.igst.toString(), TextProperties().apply { textSize = 6 }, widthToSet))

                        rows.add(this)
                    }
                }
            }

            LinkedList<Cell>().apply {
                add(TextCell(binding.tvDebitTotal.text.toString(), TextProperties().apply {
                    textSize = bottomTextSizeToSet
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, simplyPdfDocument.usablePageWidth / 3))
                add(TextCell(binding.tvCreditTotal.text.toString(), TextProperties().apply {
                    textSize = bottomTextSizeToSet
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, simplyPdfDocument.usablePageWidth / 3))
                add(TextCell(binding.tvStockQuantityTotal.text.toString(), TextProperties().apply {
                    textSize = bottomTextSizeToSet
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, simplyPdfDocument.usablePageWidth / 3))

                rows.add(this)
            }

            LinkedList<Cell>().apply {
                add(TextCell(binding.tvTotal.text.toString(), TextProperties().apply {
                    textSize = bottomTextSizeToSet
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, simplyPdfDocument.usablePageWidth))

                rows.add(this)
            }

            toast("Downloading...")
            simplyPdfDocument.table.draw(rows, properties)

            simplyPdfDocument.finish() //            MediaScannerConnection.scanFile(mContext, arrayOf(resultFile.toString()), null) { path, uri -> }

            //            try {
            //                val pdfReader = PdfReader(resultFile.getPath())
            //                val stringParse = PdfTextExtractor.getTextFromPage(pdfReader, 1).trim { it <= ' ' }
            //                pdfReader.close()
            //            } catch (e: Exception) {
            //                e.printStackTrace()
            //            }

            withContext(Dispatchers.Main) {
                pdfDownloaded = true
                Toast.makeText(baseContext, "PDF downloaded successfully", Toast.LENGTH_SHORT).show()
                Log.e("file uri", resultFileUri.toString())
                try { // Perform operations on the document using its URI.
                    //                    val path = resultFileUri?.let { createCopyAndReturnRealPath(baseContext, it, "pdf") } //                    val i = Intent(Intent.ACTION_VIEW)
                    //                    i.data = resultFileUri
                    //                    startActivity(i)
                    try { //                        val pdfReader = PdfReader(path)
                        //                        val stringParse = PdfTextExtractor.getTextFromPage(pdfReader, 1).trim { it <= ' ' }
                        //                        pdfReader.close()
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

    fun createCustomDayBookList(dayBookList: List<DayBookTableItem>): List<CustomDayBookTableItem> {
        val customDayBookList = mutableListOf<CustomDayBookTableItem>()

        for (item in dayBookList) {
            if (ledger1SearchList.firstOrNull { searchableItem -> searchableItem.code == item.ledger1 } == null) {
                val customItem = CustomDayBookTableItem(
                    date1 = item.date,
                    ledger1 = item.ledger1,
                    credit = item.credit,
                    date2 = "",
                    ledger2 = "",
                    debit = "",
                )
                customDayBookList.add(customItem)
            }
        }

        // getItemWhereLedger2IsEmpty
        dayBookList.forEach { item ->
            if (ledger2SearchList.firstOrNull { searchableItem -> searchableItem.code == item.ledger2 } == null) {
                if (customDayBookList.size > 0) {
                    val allTrue = customDayBookList.any { it.ledger2.isNullOrEmpty() }
                    if (allTrue) {
                        customDayBookList.forEachIndexed { index, customDayBookTableItem ->
                            if (customDayBookTableItem.ledger2.isNullOrEmpty()) {
                                customDayBookTableItem.date2 = item.date
                                customDayBookTableItem.ledger2 = item.ledger2
                                customDayBookTableItem.debit = item.debit
                                customDayBookList[index] = customDayBookTableItem
                                return@forEach
                            }
                        }
                    } else {
                        val customItem = CustomDayBookTableItem(
                            date1 = "",
                            ledger1 = "",
                            credit = "",
                            date2 = item.date,
                            ledger2 = item.ledger2,
                            debit = item.debit,
                        )
                        customDayBookList.add(customItem)
                    }
                } else {
                    val customItem = CustomDayBookTableItem(
                        date1 = "",
                        ledger1 = "",
                        credit = "",
                        date2 = item.date,
                        ledger2 = item.ledger2,
                        debit = item.debit,
                    )
                    customDayBookList.add(customItem)
                }
            }
        }

        return customDayBookList
    }

    fun createCell(text: String, textColorStr: String): TextCell {
        return TextCell(text, TextProperties().apply {
            textSize = 12
            textColor = textColorStr
        }, Cell.MATCH_PARENT)
    }

    protected fun createSimplyPdfDocument() {
        val file = File(Environment.getExternalStorageDirectory(), "Download")
        if (!file.exists()) {
            file.mkdirs()
        }
        resultFile = File(file.absolutePath + File.separator.toString() + "${strTitle}_${System.currentTimeMillis()}.pdf") //            simplyPdfDocument = SimplyPdf.with(baseContext, resultFile).colorMode(DocumentInfo.ColorMode.COLOR).paperSize(PrintAttributes.MediaSize.ISO_A4) //                .margin(Margin(20U, 20U, 20U, 20U)) //                .pageModifier(PageHeader(headerList)).firstPageBackgroundColor(Color.WHITE).paperOrientation(DocumentInfo.Orientation.PORTRAIT).build()
        resultFileUri = Uri.fromFile(resultFile)
        simplyPdf = SimplyPdf.with(baseContext, resultFile).colorMode(DocumentInfo.ColorMode.COLOR).paperSize(PrintAttributes.MediaSize.ISO_A4) //            .margin(Margin(15U, 15U, 15U, 15U))
            .paperOrientation(DocumentInfo.Orientation.LANDSCAPE).pageModifier(PageHeader(LinkedList<Cell>().apply {
                add(TextCell("$strTitle", TextProperties().apply {
                    textSize = 32
                    alignment = Layout.Alignment.ALIGN_CENTER
                    textColor = "#000000"
                    typeface = Typeface.DEFAULT_BOLD
                }, Cell.MATCH_PARENT)) //                add(TextCell("AS ON - $strFromDate TO $strToDate", TextProperties().apply {
                //                    textSize = 20
                //                    alignment = Layout.Alignment.ALIGN_CENTER
                //                    textColor = "#000000"
                //                }, Cell.MATCH_PARENT))
                //                add(TextCell(mostOccurredLedger, TextProperties().apply {
                //                    textSize = 20
                //                    alignment = Layout.Alignment.ALIGN_CENTER
                //                    textColor = "#000000"
                //                    typeface = Typeface.DEFAULT_BOLD
                //                }, Cell.MATCH_PARENT))
            })).pageModifier(HeaderLinePageModifier())
        simplyPdfDocument = simplyPdf.build() //            .pageModifier(PageHeader(LinkedList<Cell>().apply {
        //                add(TextCell("$strTitle", TextProperties().apply {
        //                    textSize = 27
        //                    alignment = Layout.Alignment.ALIGN_CENTER
        //                    textColor = "#000000"
        //                    typeface = Typeface.DEFAULT_BOLD
        //                }, Cell.MATCH_PARENT))
        //                add(TextCell("AS ON - $strFromDate TO $strToDate", TextProperties().apply {
        //                    textSize = 20
        //                    alignment = Layout.Alignment.ALIGN_CENTER
        //                    textColor = "#000000"
        //                }, Cell.MATCH_PARENT))
        //                add(TextCell("$strDescription", TextProperties().apply {
        //                    textSize = 17
        //                    alignment = Layout.Alignment.ALIGN_CENTER
        //                    textColor = "#000000"
        //                }, Cell.MATCH_PARENT))
        //            }))

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_day_book, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                showDateFilterDialog()
                return true
            }

            R.id.action_date -> {
                showDateRangeFilterDialog()
                return true
            }

            R.id.action_reset -> {
                resetData()
                return true
            }

            R.id.action_pdf -> {
                handlePdfClickAction()
                return true
            }

            R.id.action_ledger1 -> {
                showLedger1Popup()
                return true
            }

            R.id.action_ledger2 -> {
                showLedger2Popup()
                return true
            }

            R.id.action_vouch_type -> {
                showVouchTypePopup()
                return true
            }

            R.id.action_stock_item_name -> {
                showStockItemNamePopup()
                return true
            }

            R.id.action_filter_column -> {
                showColumnFilterPopup()
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

    private fun handlePdfClickAction() {
        if (binding.tvSL.visibility == View.VISIBLE || binding.tvDate.visibility == View.VISIBLE || binding.tvLedger1.visibility == View.VISIBLE || binding.tvLedger2.visibility == View.VISIBLE || binding.tvDebit.visibility == View.VISIBLE || binding.tvCredit.visibility == View.VISIBLE || binding.tvVouchType.visibility == View.VISIBLE || binding.tvVoucherNo.visibility == View.VISIBLE || binding.tvStockItemName.visibility == View.VISIBLE || binding.tvStockItemQuantity.visibility == View.VISIBLE || binding.tvStockItemRate.visibility == View.VISIBLE || binding.tvCgst.visibility == View.VISIBLE || binding.tvSgst.visibility == View.VISIBLE || binding.tvIgst.visibility == View.VISIBLE) downloadPdf()
        else toast("Filter by at least one column")
    }

    var indices = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)

    private fun showColumnFilterPopup() {
        MaterialDialog.Builder(this).title("Choose column to filter").items("Sr No", "Date", "Ledger 1", "Ledger 2", "Debit", "Credit", "Voucher Type", "Voucher No", "Stock Item Name", "Stock Item Quantity", "Stock Item Rate", "Taxable", "CGST", "SGST", "IGST").itemsCallbackMultiChoice(indices) { dialog, which, text ->
            if (which.isEmpty()) {
                toast("Choose at least one column to filter")
                return@itemsCallbackMultiChoice false
            }
            shouldUserCustomPdf = false
            indices = which
            Log.e("which", Arrays.toString(which))
            Log.e("text", Arrays.toString(text))
            adapter?.hideColumns(text)
            if (text?.contains("Sr No") == true) {
                binding.tvSL?.visible()
            } else {
                binding.tvSL?.gone()
            }
            if (text?.contains("Date") == true) {
                binding.tvDate?.visible()
            } else {
                binding.tvDate?.gone()
            }
            if (text?.contains("Ledger 1") == true) {
                binding.tvLedger1?.visible()
            } else {
                binding.tvLedger1?.gone()
            }
            if (text?.contains("Ledger 2") == true) {
                binding.tvLedger2?.visible()
            } else {
                binding.tvLedger2?.gone()
            }
            if (text?.contains("Debit") == true) {
                binding.tvDebit?.visible()
            } else {
                binding.tvDebit?.gone()
            }
            if (text?.contains("Credit") == true) {
                binding.tvCredit?.visible()
            } else {
                binding.tvCredit?.gone()
            }
            if (text?.contains("Voucher Type") == true) {
                binding.tvVouchType?.visible()
            } else {
                binding.tvVouchType?.gone()
            }
            if (text?.contains("Voucher No") == true) {
                binding.tvVoucherNo?.visible()
            } else {
                binding.tvVoucherNo?.gone()
            }
            if (text?.contains("Stock Item Name") == true) {
                binding.tvStockItemName?.visible()
            } else {
                binding.tvStockItemName?.gone()
            }
            if (text?.contains("Stock Item Quantity") == true) {
                binding.tvStockItemQuantity?.visible()
            } else {
                binding.tvStockItemQuantity?.gone()
            }
            if (text?.contains("Stock Item Rate") == true) {
                binding.tvStockItemRate?.visible()
            } else {
                binding.tvStockItemRate?.gone()
            }
            if (text?.contains("Taxable") == true) {
                binding.tvTaxable?.visible()
            } else {
                binding.tvTaxable?.gone()
            }
            if (text?.contains("CGST") == true) {
                binding.tvCgst?.visible()
            } else {
                binding.tvCgst?.gone()
            }
            if (text?.contains("SGST") == true) {
                binding.tvSgst?.visible()
            } else {
                binding.tvSgst?.gone()
            }
            if (text?.contains("IGST") == true) {
                binding.tvIgst?.visible()
            } else {
                binding.tvIgst?.gone()
            }
            true
        }.positiveText("Choose").show()
    }

    var ledger1SearchList = ArrayList<SearchableItem>()

    private fun showLedger1Popup() {
        val list = ArrayList<String>()
        val hashSet = TreeSet<String>()
        if (isDateRangeNotApplied) {
            records.forEachIndexed { index, dayBookTableItem ->
                hashSet.add(dayBookTableItem.ledger1.toString())
            }
        } else {
            filteredRecords.forEachIndexed { index, dayBookTableItem ->
                hashSet.add(dayBookTableItem.ledger1.toString())
            }
        }
        list.addAll(hashSet)
        val ledger1List = ArrayList<SearchableItem>()
        hashSet.forEach {
            ledger1List.add(SearchableItem(it, it))
        }
        if (ledger1SearchList.size > 0) {
            ledger1List.map { ledger1 ->
                if (ledger1SearchList.firstOrNull { searchableItem -> searchableItem.code == ledger1.code } != null) {
                    ledger1.isSelected = true
                }
                ledger1
            }
        } else { //            ledger1List.map { ledger1 ->
            //                ledger1.isSelected = true
            //                ledger1
            //            }
        }
        SearchableMultiSelectSpinner.show(this, "Filter by Ledger1", "Done", ledger1List, object : SelectionCompleteListener {
            override fun onCompleteSelection(selectedItems: ArrayList<SearchableItem>) {
                Log.e("data", selectedItems.toString())
                Utils.hideSoftKeyboard(this@DayBookActivity)
                ledger1SearchList.clear()
                ledger1SearchList = selectedItems
                if (isDateRangeNotApplied) {
                    records.forEach { dayBookTableItem ->
                        val isLedgerChecked = ledger1SearchList.any { searchableItem -> searchableItem.code == dayBookTableItem.ledger1 }
                        if (isLedgerChecked) {
                            filteredRecords.addIfNotExists(dayBookTableItem)
                        }
                    }
                }
                applyFilter()
            }
        })
    }

    var ledger2SearchList = ArrayList<SearchableItem>()

    private fun showLedger2Popup() {
        val list = ArrayList<String>()
        val hashSet = TreeSet<String>()
        if (isDateRangeNotApplied) {
            records.forEachIndexed { index, dayBookTableItem ->
                hashSet.add(dayBookTableItem.ledger2.toString())
            }
        } else {
            filteredRecords.forEachIndexed { index, dayBookTableItem ->
                hashSet.add(dayBookTableItem.ledger2.toString())
            }
        }
        list.addAll(hashSet)
        val ledger2List = ArrayList<SearchableItem>()
        hashSet.forEach {
            ledger2List.add(SearchableItem(it, it))
        }
        if (ledger2SearchList.size > 0) {
            ledger2List.map { ledger2 ->
                if (ledger2SearchList.firstOrNull { searchableItem -> searchableItem.code == ledger2.code } != null) {
                    ledger2.isSelected = true
                }
                ledger2
            }
        } else { //            ledger2List.map { ledger2 ->
            //                ledger2.isSelected = true
            //                ledger2
            //            }
        }
        SearchableMultiSelectSpinner.show(this, "Filter by Ledger2", "Done", ledger2List, object : SelectionCompleteListener {
            override fun onCompleteSelection(selectedItems: ArrayList<SearchableItem>) {
                Log.e("data", selectedItems.toString())
                Utils.hideSoftKeyboard(this@DayBookActivity)
                ledger2SearchList.clear()
                ledger2SearchList = selectedItems
                if (isDateRangeNotApplied) {
                    records.forEach { dayBookTableItem ->
                        val isLedger2Checked = ledger2SearchList.any { searchableItem -> searchableItem.code == dayBookTableItem.ledger2 }
                        if (isLedger2Checked) {
                            filteredRecords.addIfNotExists(dayBookTableItem)
                        }
                    }
                }
                applyFilter()
            }
        })
    }

    private fun showVouchTypePopup() {
        val list = ArrayList<String>()
        val hashSet = TreeSet<String>()
        hashSet.add("All")
        filteredRecords.forEachIndexed { index, dayBookTableItem ->
            hashSet.add(dayBookTableItem.voucherType.toString())
        }
        list.addAll(hashSet)
        MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
            run {
                dialog?.dismiss()
                selectedVouchType = text.toString()
                applyFilter(shouldVouchTypeChecked = true, shouldStockItemChecked = false)
            }
        }.show()
    }

    var stockItemSearchList = ArrayList<SearchableItem>()

    private fun showStockItemNamePopup() {
        val list = ArrayList<String>()
        val hashSet = TreeSet<String>()
        filteredRecords.forEachIndexed { index, dayBookTableItem ->
            hashSet.add(dayBookTableItem.stockItemName.toString())
        }
        list.addAll(hashSet)
        val stockItemList = ArrayList<SearchableItem>()
        hashSet.forEach {
            stockItemList.add(SearchableItem(it, it))
        }
        if (stockItemSearchList.size > 0) {
            stockItemList.map { ledger1 ->
                if (stockItemSearchList.firstOrNull { searchableItem -> searchableItem.code == ledger1.code } != null) {
                    ledger1.isSelected = true
                }
                ledger1
            }
        } else {
            stockItemList.map { ledger1 ->
                ledger1.isSelected = true
                ledger1
            }
        }
        SearchableMultiSelectSpinner.show(this, "Filter by Stock Item Name", "Done", stockItemList, object : SelectionCompleteListener {
            override fun onCompleteSelection(selectedItems: ArrayList<SearchableItem>) {
                Log.e("data", selectedItems.toString())
                Utils.hideSoftKeyboard(this@DayBookActivity)
                stockItemSearchList = selectedItems
                applyFilter(shouldVouchTypeChecked = false, shouldStockItemChecked = true)
            }
        })
    }

    private fun applyFilter(
        shouldVouchTypeChecked: Boolean = false, shouldStockItemChecked: Boolean = false
    ) {
        val recordsToAdd = filteredRecords.filter { dayBookTableItem ->
            var isLedger1Checked = ledger1SearchList.any { searchableItem -> searchableItem.code == dayBookTableItem.ledger1 }
            var isLedger2Checked = ledger2SearchList.any { searchableItem -> searchableItem.code == dayBookTableItem.ledger2 }
            val isVouchTypeValid = if (shouldVouchTypeChecked) selectedVouchType.isEmpty() || dayBookTableItem.voucherType == selectedVouchType || selectedVouchType == "All" else false
            val isStockItemValid = if (shouldStockItemChecked) stockItemSearchList.any { searchableItem -> searchableItem.code == dayBookTableItem.stockItemName } else false

            if (isLedger1Checked) {
                dayBookTableItem.isLedger1Checked = true
            } else {
                dayBookTableItem.isLedger1Checked = false
            }
            if (isLedger2Checked) {
                dayBookTableItem.isLedger2Checked = true
            } else {
                dayBookTableItem.isLedger2Checked = false
            }
            if (shouldVouchTypeChecked || shouldStockItemChecked) {
                isLedger1Checked = false
                isLedger2Checked = false
            }
            isLedger1Checked || isLedger2Checked || isVouchTypeValid || isStockItemValid
        }.toMutableList()

        filteredRecords.clear()
        filteredRecords.addAll(recordsToAdd)
        setFilteredData()
    }

    private fun showDateFilterDialog() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance({ view1: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val date = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear + 1) + "/" + year
            val recordsToAdd = filteredRecords.filter { dayBookTableItem ->
                val isLedger1Checked = ledger1SearchList.any { searchableItem -> searchableItem.code == dayBookTableItem.ledger1 }
                val isLedger2Checked = ledger2SearchList.any { searchableItem -> searchableItem.code == dayBookTableItem.ledger2 }

                if (isLedger1Checked) {
                    dayBookTableItem.isLedger1Checked = true
                }
                if (isLedger2Checked) {
                    dayBookTableItem.isLedger2Checked = true
                }

                dayBookTableItem.date == date
            }.toMutableList()

            filteredRecords.clear()
            filteredRecords.addAll(recordsToAdd)
            setFilteredData()
        }, now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH])
        dpd.setTitle("Select Date")
        dpd.show(supportFragmentManager, "Datepickerdialog")
    }

    private fun showDateRangeFilterDialog() {
        val dialog: Dialog? = Utils.showCustomAlertDialog(mContext, R.layout.custom_dialog_day_book_filter)
        val etFromDate: EditText? = dialog?.findViewById(R.id.etFromDate)
        val etToDate: EditText? = dialog?.findViewById(R.id.etToDate)
        val tilFromDate: TextInputLayout? = dialog?.findViewById(R.id.tilFromDate)
        val tilToDate: TextInputLayout? = dialog?.findViewById(R.id.tilToDate)
        val btnClose: Button? = dialog?.findViewById(R.id.btnClose)
        val btnOk: Button? = dialog?.findViewById(R.id.btnOk)
        btnClose?.setOnClickListener { dialog.dismiss() }
        etFromDate?.setText(selectedFromDate)
        etToDate?.setText(selectedToDate)
        etFromDate?.setOnClickListener {
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance({ view1: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                selectedFromDate = String.format("%02d", dayOfMonth) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year
                etFromDate.setText(selectedFromDate)
                tilFromDate?.error = null
            }, now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH])
            dpd.setTitle("Select From Date")
            dpd.show(supportFragmentManager, "StartDatepickerdialog")
        }
        etToDate?.setOnClickListener {
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance({ view1: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                selectedToDate = String.format("%02d", dayOfMonth) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year
                etToDate.setText(selectedToDate)
                tilToDate?.error = null
            }, now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH])
            dpd.setTitle("Select To Date")
            dpd.show(supportFragmentManager, "EndDatepickerdialog")
        }
        btnOk?.setOnClickListener { view1: View? ->
            if (TextUtils.isEmpty(selectedFromDate)) {
                tilFromDate?.error = "Select From Date"
                etFromDate?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(selectedToDate)) {
                tilToDate?.error = "Select To Date"
                etToDate?.requestFocus()
                return@setOnClickListener
            }
            val recordsToAdd = filteredRecords.filter { dayBookTableItem ->
                !TextUtils.isEmpty(dayBookTableItem.date) && Utils.isDateBetweenTwoDates(selectedFromDate, selectedToDate, dayBookTableItem.date!!)
            }.toMutableList()

            isDateRangeNotApplied = false

            filteredRecords.clear()
            filteredRecords.addAll(recordsToAdd)
            setFilteredData()
            dialog.dismiss()
        }
        dialog?.show()
    }

    private fun setFilteredData() {
        if (filteredRecords.size > 0) {
            adapter = DayBookTableAdapter(mContext, filteredRecords)
            adapter?.itemClickListener = clickListener
            binding.recyclerView?.adapter = adapter
            binding.recyclerView?.visible()
            binding.tvError?.gone()
        } else {
            binding.recyclerView?.gone()
            binding.tvError?.visible()
        }
        setAllTotals()
    }

    private fun resetData() {
        selectedFromDate = ""
        selectedToDate = ""
        selectedVouchType = ""
        filteredRecords.clear() //        filteredRecords.addAll(records)
        //        setFilteredData()
        finish()
    }

    val clickListener = object : DayBookTableAdapter.ItemClickListener {
        override fun onItemClick(item: DayBookTableItem) {
        }

        override fun onItemClick(position: Int) {
        }
    }

    fun getMostOccurredLedger(): String? {
        val combinedList = filteredRecords // Group the strings by their occurrences
        val groupedStrings1 = combinedList.filter { it.isLedger1Checked == true }.groupBy { it.ledger1 }
        Log.e("groupedStrings1", groupedStrings1.toString())
        val groupedStrings2 = combinedList.filter { it.isLedger2Checked == true }.groupBy { it.ledger2 }
        Log.e("groupedStrings2", groupedStrings2.toString())

        val groupedStrings = groupedStrings1 + groupedStrings2

        // Find the group with the maximum number of occurrences
        val mostOccurredGroup = groupedStrings.maxBy { it.value.size }

        // Extract the key (the string) from the group with the maximum occurrence
        return mostOccurredGroup?.key //        return combinedList.groupBy { it.code }.maxByOrNull { it.value.size }?.key
    }

    fun createCopyAndReturnRealPath(context: Context, uri: Uri, fileExtension: String): String? {
        val contentResolver = context.contentResolver ?: return null

        // Create file path inside app's data dir
        val filePath = (context.applicationInfo.dataDir + File.separator + System.currentTimeMillis() + "." + fileExtension)
        tempFile = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream: OutputStream = FileOutputStream(tempFile)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (ignore: IOException) {
            return null
        }
        return tempFile?.absolutePath
    }

    companion object
}
