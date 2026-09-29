package rahul.jagtap.dmas

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import rahul.jagtap.dmas.extensions.copyToClipboard
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.utils.ConnectionDetector
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity : AppCompatActivity(), CoroutineScope {
    var app: App? = null
    var mContext: Context? = null
    var cd: ConnectionDetector? = null
    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var storageRef: StorageReference
    var callList: ArrayList<Call<*>> = ArrayList()
    var screenshotAllowedEmailList = listOf("xrahuljagtap@gmail.com", "mayurdg93@gmail.com", "xrahul.jagtap@gmail.com", "mdg5435@gmail.com")
    val upiAppList = ArrayList<String>()
    val spanText = SpannableString("Note: पेमेंट झाल्यावर पेमेंट चा स्क्रीनशॉट काढा\nपेमेंट नाव - स्नेहा जगताप पाटील\nपेमेंट करण्यासाठी नंबर 9552789899\nपेमेंट साठी नंबर कॉपी करा\nCopy")

    // coroutine
    private var job: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.font_black_0)
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false

        app = application as App
        mContext = this
        cd = ConnectionDetector(this)
        auth = Firebase.auth
        database = Firebase.database.reference
        storageRef = Firebase.storage.reference //Android M Or Over
        askPermissions()
        database.child(Utils.FCM_TOKEN_TABLE).child("admin").get().addOnSuccessListener {
            app?.preferences?.adminToken = it.value.toString()
        }
        upiAppList.add("GPay")
        upiAppList.add("PhonePe")
        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                callPhone("9552789899")
            }
        }, 103, 114, 0)
        spanText.setSpan(ForegroundColorSpan(Color.BLUE), 103, 114, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanText.setSpan(StyleSpan(Typeface.BOLD), 103, 114, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val clickable2 = object : ClickableSpan() {
            override fun onClick(view: View) {
                mContext?.let { "9552789899".copyToClipboard(it) }
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true      // Underline the "Copy" text
                ds.color = Color.BLUE          // Set text color to blue
                ds.isFakeBoldText = true       // Make it bold
            }
        }
        spanText.setSpan(clickable2, 139, 144, 0)
        //        spanText.setSpan(ForegroundColorSpan(Color.BLUE), 74, 78, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE, Manifest.permission.GET_ACCOUNTS)
    fun askPermissions() {
        // Check which permissions are denied
        val deniedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this@BaseActivity, it) == PackageManager.PERMISSION_DENIED
        }.toTypedArray()

        // Request the denied permissions
        if (deniedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this@BaseActivity, deniedPermissions, 1)
        }
    }

    // Function to check and request permission.
    fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@BaseActivity, permission) == PackageManager.PERMISSION_DENIED) { // Requesting the permission
            ActivityCompat.requestPermissions(this@BaseActivity, arrayOf(permission), requestCode)
        } else { //            Toast.makeText(this@BaseActivity, "Permission already granted: $permission", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendNotification(msg: String) { //        val notification = JSONObject()
        //        val notifcationBody = JSONObject()
        //        if (!TextUtils.isEmpty(msg)) {
        //            try {
        //                notifcationBody.put("title", getString(R.string.app_name))
        //                notifcationBody.put("message", msg)
        //                notifcationBody.put("body", msg)
        //                notification.put("to", app?.preferences?.adminToken)
        //                notification.put("data", notifcationBody)
        //                notification.put("notification", notifcationBody)
        //                Log.e("TAG", "try: ${notification.toString()}")
        //            } catch (e: JSONException) {
        //                Log.e("TAG", "onCreate: " + e.message)
        //            }
        //        }
        //        Log.e("TAG", "sendNotification")
        //        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification, Response.Listener<JSONObject> { response ->
        //            Log.e("TAG", "onResponse: $response")
        //
        //        }, Response.ErrorListener {
        //            it.printStackTrace()
        //            Toast.makeText(this@BaseActivity, "Request error", Toast.LENGTH_LONG).show()
        //            Log.e("TAG", "onErrorResponse: Didn't work")
        //        }) {
        //
        //            override fun getHeaders(): Map<String, String> {
        //                val params = HashMap<String, String>()
        //                params["Authorization"] = serverKey
        //                params["Content-Type"] = contentType
        //                return params
        //            }
        //        }
        //        requestQueue.add(jsonObjectRequest)
    }

    fun callOrSms(phoneNo: String) {
        val list = ArrayList<String>()
        list.add("Call")
        list.add("SMS")
        list.add("WhatsApp")
        mContext?.let {
            MaterialDialog.Builder(it).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    when (position) {
                        0 -> callPhone(phoneNo)
                        1 -> sendTextMessage(phoneNo)
                        else -> openWhatsapp(phoneNo)
                    }
                }
            }.show()
        }
    }

    fun openWhatsapp(phoneNo: String) {
        var phoneNumber = phoneNo.replace(" ", "").trim()
        if (phoneNumber.length == 10) {
            phoneNumber = "91$phoneNumber"
        }
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phoneNumber?text")))
    }

    fun callPhone(phoneNo: String) {
        if (mContext?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.CALL_PHONE) } != PackageManager.PERMISSION_GRANTED) { // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            val ok = mContext?.let { MaterialDialog.Builder(it).content("Do you want to call this phone number?").positiveText("Yes").negativeText("No").show() }
            ok?.getActionButton(DialogAction.POSITIVE)?.setOnClickListener {
                ok?.dismiss()
                mContext?.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNo")))
            }
            ok?.getActionButton(DialogAction.NEGATIVE)?.setOnClickListener { ok?.dismiss() }
        }
    }

    private fun sendTextMessage(phoneNo: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:$phoneNo") // This ensures only SMS apps respond
        intent.putExtra("sms_body", "")
        if (intent.resolveActivity(packageManager) != null) {
            try {
                startActivity(intent)
            } catch (anfe: ActivityNotFoundException) {
                toast("No SMS app installed on your device")
            }
        }
    }

    fun payUsingUPI(amount: String?) {
        MaterialDialog.Builder(mContext!!).items(upiAppList).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
            run {
                dialog?.dismiss()
                when (position) {
                    0 -> openUPIAppByPackage("com.google.android.apps.nbu.paisa.user")
                    1 -> openUPIAppByPackage("com.phonepe.app")
                }
            }
        }.show() ////        if (amount.isNullOrEmpty()) {
        ////            Toast.makeText(this, "Amount is required", Toast.LENGTH_SHORT).show()
        ////            return
        ////        }
        //
        //        val uri = Uri.parse("upi://pay").buildUpon()
        //            .appendQueryParameter("pa", "jadhavshraddhag-1@okicici")  // Payee UPI ID
        ////            .appendQueryParameter("pn", "Maha E-Suvidha")   // Payee Name
        ////            .appendQueryParameter("tn", "Payment for services")  // Transaction Note
        //            .appendQueryParameter("am", amount)  // Amount (e.g., "100.00")
        //            .appendQueryParameter("cu", "INR")  // Currency
        //            .build()
        //
        //        val intent = Intent(Intent.ACTION_VIEW).apply {
        //            data = uri
        //            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        //        }
        //
        //        try {
        //            startActivityForResult(intent, 919)
        //        } catch (e: ActivityNotFoundException) {
        //            Toast.makeText(this, "UPI app not found", Toast.LENGTH_SHORT).show()
        //        }
    }

    private fun openUPIAppByPackage(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            Toast.makeText(this, "UPI app is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    fun extractAmountFromType(
        text: String
    ): String? { // Regular expression to find numbers followed by "रुपये" or "Rs"
        val regex = Regex("(\\d+)\\s*(रुपये|₹|Rs|फी|rupees)", RegexOption.IGNORE_CASE)
        val matchResult = regex.find(text)

        return matchResult?.groupValues?.get(1)
    }

    fun launchCoroutine(
        block: suspend () -> Unit, handler: (CoroutineContext, Throwable) -> Unit
    ): Job {
        val exceptionHandler = object : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
            override fun handleException(context: CoroutineContext, exception: Throwable) {
                handler.invoke(context, exception)
            }
        }
        return launch(exceptionHandler) {
            block.invoke()
        }
    }

    fun checkIfActivityDestroying(): Boolean {
        if (isFinishing || isDestroyed) {
            return true;
        }
        return false
    }

    override fun onDestroy() {
        coroutineContext.cancelChildren()
        super.onDestroy()
        if (callList.size > 0) {
            for (call in callList) {
                if (call.isExecuted) call.cancel()
            }
        }
    }
}