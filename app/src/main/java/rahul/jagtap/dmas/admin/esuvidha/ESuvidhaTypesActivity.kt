package rahul.jagtap.dmas.admin.esuvidha

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.google.gson.Gson
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityEsuvidhaTypesBinding
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.ESuvidhaType
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import kotlin.collections.ArrayList

class ESuvidhaTypesActivity : BaseActivity() {
    var list = ArrayList<User>()
    lateinit var binding: ActivityEsuvidhaTypesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEsuvidhaTypesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_online_e_suvidha)

        val creatorJsonString = intent.getStringExtra("creatorJsonString")
        val json = intent.getStringExtra("eSuvidhaType")
        val eSuvidhaType = Gson().fromJson(json, ESuvidhaType::class.java)
        if (eSuvidhaType.panCards != null) {
            binding.tvPanCard.visible()
            binding.panDivider.visible()
            binding.tvPanCard.setOnClickListener {
                val panCardList = eSuvidhaType.panCards!!.values.toMutableList()
                if (panCardList.size > 0) {
                    startActivity(Intent(mContext, PanCardListActivity::class.java)
                        .putExtra("data", Gson().toJson(panCardList))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.shopActs != null) {
            binding.tvShopAct.visible()
            binding.shopActDivider.visible()
            binding.tvShopAct.setOnClickListener {
                val data = eSuvidhaType.shopActs!!.values.toMutableList()
                if (data.size > 0) {
                    startActivity(Intent(mContext, ShopActListActivity::class.java)
                        .putExtra("data", Gson().toJson(data))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.udyamAadhars != null) {
            binding.tvUdyamAadhar.visible()
            binding.udyamAadharDivider.visible()
            binding.tvUdyamAadhar.setOnClickListener {
                val data = eSuvidhaType.udyamAadhars!!.values.toMutableList()
                if (data.size > 0) {
                    startActivity(Intent(mContext, UdyamAadharListActivity::class.java)
                        .putExtra("data", Gson().toJson(data))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.foodLicenses != null) {
            binding.tvFoodLicense.visible()
            binding.foodLicenseDivider.visible()
            binding.tvFoodLicense.setOnClickListener {
                val data = eSuvidhaType.foodLicenses!!.values.toMutableList()
                if (data.size > 0) {
                    startActivity(Intent(mContext, FoodLicenseListActivity::class.java)
                        .putExtra("data", Gson().toJson(data))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.providentFunds != null) {
            binding.tvProvidentFund.visible()
            binding.providentFundDivider.visible()
            binding.tvProvidentFund.setOnClickListener {
                val data = eSuvidhaType.providentFunds!!.values.toMutableList()
                if (data.size > 0) {
                    startActivity(Intent(mContext, ProvidentFundListActivity::class.java)
                        .putExtra("data", Gson().toJson(data))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.nepalMoneyTransfers != null) {
            binding.tvNepalMoneyTransfer.visible()
            binding.nepalMoneyTransferDivider.visible()
            binding.tvNepalMoneyTransfer.setOnClickListener {
                val data = eSuvidhaType.nepalMoneyTransfers!!.values.toMutableList()
                if (data.size > 0) {
                    startActivity(Intent(mContext, NepalMoneyTransferListActivity::class.java)
                        .putExtra("data", Gson().toJson(data))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.railwayTicketBookings != null) {
            binding.tvRailwayTicketBooking.visible()
            binding.railwayTicketBookingDivider.visible()
            binding.tvRailwayTicketBooking.setOnClickListener {
                val data = eSuvidhaType.railwayTicketBookings!!.values.toMutableList()
                if (data.size > 0) {
                    startActivity(Intent(mContext, RailwayTicketBookingListActivity::class.java)
                        .putExtra("data", Gson().toJson(data))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.businessPanCards != null) {
            binding.tvBusinessPanCard.visible()
            binding.businessPanDivider.visible()
            binding.tvBusinessPanCard.setOnClickListener {
                val businessPanCardList = eSuvidhaType.businessPanCards!!.values.toMutableList()
                if (businessPanCardList.size > 0) {
                    startActivity(Intent(mContext, BusinessPanCardListActivity::class.java)
                        .putExtra("data", Gson().toJson(businessPanCardList))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.electionCards != null) {
            binding.tvElectionCard.visible()
            binding.electionCardDivider.visible()
            binding.tvElectionCard.setOnClickListener {
                val electionCardList = eSuvidhaType.electionCards!!.values.toMutableList()
                if (electionCardList.size > 0) {
                    startActivity(Intent(mContext, ElectionCardListActivity::class.java)
                        .putExtra("data", Gson().toJson(electionCardList))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.passports != null) {
            binding.tvPassport.visible()
            binding.passportDivider.visible()
            binding.tvPassport.setOnClickListener {
                val list = eSuvidhaType.passports!!.values.toMutableList()
                if (list.size > 0) {
                    startActivity(Intent(mContext, PassportListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.driving_learning_licenses != null) {
            binding.tvDrivingLearningLicence.visible()
            binding.drivingLearningLicenceDivider.visible()
            binding.tvDrivingLearningLicence.setOnClickListener {
                val list = eSuvidhaType.driving_learning_licenses!!.values.toMutableList()
                if (list.size > 0) {
                    startActivity(Intent(mContext, DrivingLearningLicenseListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.edit_pan_aadhar_cards != null) {
            binding.tvEditPanOrAadharCard.visible()
            binding.editPanOrAadharCardDivider.visible()
            binding.tvEditPanOrAadharCard.setOnClickListener {
                val list = eSuvidhaType.edit_pan_aadhar_cards!!.values.toMutableList()
                if (list.size > 0) {
                    startActivity(Intent(mContext, EditPanOrAadharCardListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.police_verifications != null) {
            binding.tvPoliceVerification.visible()
            binding.policeVerificationDivider.visible()
            binding.tvPoliceVerification.setOnClickListener {
                val list = eSuvidhaType.police_verifications!!.values.toMutableList()
                if (list.size > 0) {
                    startActivity(Intent(mContext, PoliceVerificationListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.income_certificates != null) {
            binding.tvIncomeCertificate.visible()
            binding.incomeCertificateDivider.visible()
            binding.tvIncomeCertificate.setOnClickListener {
                val list = eSuvidhaType.income_certificates!!.values.toMutableList()
                if (list.size > 0) {
                    startActivity(Intent(mContext, IncomeCertificateListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.age_certificates != null) {
            binding.tvAgeCertificate.visible()
            binding.ageCertificateDivider.visible()
            binding.tvAgeCertificate.setOnClickListener {
                val list = eSuvidhaType.age_certificates!!.values.toMutableList()
                if (list.size > 0) {
                    startActivity(Intent(mContext, AgeCertificateListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.gazzets != null) {
            binding.tvGazzet.visible()
            binding.gazzetDivider.visible()
            binding.tvGazzet.setOnClickListener {
                val list = eSuvidhaType.gazzets!!.values.toMutableList()
                if (list.size > 0) {
                    startActivity(Intent(mContext, GazzetListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.jyotish_shastra != null) {
            binding.tvJyotishShastra.visible()
            binding.jyotishShastraDivider.visible()
            binding.tvJyotishShastra.setOnClickListener {
                val list = eSuvidhaType.jyotish_shastra!!.values.toMutableList()
                if (list.size > 0) {
                    startActivity(Intent(mContext, JyotishShastraListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.cibil_reports != null) {
            binding.tvCibilReport.visible()
            binding.cibilReportDivider.visible()
            binding.tvCibilReport.setOnClickListener {
                val list = eSuvidhaType.cibil_reports!!.values.toMutableList()
                if (list.size > 0) {
                    startActivity(Intent(mContext, CibilReportListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.credit_cards != null) {
            binding.tvCreditCard.visible()
            binding.creditCardDivider.visible()
            binding.tvCreditCard.setOnClickListener {
                val list = eSuvidhaType.credit_cards!!.values.toMutableList()
                if (list.size > 0) {
                    startActivity(Intent(mContext, CreditCardListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.gst_regs != null) {
            binding.tvGstReg.visible()
            binding.gstRegDivider.visible()
            binding.tvGstReg.setOnClickListener {
                val list = eSuvidhaType.gst_regs?.values?.toMutableList()
                if (list != null && list.size > 0) {
                    startActivity(Intent(mContext, GstRegistrationListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.all_govt_cards != null) {
            binding.tvAllGovtCards.visible()
            binding.allGovtCardsDivider.visible()
            binding.tvAllGovtCards.setOnClickListener {
                val list = eSuvidhaType.all_govt_cards?.values?.toMutableList()
                if (list != null && list.size > 0) {
                    startActivity(Intent(mContext, AllGovtCardListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.verification != null) {
            binding.tvVerification.visible()
            binding.verificationDivider.visible()
            binding.tvVerification.setOnClickListener {
                val list = eSuvidhaType.verification?.values?.toMutableList()
                if (list != null && list.size > 0) {
                    startActivity(Intent(mContext, VerificationListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.farmer_policies != null) {
            binding.tvFarmerPolicy.visible()
            binding.farmerPolicyDivider.visible()
            binding.tvFarmerPolicy.setOnClickListener {
                val list = eSuvidhaType.farmer_policies?.values?.toMutableList()
                if (list != null && list.size > 0) {
                    startActivity(Intent(mContext, FarmerPolicyListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.all_other_cards != null) {
            binding.tvAllOtherDocs.visible()
            binding.allOtherDocsDivider.visible()
            binding.tvAllOtherDocs.setOnClickListener {
                val list = eSuvidhaType.all_other_cards?.values?.toMutableList()
                if (list != null && list.size > 0) {
                    startActivity(Intent(mContext, AllOtherCardListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.demat_accounts != null) {
            binding.tvDematAccount.visible()
            binding.dematAccountDivider.visible()
            binding.tvDematAccount.setOnClickListener {
                val list = eSuvidhaType.demat_accounts?.values?.toMutableList()
                if (list != null && list.size > 0) {
                    startActivity(Intent(mContext, DematAccountListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
            }
        }
        if (eSuvidhaType.govt_schemes != null) {
            binding.tvGovtScheme.visible()
            binding.govtSchemeDivider.visible()
            binding.tvGovtScheme.setOnClickListener {
                val list = eSuvidhaType.govt_schemes?.values?.toMutableList()
                if (list != null && list.size > 0) {
                    startActivity(Intent(mContext, GovtSchemeListActivity::class.java)
                        .putExtra("data", Gson().toJson(list))
                        .putExtra("creatorJsonString", creatorJsonString)
                    )
                }
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

    companion object
}
