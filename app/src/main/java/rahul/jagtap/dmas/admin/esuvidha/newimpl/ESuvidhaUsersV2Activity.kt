package rahul.jagtap.dmas.admin.esuvidha.newimpl

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.JsonStorage
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.ESuvidhaUserV2ListAdapter
import rahul.jagtap.dmas.admin.esuvidha.BusinessPanCardListActivity
import rahul.jagtap.dmas.admin.esuvidha.DematAccountListActivity
import rahul.jagtap.dmas.admin.esuvidha.ElectionCardListActivity
import rahul.jagtap.dmas.admin.esuvidha.FoodLicenseListActivity
import rahul.jagtap.dmas.admin.esuvidha.NepalMoneyTransferListActivity
import rahul.jagtap.dmas.admin.esuvidha.PanCardListActivity
import rahul.jagtap.dmas.admin.esuvidha.ProvidentFundListActivity
import rahul.jagtap.dmas.admin.esuvidha.RailwayTicketBookingListActivity
import rahul.jagtap.dmas.admin.esuvidha.ShopActListActivity
import rahul.jagtap.dmas.admin.esuvidha.UdyamAadharListActivity
import rahul.jagtap.dmas.admin.esuvidha.VerificationListActivity
import rahul.jagtap.dmas.admin.esuvidha.*
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_AGE_CERT
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_ALL_GOV_CARDS
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_ALL_OTHER_DOCS
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_BUSINESS_PAN
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_CIBIL_REPORT
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_CREDIT_CARD
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_DEMAT_ACC
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_DRIVING_LICENSE
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_VERIFICATION
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_EDIT_PAN_AADHAR
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_ELECTION_CARD
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_FARMER_POLICY
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_FOOD_LICENSE
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_GAZZET
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_GOVT_SCHEME
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_GST_REG
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_INCOME_CERT
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_JYOTISH
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_NEPAL_MT
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_PAN_CARD
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_PASSPORT
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_PF
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_POLICE_VERIF
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_RAIL_TB
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_SHOP_ACT
import rahul.jagtap.dmas.admin.esuvidha.newimpl.ESuvidhaListActivity.Companion.TYPE_UDYAM_AADHAR
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.model.UserUIDInfo
import rahul.jagtap.dmas.utils.Utils
import java.lang.reflect.Type
import java.util.*

class ESuvidhaUsersV2Activity : BaseActivity() {
    var list = ArrayList<User>()
    var userUIDInfoList = ArrayList<UserUIDInfo>()
    var adapter: ESuvidhaUserV2ListAdapter? = null

    private lateinit var binding: ActivityViewBillsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = intent.getStringExtra("title")
            ?: getString(R.string.txt_online_e_suvidha)

        val json_data = JsonStorage.getUserIdsJsonString() //intent.getStringExtra("json_data")
        val type: Type = object : TypeToken<ArrayList<UserUIDInfo>>() {}.type
        if (!TextUtils.isEmpty(json_data)) userUIDInfoList = Gson().fromJson(json_data, type)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = ESuvidhaUserV2ListAdapter(mContext, list)
        binding.recyclerView?.adapter = adapter

        setUserList()

        adapter?.dateListListener = object : ESuvidhaUserV2ListAdapter.DateListListener {
            override fun onItemClick(position: Int) {
                val uid = list[position].uid
                userUIDInfoList.forEachIndexed { index, s ->
                    if (uid?.equals(s.strUserUid, true) == true) {
                        when (intent.getStringExtra("type")) {
                            TYPE_PAN_CARD -> {
                                if (s.panCardList != null && s.panCardList?.size!! > 0) { //                                    JsonStorage.setPanCardListJsonString(Gson().toJson(s.panCardList))
                                    startActivity(Intent(mContext, PanCardListActivity::class.java).putExtra("data", Gson().toJson(s.panCardList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_SHOP_ACT -> {
                                if (s.shopActList != null && s.shopActList?.size!! > 0) { //                                    JsonStorage.setShopActListJsonString(Gson().toJson(s.shopActList))
                                    startActivity(Intent(mContext, ShopActListActivity::class.java).putExtra("data", Gson().toJson(s.shopActList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_UDYAM_AADHAR -> {
                                if (s.udyamAadharList != null && s.udyamAadharList?.size!! > 0) { //                                    JsonStorage.setUdyamAadharListJsonString(Gson().toJson(s.udyamAadharList))
                                    startActivity(Intent(mContext, UdyamAadharListActivity::class.java).putExtra("data", Gson().toJson(s.udyamAadharList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_FOOD_LICENSE -> {
                                if (s.foodLicenseList != null && s.foodLicenseList?.size!! > 0) { //                                    JsonStorage.setFoodLicenseListJsonString(Gson().toJson(s.foodLicenseList))
                                    startActivity(Intent(mContext, FoodLicenseListActivity::class.java).putExtra("data", Gson().toJson(s.foodLicenseList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_PF -> {
                                if (s.providentFundList != null && s.providentFundList?.size!! > 0) { //                                    JsonStorage.setProvidentFundListJsonString(Gson().toJson(s.providentFundList))
                                    startActivity(Intent(mContext, ProvidentFundListActivity::class.java).putExtra("data", Gson().toJson(s.providentFundList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_NEPAL_MT -> {
                                if (s.nepalMoneyTransferList != null && s.nepalMoneyTransferList?.size!! > 0) { //                                    JsonStorage.setNepalMoneyTransferListJsonString(Gson().toJson(s.nepalMoneyTransferList))
                                    startActivity(Intent(mContext, NepalMoneyTransferListActivity::class.java).putExtra("data", Gson().toJson(s.nepalMoneyTransferList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_RAIL_TB -> {
                                if (s.railwayTicketBookingList != null && s.railwayTicketBookingList?.size!! > 0) { //                                    JsonStorage.setRailwayTicketBookingListJsonString(Gson().toJson(s.railwayTicketBookingList))
                                    startActivity(Intent(mContext, RailwayTicketBookingListActivity::class.java).putExtra("data", Gson().toJson(s.railwayTicketBookingList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_BUSINESS_PAN -> {
                                if (s.businessPanCardList != null && s.businessPanCardList?.size!! > 0) { //                                    JsonStorage.setBusinessPanCardListJsonString(Gson().toJson(s.businessPanCardList))
                                    startActivity(Intent(mContext, BusinessPanCardListActivity::class.java).putExtra("data", Gson().toJson(s.businessPanCardList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_ELECTION_CARD -> {
                                if (s.electionCardList != null && s.electionCardList?.size!! > 0) { //                                    JsonStorage.setElectionCardListJsonString(Gson().toJson(s.electionCardList))
                                    startActivity(Intent(mContext, ElectionCardListActivity::class.java).putExtra("data", Gson().toJson(s.electionCardList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_PASSPORT -> {
                                if (s.passportList != null && s.passportList?.size!! > 0) { //                                    JsonStorage.setPassportListJsonString(Gson().toJson(s.passportList))
                                    startActivity(Intent(mContext, PassportListActivity::class.java).putExtra("data", Gson().toJson(s.passportList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_DRIVING_LICENSE -> {
                                if (s.drivingLearningLicenseList != null && s.drivingLearningLicenseList?.size!! > 0) { //                                    JsonStorage.setDrivingLearningLicenseListJsonString(Gson().toJson(s.drivingLearningLicenseList))
                                    startActivity(Intent(mContext, DrivingLearningLicenseListActivity::class.java).putExtra("data", Gson().toJson(s.drivingLearningLicenseList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_VERIFICATION -> {
                                if (s.verificationList != null && s.verificationList?.size!! > 0) {
                                    startActivity(Intent(mContext, VerificationListActivity::class.java).putExtra("data", Gson().toJson(s.verificationList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_EDIT_PAN_AADHAR -> {
                                if (s.editPanAadharCardList != null && s.editPanAadharCardList?.size!! > 0) { //                                    JsonStorage.setEditPanAadharCardListJsonString(Gson().toJson(s.editPanAadharCardList))
                                    startActivity(Intent(mContext, EditPanOrAadharCardListActivity::class.java).putExtra("data", Gson().toJson(s.editPanAadharCardList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_POLICE_VERIF -> {
                                if (s.policeVerificationList != null && s.policeVerificationList?.size!! > 0) { //                                    JsonStorage.setPoliceVerificationListJsonString(Gson().toJson(s.policeVerificationList))
                                    startActivity(Intent(mContext, PoliceVerificationListActivity::class.java).putExtra("data", Gson().toJson(s.policeVerificationList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_INCOME_CERT -> {
                                if (s.incomeCertificateList != null && s.incomeCertificateList?.size!! > 0) { //                                    JsonStorage.setIncomeCertificateListJsonString(Gson().toJson(s.incomeCertificateList))
                                    startActivity(Intent(mContext, IncomeCertificateListActivity::class.java).putExtra("data", Gson().toJson(s.incomeCertificateList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_AGE_CERT -> {
                                if (s.ageCertificateList != null && s.ageCertificateList?.size!! > 0) { //                                    JsonStorage.setAgeCertificateListJsonString(Gson().toJson(s.ageCertificateList))
                                    startActivity(Intent(mContext, AgeCertificateListActivity::class.java).putExtra("data", Gson().toJson(s.ageCertificateList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_GAZZET -> {
                                if (s.gazzetList != null && s.gazzetList?.size!! > 0) { //                                    JsonStorage.setGazzetListJsonString(Gson().toJson(s.gazzetList))
                                    startActivity(Intent(mContext, GazzetListActivity::class.java).putExtra("data", Gson().toJson(s.gazzetList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_JYOTISH -> {
                                if (s.jyotishShastraList != null && s.jyotishShastraList?.size!! > 0) { //                                    JsonStorage.setJyotishShastraListJsonString(Gson().toJson(s.jyotishShastraList))
                                    startActivity(Intent(mContext, JyotishShastraListActivity::class.java).putExtra("data", Gson().toJson(s.jyotishShastraList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_CIBIL_REPORT -> {
                                if (s.cibilReportList != null && s.cibilReportList?.size!! > 0) { //                                    JsonStorage.setCibilReportListJsonString(Gson().toJson(s.cibilReportList))
                                    startActivity(Intent(mContext, CibilReportListActivity::class.java).putExtra("data", Gson().toJson(s.cibilReportList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_CREDIT_CARD -> {
                                if (s.creditCardList != null && s.creditCardList?.size!! > 0) { //                                    JsonStorage.setCreditCardListJsonString(Gson().toJson(s.creditCardList))
                                    startActivity(Intent(mContext, CreditCardListActivity::class.java).putExtra("data", Gson().toJson(s.creditCardList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_GST_REG -> {
                                if (s.gstRegistrationList != null && s.gstRegistrationList?.size!! > 0) { //                                    JsonStorage.setGstRegistrationListJsonString(Gson().toJson(s.gstRegistrationList))
                                    startActivity(Intent(mContext, GstRegistrationListActivity::class.java).putExtra("data", Gson().toJson(s.gstRegistrationList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_ALL_GOV_CARDS -> {
                                if (s.allGovtCardList != null && s.allGovtCardList?.size!! > 0) { //                                    JsonStorage.setAllGovtCardListJsonString(Gson().toJson(s.allGovtCardList))
                                    startActivity(Intent(mContext, AllGovtCardListActivity::class.java).putExtra("data", Gson().toJson(s.allGovtCardList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_FARMER_POLICY -> {
                                if (s.farmerPolicyList != null && s.farmerPolicyList?.size!! > 0) { //                                    JsonStorage.setFarmerPolicyListJsonString(Gson().toJson(s.farmerPolicyList))
                                    startActivity(Intent(mContext, FarmerPolicyListActivity::class.java).putExtra("data", Gson().toJson(s.farmerPolicyList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_ALL_OTHER_DOCS -> {
                                if (s.allOtherCardList != null && s.allOtherCardList?.size!! > 0) { //                                    JsonStorage.setAllOtherCardListJsonString(Gson().toJson(s.allOtherCardList))
                                    startActivity(Intent(mContext, AllOtherCardListActivity::class.java).putExtra("data", Gson().toJson(s.allOtherCardList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_DEMAT_ACC -> {
                                if (s.dematAccountList != null && s.dematAccountList?.size!! > 0) { //                                    JsonStorage.setDematAccountListJsonString(Gson().toJson(s.dematAccountList))
                                    startActivity(Intent(mContext, DematAccountListActivity::class.java).putExtra("data", Gson().toJson(s.dematAccountList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }

                            TYPE_GOVT_SCHEME -> {
                                if (s.govtSchemeList != null && s.govtSchemeList?.size!! > 0) {
                                    startActivity(Intent(mContext, GovtSchemeListActivity::class.java).putExtra("data", Gson().toJson(s.govtSchemeList)).putExtra("creatorJsonString", Gson().toJson(list[position])))
                                }
                            }
                        }
                    }
                } //                val eSuvidhaType = userUIDInfoList?.get(uid) //                if (eSuvidhaType != null) {
                //                    startActivity(Intent(mContext, ESuvidhaTypesActivity::class.java)
                //                        .putExtra("eSuvidhaType", Gson().toJson(eSuvidhaType))
                //                        .putExtra("creatorJsonString", Gson().toJson(list[position]))
                //                    )
                //                } else toast("No records found.")
            }
        }
    }

    fun setUserList() {
        userUIDInfoList.forEachIndexed { index, s ->
            s.strUserUid?.let {
                database.child(Utils.USERS_TABLE).child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("TAG", "getUser:onCancelled", databaseError.toException())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) { // Get user value
                        val userInfo = dataSnapshot.getValue(User::class.java)
                        if (userInfo != null) {
                            list.add(userInfo)
                            notifyAdapter()
                        }
                    }
                })
            }
        }
        if (list.size > 0) {
            notifyAdapter()
            binding.recyclerView?.visible()
            binding.tvError?.gone()
        } else {
            binding.recyclerView?.gone()
            binding.tvError?.visible()
        }
    }

    private fun notifyAdapter() {
        binding.progressBar?.gone()
        adapter?.notifyDataSetChanged()
        if (list.size == 0) {
            binding.recyclerView?.gone()
            binding.tvError?.visible()
        } else {
            binding.recyclerView?.visible()
            binding.tvError?.gone()
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
}
