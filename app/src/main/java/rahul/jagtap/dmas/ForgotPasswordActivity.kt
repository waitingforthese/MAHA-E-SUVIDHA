package rahul.jagtap.dmas

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import rahul.jagtap.dmas.databinding.ActivityForgotPasswordBinding
import rahul.jagtap.dmas.extensions.longToast
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.utils.Utils
import androidx.core.widget.doAfterTextChanged

class ForgotPasswordActivity : BaseActivity() {
    private val TAG = ForgotPasswordActivity::class.java.simpleName
    lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)

        // input-field migration: clear errors on edit
        listOf(binding.tilEmail)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_forgot_password)
        binding.btnSubmit?.setOnClickListener {
            sendResetLink()
        }
    }

    private fun sendResetLink() {
        val strEmail = binding.etEmail.text.toString().trim()
        if (TextUtils.isEmpty(strEmail)) {
            binding.tilEmail.error = "Enter Email ID"
            binding.etEmail.requestFocus()
            return
        }
        if (!Utils.isValidEmail(strEmail)) {
            binding.tilEmail.error = "Enter Valid Email ID"
            binding.etEmail.requestFocus()
            return
        }

        binding.progressBar?.visible()
        auth.sendPasswordResetEmail(strEmail)
            .addOnCompleteListener { task ->
                binding.progressBar?.gone()
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    longToast("Password Reset link has been sent successfully")
                    finish()
                }
            }.addOnFailureListener {
                binding.progressBar?.gone()
                val stringBuilder = StringBuilder()
                stringBuilder.append("Error sendPasswordResetEmail: ")
                stringBuilder.append(it.toString())
                Log.e(TAG, "$it")
                longToast("Email is not registered")
            }
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
}
