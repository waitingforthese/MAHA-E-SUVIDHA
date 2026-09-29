package rahul.jagtap.dmas.admin

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.databinding.ActivityAddTextMsgBinding
import rahul.jagtap.dmas.model.TextMsg
import rahul.jagtap.dmas.utils.Utils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.widget.doAfterTextChanged

class AddTextMsgActivity : BaseActivity() {
    private val TAG = AddTextMsgActivity::class.java.simpleName
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    var cpd: ProgressDialog? = null
    var textMsg: TextMsg? = null
    lateinit var binding: ActivityAddTextMsgBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTextMsgBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilTitle, binding.tilTextMsg)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = if (textMsg != null) "Edit Text Msg" else "Create Text Msg"
        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid
        username = app?.preferences?.loggedInUser?.username
        name = app?.preferences?.loggedInUser?.name
        textMsg = intent.getParcelableExtra("textMsg")

        if (textMsg != null) {
            binding.etTitle?.setText(textMsg?.title)
            binding.etTextMsg?.setText(textMsg?.textMsg)
        }

        binding.btnSubmit?.setOnClickListener {
            val strTitle = binding.etTitle.text.toString()
            val strTextMsg = binding.etTextMsg.text.toString()
            if (TextUtils.isEmpty(strTitle)) {
                binding.tilTitle?.error = binding.tilTitle.hint.toString()
                binding.etTitle?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strTextMsg)) {
                binding.tilTextMsg?.error = binding.tilTextMsg.hint.toString()
                binding.etTextMsg?.requestFocus()
                return@setOnClickListener
            }
            createDbRecord()
        }
    }

    private fun createDbRecord() {
        cpd = ProgressDialog(mContext)
        cpd?.setCancelable(false)
        cpd?.show()
        if (textMsg == null) {
            val createdDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
            val billMap = HashMap<String, Any?>()
            billMap["email"] = email
            billMap["createdBy"] = name
            billMap["createdByUserName"] = username
            billMap["uid"] = uid
            billMap["createdDateTime"] = createdDateTime
            billMap["title"] = binding.etTitle.text.toString()
            billMap["textMsg"] = binding.etTextMsg.text.toString()
            billMap["timeStamp"] = System.currentTimeMillis()

            val pushKey = database.child(Utils.TEXT_MSG_TABLE).push().key
            billMap["pushKey"] = pushKey

            val messageUserMap = HashMap<String, Any?>()
            messageUserMap["${Utils.TEXT_MSG_TABLE}/$pushKey"] = billMap

            database.updateChildren(messageUserMap) { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
                if (databaseError != null) {
                    Log.e("db error", databaseError.message)
                }
            }

            // Create a backup record
//            val backupMap = HashMap<String, Any?>()
//            backupMap["${Utils.TEXT_MSG_TABLE}_backup/$pushKey"] = billMap
//            database.updateChildren(backupMap)
            if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
            toast("Entry added successfully")
        } else {
            textMsg?.title = binding.etTitle.text.toString()
            textMsg?.textMsg = binding.etTextMsg.text.toString()
            database.child(Utils.TEXT_MSG_TABLE).child(textMsg?.pushKey!!).setValue(textMsg)
//            database.child("${Utils.TEXT_MSG_TABLE}_backup").child(textMsg?.pushKey!!).setValue(textMsg)
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
