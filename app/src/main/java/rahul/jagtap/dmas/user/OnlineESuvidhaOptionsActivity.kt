package rahul.jagtap.dmas.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.*
import rahul.jagtap.dmas.databinding.ActivityOnlineESuvidhaOptionsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.ImageDetails
import rahul.jagtap.dmas.utils.EsuvidhaCache
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class OnlineESuvidhaOptionsActivity : BaseActivity() {
    private val TAG = OnlineESuvidhaOptionsActivity::class.java.simpleName
    lateinit var binding: ActivityOnlineESuvidhaOptionsBinding
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
    var farmer_policies_suchna = "" //
    var all_other_docs_suchna = ""
    var demat_accounts_suchna = "" //
    var govt_schemes_suchna = "" //
    var typesMap: HashMap<String, HashMap<String, HashMap<String, String>>>? = null
    private var renderedFromCache = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnlineESuvidhaOptionsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        renderFromCache()
        fetchDynamicTypes()
        binding.toolbarLayout.toolbarTitle?.text = "महा ई सुविधा"
        binding.btnContact?.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://wa.me/919552789899?text")))
        }
        binding.btnPanCard?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("pan_cards"))
            startActivity(Intent(mContext, PanCardActivity::class.java).putExtra("suchna", pan_cards_suchna).putExtras(bundle))
        }
        binding.btnShopAct?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("shop_acts"))
            startActivity(Intent(mContext, ShopActActivity::class.java).putExtra("suchna", shop_acts_suchna).putExtras(bundle))
        }
        binding.btnUdyamAadhar?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("udyam_aadhar"))
            startActivity(Intent(mContext, UdyamAadharActivity::class.java).putExtra("suchna", udyam_aadhar_suchna).putExtras(bundle))
        }
        binding.btnFoodLicense?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("food_license"))
            startActivity(Intent(mContext, FoodLicenseActivity::class.java).putExtra("suchna", food_license_suchna).putExtras(bundle))
        }
        binding.btnProvidentFund?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("provident_fund"))
            startActivity(Intent(mContext, ProvidentFundActivity::class.java).putExtra("suchna", provident_fund_suchna).putExtras(bundle))
        }
        binding.btnManualAadharPanCard?.setOnClickListener {
            startActivity(Intent(mContext, ManualAadharPanActivity::class.java))
        }
        binding.btnNepalMoneyTransfer?.setOnClickListener {
            startActivity(Intent(mContext, NepalMoneyTransferActivity::class.java).putExtra("suchna", nepal_money_transfer_suchna))
        }
        binding.btnRailwayTicketBooking?.setOnClickListener {
            startActivity(Intent(mContext, RailwayTicketBookingActivity::class.java).putExtra("suchna", railway_ticket_booking_suchna))
        }
        binding.btnPay?.setOnClickListener {
            payUsingUpi("100", "9552064906@upi", "Mayur Gangurde", "Payment")
        }
        binding.btnBusinessPanCard?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("business_pan_cards"))
            startActivity(Intent(mContext, BusinessPanCardActivity::class.java).putExtra("suchna", business_pan_cards_suchna).putExtras(bundle))
        }
        binding.tvElectionCard?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("election_cards"))
            startActivity(Intent(mContext, ElectionCardActivity::class.java).putExtra("suchna", election_cards_suchna).putExtras(bundle))
        }
        binding.tvCibilReport?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("cibil_reports"))
            startActivity(Intent(mContext, CibilReportActivity::class.java).putExtra("suchna", cibil_reports_suchna).putExtras(bundle))
        }
        binding.tvDrivingLearningLicence?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("driving_learning_licenses"))
            startActivity(Intent(mContext, DrivingLearningLicenseActivity::class.java).putExtra("suchna", driving_learning_licenses_suchna).putExtras(bundle))
        }
        binding.tvPoliceVerification?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("other_new_schemes"))
            startActivity(Intent(mContext, PoliceVerificationActivity::class.java).putExtra("suchna", police_verifications_suchna).putExtras(bundle))
        }
//        tvIncomeCertificate?.setOnClickListener {
//            startActivity(Intent(mContext, IncomeCertificateActivity::class.java))
//        }
//        tvAgeNationality?.setOnClickListener {
//            startActivity(Intent(mContext, AgeCertificateActivity::class.java))
//        }
        binding.tvGstReg?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("gst_regs"))
            startActivity(Intent(mContext, GstRegistrationActivity::class.java).putExtra("suchna", gst_regs_suchna).putExtras(bundle))
        }
        binding.tvAadharCardUpdate?.setOnClickListener {
            startActivity(Intent(mContext, AadharCardUpdateActivity::class.java).putExtra("suchna", aadhar_card_update_suchna))
        }
        binding.tvEditPanOrAadharCard?.setOnClickListener {
            startActivity(Intent(mContext, EditPanOrAadharCardActivity::class.java).putExtra("suchna", edit_pan_aadhar_cards_suchna))
        }
        binding.tvPassport?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("passports"))
            startActivity(Intent(mContext, PassportActivity::class.java).putExtra("suchna", passports_suchna).putExtras(bundle))
        }
        binding.tvJanmaKundli?.setOnClickListener {
            startActivity(Intent(mContext, JanmaKundliActivity::class.java).putExtra("suchna", achuk_janma_kundli_suchna))
        }
        binding.tvAadharPanLink?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("tax_agent_work"))
            startActivity(Intent(mContext, CreditCardActivity::class.java).putExtra("suchna", free_credit_cards_suchna).putExtras(bundle))
//            startActivity(Intent(mContext, AadharPanLinkActivity::class.java).putExtra("suchna", aadhar_pan_link_suchna))
        }
        binding.tvGazzet?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("gazzets"))
            startActivity(Intent(mContext, GazzetActivity::class.java).putExtra("suchna", gazzets_suchna).putExtras(bundle))
        }
        binding.tvFreeCreditCard?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("tax_agent_work"))
            startActivity(Intent(mContext, CreditCardActivity::class.java).putExtra("suchna", free_credit_cards_suchna).putExtras(bundle))
        }
        binding.btnTalukaSetuSuvidha?.setOnClickListener {
            startActivity(Intent(mContext, ShriGondaSetuKendraActivity::class.java))
        }
        binding.tvAllGovtCards?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("all_govt_cards"))
            startActivity(Intent(mContext, AllGovtCardsActivity::class.java).putExtra("suchna", all_govt_cards_suchna).putExtras(bundle))
        }
        binding.tvFarmerPolicy?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("farmer_policies"))
            startActivity(Intent(mContext, FarmerPolicyActivity::class.java).putExtra("suchna", farmer_policies_suchna).putExtras(bundle))
        }
        binding.tvDematAccount?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("primary_school_work"))
            startActivity(Intent(mContext, DematAccountActivity::class.java).putExtra("suchna", demat_accounts_suchna).putExtras(bundle))
        }
        binding.tvGovtScheme?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("govt_schemes"))
            startActivity(Intent(mContext, GovtSchemesActivity::class.java).putExtra("suchna", govt_schemes_suchna).putExtras(bundle))
        }
        binding.tvProjectReport?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("tax_agent_work"))
            startActivity(Intent(mContext, CreditCardActivity::class.java).putExtra("suchna", free_credit_cards_suchna).putExtras(bundle))
        }
        binding.tvItrReturn?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("tax_agent_work"))
            startActivity(Intent(mContext, CreditCardActivity::class.java).putExtra("suchna", free_credit_cards_suchna).putExtras(bundle))
        }
        binding.tvTdsReturn?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("tax_agent_work"))
            startActivity(Intent(mContext, CreditCardActivity::class.java).putExtra("suchna", free_credit_cards_suchna).putExtras(bundle))
        }
        binding.tvGstReturn?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("tax_agent_work"))
            startActivity(Intent(mContext, CreditCardActivity::class.java).putExtra("suchna", free_credit_cards_suchna).putExtras(bundle))
        }
        binding.tvPrimarySchoolWork?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("primary_school_work"))
            startActivity(Intent(mContext, DematAccountActivity::class.java).putExtra("suchna", demat_accounts_suchna).putExtras(bundle))
        }
        binding.tvSelfHelpGroupWork?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("hashMap", typesMap?.get("primary_school_work"))
            startActivity(Intent(mContext, DematAccountActivity::class.java).putExtra("suchna", demat_accounts_suchna).putExtras(bundle))
        }
        setESuvidhaImage()
    }

    internal val UPI_PAYMENT = 0

    fun payUsingUpi(amount: String, upiId: String, name: String, note: String) {

        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", name)
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR")
            .build()


        val upiPayIntent = Intent(Intent.ACTION_VIEW)
        upiPayIntent.data = uri

        // will always show a dialog to user to choose an app
        val chooser = Intent.createChooser(upiPayIntent, "Pay with")

        // check if intent resolves
        if (null != chooser.resolveActivity(packageManager)) {
            startActivityForResult(chooser, UPI_PAYMENT)
        } else {
            Toast.makeText(this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setESuvidhaImage() {
        database.child(Utils.ESUVIDHA_LIST_IMAGE_TABLE).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val imageDetails = dataSnapshot.getValue(ImageDetails::class.java)
                if (imageDetails != null) {
                    if (!TextUtils.isEmpty(imageDetails.imageDownloadUrl)) mContext?.let { Glide.with(it).load(imageDetails.imageDownloadUrl).into(binding.imageView1) }
                }
            }
        })
    }

    /** Paint instantly from the last cached snapshot so the buttons never sit hidden behind two calls. */
    private fun renderFromCache() {
        val prefs = app?.preferences
        val cachedTypes = EsuvidhaCache.getDynamicTypes(prefs)
        val cachedSuchna = EsuvidhaCache.getSuchna(prefs)
        if (cachedTypes != null || cachedSuchna != null) {
            renderedFromCache = true
            typesMap = cachedTypes
            applySuchna(cachedSuchna)
            binding.llButtons.visible()
        }
    }

    private fun fetchDynamicTypes() {
        if (!renderedFromCache) binding.progressBar?.visible()
        app?.apiRequestHelper?.apiService?.esuvidhaDynamicTypes?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                binding.progressBar?.gone()
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (json == null || json == "null") {
                        return
                    }
                    val type: Type = object : TypeToken<HashMap<String, HashMap<String, HashMap<String, String>>>?>() {}.type
                    typesMap = Gson().fromJson(json, type)
                    EsuvidhaCache.saveDynamicTypesJson(app?.preferences, json)
                }
                fetchSuchna()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.progressBar?.gone()
                Log.e("in", "failure")
                fetchSuchna()
            }
        })
    }

    private fun fetchSuchna() {
        if (!renderedFromCache) binding.progressBar?.visible()
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
                    EsuvidhaCache.saveSuchnaJson(app?.preferences, json)
                    applySuchna(map)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.progressBar?.gone()
                binding.llButtons.visible()
                Log.e("in", "failure")
            }
        })
    }

    /** Fan the suchna map out into the per-service notice fields used when launching each form. */
    private fun applySuchna(map: HashMap<String, String>?) {
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
                        farmer_policies_suchna = map[Utils.FARMER_POLICIES_SUCHNA] ?: ""
                        all_other_docs_suchna = map[Utils.ALL_OTHER_DOCS_SUCHNA] ?: ""
                        demat_accounts_suchna = map[Utils.DEMAT_ACCOUNTS_SUCHNA] ?: ""
                        govt_schemes_suchna = map[Utils.GOVT_SCHEMES_SUCHNA] ?: ""
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

    companion object {
    }

    fun openWhatsapp(view: View) {
        startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse("https://wa.me/919552789899?text")))
    }
}
