package rahul.jagtap.dmas

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import rahul.jagtap.dmas.databinding.ActivityEditUserBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.longToast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.DailyEntry
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import androidx.core.widget.doAfterTextChanged

class EditUserActivity : BaseActivity() {
    private val TAG = EditUserActivity::class.java.simpleName
    var user: User? = null
    var isBlock = false
    var isDailyEntryBlocked = false
    lateinit var binding: ActivityEditUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)

        // input-field migration: clear errors on edit
        listOf(binding.tilName, binding.tilShopName, binding.tilEmail, binding.tilContactNo, binding.tilAddress, binding.tilReferrer, binding.tilAadharNo, binding.tilUserType, binding.tilType, binding.tilDailyEntryType)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        user = intent?.getSerializableExtra("user") as User?
        binding.toolbarLayout.toolbarTitle?.text = "Edit User"
        binding.etName.setText(user?.name)
        binding.etShopName.setText(user?.shopName)
        binding.etEmail.setText(user?.email)
        binding.etContactNo.setText(user?.contactNo)
        binding.etAddress.setText(user?.address)
        binding.etReferrer.setText(user?.referrer)
        binding.etType.setText(if (user?.isBlocked == "1") "Block" else "Unblock")
        binding.etUserType.setText(if (user?.userType == "0") "User" else "Employee")
        binding.etDailyEntryType.setText(if (user?.isDailyEntryBlocked == "1") "Block" else "Unblock")
        isBlock = user?.isBlocked == "1"
        isDailyEntryBlocked = user?.isDailyEntryBlocked == "1"
        getDailyEntries()
        binding.btnSubmit?.setOnClickListener {
            createAccount()
        }
        binding.etType?.setOnClickListener {
            val list = java.util.ArrayList<String>()
            list.add("Block")
            list.add("Unblock")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    binding.etType.setText(list[position])
                    isBlock = position == 0
                }
            }.show()
        }
        binding.etUserType?.setOnClickListener {
            val list = java.util.ArrayList<String>()
            list.add("User")
            list.add("Employee")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    binding.etUserType.setText(list[position])
                }
            }.show()
        }
        binding.etDailyEntryType?.setOnClickListener {
            val list = java.util.ArrayList<String>()
            list.add("Block")
            list.add("Unblock")
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    binding.etDailyEntryType.setText(list[position])
                    isDailyEntryBlocked = position == 0
                }
            }.show()
        }
    }

    private fun createAccount() {
        val strName = binding.etName.text.toString().trim()
        val strShopName = binding.etShopName.text.toString().trim()
        val strEmail = binding.etEmail.text.toString().trim()
        val strContactNo = binding.etContactNo.text.toString().trim()
        val strAddress = binding.etAddress.text.toString().trim()
        val strReferrer = binding.etReferrer.text.toString().trim()
        val strUserType = binding.etUserType.text.toString()
        val strType = binding.etType.text.toString()
        val strDailyEntryType = binding.etDailyEntryType.text.toString()
        if (TextUtils.isEmpty(strName)) {
            binding.tilName.error = "Enter Name"
            binding.etName.requestFocus()
            return
        }
        if (TextUtils.isEmpty(strShopName)) {
            binding.tilShopName.error = "Enter Shop Name"
            binding.etShopName.requestFocus()
            return
        }
        if (TextUtils.isEmpty(strContactNo)) {
            binding.tilContactNo.error = "Enter Contact Number"
            binding.etContactNo.requestFocus()
            return
        }
        if (TextUtils.isEmpty(strAddress)) {
            binding.tilAddress.error = "Enter Address"
            binding.etAddress.requestFocus()
            return
        }
        if (TextUtils.isEmpty(strReferrer)) {
            binding.tilReferrer.error = "Enter Referrer"
            binding.etReferrer.requestFocus()
            return
        }
        if (TextUtils.isEmpty(strUserType)) {
            binding.tilUserType?.error = "Select User Type"
            binding.etUserType?.requestFocus()
            return
        }
        if (TextUtils.isEmpty(strType)) {
            binding.tilType?.error = "Select Block/Unblock"
            binding.etType?.requestFocus()
            return
        }
        if (TextUtils.isEmpty(strDailyEntryType)) {
            binding.tilDailyEntryType?.error = "Select Block/Unblock"
            binding.etDailyEntryType?.requestFocus()
            return
        }

        binding.progressBar?.visible()

        val localUser =
            User(uid = user?.uid, name = strName, username = user?.username, shopName = strShopName,
                isAdmin = user?.isAdmin, email = user?.email!!, contactNo = strContactNo, address = strAddress,
                referrer = strReferrer, createdAt = user?.createdAt, aadharNo = "", userType = if (strUserType == "User") "0" else "2", isBlocked = if (isBlock) "1" else "0",
                isDailyEntryBlocked = if (isDailyEntryBlocked) "1" else "0", dailyEntriesCount = binding.tvDailyEntriesCount?.text.toString().toLong())
        user?.uid?.let { database.child(Utils.USERS_TABLE).child(it).setValue(localUser) }
        binding.progressBar?.gone()
        longToast("User info updated successfully")
        finish()
    }

    private fun getDailyEntries() {
        app?.apiRequestHelper?.apiService?.getDailyEntriesByUid(user?.uid)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!isFinishing) {
                    if (response.isSuccessful) {
                        val json = response.body()?.string()
                        if (json == null || json == "null") {
                            binding.tvDailyEntriesCount?.text = "0"
                            return
                        }
                        val type: Type = object : TypeToken<HashMap<String, DailyEntry>?>() {}.type
                        val map: HashMap<String, DailyEntry>? = Gson().fromJson(json, type)
                        val dailyEntries = map?.values
                        binding.tvDailyEntriesCount?.text = dailyEntries?.size.toString() ?: "0"
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("in", "failure")
            }
        })
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

    public override fun onStop() {
        super.onStop()
        binding.progressBar?.gone()
    }
}
