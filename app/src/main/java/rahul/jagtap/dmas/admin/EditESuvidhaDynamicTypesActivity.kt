package rahul.jagtap.dmas.admin

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.databinding.ActivityEditEsuvidhaDynamicTypesBinding
import rahul.jagtap.dmas.extensions.copyToClipboard
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.utils.Utils
import androidx.core.widget.doAfterTextChanged

class EditESuvidhaDynamicTypesActivity : BaseActivity() {
    private val TAG = EditESuvidhaDynamicTypesActivity::class.java.simpleName
    var cpd: ProgressDialog? = null
    lateinit var binding: ActivityEditEsuvidhaDynamicTypesBinding
    var strSelectedType: String = ""
    var nodeName: String = ""
    var typeTitle: String = ""
    private var typeMap: HashMap<String, HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEsuvidhaDynamicTypesBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilType, binding.tilSelectedType, binding.tilText, binding.tilEdittext1, binding.tilEdittext2, binding.tilEdittext3, binding.tilEdittext4, binding.tilEdittext5, binding.tilEdittextDate1, binding.tilEdittextPhoto1, binding.tilEdittextPhoto2, binding.tilEdittextPhoto3, binding.tilEdittextPhoto4, binding.tilEdittextPhoto5, binding.tilEdittextPhoto6, binding.tilEdittextPhoto7, binding.tilEdittextPhoto8, binding.tilEdittextPhoto9, binding.tilEdittextPhoto10)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val extras = intent.extras
        nodeName = extras?.getString("nodeName") ?: ""
        typeTitle = extras?.getString("typeTitle") ?: ""
        binding.toolbarLayout.toolbarTitle?.text = typeTitle

        // Safe cast: a service whose esuvidha_dynamic_types node doesn't exist yet passes a null map;
        // fall back to an empty map instead of crashing (the type dropdown will just be empty).
        typeMap = extras?.getSerializable("hashMap") as? HashMap<String, HashMap<String, String>> ?: HashMap()
        binding.etType.setOnClickListener {
            val list = ArrayList<String>()
            typeMap?.forEach {
                list.add(it.key)
            }
            MaterialDialog.Builder(mContext!!).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                run {
                    dialog?.dismiss()
                    strSelectedType = list[position]
                    binding.etType.setText(strSelectedType)
                    binding.etSelectedType.setText(typeMap?.get(list[position])?.get("type_title").toString())
                    setInputDataAndVisibility()
                }
            }.show()
        }
        // Let the admin tap the read-only "Selected Type" field to copy its current text,
        // then long-press any box below to paste it.
        binding.etSelectedType.setOnClickListener {
            val text = binding.etSelectedType.text.toString()
            if (!TextUtils.isEmpty(text)) {
                text.copyToClipboard(mContext!!)
            }
        }
        binding.btnSave.setOnClickListener {
            val strText = binding.etText.text.toString()
            if (TextUtils.isEmpty(strText)) {
                binding.tilText?.error = binding.tilText.hint.toString()
                binding.etText?.requestFocus()
                return@setOnClickListener
            }
            if (typeMap != null && !TextUtils.isEmpty(strSelectedType)) {
                typeMap?.get(strSelectedType)?.set("type_title", strText)
            }
            binding.btnSubmit.isEnabled = true
            toast("Saved")
        }

        binding.btnSubmit?.setOnClickListener {
            setInputDataToMap()
            updateDbRecord()
        }
    }

    private fun setInputDataAndVisibility() {
        when (typeTitle) {
            "Farmer Policy Types" -> {
                binding.tilEdittext1.visible()
                binding.tilEdittext2.visible()
                binding.tilEdittext3.visible()
                binding.tilEdittext4.visible()
                binding.tilEdittext5.visible()
                binding.tilEdittextDate1.visible()
                binding.tilEdittextPhoto1.visible()
                binding.tilEdittextPhoto2.visible()
                binding.tilEdittextPhoto3.visible()
                binding.tilEdittextPhoto4.visible()
                binding.tilEdittextPhoto5.visible()
                binding.tilEdittextPhoto6.visible()
                binding.tilEdittextPhoto7.visible()
                binding.tilEdittextPhoto8.visible()
                binding.tilEdittextPhoto9.visible()
                binding.tilEdittextPhoto10.visible()
                binding.tilEdittext1.hint = "कस्टमरचे नाव"
                binding.tilEdittext2.hint = "आधार लिंक मोबाईल नंबर"
                binding.tilEdittext3.hint = "7/12 लिंक साठी मोबाईल नंबर द्या"
                binding.tilEdittext4.hint = "textbox 4"
                binding.tilEdittext5.hint = "textbox 5"
                binding.tilEdittextDate1.hint = "datebox"
                binding.tilEdittextPhoto1.hint = "आधार कार्ड पुढील फोटो"
                binding.tilEdittextPhoto2.hint = "आधार कार्ड मागील फोटो"
                binding.tilEdittextPhoto3.hint = "जमिनीचा 8 अ फोटो निवडा"
                binding.tilEdittextPhoto4.hint = "पँण कार्ड फोटो निवडा"
                binding.tilEdittextPhoto5.hint = "कस्टमर च्या 8अ चा फोटो"
                binding.tilEdittextPhoto6.hint = "इतर 8अ किव्हा इतर माहिती"
                binding.tilEdittextPhoto7.hint = "कोटेशन/बिल"
                binding.tilEdittextPhoto8.hint = "जातीचा दाखला/सहीचा नमुना"
                binding.tilEdittextPhoto9.hint = "पूर्व संमती पत्र"
                binding.tilEdittextPhoto10.hint = "पेमेंट स्क्रीन शॉट"
                binding.edittext1.setText(typeMap?.get(strSelectedType)?.get("customer_name").toString())
                binding.edittext2.setText(typeMap?.get(strSelectedType)?.get("aadhar_linked_mob").toString())
                binding.edittext3.setText(typeMap?.get(strSelectedType)?.get("7_12_linked_mob").toString())
                binding.edittext4.setText(typeMap?.get(strSelectedType)?.get("textbox4").toString())
                binding.edittext5.setText(typeMap?.get(strSelectedType)?.get("textbox5").toString())
                binding.edittextDate1.setText(typeMap?.get(strSelectedType)?.get("datebox").toString())
                binding.edittextPhoto1.setText(typeMap?.get(strSelectedType)?.get("aadhar_front_photo").toString())
                binding.edittextPhoto2.setText(typeMap?.get(strSelectedType)?.get("aadhar_back_photo").toString())
                binding.edittextPhoto3.setText(typeMap?.get(strSelectedType)?.get("land_8a_photo").toString())
                binding.edittextPhoto4.setText(typeMap?.get(strSelectedType)?.get("pan_photo").toString())
                binding.edittextPhoto5.setText(typeMap?.get(strSelectedType)?.get("customer_8a_photo").toString())
                binding.edittextPhoto6.setText(typeMap?.get(strSelectedType)?.get("other_8a_photo").toString())
                binding.edittextPhoto7.setText(typeMap?.get(strSelectedType)?.get("quotation_photo").toString())
                binding.edittextPhoto8.setText(typeMap?.get(strSelectedType)?.get("cast_cert_photo").toString())
                binding.edittextPhoto9.setText(typeMap?.get(strSelectedType)?.get("consent_letter_photo").toString())
                binding.edittextPhoto10.setText(typeMap?.get(strSelectedType)?.get("payment_sc_photo").toString())
            }
            "रजिस्ट्रेशन / नोंदणी", "टॅक्स एजंट ची कामे", "इतर सेवा / सुविधा", "New Govt Scheme Types", "Pan Card Types", "All Govt Card Types", "Driving Learning License Types", "Verification Types" -> {
                binding.tilEdittext1.visible()
                binding.tilEdittext2.visible()
                binding.tilEdittext3.visible()
                binding.tilEdittext4.visible()
                binding.tilEdittext5.visible()
                binding.tilEdittextDate1.visible()
                binding.tilEdittextPhoto1.visible()
                binding.tilEdittextPhoto2.visible()
                binding.tilEdittextPhoto3.visible()
                binding.tilEdittextPhoto4.visible()
                binding.tilEdittextPhoto5.visible()
                binding.tilEdittextPhoto6.visible()
                binding.tilEdittextPhoto7.visible()
                binding.tilEdittextPhoto8.visible()
                binding.tilEdittextPhoto9.visible()
                binding.tilEdittextPhoto10.visible()
                binding.tilEdittext1.hint = "TEXTBOX 1"
                binding.tilEdittext2.hint = "TEXTBOX 2"
                binding.tilEdittext3.hint = "TEXTBOX 3"
                binding.tilEdittext4.hint = "TEXTBOX 4"
                binding.tilEdittext5.hint = "TEXTBOX 5"
                binding.tilEdittextDate1.hint = "DATEBOX 1"
                binding.tilEdittextPhoto1.hint = "ATTACH 1"
                binding.tilEdittextPhoto2.hint = "ATTACH 2"
                binding.tilEdittextPhoto3.hint = "ATTACH 3"
                binding.tilEdittextPhoto4.hint = "ATTACH 4"
                binding.tilEdittextPhoto5.hint = "ATTACH 5"
                binding.tilEdittextPhoto6.hint = "ATTACH 6"
                binding.tilEdittextPhoto7.hint = "ATTACH 7"
                binding.tilEdittextPhoto8.hint = "ATTACH 8"
                binding.tilEdittextPhoto9.hint = "ATTACH 9"
                binding.tilEdittextPhoto10.hint = "ATTACH 10"
                binding.edittext1.setText(typeMap?.get(strSelectedType)?.get("textbox1").toString())
                binding.edittext2.setText(typeMap?.get(strSelectedType)?.get("textbox2").toString())
                binding.edittext3.setText(typeMap?.get(strSelectedType)?.get("textbox3").toString())
                binding.edittext4.setText(typeMap?.get(strSelectedType)?.get("textbox4").toString())
                binding.edittext5.setText(typeMap?.get(strSelectedType)?.get("textbox5").toString())
                binding.edittextDate1.setText(typeMap?.get(strSelectedType)?.get("datebox").toString())
                binding.edittextPhoto1.setText(typeMap?.get(strSelectedType)?.get("attachment1").toString())
                binding.edittextPhoto2.setText(typeMap?.get(strSelectedType)?.get("attachment2").toString())
                binding.edittextPhoto3.setText(typeMap?.get(strSelectedType)?.get("attachment3").toString())
                binding.edittextPhoto4.setText(typeMap?.get(strSelectedType)?.get("attachment4").toString())
                binding.edittextPhoto5.setText(typeMap?.get(strSelectedType)?.get("attachment5").toString())
                binding.edittextPhoto6.setText(typeMap?.get(strSelectedType)?.get("attachment6").toString())
                binding.edittextPhoto7.setText(typeMap?.get(strSelectedType)?.get("attachment7").toString())
                binding.edittextPhoto8.setText(typeMap?.get(strSelectedType)?.get("attachment8").toString())
                binding.edittextPhoto9.setText(typeMap?.get(strSelectedType)?.get("attachment9").toString())
                binding.edittextPhoto10.setText(typeMap?.get(strSelectedType)?.get("attachment10").toString())
            }
        }
    }

    private fun setInputDataToMap() {
        when (typeTitle) {
            "Farmer Policy Types" -> {
                typeMap?.get(strSelectedType)?.set("type_title", binding.etText.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("customer_name", binding.edittext1.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("aadhar_linked_mob", binding.edittext2.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("7_12_linked_mob", binding.edittext3.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("textbox4", binding.edittext4.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("textbox5", binding.edittext5.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("datebox", binding.edittextDate1.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("aadhar_front_photo", binding.edittextPhoto1.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("aadhar_back_photo", binding.edittextPhoto2.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("land_8a_photo", binding.edittextPhoto3.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("pan_photo", binding.edittextPhoto4.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("customer_8a_photo", binding.edittextPhoto5.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("other_8a_photo", binding.edittextPhoto6.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("quotation_photo", binding.edittextPhoto7.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("cast_cert_photo", binding.edittextPhoto8.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("consent_letter_photo", binding.edittextPhoto9.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("payment_sc_photo", binding.edittextPhoto10.text.toString().trim())
            }
            "रजिस्ट्रेशन / नोंदणी", "टॅक्स एजंट ची कामे", "इतर सेवा / सुविधा", "New Govt Scheme Types", "Pan Card Types", "All Govt Card Types", "Driving Learning License Types", "Verification Types" -> {
                typeMap?.get(strSelectedType)?.set("type_title", binding.etText.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("textbox1", binding.edittext1.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("textbox2", binding.edittext2.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("textbox3", binding.edittext3.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("textbox4", binding.edittext4.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("textbox5", binding.edittext5.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("datebox", binding.edittextDate1.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("attachment1", binding.edittextPhoto1.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("attachment2", binding.edittextPhoto2.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("attachment3", binding.edittextPhoto3.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("attachment4", binding.edittextPhoto4.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("attachment5", binding.edittextPhoto5.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("attachment6", binding.edittextPhoto6.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("attachment7", binding.edittextPhoto7.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("attachment8", binding.edittextPhoto8.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("attachment9", binding.edittextPhoto9.text.toString().trim())
                typeMap?.get(strSelectedType)?.set("attachment10", binding.edittextPhoto10.text.toString().trim())
            }
        }
    }

    private fun updateDbRecord() {
//        Log.e(TAG, "updateDbRecord: " + typeMap.toString())
//        return
        if (!TextUtils.isEmpty(nodeName) && typeMap != null) {
            cpd = ProgressDialog(mContext)
            cpd?.setCancelable(false)
            cpd?.show()
            database.child(Utils.ESUVIDHA_DYNAMIC_TYPES_TABLE).child(nodeName).setValue(typeMap).addOnSuccessListener { // Write was successful!
                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
                toast("Types updated successfully")
                setResult(RESULT_OK)
                Utils.hideSoftKeyboard(this)
                finish()
            }.addOnFailureListener { // Write failed
                if (cpd != null && cpd?.isShowing == true) cpd?.dismiss()
                toast("Failed to update types")
            }
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

    companion object {}
}
