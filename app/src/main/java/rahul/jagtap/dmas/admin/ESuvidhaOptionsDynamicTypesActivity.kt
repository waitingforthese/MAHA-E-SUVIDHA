package rahul.jagtap.dmas.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.databinding.ActivityEsuvidhaOptionsDynamicTypesBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type


class ESuvidhaOptionsDynamicTypesActivity : BaseActivity() {
    private lateinit var binding: ActivityEsuvidhaOptionsDynamicTypesBinding
    var typesMap: HashMap<String, HashMap<String, HashMap<String, String>>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEsuvidhaOptionsDynamicTypesBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "सुविधा प्रकार बदल"

        binding.btnPanCard?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("pan_cards"), "Pan Card Types", "pan_cards")
        }
        binding.btnShopAct?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("shop_acts"), "Shop Act Types", "shop_acts")
        }
        binding.btnUdyamAadhar?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("udyam_aadhar"), "Udyam Aadhar Types", "udyam_aadhar")
        }
        binding.btnFoodLicense?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("food_license"), "Food License Types", "food_license")
        }
        binding.btnProvidentFund?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("provident_fund"), "Provident Fund Types", "provident_fund")
        }
        binding.btnBusinessPanCard?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("business_pan_cards"), "Business Pan Card Types", "business_pan_cards")
        }
        binding.tvElectionCard?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("election_cards"), "Election Card Types", "election_cards")
        }
        binding.tvCibilReport?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("cibil_reports"), "Cibil Report Types", "cibil_reports")
        }
        binding.tvDrivingLearningLicence?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("driving_learning_licenses"), "Driving Learning License Types", "driving_learning_licenses")
        }
        binding.tvPoliceVerification?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("other_new_schemes"), "रजिस्ट्रेशन / नोंदणी", "other_new_schemes")
        }
        binding.tvPassport?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("passports"), "Passport Types", "passports")
        }
        binding.tvGazzet?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("gazzets"), "Gazzet Types", "gazzets")
        }
        binding.tvAllGovtCards?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("all_govt_cards"), "All Govt Card Types", "all_govt_cards")
        }
        binding.tvVerification?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("verification"), "Verification Types", "verification")
        }
        binding.tvFarmerPolicy?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("farmer_policies"), "Farmer Policy Types", "farmer_policies")
        }
        binding.tvGstReg?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("gst_regs"), "GST Reg Types", "gst_regs")
        }
        binding.tvGovtScheme?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("govt_schemes"), "New Govt Scheme Types", "govt_schemes")
        }
        binding.tvJyotishShastra?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("jyotish_shastra"), "ज्योतिष शास्त्रींना प्रश्न विचारा Types", "jyotish_shastra")
        }
        binding.tvTaxAgentWork?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("tax_agent_work"), "टॅक्स एजंट ची कामे", "tax_agent_work")
        }
        binding.tvPrimarySchoolWork?.setOnClickListener {
            redirectToNextScreen(typesMap?.get("primary_school_work"), "इतर सेवा / सुविधा", "primary_school_work")
        }
        //        binding.tvDematAccount?.setOnClickListener {
        //            redirectToNextScreen(typesMap?.get("udyam_aadhar"), "Udyam Aadhar Types")
        //        }
        //        binding.tvFreeCreditCard?.setOnClickListener {
        //            redirectToNextScreen(typesMap?.get("udyam_aadhar"), "Udyam Aadhar Types")
        //        }
        //        binding.tvJanmaKundli?.setOnClickListener {
        //            redirectToNextScreen(typesMap?.get("udyam_aadhar"), "Udyam Aadhar Types")
        //        }
        //        binding.tvAadharPanLink?.setOnClickListener {
        //            redirectToNextScreen(typesMap?.get("udyam_aadhar"), "Udyam Aadhar Types")
        //        }
        //        binding.tvAadharCardUpdate?.setOnClickListener {
        //            redirectToNextScreen(typesMap?.get("udyam_aadhar"), "Udyam Aadhar Types")
        //        }
        //        binding.tvEditPanOrAadharCard?.setOnClickListener {
        //            redirectToNextScreen(typesMap?.get("udyam_aadhar"), "Udyam Aadhar Types")
        //        }
        //        binding.btnNepalMoneyTransfer?.setOnClickListener {
        //            redirectToNextScreen(typesMap?.get("udyam_aadhar"), "Udyam Aadhar Types")
        //        }
        //        binding.btnRailwayTicketBooking?.setOnClickListener {
        //            redirectToNextScreen(typesMap?.get("udyam_aadhar"), "Udyam Aadhar Types")
        //        }

        fetchDynamicTypes()
    }

    private fun redirectToNextScreen(map: java.util.HashMap<String, HashMap<String, String>>?, typeTitle: String, nodeName: String) {
        val intent = Intent(mContext, EditESuvidhaDynamicTypesActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("hashMap", map)
        bundle.putString("typeTitle", typeTitle)
        bundle.putString("nodeName", nodeName)
        intent.putExtras(bundle)
        resultLauncher.launch(intent)
    }

    private fun fetchDynamicTypes() {
        binding.progressBar?.visible()
        app?.apiRequestHelper?.apiService?.esuvidhaDynamicTypes?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                binding.progressBar?.gone()
                binding.llButtons.visible()
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (json == null || json == "null") {
                        return
                    }
                    val type: Type = object : TypeToken<HashMap<String, HashMap<String, HashMap<String, String>>>?>() {}.type
                    typesMap = Gson().fromJson(json, type)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.progressBar?.gone()
                Log.e("in", "failure")
            }
        })
    }

//    private fun isAdminOrEmployee(): Boolean = app?.preferences?.loggedInUser?.isAdmin == "1" || app?.preferences?.loggedInUser?.userType == "2"

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

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            fetchDynamicTypes()
        }
    }

    companion object {

    }
}
