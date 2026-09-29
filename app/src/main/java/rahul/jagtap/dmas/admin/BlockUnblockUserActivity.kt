package rahul.jagtap.dmas.admin

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityBlockUserBinding
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.widget.doAfterTextChanged


class BlockUnblockUserActivity : BaseActivity() {
    private var selectedUser: User? = null
    var userList = ArrayList<User>()
    var isBlock = false
    lateinit var binding: ActivityBlockUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockUserBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilUserEmail, binding.tilType)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_send_report)
        //        email = app?.preferences?.loggedInUser?.email
        //        uid = app?.preferences?.loggedInUser?.uid
        //        username = app?.preferences?.loggedInUser?.username
        //        name = app?.preferences?.loggedInUser?.name

        binding.btnSubmit?.setOnClickListener {
            val strUserEmail = binding.etUserEmail.text.toString()
            val strType = binding.etType.text.toString()
            if (TextUtils.isEmpty(strUserEmail)) {
                binding.tilUserEmail?.error = "Select email/name"
                binding.etUserEmail?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strType)) {
                binding.tilType?.error = "Select Block/Unblock"
                binding.etType?.requestFocus()
                return@setOnClickListener
            }
            if (selectedUser == null) {
                toast("user not found")
                return@setOnClickListener
            }
            selectedUser?.uid?.let { uid ->
                selectedUser?.isBlocked = if (isBlock) "1" else "0"
                database.child(Utils.USERS_TABLE).child(uid).setValue(selectedUser).addOnSuccessListener {
                    // Write was successful!
                    if (isBlock) toast("User blocked successfully")
                    else toast("User un-blocked successfully")
                    finish()
                }.addOnFailureListener {
                    // Write failed
                    if (isBlock) toast("Failed to block user")
                    else toast("Failed to un-block user")
                    finish()
                }
            }
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
        binding.etUserEmail?.setOnClickListener {
            if (userList != null && userList.size > 0) {
                val list = java.util.ArrayList<String>()
                for (user in userList) {
                    list.add("${user.email}(${user.name})")
                }
                MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                    run {
                        dialog?.dismiss()
                        binding.etUserEmail.setText(list[position])
                        selectedUser = userList[position]
                    }
                }.show()
            } else {
                toast("No users found.")
            }
        }
        getUserList()
    }

    private fun getUserList() {
        database.child(Utils.USERS_TABLE).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("firebase", "Got value ${snapshot.value}")
                // Get user value
                val json = Gson().toJson(snapshot.value)
                val type: Type = object : TypeToken<HashMap<String, User>?>() {}.type
                val map: HashMap<String, User> = Gson().fromJson(json, type)
                userList.addAll(map.values.toMutableList())
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
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
