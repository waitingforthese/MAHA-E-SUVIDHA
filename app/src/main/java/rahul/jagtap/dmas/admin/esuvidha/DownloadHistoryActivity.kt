package rahul.jagtap.dmas.admin.esuvidha

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.FileProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.BuildConfig
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityDownloadHistoryBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.AgeCertificate
import rahul.jagtap.dmas.model.AllGovtCard
import rahul.jagtap.dmas.model.Verification
import rahul.jagtap.dmas.model.AllOtherCard
import rahul.jagtap.dmas.model.BusinessPanCard
import rahul.jagtap.dmas.model.CibilReport
import rahul.jagtap.dmas.model.CreditCard
import rahul.jagtap.dmas.model.DematAccount
import rahul.jagtap.dmas.model.DrivingLearningLicense
import rahul.jagtap.dmas.model.ESuvidhaData
import rahul.jagtap.dmas.model.ESuvidhaType
import rahul.jagtap.dmas.model.EditPanAadharCard
import rahul.jagtap.dmas.model.ElectionCard
import rahul.jagtap.dmas.model.FarmerPolicy
import rahul.jagtap.dmas.model.FoodLicense
import rahul.jagtap.dmas.model.Gazzet
import rahul.jagtap.dmas.model.GovtScheme
import rahul.jagtap.dmas.model.GstRegistration
import rahul.jagtap.dmas.model.IncomeCertificate
import rahul.jagtap.dmas.model.JyotishShastra
import rahul.jagtap.dmas.model.ManualAadharPanCard
import rahul.jagtap.dmas.model.NepalMoneyTransfer
import rahul.jagtap.dmas.model.PanCard
import rahul.jagtap.dmas.model.Passport
import rahul.jagtap.dmas.model.PoliceVerification
import rahul.jagtap.dmas.model.ProvidentFund
import rahul.jagtap.dmas.model.RailwayTicketBooking
import rahul.jagtap.dmas.model.ServiceModel
import rahul.jagtap.dmas.model.ShopAct
import rahul.jagtap.dmas.model.UdyamAadhar
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import rahul.jagtap.dmas.widget.searchablemultiselectspinner.SearchableItem
import rahul.jagtap.dmas.widget.searchablemultiselectspinner.SearchableMultiSelectSpinner
import rahul.jagtap.dmas.widget.searchablemultiselectspinner.SelectionCompleteListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.core.widget.doAfterTextChanged


class DownloadHistoryActivity : BaseActivity() {
    private var eSuvidhaData: ESuvidhaData? = null
    private val TAG = DownloadHistoryActivity::class.java.simpleName
    private var email: String? = ""
    private var username: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    var cpd: ProgressDialog? = null

    var selectedFromDate = ""
    var selectedToDate = ""

    var suvidhaTypes = ArrayList<SearchableItem>()
    var userList = java.util.ArrayList<User>()

    private var resultFileUri: Uri? = null
    private lateinit var resultFile: File
    private var workbook: Workbook? = null
    private var sheet: Sheet? = null
    private var cell: Cell? = null
    var esuvidhaTypeList = ArrayList<SearchableItem>()
    lateinit var binding: ActivityDownloadHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadHistoryBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        // input-field migration: clear errors on edit
        listOf(binding.tilSelectESuvidha, binding.tilFromDate, binding.tilToDate)
            .forEach { til -> til.editText?.doAfterTextChanged { til.error = null } }
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        fetchData()
        binding.toolbarLayout.toolbarTitle?.text = "Download History"
        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid
        username = app?.preferences?.loggedInUser?.username
        name = app?.preferences?.loggedInUser?.name //        searchableItemOf("All"),
        suvidhaTypes = arrayListOf(
            searchableItemOf(getString(R.string.txt_pan_card)), searchableItemOf(getString(R.string.txt_shop_act)),
            searchableItemOf(getString(R.string.txt_udyam_aadhar)), searchableItemOf(getString(R.string.txt_food_license)), searchableItemOf(getString(R.string.txt_provident_fund)),
            searchableItemOf(getString(R.string.txt_nepal_money_transfer)), searchableItemOf(getString(R.string.txt_railway_ticket_booking)),
            searchableItemOf(getString(R.string.txt_business_pan_card)), searchableItemOf(getString(R.string.txt_election_card)),
            searchableItemOf(getString(R.string.txt_passport)), searchableItemOf(getString(R.string.txt_driving_learning_license)), searchableItemOf(getString(R.string.txt_verification)),
            searchableItemOf(getString(R.string.edit_pan_card_aadhar_card)), searchableItemOf(getString(R.string.police_verification)),
            searchableItemOf(getString(R.string.income_certificate)), searchableItemOf(getString(R.string.age_nationality_amp_domicile_certificate)),
            searchableItemOf(getString(R.string.gazzet)), searchableItemOf(getString(R.string.jyotish_shastriche_prashn)), searchableItemOf(getString(R.string.cibil_report)),
            searchableItemOf(getString(R.string.free_credit_card)), searchableItemOf(getString(R.string.gst_registration)), searchableItemOf(getString(R.string.all_govt_cards)),
            searchableItemOf(getString(R.string.farmer_policy)), searchableItemOf(getString(R.string.all_other_docs)), searchableItemOf(getString(R.string.demat_account)),
            searchableItemOf(getString(R.string.txt_govt_schemes)),
        )

        getUserList()

        binding.etSelectESuvidha?.setOnClickListener {
            if (esuvidhaTypeList.size > 0) {
                suvidhaTypes.map { suvidha ->
                    if (esuvidhaTypeList.firstOrNull { searchableItem -> searchableItem.code == suvidha.code } != null) {
                        suvidha.isSelected = true
                    }
                    suvidha
                }
            }
            SearchableMultiSelectSpinner.show(this, "Select E-Suvidha", "Done", suvidhaTypes, object : SelectionCompleteListener {
                override fun onCompleteSelection(selectedItems: ArrayList<SearchableItem>) {
                    Log.e("data", selectedItems.toString())
                    esuvidhaTypeList = selectedItems
                    val commaSeparatedString = selectedItems.joinToString(separator = ", ") { item ->
                        item.text
                    }
                    binding.etSelectESuvidha?.setText(commaSeparatedString)
                }
            }) //            MaterialDialog.Builder(mContext!!).itemsCallbackMultiChoice(indices) { dialog, which, text ->
            //                if (which.isEmpty()) {
            //                    toast("Choose at least one E-Suvidha")
            //                    return@itemsCallbackMultiChoice false
            //                }
            //                indices = which
            //                etSelectESuvidha?.setText(text.toString())
            //                true
            //            }.positiveText("Choose").show()
            //            MaterialDialog.Builder(mContext!!).items(suvidhaTypes).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
            //                run {
            //                    dialog?.dismiss()
            //                    selectedSuvidha = suvidhaTypes[position]
            //                    etSelectESuvidha?.setText(suvidhaTypes[position])
            //                }
            //            }.show()
        }

        binding.btnSubmit?.setOnClickListener {
            val strSelectESuvidha = binding.etSelectESuvidha.text.toString()
            val strFromDate = binding.etFromDate.text.toString()
            val strToDate = binding.etToDate.text.toString() //            val strJson = etJson.text.toString()
            if (TextUtils.isEmpty(strSelectESuvidha)) {
                binding.tilSelectESuvidha?.error = binding.tilSelectESuvidha?.hint.toString()
                binding.etSelectESuvidha?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strFromDate)) {
                binding.tilFromDate?.error = binding.tilFromDate?.hint.toString()
                binding.etFromDate?.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(strToDate)) {
                binding.tilToDate?.error = binding.tilToDate?.hint.toString()
                binding.etToDate?.requestFocus()
                return@setOnClickListener
            }
            toast("Started filtering data...wait a bit")
            filterData(strSelectESuvidha, strFromDate, strToDate)
        }
        binding.etFromDate?.setOnClickListener {
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance({ view1: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                selectedFromDate = String.format("%02d", dayOfMonth) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year
                binding.etFromDate.setText(selectedFromDate)
            }, now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH])
            dpd.setTitle("Select From Date")
            dpd.show(supportFragmentManager, "StartDatepickerdialog")
        }
        binding.etToDate?.setOnClickListener {
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance({ view1: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                selectedToDate = String.format("%02d", dayOfMonth) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year
                binding.etToDate.setText(selectedToDate)
            }, now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH])
            dpd.setTitle("Select To Date")
            dpd.show(supportFragmentManager, "EndDatepickerdialog")
        }
    }

    private fun searchableItemOf(s: String): SearchableItem {
        return SearchableItem(s, s)
    }

    private fun filterData(strSelectESuvidha: String, strFromDate: String, strToDate: String) {
        initWorkbook()

        //        val panCards: MutableCollection<PanCard> = ArrayList()
        //        val shopActs: MutableCollection<ShopAct> = ArrayList()
        //        val udyamAadhars: MutableCollection<UdyamAadhar> = ArrayList()
        //        val foodLicenses: MutableCollection<FoodLicense> = ArrayList()
        //        val providentFunds: MutableCollection<ProvidentFund> = ArrayList()
        //        val nepalMoneyTransfers: MutableCollection<NepalMoneyTransfer> = ArrayList()
        //        val railwayTicketBookings: MutableCollection<RailwayTicketBooking> = ArrayList()
        //        val businessPanCards: MutableCollection<BusinessPanCard> = ArrayList()
        //        val electionCards: MutableCollection<ElectionCard> = ArrayList()
        //        val passports: MutableCollection<Passport> = ArrayList()
        //        val driving_learning_licenses: MutableCollection<DrivingLearningLicense> = ArrayList()
        //        val edit_pan_aadhar_cards: MutableCollection<EditPanAadharCard> = ArrayList()
        //        val police_verifications: MutableCollection<PoliceVerification> = ArrayList()
        //        val income_certificates: MutableCollection<IncomeCertificate> = ArrayList()
        //        val age_certificates: MutableCollection<AgeCertificate> = ArrayList()
        //        val gazzets: MutableCollection<Gazzet> = ArrayList()
        //        val jyotish_shastra: MutableCollection<JyotishShastra> = ArrayList()
        //        val cibil_reports: MutableCollection<CibilReport> = ArrayList()
        //        val credit_cards: MutableCollection<CreditCard> = ArrayList()
        //        val gst_regs: MutableCollection<GstRegistration> = ArrayList()
        //        val allgovt_cards: MutableCollection<AllGovtCard> = ArrayList()
        //        val farmer_policies: MutableCollection<FarmerPolicy> = ArrayList()
        //        val allother_cards: MutableCollection<AllOtherCard> = ArrayList()
        //        val demat_accounts: MutableCollection<DematAccount> = ArrayList()
        //        val govt_schemes: MutableCollection<GovtScheme> = ArrayList()
        val service_models: MutableCollection<ServiceModel> = ArrayList()

        eSuvidhaData?.map?.keys?.forEach {
            if (Utils.isDateBetweenTwoDates(strFromDate, strToDate, it)) {
                val hashMap = eSuvidhaData?.map?.get(it)
                hashMap?.keys?.forEach { uid -> //                    when (strSelectESuvidha) {
                    if (strSelectESuvidha.contains(getString(R.string.txt_pan_card))) { //getString(R.string.txt_pan_card) -> {
                        if (hashMap[uid]?.panCards != null) {
                            val items = hashMap[uid]?.panCards
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.txt_udyam_aadhar))) { // -> {
                        if (hashMap[uid]?.udyamAadhars != null) {
                            val items = hashMap[uid]?.udyamAadhars
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.txt_shop_act))) { // -> {
                        if (hashMap[uid]?.shopActs != null) {
                            val items = hashMap[uid]?.shopActs
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.txt_food_license))) { // -> {
                        if (hashMap[uid]?.foodLicenses != null) {
                            val items = hashMap[uid]?.foodLicenses
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.txt_provident_fund))) { // -> {
                        if (hashMap[uid]?.providentFunds != null) {
                            val items = hashMap[uid]?.providentFunds
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.txt_nepal_money_transfer))) { // -> {
                        if (hashMap[uid]?.nepalMoneyTransfers != null) {
                            val items = hashMap[uid]?.nepalMoneyTransfers
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.txt_railway_ticket_booking))) { // -> {
                        if (hashMap[uid]?.railwayTicketBookings != null) {
                            val items = hashMap[uid]?.railwayTicketBookings
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.txt_business_pan_card))) { // -> {
                        if (hashMap[uid]?.businessPanCards != null) {
                            val items = hashMap[uid]?.businessPanCards
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.txt_election_card))) { // -> {
                        if (hashMap[uid]?.electionCards != null) {
                            val items = hashMap[uid]?.electionCards
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.txt_passport))) { // -> {
                        if (hashMap[uid]?.passports != null) {
                            val items = hashMap[uid]?.passports
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.txt_driving_learning_license))) { // -> {
                        if (hashMap[uid]?.driving_learning_licenses != null) {
                            val items = hashMap[uid]?.driving_learning_licenses
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.txt_verification))) {
                        if (hashMap[uid]?.verification != null) {
                            val items = hashMap[uid]?.verification
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.edit_pan_card_aadhar_card))) { // -> {
                        if (hashMap[uid]?.edit_pan_aadhar_cards != null) {
                            val items = hashMap[uid]?.edit_pan_aadhar_cards
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.police_verification))) { // -> {
                        if (hashMap[uid]?.police_verifications != null) {
                            val items = hashMap[uid]?.police_verifications
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.income_certificate))) { // -> {
                        if (hashMap[uid]?.income_certificates != null) {
                            val items = hashMap[uid]?.income_certificates
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.age_nationality_amp_domicile_certificate))) { // -> {
                        if (hashMap[uid]?.age_certificates != null) {
                            val items = hashMap[uid]?.age_certificates
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.gazzet))) { // -> {
                        if (hashMap[uid]?.gazzets != null) {
                            val items = hashMap[uid]?.gazzets
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.jyotish_shastriche_prashn))) { // -> {
                        if (hashMap[uid]?.jyotish_shastra != null) {
                            val items = hashMap[uid]?.jyotish_shastra
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.cibil_report))) { // -> {
                        if (hashMap[uid]?.cibil_reports != null) {
                            val items = hashMap[uid]?.cibil_reports
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.free_credit_card))) { // -> {
                        if (hashMap[uid]?.credit_cards != null) {
                            val items = hashMap[uid]?.credit_cards
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.gst_registration))) { // -> {
                        if (hashMap[uid]?.gst_regs != null) {
                            val items = hashMap[uid]?.gst_regs
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.all_govt_cards))) { // -> {
                        if (hashMap[uid]?.all_govt_cards != null) {
                            val items = hashMap[uid]?.all_govt_cards
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.farmer_policy))) { // -> {
                        if (hashMap[uid]?.farmer_policies != null) {
                            val items = hashMap[uid]?.farmer_policies
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.all_other_docs))) { // -> {
                        if (hashMap[uid]?.all_other_cards != null) {
                            val items = hashMap[uid]?.all_other_cards
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.demat_account))) { // -> {
                        if (hashMap[uid]?.demat_accounts != null) {
                            val items = hashMap[uid]?.demat_accounts
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    }

                    if (strSelectESuvidha.contains(getString(R.string.txt_govt_schemes))) { // -> {
                        if (hashMap[uid]?.govt_schemes != null) {
                            val items = hashMap[uid]?.govt_schemes
                            items?.values?.let { it1 -> service_models.addAll(it1) }
                        }
                    } //                        "All" -> {
                    //                            if (hashMap[uid]?.panCards != null) {
                    //                                val items = hashMap[uid]?.panCards
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.udyamAadhars != null) {
                    //                                val items = hashMap[uid]?.udyamAadhars
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.shopActs != null) {
                    //                                val items = hashMap[uid]?.shopActs
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.foodLicenses != null) {
                    //                                val items = hashMap[uid]?.foodLicenses
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.providentFunds != null) {
                    //                                val items = hashMap[uid]?.providentFunds
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.nepalMoneyTransfers != null) {
                    //                                val items = hashMap[uid]?.nepalMoneyTransfers
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.railwayTicketBookings != null) {
                    //                                val items = hashMap[uid]?.railwayTicketBookings
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.businessPanCards != null) {
                    //                                val items = hashMap[uid]?.businessPanCards
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.electionCards != null) {
                    //                                val items = hashMap[uid]?.electionCards
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.passports != null) {
                    //                                val items = hashMap[uid]?.passports
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.driving_learning_licenses != null) {
                    //                                val items = hashMap[uid]?.driving_learning_licenses
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.edit_pan_aadhar_cards != null) {
                    //                                val items = hashMap[uid]?.edit_pan_aadhar_cards
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.police_verifications != null) {
                    //                                val items = hashMap[uid]?.police_verifications
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.income_certificates != null) {
                    //                                val items = hashMap[uid]?.income_certificates
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.age_certificates != null) {
                    //                                val items = hashMap[uid]?.age_certificates
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.gazzets != null) {
                    //                                val items = hashMap[uid]?.gazzets
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.jyotish_shastra != null) {
                    //                                val items = hashMap[uid]?.jyotish_shastra
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.cibil_reports != null) {
                    //                                val items = hashMap[uid]?.cibil_reports
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.credit_cards != null) {
                    //                                val items = hashMap[uid]?.credit_cards
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.gst_regs != null) {
                    //                                val items = hashMap[uid]?.gst_regs
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.all_govt_cards != null) {
                    //                                val items = hashMap[uid]?.all_govt_cards
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.farmer_policies != null) {
                    //                                val items = hashMap[uid]?.farmer_policies
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.all_other_cards != null) {
                    //                                val items = hashMap[uid]?.all_other_cards
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.demat_accounts != null) {
                    //                                val items = hashMap[uid]?.demat_accounts
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                            if (hashMap[uid]?.govt_schemes != null) {
                    //                                val items = hashMap[uid]?.govt_schemes
                    //                                items?.values?.let { it1 -> service_models.addAll(it1) }
                    //                            }
                    //                        }
                    //                    }
                }
            }
        }

        if (service_models.isNotEmpty()) {
            val sortedEntries = service_models.sortedBy { entry ->
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
            }
            sortedEntries.forEach { item ->
                val columnList = LinkedHashMap<String, String>()
                columnList["Date"] = item.createdDateTime
                columnList["Client Name"] = item.createdBy
                columnList["Client Mobile"] = findUserByUid(item.uid)?.contactNo.orEmpty()
                columnList["Service Name"] = findServiceNameByServiceModelType(item)
                columnList["Type"] = item.suvidhaType
                Log.e("applicantName", item?.applicantName ?: "")
                Log.e("mobileNo", item?.mobileNo ?: "")
                columnList["Customer Name"] = customerName(item)
                columnList["Customer Mobile"] = customerMobile(item) //                if (item is PanCard) {
                columnList["Payment Confirmation"] = if (item.paymentStatus == "1") "PAID" else if (item.paymentStatus == "2") "UN-PAID" else ""
                columnList["Closing Update"] = item.closingUpdate ?: "" //                }
                createExcelWorkbook(columnList)
            }
        }

        //        if (panCards.isNotEmpty()) {
        //            val sortedEntries = panCards.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.txt_pan_card)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.mobileNo.toString()
        //                columnList["Payment Confirmation"] = if (item.paymentStatus == "1") "PAID" else if (item.paymentStatus == "2") "UN-PAID" else ""
        //                columnList["Closing Update"] = item.closingUpdate ?: ""
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (udyamAadhars.isNotEmpty()) {
        //            val sortedEntries = udyamAadhars.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.txt_udyam_aadhar)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.ownerMobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (shopActs.isNotEmpty()) {
        //            val sortedEntries = shopActs.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.txt_shop_act)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.ownerMobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (foodLicenses.isNotEmpty()) {
        //            val sortedEntries = foodLicenses.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.txt_food_license)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.mobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (providentFunds.isNotEmpty()) {
        //            val sortedEntries = providentFunds.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.txt_provident_fund)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.personMobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (nepalMoneyTransfers.isNotEmpty()) {
        //            val sortedEntries = nepalMoneyTransfers.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.txt_nepal_money_transfer)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.sender_name.toString()
        //                columnList["Customer Mobile"] = item.receiver_mobile.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (railwayTicketBookings.isNotEmpty()) {
        //            val sortedEntries = railwayTicketBookings.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.txt_railway_ticket_booking)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.rail_booking_name.toString()
        //                columnList["Customer Mobile"] = item.rail_booking_mobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (businessPanCards.isNotEmpty()) {
        //            val sortedEntries = businessPanCards.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.txt_business_pan_card)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.mobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (electionCards.isNotEmpty()) {
        //            val sortedEntries = electionCards.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.txt_election_card)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.mobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (passports.isNotEmpty()) {
        //            val sortedEntries = passports.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.txt_passport)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.mobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (driving_learning_licenses.isNotEmpty()) {
        //            val sortedEntries = driving_learning_licenses.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.txt_driving_learning_license)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.mobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (edit_pan_aadhar_cards.isNotEmpty()) {
        //            val sortedEntries = edit_pan_aadhar_cards.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.edit_pan_card_aadhar_card)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.fullName.toString()
        //                columnList["Customer Mobile"] = ""
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (police_verifications.isNotEmpty()) {
        //            val sortedEntries = police_verifications.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.police_verification)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.fullName.toString()
        //                columnList["Customer Mobile"] = item.mobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (income_certificates.isNotEmpty()) {
        //            val sortedEntries = income_certificates.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.income_certificate)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.fullName.toString()
        //                columnList["Customer Mobile"] = item.mobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (age_certificates.isNotEmpty()) {
        //            val sortedEntries = age_certificates.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.age_nationality_amp_domicile_certificate)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.fullName.toString()
        //                columnList["Customer Mobile"] = item.mobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (gazzets.isNotEmpty()) {
        //            val sortedEntries = gazzets.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.gazzet)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.fullName.toString()
        //                columnList["Customer Mobile"] = item.mobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (jyotish_shastra.isNotEmpty()) {
        //            val sortedEntries = jyotish_shastra.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.jyotish_shastriche_prashn)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.fullName.toString()
        //                columnList["Customer Mobile"] = ""
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (cibil_reports.isNotEmpty()) {
        //            val sortedEntries = cibil_reports.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.cibil_report)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.customerName.toString()
        //                columnList["Customer Mobile"] = item.customerMobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (credit_cards.isNotEmpty()) {
        //            val sortedEntries = credit_cards.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.free_credit_card)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.customerName.toString()
        //                columnList["Customer Mobile"] = item.customerMobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (gst_regs.isNotEmpty()) {
        //            val sortedEntries = gst_regs.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.gst_registration)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.customerMobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (allgovt_cards.isNotEmpty()) {
        //            val sortedEntries = allgovt_cards.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.all_govt_cards)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = ""
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (farmer_policies.isNotEmpty()) {
        //            val sortedEntries = farmer_policies.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.farmer_policy)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = ""
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (allother_cards.isNotEmpty()) {
        //            val sortedEntries = allother_cards.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.all_other_docs)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.mobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (demat_accounts.isNotEmpty()) {
        //            val sortedEntries = demat_accounts.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.demat_account)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.customerMobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        //        if (govt_schemes.isNotEmpty()) {
        //            val sortedEntries = govt_schemes.sortedByDescending { entry ->
        //                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.createdDateTime)
        //            }
        //            sortedEntries.forEach { item ->
        //                val columnList = LinkedHashMap<String, String>()
        //                columnList["Date"] = item.createdDateTime.toString()
        //                columnList["Client Name"] = item.createdBy.toString()
        //                columnList["Client Mobile"] = findUserByUid(item.uid.toString())?.contactNo.orEmpty()
        //                columnList["Service Name"] = getString(R.string.txt_govt_schemes)
        //                columnList["Type"] = item.typeForModel
        //                columnList["Customer Name"] = item.applicantName.toString()
        //                columnList["Customer Mobile"] = item.ownerMobileNo.toString()
        //                createExcelWorkbook(columnList)
        //            }
        //        }
        downloadExelFile()
    }

    private fun findServiceNameByServiceModelType(item: ServiceModel): String {
        when (item) {
            is PanCard -> return getString(R.string.txt_pan_card)
            is ShopAct -> return getString(R.string.txt_shop_act)
            is UdyamAadhar -> return getString(R.string.txt_udyam_aadhar)
            is ProvidentFund -> return getString(R.string.txt_provident_fund)
            is FoodLicense -> return getString(R.string.txt_food_license)
            is ManualAadharPanCard -> return getString(R.string.txt_manual_aadhar_pan_card)
            is NepalMoneyTransfer -> return getString(R.string.txt_nepal_money_transfer)
            is RailwayTicketBooking -> return getString(R.string.txt_railway_ticket_booking)
            is BusinessPanCard -> return getString(R.string.txt_business_pan_card)
            is ElectionCard -> return getString(R.string.txt_election_card)
            is Passport -> return getString(R.string.txt_passport)
            is DrivingLearningLicense -> return getString(R.string.txt_driving_learning_license)
            is EditPanAadharCard -> return getString(R.string.edit_pan_card_aadhar_card)
            is PoliceVerification -> return getString(R.string.police_verification)
            is IncomeCertificate -> return getString(R.string.income_certificate)
            is AgeCertificate -> return getString(R.string.age_nationality_amp_domicile_certificate)
            is Gazzet -> return getString(R.string.gazzet)
            is JyotishShastra -> return getString(R.string.jyotish_shastriche_prashn)
            is CibilReport -> return getString(R.string.cibil_report)
            is CreditCard -> return getString(R.string.free_credit_card)
            is GstRegistration -> return getString(R.string.gst_registration)
            is AllGovtCard -> return getString(R.string.all_govt_cards)
            is FarmerPolicy -> return getString(R.string.farmer_policy)
            is AllOtherCard -> return getString(R.string.all_other_docs)
            is DematAccount -> return getString(R.string.demat_account)
            is GovtScheme -> return getString(R.string.txt_govt_schemes)
            is Verification -> return getString(R.string.txt_verification)
            else -> return ""
        }
    }

    /** Dynamic-field services keep the customer name/mobile in textbox1/textbox2; fall back to those when the
     *  named applicantName/mobileNo are empty (they're empty for dynamic services). */
    private fun customerName(item: ServiceModel): String {
        if (item.applicantName.isNotBlank()) return item.applicantName
        return when (item) {
            is AllGovtCard -> item.textbox1
            is CreditCard -> item.textbox1
            is DematAccount -> item.textbox1
            is DrivingLearningLicense -> item.textbox1
            is GovtScheme -> item.textbox1
            is PanCard -> item.textbox1
            is PoliceVerification -> item.textbox1
            is Verification -> item.textbox1
            else -> null
        }.orEmpty()
    }

    private fun customerMobile(item: ServiceModel): String {
        if (item.mobileNo.isNotBlank()) return item.mobileNo
        return when (item) {
            is AllGovtCard -> item.textbox2
            is CreditCard -> item.textbox2
            is DematAccount -> item.textbox2
            is DrivingLearningLicense -> item.textbox2
            is GovtScheme -> item.textbox2
            is PanCard -> item.textbox2
            is PoliceVerification -> item.textbox2
            is Verification -> item.textbox2
            else -> null
        }.orEmpty()
    }

    private fun initWorkbook() {
        workbook = HSSFWorkbook()
        cell = null

        // New Sheet
        sheet = null
        sheet = workbook?.createSheet("Sheet1")

        // Cell style for header row
        val cellStyle = workbook?.createCellStyle()
        cellStyle?.fillForegroundColor = HSSFColor.AQUA.index
        cellStyle?.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
        cellStyle?.alignment = CellStyle.ALIGN_CENTER

        val row = sheet?.createRow(0)
        cell = row?.createCell(0)
        cell?.setCellValue("Date")
        cell?.cellStyle = cellStyle // Create Cells for each row
        cell = row?.createCell(1)
        cell?.setCellValue("Client Name")
        cell?.cellStyle = cellStyle // Create Cells for each row
        cell = row?.createCell(2)
        cell?.setCellValue("Client Mobile")
        cell?.cellStyle = cellStyle // Create Cells for each row
        cell = row?.createCell(3)
        cell?.setCellValue("Service Name")
        cell?.cellStyle = cellStyle // Create Cells for each row
        cell = row?.createCell(4)
        cell?.setCellValue("Type")
        cell?.cellStyle = cellStyle // Create Cells for each row
        cell = row?.createCell(5)
        cell?.setCellValue("Customer Name")
        cell?.cellStyle = cellStyle // Create Cells for each row
        cell = row?.createCell(6)
        cell?.setCellValue("Customer Mobile")
        cell?.cellStyle = cellStyle // Create Cells for each row
        cell = row?.createCell(7)
        cell?.setCellValue("Payment Confirmation")
        cell?.cellStyle = cellStyle // Create Cells for each row
        cell = row?.createCell(8)
        cell?.setCellValue("Closing Update")
        cell?.cellStyle = cellStyle // Create Cells for each row
    }

    private fun fetchData() { //        val cpd: ProgressDialog? = ProgressDialog(mContext)
        //        cpd?.setCancelable(false)
        //        cpd?.show()
        binding.progressBar?.visible()
        launchCoroutine({
            database.child("${Utils.ESUVIDHA_TABLE}_backup").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isFinishing) return
                    binding.progressBar?.gone()
                    Log.i("firebase", "Got value ${snapshot.value}") // Get user value
                    val json = Gson().toJson(snapshot.value)
                    eSuvidhaData = ESuvidhaData()
                    val type: Type = object : TypeToken<HashMap<String, HashMap<String, ESuvidhaType>>?>() {}.type // date, <uid, ESuvidhaType>
                    val map: HashMap<String, HashMap<String, ESuvidhaType>> = Gson().fromJson(json, type)
                    Log.e(TAG, "eSuvidhaData?.map?.: " + eSuvidhaData?.map?.entries?.size.toString())
                    eSuvidhaData?.map = map //                    val dates = eSuvidhaData?.map?.keys?.filterIndexed { index, s ->
                    // Convert map entries to a list and sort by keys in descending order
                    val sortedEntries = eSuvidhaData?.map?.entries?.sortedByDescending { entry ->
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.key)
                    } // Create a new LinkedHashMap with sorted entries
                    val sortedDateMap = HashMap<String, HashMap<String, ESuvidhaType>>()
                    sortedEntries?.forEach { (key, value) ->
                        sortedDateMap[key] = value
                    }
                    eSuvidhaData?.map = sortedDateMap

                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar?.gone()
                }
            })
        }, { coroutineContext, throwable ->
            binding.progressBar?.gone()
            throwable.printStackTrace()
        }) //        app?.apiRequestHelper?.apiService?.esuvidha?.enqueue(object : Callback<ResponseBody> {
        //            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        //                if (cpd != null && cpd.isShowing) cpd.dismiss()
        //                if (response.isSuccessful) {
        //                    val json = response.body()?.string()
        //                    if (json == null || json == "null") {
        //                        toast("No Data found")
        //                        return
        //                    }
        //                    eSuvidhaData = ESuvidhaData()
        //                    val type: Type = object : TypeToken<HashMap<String, HashMap<String, ESuvidhaType>>?>() {}.type // date, <uid, ESuvidhaType>
        //                    val map: HashMap<String, HashMap<String, ESuvidhaType>> = Gson().fromJson(json, type)
        //                    Log.e(TAG, "eSuvidhaData?.map?.: " + eSuvidhaData?.map?.entries?.size.toString())
        //                    eSuvidhaData?.map = map //                    val dates = eSuvidhaData?.map?.keys?.filterIndexed { index, s ->
        //                    // Convert map entries to a list and sort by keys in descending order
        //                    val sortedEntries = eSuvidhaData?.map?.entries?.sortedByDescending { entry ->
        //                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(entry.key)
        //                    }
        //                    // Create a new LinkedHashMap with sorted entries
        //                    val sortedDateMap = HashMap<String, HashMap<String, ESuvidhaType>>()
        //                    sortedEntries?.forEach { (key, value) ->
        //                        sortedDateMap[key] = value
        //                    }
        //                    eSuvidhaData?.map = sortedDateMap
        //                } else {
        //                    Log.e("in", "fail response")
        //                }
        //            }
        //
        //            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        //                progressBar?.gone()
        //                Log.e("in", "failure")
        //                if (cpd != null && cpd.isShowing) cpd.dismiss()
        //            }
        //        })
    }

    private fun downloadExelFile() {
        toast("Downloading...")
        val isExcelGenerated = storeExcelInStorage()
        if (isExcelGenerated) {
            toast("Excel file downloaded successfully")
            try {
                val uriForFile = FileProvider.getUriForFile(baseContext, "${BuildConfig.APPLICATION_ID}.fileprovider", resultFile)
                val intent = Intent(Intent.ACTION_VIEW, uriForFile)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(uriForFile, "application/vnd.ms-excel")
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Method: Generate Excel Workbook
     */
    fun createExcelWorkbook(columnList: LinkedHashMap<String, String>) { // New Workbook
        val rowData = sheet?.createRow(sheet?.lastRowNum?.plus(1) ?: 1)
        columnList.keys.forEachIndexed { index, s -> // Generate column headings
            cell = rowData?.createCell(index)
            cell?.setCellValue(columnList[s])
        }
    }

    private fun storeExcelInStorage(): Boolean {
        var isSuccess: Boolean
        val fileDirectory = File(Environment.getExternalStorageDirectory(), "Download")
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs()
        }
        resultFile = File(fileDirectory.absolutePath + File.separator.toString() + "${System.currentTimeMillis()}.xls") //            simplyPdfDocument = SimplyPdf.with(baseContext, resultFile).colorMode(DocumentInfo.ColorMode.COLOR).paperSize(PrintAttributes.MediaSize.ISO_A4) //                .margin(Margin(20U, 20U, 20U, 20U)) //                .pageModifier(PageHeader(headerList)).firstPageBackgroundColor(Color.WHITE).paperOrientation(DocumentInfo.Orientation.PORTRAIT).build()
        resultFileUri = Uri.fromFile(resultFile) //        val file = File(this.getExternalFilesDir(null), fileName)
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(resultFile)
            workbook?.write(fileOutputStream)
            Log.e("storeExcel", "Writing file $resultFile")
            isSuccess = true
        } catch (e: IOException) {
            Log.e("storeExcel", "Error writing Exception: ", e)
            isSuccess = false
        } catch (e: Exception) {
            Log.e("storeExcel", "Failed to save file due to Exception: ", e)
            isSuccess = false
        } finally {
            try {
                fileOutputStream?.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return isSuccess
    }

    fun getTodayDate(): String {
        return SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).format(Date())
    }

    private fun getUserList() {
        database.child(Utils.USERS_TABLE).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("firebase", "Got value ${snapshot.value}") // Get user value
                val json = Gson().toJson(snapshot.value)
                val type: Type = object : TypeToken<HashMap<String, User>?>() {}.type
                val map: HashMap<String, User> = Gson().fromJson(json, type)
                userList.addAll(map.values.toMutableList())
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun findUserByUid(uid: String): User? {
        userList.forEach {
            if (it.uid.equals(uid)) {
                return it
            }
        }
        return null
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
