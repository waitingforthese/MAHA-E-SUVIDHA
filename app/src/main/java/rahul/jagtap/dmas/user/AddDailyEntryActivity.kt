package rahul.jagtap.dmas.user

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.DatePicker
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityAddDailyEntryBinding
import rahul.jagtap.dmas.model.DailyEntry
import rahul.jagtap.dmas.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged

class AddDailyEntryActivity : BaseActivity() {
    private val TAG = AddDailyEntryActivity::class.java.simpleName
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private var strDate: String? = ""
    var cpd: ProgressDialog? = null
    var dailyEntry: DailyEntry? = null
    lateinit var binding: ActivityAddDailyEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDailyEntryBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilCashBank, binding.tilDate, binding.tilDetails, binding.tilDebitCredit, binding.tilAmount)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = if (dailyEntry != null) "Edit Daily Entry" else "Create Daily Entry"
        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid
        username = app?.preferences?.loggedInUser?.username
        name = app?.preferences?.loggedInUser?.name
        dailyEntry = intent.getParcelableExtra("dailyEntry")

        if (dailyEntry != null) {
            strDate = dailyEntry?.date
            binding.etDate?.setText(strDate)
            binding.etCashBank?.setText(dailyEntry?.cash_bank)
            binding.etDetails?.setText(dailyEntry?.details)
            binding.etDebitCredit?.setText(dailyEntry?.debit_credit)
            binding.etAmount?.setText(dailyEntry?.amount)
        }

        binding.btnSubmit?.setOnClickListener {
            val strCashBank = binding.etCashBank.text.toString()
            val strDate = binding.etDate.text.toString()
            val strDetails = binding.etDetails.text.toString()
            val strDebitCredit = binding.etDebitCredit.text.toString()
            val strAmount = binding.etAmount.text.toString()
            if (TextUtils.isEmpty(strCashBank)) {
                binding.tilCashBank?.error = "Select Cash/Bank"
                binding.etCashBank?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strDate)) {
                binding.tilDate?.error = "Select Date"
                binding.etDate?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strDetails)) {
                binding.tilDetails?.error = "Enter Details"
                binding.etDetails?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strDebitCredit)) {
                binding.tilDebitCredit?.error = "Select Debit/Credit"
                binding.etDebitCredit?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strAmount)) {
                binding.tilAmount?.error = "Enter Amount"
                binding.etAmount?.requestFocus()
                return@setOnClickListener
            }
            createDbRecord()
        }
        binding.etDate?.setOnClickListener {
            val mMaxDate = Calendar.getInstance()
            val datePickerDialog = mContext?.let { it1 ->
                DatePickerDialog(it1, { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                    val date = "" + dayOfMonth + "/" + (month + 1) + "/" + year
                    strDate = year.toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth)
                    binding.etDate.setText(date)
                }, mMaxDate[Calendar.YEAR], mMaxDate[Calendar.MONTH], mMaxDate[Calendar.DAY_OF_MONTH])
            }
            datePickerDialog?.show()
        }
        binding.etCashBank?.setOnClickListener {
            val list = ArrayList<String>()
            list.add("Cash")
            list.add("Bank")
            mContext?.let { it1 ->
                MaterialDialog.Builder(it1).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                    run {
                        dialog?.dismiss()
                        binding.etCashBank.setText(list[position])
                    }
                }.show()
            }
        }
        binding.etDebitCredit?.setOnClickListener {
            val list = ArrayList<String>()
            list.add("Debit")
            list.add("Credit")
            mContext?.let { it1 ->
                MaterialDialog.Builder(it1).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                    run {
                        dialog?.dismiss()
                        binding.etDebitCredit.setText(list[position])
                    }
                }.show()
            }
        }
    }

    private fun createDbRecord() {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        if (dailyEntry == null) {
            val createdDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
            val billMap = HashMap<String, Any?>()
            billMap["email"] = email
            billMap["createdBy"] = name
            billMap["createdByUserName"] = username
            billMap["uid"] = uid
            billMap["createdDateTime"] = createdDateTime
            billMap["cash_bank"] = binding.etCashBank.text.toString()
            billMap["date"] = strDate
            billMap["details"] = binding.etDetails.text.toString()
            billMap["debit_credit"] = binding.etDebitCredit.text.toString()
            billMap["amount"] = binding.etAmount.text.toString()
            billMap["timeStamp"] = System.currentTimeMillis()

            val pushKey = database.child(Utils.DAILY_ENTRY_TABLE).child(uid!!).push().key
            billMap["pushKey"] = pushKey

            val messageUserMap = HashMap<String, Any?>()
            messageUserMap["${Utils.DAILY_ENTRY_TABLE}/${uid!!}/$pushKey"] = billMap

            database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
                if (databaseError != null) {
                    Log.e("db error", databaseError.message)
                }
            }

            // Create a backup record
            val backupMap = HashMap<String, Any?>()
            backupMap["${Utils.DAILY_ENTRY_TABLE}_backup/${uid!!}/$pushKey"] = billMap
            database.updateChildren(backupMap)
            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
            toast("Entry added successfully")
        } else {
            dailyEntry?.cash_bank = binding.etCashBank.text.toString()
            dailyEntry?.date = strDate
            dailyEntry?.details = binding.etDetails.text.toString()
            dailyEntry?.debit_credit = binding.etDebitCredit.text.toString()
            dailyEntry?.amount = binding.etAmount.text.toString()
            database.child(Utils.DAILY_ENTRY_TABLE).child(dailyEntry?.uid!!).child(dailyEntry?.pushKey!!).setValue(dailyEntry)
            database.child("${Utils.DAILY_ENTRY_TABLE}_backup").child(dailyEntry?.uid!!).child(dailyEntry?.pushKey!!).setValue(dailyEntry)
            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
            toast("Entry updated successfully")
        }
        setResult(RESULT_OK)
        Utils.hideSoftKeyboard(this)
        finish()
    }

    fun getTodayDate(): String {
        return SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).format(Date())
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

    companion object {}
}
