package rahul.jagtap.dmas.admin.esuvidha.newimpl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.JsonStorage
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.ESuvidhaDateListAdapter
import rahul.jagtap.dmas.admin.esuvidha.ESuvidhaDatesActivity
import rahul.jagtap.dmas.databinding.ActivityEsuvidhaTypesBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.isVisible
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.AgeCertificate
import rahul.jagtap.dmas.model.AllGovtCard
import rahul.jagtap.dmas.model.AllOtherCard
import rahul.jagtap.dmas.model.BusinessPanCard
import rahul.jagtap.dmas.model.CibilReport
import rahul.jagtap.dmas.model.CreditCard
import rahul.jagtap.dmas.model.DematAccount
import rahul.jagtap.dmas.model.DrivingLearningLicense
import rahul.jagtap.dmas.model.Verification
import rahul.jagtap.dmas.model.ESuvidhaData
import rahul.jagtap.dmas.model.ESuvidhaType
import rahul.jagtap.dmas.model.EditPanAadharCard
import rahul.jagtap.dmas.model.ElectionCard
import rahul.jagtap.dmas.model.EsuvidhaInfo
import rahul.jagtap.dmas.model.FarmerPolicy
import rahul.jagtap.dmas.model.FoodLicense
import rahul.jagtap.dmas.model.Gazzet
import rahul.jagtap.dmas.model.GovtScheme
import rahul.jagtap.dmas.model.GstRegistration
import rahul.jagtap.dmas.model.IncomeCertificate
import rahul.jagtap.dmas.model.JyotishShastra
import rahul.jagtap.dmas.model.NepalMoneyTransfer
import rahul.jagtap.dmas.model.PanCard
import rahul.jagtap.dmas.model.Passport
import rahul.jagtap.dmas.model.PoliceVerification
import rahul.jagtap.dmas.model.ProvidentFund
import rahul.jagtap.dmas.model.RailwayTicketBooking
import rahul.jagtap.dmas.model.ShopAct
import rahul.jagtap.dmas.model.UdyamAadhar
import rahul.jagtap.dmas.model.UserUIDInfo
import rahul.jagtap.dmas.utils.Utils
import java.lang.reflect.Type
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ESuvidhaListActivity : BaseActivity() {
    private var eSuvidhaData: ESuvidhaData? = null
    var adapter: ESuvidhaDateListAdapter? = null
    var panCards = ArrayList<EsuvidhaInfo>()
    var shopActs = ArrayList<EsuvidhaInfo>()
    var udyamAadhars = ArrayList<EsuvidhaInfo>()
    var foodLicenses = ArrayList<EsuvidhaInfo>()
    var providentFunds = ArrayList<EsuvidhaInfo>()
    var nepalMoneyTransfers = ArrayList<EsuvidhaInfo>()
    var railwayTicketBookings = ArrayList<EsuvidhaInfo>()
    var businessPanCards = ArrayList<EsuvidhaInfo>()
    var electionCards = ArrayList<EsuvidhaInfo>()
    var passports = ArrayList<EsuvidhaInfo>()
    var driving_learning_licenses = ArrayList<EsuvidhaInfo>()
    var verification = ArrayList<EsuvidhaInfo>()
    var edit_pan_aadhar_cards = ArrayList<EsuvidhaInfo>()
    var police_verifications = ArrayList<EsuvidhaInfo>()
    var income_certificates = ArrayList<EsuvidhaInfo>()
    var age_certificates = ArrayList<EsuvidhaInfo>()
    var gazzets = ArrayList<EsuvidhaInfo>()
    var jyotish_shastra = ArrayList<EsuvidhaInfo>()
    var cibil_reports = ArrayList<EsuvidhaInfo>()
    var credit_cards = ArrayList<EsuvidhaInfo>()
    var gst_regs = ArrayList<EsuvidhaInfo>()
    var all_govt_cards = ArrayList<EsuvidhaInfo>()
    var farmer_policies = ArrayList<EsuvidhaInfo>()
    var all_other_cards = ArrayList<EsuvidhaInfo>()
    var demat_accounts = ArrayList<EsuvidhaInfo>()
    var govt_schemes = ArrayList<EsuvidhaInfo>()
    var gson = Gson()
    private lateinit var binding: ActivityEsuvidhaTypesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEsuvidhaTypesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_online_e_suvidha)

        binding.tvPanCard.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(panCards))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_PAN_CARD)) //                .putExtra("json_data", Gson().toJson(panCards)))
        }
        binding.tvShopAct.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(shopActs))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_SHOP_ACT)) //                .putExtra("json_data", gson.toJson(shopActs)))
        }
        binding.tvUdyamAadhar.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(udyamAadhars))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_UDYAM_AADHAR)) //                .putExtra("json_data", gson.toJson(udyamAadhars)))
        }
        binding.tvFoodLicense.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(foodLicenses))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_FOOD_LICENSE)) //                .putExtra("json_data", gson.toJson(foodLicenses)))
        }
        binding.tvProvidentFund.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(providentFunds))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_PF)) //                .putExtra("json_data", gson.toJson(providentFunds)))
        }
        binding.tvNepalMoneyTransfer.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(nepalMoneyTransfers))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_NEPAL_MT)) //                .putExtra("json_data", gson.toJson(nepalMoneyTransfers)))
        }
        binding.tvRailwayTicketBooking.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(railwayTicketBookings))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_RAIL_TB)) //                .putExtra("json_data", gson.toJson(railwayTicketBookings)))
        }
        binding.tvBusinessPanCard.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(businessPanCards))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_BUSINESS_PAN)) //                .putExtra("json_data", gson.toJson(businessPanCards)))
        }
        binding.tvElectionCard.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(electionCards))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_ELECTION_CARD)) //                .putExtra("json_data", gson.toJson(electionCards)))
        }
        binding.tvPassport.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(passports))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_PASSPORT)) //                .putExtra("json_data", gson.toJson(passports)))
        }
        binding.tvDrivingLearningLicence.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(driving_learning_licenses))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_DRIVING_LICENSE)) //                .putExtra("json_data", gson.toJson(driving_learning_licenses)))
        }
        binding.tvVerification.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(verification))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_VERIFICATION))
        }
        binding.tvEditPanOrAadharCard.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(edit_pan_aadhar_cards))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_EDIT_PAN_AADHAR)) //                .putExtra("json_data", gson.toJson(edit_pan_aadhar_cards)))
        }
        binding.tvPoliceVerification.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(police_verifications))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_POLICE_VERIF)) //                .putExtra("json_data", gson.toJson(police_verifications)))
        }
        binding.tvIncomeCertificate.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(income_certificates))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_INCOME_CERT)) //                .putExtra("json_data", gson.toJson(income_certificates)))
        }
        binding.tvAgeCertificate.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(age_certificates))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_AGE_CERT)) //                .putExtra("json_data", gson.toJson(age_certificates)))
        }
        binding.tvGazzet.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(gazzets))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_GAZZET)) //                .putExtra("json_data", gson.toJson(gazzets)))
        }
        binding.tvJyotishShastra.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(jyotish_shastra))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_JYOTISH)) //                .putExtra("json_data", gson.toJson(jyotish_shastra)))
        }
        binding.tvCibilReport.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(cibil_reports))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_CIBIL_REPORT)) //                .putExtra("json_data", gson.toJson(cibil_reports)))
        }
        binding.tvCreditCard.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(credit_cards))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_CREDIT_CARD)) //                .putExtra("json_data", gson.toJson(credit_cards)))
        }
        binding.tvGstReg.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(gst_regs))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_GST_REG)) //                .putExtra("json_data", gson.toJson(gst_regs)))
        }
        binding.tvAllGovtCards.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(all_govt_cards))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_ALL_GOV_CARDS)) //                .putExtra("json_data", gson.toJson(all_govt_cards)))
        }
        binding.tvFarmerPolicy.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(farmer_policies))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_FARMER_POLICY)) //                .putExtra("json_data", gson.toJson(farmer_policies)))
        }
        binding.tvAllOtherDocs.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(all_other_cards))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_ALL_OTHER_DOCS)) //                .putExtra("json_data", gson.toJson(all_other_cards)))
        }
        binding.tvDematAccount.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(demat_accounts))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_DEMAT_ACC)) //                .putExtra("json_data", gson.toJson(demat_accounts)))
        }
        binding.tvGovtScheme.setOnClickListener {
            JsonStorage.setJsonString(Gson().toJson(govt_schemes))
            startActivity(Intent(mContext, ESuvidhaDatesV2Activity::class.java).putExtra("type", TYPE_GOVT_SCHEME))
        }
        setAdminBillsData()
    }

    private fun setAdminBillsData() {
        binding.progressBar?.visible()
        launchCoroutine({
            database.child(Utils.ESUVIDHA_TABLE).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isFinishing) return
                    binding.progressBar?.gone()
                    Log.i("firebase", "Got value ${snapshot.value}") // Get user value
                    val json = Gson().toJson(snapshot.value)
                    eSuvidhaData = ESuvidhaData()
                    val type: Type = object : TypeToken<HashMap<String, HashMap<String, ESuvidhaType>>?>() {}.type // date, <uid, ESuvidhaType>
                    val map: HashMap<String, HashMap<String, ESuvidhaType>> = Gson().fromJson(json, type)
                    val dates = map.keys.filterIndexed { index, s ->
                        true
                    } //                    val dateList: ArrayList<ESuvidhaDate> = ArrayList()
                    dates.forEach { date ->
                        val hashMap = map[date] //                        val strEsuvidhaTypes = StringBuilder()
                        val panUUIDInfoList = ArrayList<UserUIDInfo>()
                        val shopActUUIDInfoList = ArrayList<UserUIDInfo>()
                        val udyamAadharUUIDInfoList = ArrayList<UserUIDInfo>()
                        val foodLicenseUUIDInfoList = ArrayList<UserUIDInfo>()
                        val providentFundUUIDInfoList = ArrayList<UserUIDInfo>()
                        val nepalMoneyTransferUUIDInfoList = ArrayList<UserUIDInfo>()
                        val railwayTicketBookingUUIDInfoList = ArrayList<UserUIDInfo>()
                        val businessPanCardUUIDInfoList = ArrayList<UserUIDInfo>()
                        val electionCardUUIDInfoList = ArrayList<UserUIDInfo>()
                        val passportUUIDInfoList = ArrayList<UserUIDInfo>()
                        val drivingLearningLicenseUUIDInfoList = ArrayList<UserUIDInfo>()
                        val verificationUUIDInfoList = ArrayList<UserUIDInfo>()
                        val editPanAadharCardUUIDInfoList = ArrayList<UserUIDInfo>()
                        val policeVerificationUUIDInfoList = ArrayList<UserUIDInfo>()
                        val incomeCertificateUUIDInfoList = ArrayList<UserUIDInfo>()
                        val ageCertificateUUIDInfoList = ArrayList<UserUIDInfo>()
                        val gazzetUUIDInfoList = ArrayList<UserUIDInfo>()
                        val jyotishShastraUUIDInfoList = ArrayList<UserUIDInfo>()
                        val cibilReportUUIDInfoList = ArrayList<UserUIDInfo>()
                        val creditCardUUIDInfoList = ArrayList<UserUIDInfo>()
                        val gstRegistrationUUIDInfoList = ArrayList<UserUIDInfo>()
                        val allGovtCardUUIDInfoList = ArrayList<UserUIDInfo>()
                        val farmerPolicyUUIDInfoList = ArrayList<UserUIDInfo>()
                        val allOtherCardUUIDInfoList = ArrayList<UserUIDInfo>()
                        val dematAccountUUIDInfoList = ArrayList<UserUIDInfo>()
                        val govtSchemeUUIDInfoList = ArrayList<UserUIDInfo>()
                        hashMap?.keys?.map { uid ->
                            if (hashMap[uid]?.panCards != null) {
                                val panCardList: ArrayList<PanCard> = ArrayList()
                                panCardList.addAll(hashMap[uid]?.panCards?.values!!)
                                panUUIDInfoList.add(UserUIDInfo(uid, panCardList = panCardList))
                                if (!binding.tvPanCard.isVisible()) {
                                    binding.tvPanCard.visible()
                                    binding.panDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.shopActs != null) {
                                val list: ArrayList<ShopAct> = ArrayList()
                                list.addAll(hashMap[uid]?.shopActs?.values!!)
                                shopActUUIDInfoList.add(UserUIDInfo(uid, shopActList = list))
                                if (!binding.tvShopAct.isVisible()) {
                                    binding.tvShopAct.visible()
                                    binding.shopActDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.udyamAadhars != null) {
                                val list: ArrayList<UdyamAadhar> = ArrayList()
                                list.addAll(hashMap[uid]?.udyamAadhars?.values!!)
                                udyamAadharUUIDInfoList.add(UserUIDInfo(uid, udyamAadharList = list))
                                if (!binding.tvUdyamAadhar.isVisible()) {
                                    binding.tvUdyamAadhar.visible()
                                    binding.udyamAadharDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.foodLicenses != null) {
                                val list: ArrayList<FoodLicense> = ArrayList()
                                list.addAll(hashMap[uid]?.foodLicenses?.values!!)
                                foodLicenseUUIDInfoList.add(UserUIDInfo(uid, foodLicenseList = list))
                                if (!binding.tvFoodLicense.isVisible()) {
                                    binding.tvFoodLicense.visible()
                                    binding.foodLicenseDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.providentFunds != null) {
                                val list: ArrayList<ProvidentFund> = ArrayList()
                                list.addAll(hashMap[uid]?.providentFunds?.values!!)
                                providentFundUUIDInfoList.add(UserUIDInfo(uid, providentFundList = list))
                                if (!binding.tvProvidentFund.isVisible()) {
                                    binding.tvProvidentFund.visible()
                                    binding.providentFundDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.nepalMoneyTransfers != null) {
                                val list: ArrayList<NepalMoneyTransfer> = ArrayList()
                                list.addAll(hashMap[uid]?.nepalMoneyTransfers?.values!!)
                                nepalMoneyTransferUUIDInfoList.add(UserUIDInfo(uid, nepalMoneyTransferList = list))
                                if (!binding.tvNepalMoneyTransfer.isVisible()) {
                                    binding.tvNepalMoneyTransfer.visible()
                                    binding.nepalMoneyTransferDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.railwayTicketBookings != null) {
                                val list: ArrayList<RailwayTicketBooking> = ArrayList()
                                list.addAll(hashMap[uid]?.railwayTicketBookings?.values!!)
                                railwayTicketBookingUUIDInfoList.add(UserUIDInfo(uid, railwayTicketBookingList = list))
                                if (!binding.tvRailwayTicketBooking.isVisible()) {
                                    binding.tvRailwayTicketBooking.visible()
                                    binding.railwayTicketBookingDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.businessPanCards != null) {
                                val list: ArrayList<BusinessPanCard> = ArrayList()
                                list.addAll(hashMap[uid]?.businessPanCards?.values!!)
                                businessPanCardUUIDInfoList.add(UserUIDInfo(uid, businessPanCardList = list))
                                if (!binding.tvBusinessPanCard.isVisible()) {
                                    binding.tvBusinessPanCard.visible()
                                    binding.businessPanDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.electionCards != null) {
                                val list: ArrayList<ElectionCard> = ArrayList()
                                list.addAll(hashMap[uid]?.electionCards?.values!!)
                                electionCardUUIDInfoList.add(UserUIDInfo(uid, electionCardList = list))
                                if (!binding.tvElectionCard.isVisible()) {
                                    binding.tvElectionCard.visible()
                                    binding.electionCardDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.passports != null) {
                                val list: ArrayList<Passport> = ArrayList()
                                list.addAll(hashMap[uid]?.passports?.values!!)
                                passportUUIDInfoList.add(UserUIDInfo(uid, passportList = list))
                                if (!binding.tvPassport.isVisible()) {
                                    binding.tvPassport.visible()
                                    binding.passportDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.driving_learning_licenses != null) {
                                val list: ArrayList<DrivingLearningLicense> = ArrayList()
                                list.addAll(hashMap[uid]?.driving_learning_licenses?.values!!)
                                drivingLearningLicenseUUIDInfoList.add(UserUIDInfo(uid, drivingLearningLicenseList = list))
                                if (!binding.tvDrivingLearningLicence.isVisible()) {
                                    binding.tvDrivingLearningLicence.visible()
                                    binding.drivingLearningLicenceDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.verification != null) {
                                val list: ArrayList<Verification> = ArrayList()
                                list.addAll(hashMap[uid]?.verification?.values!!)
                                verificationUUIDInfoList.add(UserUIDInfo(uid, verificationList = list))
                                if (!binding.tvVerification.isVisible()) {
                                    binding.tvVerification.visible()
                                    binding.verificationDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.edit_pan_aadhar_cards != null) {
                                val list: ArrayList<EditPanAadharCard> = ArrayList()
                                list.addAll(hashMap[uid]?.edit_pan_aadhar_cards?.values!!)
                                editPanAadharCardUUIDInfoList.add(UserUIDInfo(uid, editPanAadharCardList = list))
                                if (!binding.tvEditPanOrAadharCard.isVisible()) {
                                    binding.tvEditPanOrAadharCard.visible()
                                    binding.editPanOrAadharCardDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.police_verifications != null) {
                                val list: ArrayList<PoliceVerification> = ArrayList()
                                list.addAll(hashMap[uid]?.police_verifications?.values!!)
                                policeVerificationUUIDInfoList.add(UserUIDInfo(uid, policeVerificationList = list))
                                if (!binding.tvPoliceVerification.isVisible()) {
                                    binding.tvPoliceVerification.visible()
                                    binding.policeVerificationDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.income_certificates != null) {
                                val list: ArrayList<IncomeCertificate> = ArrayList()
                                list.addAll(hashMap[uid]?.income_certificates?.values!!)
                                incomeCertificateUUIDInfoList.add(UserUIDInfo(uid, incomeCertificateList = list))
                                if (!binding.tvIncomeCertificate.isVisible()) {
                                    binding.tvIncomeCertificate.visible()
                                    binding.incomeCertificateDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.age_certificates != null) {
                                val list: ArrayList<AgeCertificate> = ArrayList()
                                list.addAll(hashMap[uid]?.age_certificates?.values!!)
                                ageCertificateUUIDInfoList.add(UserUIDInfo(uid, ageCertificateList = list))
                                if (!binding.tvAgeCertificate.isVisible()) {
                                    binding.tvAgeCertificate.visible()
                                    binding.ageCertificateDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.gazzets != null) {
                                val list: ArrayList<Gazzet> = ArrayList()
                                list.addAll(hashMap[uid]?.gazzets?.values!!)
                                gazzetUUIDInfoList.add(UserUIDInfo(uid, gazzetList = list))
                                if (!binding.tvGazzet.isVisible()) {
                                    binding.tvGazzet.visible()
                                    binding.gazzetDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.jyotish_shastra != null) {
                                val list: ArrayList<JyotishShastra> = ArrayList()
                                list.addAll(hashMap[uid]?.jyotish_shastra?.values!!)
                                jyotishShastraUUIDInfoList.add(UserUIDInfo(uid, jyotishShastraList = list))
                                if (!binding.tvJyotishShastra.isVisible()) {
                                    binding.tvJyotishShastra.visible()
                                    binding.jyotishShastraDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.cibil_reports != null) {
                                val list: ArrayList<CibilReport> = ArrayList()
                                list.addAll(hashMap[uid]?.cibil_reports?.values!!)
                                cibilReportUUIDInfoList.add(UserUIDInfo(uid, cibilReportList = list))
                                if (!binding.tvCibilReport.isVisible()) {
                                    binding.tvCibilReport.visible()
                                    binding.cibilReportDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.credit_cards != null) {
                                val list: ArrayList<CreditCard> = ArrayList()
                                list.addAll(hashMap[uid]?.credit_cards?.values!!)
                                creditCardUUIDInfoList.add(UserUIDInfo(uid, creditCardList = list))
                                if (!binding.tvCreditCard.isVisible()) {
                                    binding.tvCreditCard.visible()
                                    binding.creditCardDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.gst_regs != null) {
                                val list: ArrayList<GstRegistration> = ArrayList()
                                list.addAll(hashMap[uid]?.gst_regs?.values!!)
                                gstRegistrationUUIDInfoList.add(UserUIDInfo(uid, gstRegistrationList = list))
                                if (!binding.tvGstReg.isVisible()) {
                                    binding.tvGstReg.visible()
                                    binding.gstRegDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.all_govt_cards != null) {
                                val list: ArrayList<AllGovtCard> = ArrayList()
                                list.addAll(hashMap[uid]?.all_govt_cards?.values!!)
                                allGovtCardUUIDInfoList.add(UserUIDInfo(uid, allGovtCardList = list))
                                if (!binding.tvAllGovtCards.isVisible()) {
                                    binding.tvAllGovtCards.visible()
                                    binding.allGovtCardsDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.farmer_policies != null) {
                                val list: ArrayList<FarmerPolicy> = ArrayList()
                                list.addAll(hashMap[uid]?.farmer_policies?.values!!)
                                farmerPolicyUUIDInfoList.add(UserUIDInfo(uid, farmerPolicyList = list))
                                if (!binding.tvFarmerPolicy.isVisible()) {
                                    binding.tvFarmerPolicy.visible()
                                    binding.farmerPolicyDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.all_other_cards != null) {
                                val list: ArrayList<AllOtherCard> = ArrayList()
                                list.addAll(hashMap[uid]?.all_other_cards?.values!!)
                                allOtherCardUUIDInfoList.add(UserUIDInfo(uid, allOtherCardList = list))
                                if (!binding.tvAllOtherDocs.isVisible()) {
                                    binding.tvAllOtherDocs.visible()
                                    binding.allOtherDocsDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.demat_accounts != null) {
                                val list: ArrayList<DematAccount> = ArrayList()
                                list.addAll(hashMap[uid]?.demat_accounts?.values!!)
                                dematAccountUUIDInfoList.add(UserUIDInfo(uid, dematAccountList = list))
                                if (!binding.tvDematAccount.isVisible()) {
                                    binding.tvDematAccount.visible()
                                    binding.dematAccountDivider.visible()
                                }
                            }
                            if (hashMap[uid]?.govt_schemes != null) {
                                val list: ArrayList<GovtScheme> = ArrayList()
                                list.addAll(hashMap[uid]?.govt_schemes?.values!!)
                                govtSchemeUUIDInfoList.add(UserUIDInfo(uid, govtSchemeList = list))
                                if (!binding.tvGovtScheme.isVisible()) {
                                    binding.tvGovtScheme.visible()
                                    binding.govtSchemeDivider.visible()
                                }
                            }
                        } //                        esuvidhaInfoList.add(EsuvidhaInfo(date, userUIDInfoList))
                        if (panUUIDInfoList.size > 0) panCards.add(EsuvidhaInfo(date, panUUIDInfoList))
                        if (shopActUUIDInfoList.size > 0) shopActs.add(EsuvidhaInfo(date, shopActUUIDInfoList))
                        if (udyamAadharUUIDInfoList.size > 0) udyamAadhars.add(EsuvidhaInfo(date, udyamAadharUUIDInfoList))
                        if (foodLicenseUUIDInfoList.size > 0) foodLicenses.add(EsuvidhaInfo(date, foodLicenseUUIDInfoList))
                        if (providentFundUUIDInfoList.size > 0) providentFunds.add(EsuvidhaInfo(date, providentFundUUIDInfoList))
                        if (nepalMoneyTransferUUIDInfoList.size > 0) nepalMoneyTransfers.add(EsuvidhaInfo(date, nepalMoneyTransferUUIDInfoList))
                        if (railwayTicketBookingUUIDInfoList.size > 0) railwayTicketBookings.add(EsuvidhaInfo(date, railwayTicketBookingUUIDInfoList))
                        if (businessPanCardUUIDInfoList.size > 0) businessPanCards.add(EsuvidhaInfo(date, businessPanCardUUIDInfoList))
                        if (electionCardUUIDInfoList.size > 0) electionCards.add(EsuvidhaInfo(date, electionCardUUIDInfoList))
                        if (passportUUIDInfoList.size > 0) passports.add(EsuvidhaInfo(date, passportUUIDInfoList))
                        if (drivingLearningLicenseUUIDInfoList.size > 0) driving_learning_licenses.add(EsuvidhaInfo(date, drivingLearningLicenseUUIDInfoList))
                        if (verificationUUIDInfoList.size > 0) verification.add(EsuvidhaInfo(date, verificationUUIDInfoList))
                        if (editPanAadharCardUUIDInfoList.size > 0) edit_pan_aadhar_cards.add(EsuvidhaInfo(date, editPanAadharCardUUIDInfoList))
                        if (policeVerificationUUIDInfoList.size > 0) police_verifications.add(EsuvidhaInfo(date, policeVerificationUUIDInfoList))
                        if (incomeCertificateUUIDInfoList.size > 0) income_certificates.add(EsuvidhaInfo(date, incomeCertificateUUIDInfoList))
                        if (ageCertificateUUIDInfoList.size > 0) age_certificates.add(EsuvidhaInfo(date, ageCertificateUUIDInfoList))
                        if (gazzetUUIDInfoList.size > 0) gazzets.add(EsuvidhaInfo(date, gazzetUUIDInfoList))
                        if (jyotishShastraUUIDInfoList.size > 0) jyotish_shastra.add(EsuvidhaInfo(date, jyotishShastraUUIDInfoList))
                        if (cibilReportUUIDInfoList.size > 0) cibil_reports.add(EsuvidhaInfo(date, cibilReportUUIDInfoList))
                        if (creditCardUUIDInfoList.size > 0) credit_cards.add(EsuvidhaInfo(date, creditCardUUIDInfoList))
                        if (gstRegistrationUUIDInfoList.size > 0) gst_regs.add(EsuvidhaInfo(date, gstRegistrationUUIDInfoList))
                        if (allGovtCardUUIDInfoList.size > 0) all_govt_cards.add(EsuvidhaInfo(date, allGovtCardUUIDInfoList))
                        if (farmerPolicyUUIDInfoList.size > 0) farmer_policies.add(EsuvidhaInfo(date, farmerPolicyUUIDInfoList))
                        if (allOtherCardUUIDInfoList.size > 0) all_other_cards.add(EsuvidhaInfo(date, allOtherCardUUIDInfoList))
                        if (dematAccountUUIDInfoList.size > 0) demat_accounts.add(EsuvidhaInfo(date, dematAccountUUIDInfoList))
                        if (govtSchemeUUIDInfoList.size > 0) govt_schemes.add(EsuvidhaInfo(date, govtSchemeUUIDInfoList))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar?.gone()
                }
            })
        }, { coroutineContext, throwable ->
            binding.progressBar?.gone()
            throwable.printStackTrace()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_esuvidha, menu) //            val item = menu.findItem(R.id.action_search)
        //            val action_download = menu.findItem(R.id.action_download)
        //            searchView.setMenuItem(item)
        //            action_download?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Utils.hideSoftKeyboard(this)
                finish()
                return true
            }
            R.id.action_esuvidha_datewise -> {
                startActivity(Intent(mContext, ESuvidhaDatesActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        var TYPE_PAN_CARD = "type_pan_card"
        var TYPE_SHOP_ACT = "type_shop_act"
        var TYPE_UDYAM_AADHAR = "type_udyam_aadhar"
        var TYPE_FOOD_LICENSE = "type_food_license"
        var TYPE_PF = "type_pf"
        var TYPE_NEPAL_MT = "type_nepal_mt"
        var TYPE_RAIL_TB = "type_rail_tb"
        var TYPE_BUSINESS_PAN = "type_business_pan"
        var TYPE_ELECTION_CARD = "type_election_card"
        var TYPE_PASSPORT = "type_passport"
        var TYPE_DRIVING_LICENSE = "type_driving_license"
        var TYPE_VERIFICATION = "type_verification"
        var TYPE_EDIT_PAN_AADHAR = "type_edit_pan_aadhar"
        var TYPE_POLICE_VERIF = "type_police_verif"
        var TYPE_INCOME_CERT = "type_income_cert"
        var TYPE_AGE_CERT = "type_age_cert"
        var TYPE_GAZZET = "type_gazzet"
        var TYPE_JYOTISH = "type_jyotish"
        var TYPE_CIBIL_REPORT = "type_cibil_report"
        var TYPE_CREDIT_CARD = "type_credit_card"
        var TYPE_GST_REG = "type_gst_reg"
        var TYPE_ALL_GOV_CARDS = "type_all_gov_cards"
        var TYPE_FARMER_POLICY = "type_farmer_policy"
        var TYPE_ALL_OTHER_DOCS = "type_all_other_docs"
        var TYPE_DEMAT_ACC = "type_demat_acc"
        var TYPE_GOVT_SCHEME = "type_govt_scheme"
    }
}
