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
import rahul.jagtap.dmas.EditSuchnaActivity
import rahul.jagtap.dmas.databinding.ActivityEsuvidhaSuchnaOptionsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type


class ESuvidhaSuchnaOptionsActivity : BaseActivity() {
    private lateinit var binding: ActivityEsuvidhaSuchnaOptionsBinding
    var pan_cards_suchna = "" //
    var shop_acts_suchna = "" //
    var udyam_aadhar_suchna = "" //
    var food_license_suchna = "" //
    var provident_fund_suchna = "" //
    var nepal_money_transfer_suchna = "" //
    var railway_ticket_booking_suchna = "" //
    var business_pan_cards_suchna = "" //
    var election_cards_suchna = "" //
    var passports_suchna = "" //
    var driving_learning_licenses_suchna = "" //
    var aadhar_card_update_suchna = "" //
    var edit_pan_aadhar_cards_suchna = "" //
    var achuk_janma_kundli_suchna = "" //
    var aadhar_pan_link_suchna = "" //
    var police_verifications_suchna = "" //
    var income_certificates_suchna = ""
    var age_certificates_suchna = ""
    var gazzets_suchna = "" //
    var jyotish_shastra_suchna = ""
    var cibil_reports_suchna = "" //
    var free_credit_cards_suchna = "" //
    var gst_regs_suchna = "" //
    var all_govt_cards_suchna = "" //
    var verification_suchna = ""
    var farmer_policies_suchna = "" //
    var all_other_docs_suchna = ""
    var demat_accounts_suchna = "" //
    var govt_schemes_suchna = "" //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEsuvidhaSuchnaOptionsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "फी - सूचना - सुविधा माहिती"

        binding.btnPanCard?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.PAN_CARDS_SUCHNA , pan_cards_suchna)
            intent.putExtra("type" , Utils.PAN_CARDS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.btnShopAct?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.SHOP_ACTS_SUCHNA , shop_acts_suchna)
            intent.putExtra("type" , Utils.SHOP_ACTS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.btnUdyamAadhar?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.UDYAM_AADHAR_SUCHNA , udyam_aadhar_suchna)
            intent.putExtra("type" , Utils.UDYAM_AADHAR_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.btnFoodLicense?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.FOOD_LICENSE_SUCHNA , food_license_suchna)
            intent.putExtra("type" , Utils.FOOD_LICENSE_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.btnProvidentFund?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.PROVIDENT_FUND_SUCHNA , provident_fund_suchna)
            intent.putExtra("type" , Utils.PROVIDENT_FUND_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.btnNepalMoneyTransfer?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.NEPAL_MONEY_TRANSFER_SUCHNA , nepal_money_transfer_suchna)
            intent.putExtra("type" , Utils.NEPAL_MONEY_TRANSFER_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.btnRailwayTicketBooking?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.RAILWAY_TICKET_BOOKING_SUCHNA , railway_ticket_booking_suchna)
            intent.putExtra("type" , Utils.RAILWAY_TICKET_BOOKING_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.btnBusinessPanCard?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.BUSINESS_PAN_CARDS_SUCHNA , business_pan_cards_suchna)
            intent.putExtra("type" , Utils.BUSINESS_PAN_CARDS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvElectionCard?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.ELECTION_CARDS_SUCHNA , election_cards_suchna)
            intent.putExtra("type" , Utils.ELECTION_CARDS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvCibilReport?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.CIBIL_REPORTS_SUCHNA , cibil_reports_suchna)
            intent.putExtra("type" , Utils.CIBIL_REPORTS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvDrivingLearningLicence?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.DRIVING_LEARNING_LICENSES_SUCHNA , driving_learning_licenses_suchna)
            intent.putExtra("type" , Utils.DRIVING_LEARNING_LICENSES_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvPoliceVerification?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.POLICE_VERIFICATIONS_SUCHNA , police_verifications_suchna)
            intent.putExtra("type" , Utils.POLICE_VERIFICATIONS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvAadharCardUpdate?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.AADHAR_CARD_UPDATE_SUCHNA , aadhar_card_update_suchna)
            intent.putExtra("type" , Utils.AADHAR_CARD_UPDATE_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvEditPanOrAadharCard?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.EDIT_PAN_AADHAR_CARDS_SUCHNA , edit_pan_aadhar_cards_suchna)
            intent.putExtra("type" , Utils.EDIT_PAN_AADHAR_CARDS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvPassport?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.PASSPORTS_SUCHNA , passports_suchna)
            intent.putExtra("type" , Utils.PASSPORTS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvJanmaKundli?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.ACHUK_JANMA_KUNDLI_SUCHNA , achuk_janma_kundli_suchna)
            intent.putExtra("type" , Utils.ACHUK_JANMA_KUNDLI_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvAadharPanLink?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.AADHAR_PAN_LINK_SUCHNA , aadhar_pan_link_suchna)
            intent.putExtra("type" , Utils.AADHAR_PAN_LINK_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvGazzet?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.GAZZETS_SUCHNA , gazzets_suchna)
            intent.putExtra("type" , Utils.GAZZETS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvFreeCreditCard?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.FREE_CREDIT_CARDS_SUCHNA , free_credit_cards_suchna)
            intent.putExtra("type" , Utils.FREE_CREDIT_CARDS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvAllGovtCards?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.ALL_GOVT_CARDS_SUCHNA , all_govt_cards_suchna)
            intent.putExtra("type" , Utils.ALL_GOVT_CARDS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvVerification?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.VERIFICATION_SUCHNA , verification_suchna)
            intent.putExtra("type" , Utils.VERIFICATION_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvFarmerPolicy?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.FARMER_POLICIES_SUCHNA , farmer_policies_suchna)
            intent.putExtra("type" , Utils.FARMER_POLICIES_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvGstReg?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.GST_REGS_SUCHNA , gst_regs_suchna)
            intent.putExtra("type" , Utils.GST_REGS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvDematAccount?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.DEMAT_ACCOUNTS_SUCHNA , demat_accounts_suchna)
            intent.putExtra("type" , Utils.DEMAT_ACCOUNTS_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvGovtScheme?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.GOVT_SCHEMES_SUCHNA , govt_schemes_suchna)
            intent.putExtra("type" , Utils.GOVT_SCHEMES_SUCHNA)
            resultLauncher.launch(intent)
        }
        binding.tvJyotishShastra?.setOnClickListener {
            val intent = Intent(mContext, EditSuchnaActivity::class.java)
            intent.putExtra(Utils.JYOTISH_SHASTRA_SUCHNA , jyotish_shastra_suchna)
            intent.putExtra("type" , Utils.JYOTISH_SHASTRA_SUCHNA)
            resultLauncher.launch(intent)
        }

        fetchSuchna()
    }

    private fun fetchSuchna() {
        binding.progressBar?.visible()
        app?.apiRequestHelper?.apiService?.suchna?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                binding.progressBar?.gone()
                binding.llButtons.visible()
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (json == null || json == "null") {
                        return
                    }
                    val type: Type = object : TypeToken<HashMap<String, String>?>() {}.type
                    val map: HashMap<String, String>? = Gson().fromJson(json, type)
                    val values = map?.values
                    if (!values.isNullOrEmpty()) {
                        pan_cards_suchna = map[Utils.PAN_CARDS_SUCHNA] ?: ""
                        shop_acts_suchna = map[Utils.SHOP_ACTS_SUCHNA] ?: ""
                        udyam_aadhar_suchna = map[Utils.UDYAM_AADHAR_SUCHNA] ?: ""
                        food_license_suchna = map[Utils.FOOD_LICENSE_SUCHNA] ?: ""
                        provident_fund_suchna = map[Utils.PROVIDENT_FUND_SUCHNA] ?: ""
                        nepal_money_transfer_suchna = map[Utils.NEPAL_MONEY_TRANSFER_SUCHNA] ?: ""
                        railway_ticket_booking_suchna = map[Utils.RAILWAY_TICKET_BOOKING_SUCHNA] ?: ""
                        business_pan_cards_suchna = map[Utils.BUSINESS_PAN_CARDS_SUCHNA] ?: ""
                        election_cards_suchna = map[Utils.ELECTION_CARDS_SUCHNA] ?: ""
                        passports_suchna = map[Utils.PASSPORTS_SUCHNA] ?: ""
                        driving_learning_licenses_suchna = map[Utils.DRIVING_LEARNING_LICENSES_SUCHNA] ?: ""
                        aadhar_card_update_suchna = map[Utils.AADHAR_CARD_UPDATE_SUCHNA] ?: ""
                        edit_pan_aadhar_cards_suchna = map[Utils.EDIT_PAN_AADHAR_CARDS_SUCHNA] ?: ""
                        achuk_janma_kundli_suchna = map[Utils.ACHUK_JANMA_KUNDLI_SUCHNA] ?: ""
                        aadhar_pan_link_suchna = map[Utils.AADHAR_PAN_LINK_SUCHNA] ?: ""
                        police_verifications_suchna = map[Utils.POLICE_VERIFICATIONS_SUCHNA] ?: ""
                        income_certificates_suchna = map[Utils.INCOME_CERTIFICATES_SUCHNA] ?: ""
                        age_certificates_suchna = map[Utils.AGE_CERTIFICATES_SUCHNA] ?: ""
                        gazzets_suchna = map[Utils.GAZZETS_SUCHNA] ?: ""
                        jyotish_shastra_suchna = map[Utils.JYOTISH_SHASTRA_SUCHNA] ?: ""
                        cibil_reports_suchna = map[Utils.CIBIL_REPORTS_SUCHNA] ?: ""
                        free_credit_cards_suchna = map[Utils.FREE_CREDIT_CARDS_SUCHNA] ?: ""
                        gst_regs_suchna = map[Utils.GST_REGS_SUCHNA] ?: ""
                        all_govt_cards_suchna = map[Utils.ALL_GOVT_CARDS_SUCHNA] ?: ""
                        verification_suchna = map[Utils.VERIFICATION_SUCHNA] ?: ""
                        farmer_policies_suchna = map[Utils.FARMER_POLICIES_SUCHNA] ?: ""
                        all_other_docs_suchna = map[Utils.ALL_OTHER_DOCS_SUCHNA] ?: ""
                        demat_accounts_suchna = map[Utils.DEMAT_ACCOUNTS_SUCHNA] ?: ""
                        govt_schemes_suchna = map[Utils.GOVT_SCHEMES_SUCHNA] ?: ""
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.progressBar?.gone()
                Log.e("in", "failure")
            }
        })
    }

    private fun isAdminOrEmployee(): Boolean = app?.preferences?.loggedInUser?.isAdmin == "1" || app?.preferences?.loggedInUser?.userType == "2"

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
            fetchSuchna()
        }
    }

    companion object {

    }
}
