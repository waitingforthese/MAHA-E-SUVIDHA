package rahul.jagtap.dmas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import rahul.jagtap.dmas.databinding.ActivityPayByUpiIdBinding
import rahul.jagtap.dmas.utils.Utils
import androidx.core.widget.doAfterTextChanged


class PayByUpiActivity : BaseActivity() {
    private val TAG = PayByUpiActivity::class.java.simpleName
//    private var email: String? = ""
//    private var username: String? = ""
//    private var uid: String? = ""
//    private var name: String? = ""
    internal val UPI_PAYMENT = 110
    lateinit var binding: ActivityPayByUpiIdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayByUpiIdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilName, binding.tilNote, binding.tilUpiID, binding.tilAmount)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_upi_mode)
//        email = app?.preferences?.loggedInUser?.email
//        uid = app?.preferences?.loggedInUser?.uid
//        username = app?.preferences?.loggedInUser?.username
//        name = app?.preferences?.loggedInUser?.name
//        etName?.setText("Jagtap Patil E Suvidha Kendra")

        binding.btnSubmit?.setOnClickListener {
            val strName = binding.etName.text.toString()
            val strNote = binding.etNote.text.toString()
            val strUpiID = binding.etUpiID.text.toString()
            val strAmount = binding.etAmount.text.toString()
            if (TextUtils.isEmpty(strName)) {
                binding.tilName?.error = "Enter Sender Name"
                binding.etName?.requestFocus()
                return@setOnClickListener
            }
//            if (TextUtils.isEmpty(strNote)) {
//                etNote?.error = "Enter Note"
//                etNote?.requestFocus()
//                return@setOnClickListener
//            }
            if (TextUtils.isEmpty(strAmount)) {
                binding.tilAmount?.error = "Enter Amount"
                binding.etAmount?.requestFocus()
                return@setOnClickListener
            }
            payUsingUpi(strAmount, strUpiID, strName, strNote)
//            val uri = Uri.Builder().scheme("upi").authority("pay").appendQueryParameter("pa", "Maha-e-suvidha@ybl").appendQueryParameter("pn", "Test").appendQueryParameter("mc", "1234").appendQueryParameter("tr", "1234").appendQueryParameter("tn", "test").appendQueryParameter("am", "10").appendQueryParameter("cu", "INR").appendQueryParameter("url", "").build()
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = uri
//            intent.setPackage(GOOGLE_TEZ_PACKAGE_NAME)
//            startActivityForResult(intent, TEZ_REQUEST_CODE)
        }
    }

    fun payUsingUpi(amount: String, upiId: String, name: String, note: String) {
//        val uri = Uri.parse("upi://pay").buildUpon()
//            .appendQueryParameter("pa", upiId)
//            .appendQueryParameter("pn", "Jagtap Patil E Suvidha Kendra")
//            .appendQueryParameter("tn", note)
//            .appendQueryParameter("am", amount)
//            .appendQueryParameter("cu", "INR")
//            .build()
        val transactionId = "TID" + System.currentTimeMillis()
        // Set Parameters for UPI
        val paymentUri = Uri.Builder().apply {
            scheme("upi").authority("pay")
            appendQueryParameter("pa", upiId)
            appendQueryParameter("pn", "bharatpe")
            appendQueryParameter("tid", transactionId)
            appendQueryParameter("mc", "5732")
            appendQueryParameter("tr", transactionId) // Transaction Reference ID
            appendQueryParameter("tn", note)
            appendQueryParameter("am", amount)
            appendQueryParameter("mam", amount)
            appendQueryParameter("cu", "INR")
        }.build()

        val upiPayIntent = Intent(Intent.ACTION_VIEW)
        upiPayIntent.data = paymentUri

        // will always show a dialog to user to choose an app
        val chooser = Intent.createChooser(upiPayIntent, "Pay with")

        // check if intent resolves
        if (null != chooser.resolveActivity(packageManager)) {
//            startActivityForResult(chooser, UPI_PAYMENT)
            resultLauncher.launch(chooser)
        } else {
            Toast.makeText(this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show()
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
//            doSomeOperations()
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            UPI_PAYMENT -> if (RESULT_OK == resultCode || resultCode == 11) {
//                if (data != null) {
//                    val trxt = data.getStringExtra("response")
//                    Log.d("UPI", "onActivityResult: $trxt")
//                    val dataList: ArrayList<String?> = ArrayList()
//                    dataList.add(trxt)
//                    upiPaymentDataOperation(dataList)
//                } else {
//                    Log.d("UPI", "onActivityResult: " + "Return data is null")
//                    val dataList: ArrayList<String> = ArrayList()
//                    dataList.add("nothing")
//                    upiPaymentDataOperation(dataList)
//                }
//            } else {
//                Log.d("UPI", "onActivityResult: " + "Return data is null") //when user simply back without payment
//                val dataList: ArrayList<String> = ArrayList()
//                dataList.add("nothing")
//                upiPaymentDataOperation(dataList)
//            }
//        }
//    }


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
        private const val TEZ_REQUEST_CODE = 123

        private const val GOOGLE_TEZ_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user"
    }
}
