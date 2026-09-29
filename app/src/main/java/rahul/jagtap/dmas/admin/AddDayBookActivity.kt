package rahul.jagtap.dmas.admin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.google.gson.Gson
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import rahul.jagtap.dmas.extensions.toast
import org.json.simple.parser.JSONParser
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityCreateDayBookEntryBinding
import rahul.jagtap.dmas.model.DayBook
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.doAfterTextChanged


class AddDayBookActivity : BaseActivity() {
    private var selectedFromDate: String = ""
    private var selectedToDate: String = ""
    private val TAG = AddDayBookActivity::class.java.simpleName
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    var cpd: ProgressDialog? = null
    private var selectedUser: User? = null
    var csvFileUri: Uri? = null
    var tempFile: File? = null
    lateinit var binding: ActivityCreateDayBookEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDayBookEntryBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilUserEmail, binding.tilTitle, binding.tilFromDate, binding.tilToDate, binding.tilCsvFile)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "Create/Update Day Book"
        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid
        username = app?.preferences?.loggedInUser?.username
        name = app?.preferences?.loggedInUser?.name

        binding.etFromDate?.setOnClickListener {
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance({ view1: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                selectedFromDate = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear + 1) + "/" + year
                binding.etFromDate.setText(selectedFromDate)
            }, now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH])
            dpd.setTitle("Select From Date")
            dpd.show(supportFragmentManager, "StartDatepickerdialog")
        }
        binding.etToDate?.setOnClickListener {
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance({ view1: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                selectedToDate = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear + 1) + "/" + year
                binding.etToDate.setText(selectedToDate)
            }, now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH])
            dpd.setTitle("Select To Date")
            dpd.show(supportFragmentManager, "EndDatepickerdialog")
        }

        binding.btnSubmit?.setOnClickListener {
            val strUserEmail = binding.etUserEmail.text.toString()
            val strTitle = binding.etTitle.text.toString()
            val strFromDate = binding.etFromDate.text.toString()
            val strToDate = binding.etToDate.text.toString()
//            val strJson = etJson.text.toString()
            if (TextUtils.isEmpty(strTitle)) {
                binding.tilTitle?.error = binding.tilTitle?.hint.toString()
                binding.etTitle?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strUserEmail)) {
                binding.tilUserEmail?.error = binding.tilUserEmail?.hint.toString()
                binding.etUserEmail?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strFromDate)) {
                binding.tilFromDate?.error = binding.tilFromDate?.hint.toString()
                binding.etFromDate?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strToDate)) {
                binding.tilToDate?.error = binding.tilToDate?.hint.toString()
                binding.etToDate?.requestFocus()
                return@setOnClickListener
            }
//            if (TextUtils.isEmpty(strJson)) {
//                etJson?.error = "Copy and Paste JSON text here"
//                etJson?.requestFocus()
//                return@setOnClickListener
//            }
            if (csvFileUri == null) {
                binding.tilCsvFile?.error = binding.tilCsvFile?.hint.toString()
                binding.etCsvFile?.requestFocus()
                return@setOnClickListener
            }
            if (selectedUser != null) {
                createDbRecord()
            } else {
                toast("User not found")
            }
        }
        binding.etUserEmail?.setOnClickListener {
            val userIntent = Intent(mContext, SelectUserActivity::class.java)
//            userIntent.putExtra("userList", Gson().toJson(userList))
            startActivityForResult(userIntent, SELECT_USER_INTENT)
        }
        binding.etCsvFile?.setOnClickListener {
            openFile()
        }
//        getUserList()
    }

//    private fun getUserList() {
//        database.child(Utils.USERS_TABLE).addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.i("firebase", "Got value ${snapshot.value}")
//                // Get user value
//                val json = Gson().toJson(snapshot.value)
//                val type: Type = object : TypeToken<HashMap<String, User>?>() {}.type
//                val map: HashMap<String, User> = Gson().fromJson(json, type)
//                userList.addAll(map.values.toMutableList())
//                //                setupSpinner()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_USER_INTENT && resultCode == RESULT_OK) {
            val returnString: String? = data?.getStringExtra("selectedUser")
            selectedUser = Gson().fromJson<User>(returnString, User::class.java)
            binding.etUserEmail?.setText(selectedUser?.name)
        }
        if (requestCode == PICK_CSV_FILE && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            data?.data?.also { uri ->
                // Perform operations on the document using its URI.
                Log.e("uti", uri.toString())
                val path = mContext?.let { createCopyAndReturnRealPath(it, uri) }
                Log.e("path", path.toString())
                binding.etCsvFile?.setText(uri.toString())
                csvFileUri = uri
//                val json = getJsonFromCsv()
//                Log.e("json", json)
            }
        }
    }

    private fun getJsonFromCsv(): String {
        val parser = JSONParser()
        val csvSchema = CsvSchema.builder().setUseHeader(true).build()
        val csvMapper = CsvMapper()

        // Read data from CSV file
        val readAll = csvMapper.readerFor(MutableMap::class.java).with(csvSchema).readValues<Any>(tempFile).readAll()

        val mapper = ObjectMapper()
        val json = parser.parse(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll)).toString()

        Log.e("json", json)
        print(parser.parse(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll)).toString())
        return json
    }

    private fun createDbRecord() {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        val createdDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
        val selectedUserUid = selectedUser?.uid.toString()
        val dayBook = DayBook()
        dayBook.title = binding.etTitle?.text.toString().trim()
        dayBook.fromDate = selectedFromDate
        dayBook.toDate = selectedToDate
        dayBook.json = getJsonFromCsv()
        dayBook.userEmail = selectedUser?.email
        dayBook.userFullName = selectedUser?.name
        dayBook.userContactNo = selectedUser?.contactNo
        dayBook.userUid = selectedUser?.uid
        dayBook.createdEmail = email
        dayBook.createdBy = name
        dayBook.createdByUserName = username
        dayBook.createdByUid = uid
        dayBook.createdDateTime = createdDateTime
        dayBook.timeStamp = System.currentTimeMillis()
        database.child(Utils.DAY_BOOK_TABLE).child(selectedUserUid).setValue(dayBook).addOnSuccessListener {
            // Write was successful!
            toast("Day book added/updated successfully")
            setResult(RESULT_OK)
            Utils.hideSoftKeyboard(this)
            finish()
        }.addOnFailureListener {
            // Write failed
            toast("Failed to add/update day book")
            Utils.hideSoftKeyboard(this)
            finish()
        }
//        val billMap = HashMap<String, Any?>()
//        billMap["json"] = etJson.text.toString().trim()
//        billMap["userEmail"] = selectedUser?.email
//        billMap["userFullName"] = selectedUser?.name
//        billMap["userContactNo"] = selectedUser?.name
//        billMap["userUid"] = selectedUser?.uid
//        billMap["createdEmail"] = email
//        billMap["createdBy"] = name
//        billMap["createdByUserName"] = username
//        billMap["createdByUid"] = uid
//        billMap["createdDateTime"] = createdDateTime
//        billMap["timeStamp"] = System.currentTimeMillis()
//
//        val pushKey = database.child(Utils.DAY_BOOK_TABLE).child(selectedUserUid).push().key
//        billMap["pushKey"] = pushKey
//
//        val messageUserMap = HashMap<String, Any?>()
//        messageUserMap["${Utils.DAY_BOOK_TABLE}/${selectedUserUid}/$pushKey"] = billMap
//
//        database.updateChildren(messageUserMap) { databaseError: DatabaseError?, _: DatabaseReference? ->
//            if (databaseError != null) {
//                Log.e("db error", databaseError.message)
//            }
//        }
//
//        if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
    }

    fun getTodayDate(): String {
        return SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).format(Date())
    }

    fun openFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            //            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //                putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
            //            }
        }

        startActivityForResult(intent, PICK_CSV_FILE)
    }

    @Nullable
    fun createCopyAndReturnRealPath(@NonNull context: Context, uri: Uri?): String? {
        val contentResolver = context.contentResolver ?: return null

        // Create file path inside app's data dir
        val filePath = (context.applicationInfo.dataDir + File.separator + System.currentTimeMillis())
        tempFile = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri!!) ?: return null
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

    companion object {
        const val PICK_CSV_FILE = 3
        const val SELECT_USER_INTENT = 4
    }
}
