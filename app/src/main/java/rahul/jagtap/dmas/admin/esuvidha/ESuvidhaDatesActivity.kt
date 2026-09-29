package rahul.jagtap.dmas.admin.esuvidha

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rahul.jagtap.dmas.widget.MaterialSearchView
import okhttp3.ResponseBody
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.JsonStorage
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.ESuvidhaDateListAdapter
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.ESuvidhaData
import rahul.jagtap.dmas.model.ESuvidhaDate
import rahul.jagtap.dmas.model.ESuvidhaType
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ESuvidhaDatesActivity : BaseActivity() {
    private var eSuvidhaData: ESuvidhaData? = null
    private var uid: String? = ""
    private var isAdmin: String? = ""
    var list = ArrayList<ESuvidhaDate>()
    var adapter: ESuvidhaDateListAdapter? = null

    private lateinit var binding: ActivityViewBillsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid = app?.preferences?.loggedInUser?.uid
        isAdmin = app?.preferences?.loggedInUser?.isAdmin

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_online_e_suvidha)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = ESuvidhaDateListAdapter(mContext, list)
        binding.recyclerView?.adapter = adapter
        adapter?.dateListListener = object : ESuvidhaDateListAdapter.DateListListener {
            override fun onItemClick(position: Int) {
                val date = list[position]
                val usersData: HashMap<String, ESuvidhaType>? = eSuvidhaData?.map?.get(date?.date)
                if (usersData != null) {
                    if (isAdmin == "1" || app?.preferences?.loggedInUser?.userType == "2") {
                        JsonStorage.setJsonString(Gson().toJson(usersData))
                        startActivity(Intent(mContext, ESuvidhaUsersActivity::class.java)//.putExtra("usersData", usersData)
                            .putExtra("title", date?.date))
                    } else {
                        val eSuvidhaType = usersData[uid]
                        if (eSuvidhaType != null) {
                            startActivity(Intent(mContext, ESuvidhaTypesActivity::class.java).putExtra("eSuvidhaType", Gson().toJson(eSuvidhaType)))
                        } else {
                            toast("No records found.")
                        }
                    }
                } else toast("No records found.")
            }
        }
        setAdminBillsData()
    }

    private fun setAdminBillsData() {
        binding.progressBar?.visible()
        app?.apiRequestHelper?.apiService?.esuvidha?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                binding.progressBar?.gone()
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (json == null || json == "null") {
                        notifyAdapter()
                        return
                    }
                    eSuvidhaData = ESuvidhaData()
                    val type: Type = object : TypeToken<HashMap<String, HashMap<String, ESuvidhaType>>?>() {}.type
                    val map: HashMap<String, HashMap<String, ESuvidhaType>> = Gson().fromJson(json, type)
                    eSuvidhaData?.map = map
                    val dates = eSuvidhaData?.map?.keys?.filterIndexed { index, s ->
                        if (isAdmin != "1" && app?.preferences?.loggedInUser?.userType != "2") eSuvidhaData?.map?.get(s)?.containsKey(uid) == true else true
                    }
                    val dateList: ArrayList<ESuvidhaDate> = ArrayList()
                    dates?.forEach { date ->
                        val hashMap = map[date]
                        val strEsuvidhaTypes = StringBuilder()
                        hashMap?.values?.mapIndexed { _, type ->
                            if (type.panCards != null) strEsuvidhaTypes.append("Pan Card, पॅन कार्ड, ")
                            if (type.shopActs != null) strEsuvidhaTypes.append("Shop Act, शॉप ऍक्ट, ")
                            if (type.udyamAadhars != null) strEsuvidhaTypes.append("Udyam Aadhar, उद्यम आधार")
                            if (type.foodLicenses != null) strEsuvidhaTypes.append("Food License, फूड लायसन्स, ")
                            if (type.providentFunds != null) strEsuvidhaTypes.append("Provident Fund, प्रोव्हिडंट फंड, ")
                            if (type.nepalMoneyTransfers != null) strEsuvidhaTypes.append("Nepal Money Transfer, नेपाळ मनी ट्रान्सफर, ")
                            if (type.railwayTicketBookings != null) strEsuvidhaTypes.append("Railway Ticket Booking, रेल्वे तिकीट बुकिंग, ")
                            if (type.businessPanCards != null) strEsuvidhaTypes.append("Business Pan Card, व्यवसाय पॅन कार्ड, ")
                            if (type.electionCards != null) strEsuvidhaTypes.append("Election Card, इलेक्शन कार्ड, ")
                            if (type.passports != null) strEsuvidhaTypes.append("Passport, पासपोर्ट (नवीन/दुरूस्ती), ")
                            if (type.driving_learning_licenses != null) strEsuvidhaTypes.append("Driving Learning License, ड्रायविंग लर्निंग लायसेन्स, ")
                            if (type.edit_pan_aadhar_cards != null) strEsuvidhaTypes.append("Edit Pan Aadhar Card, फोटो शॉप पँण कार्ड / आधार कार्ड, ")
                            if (type.police_verifications != null) strEsuvidhaTypes.append("Police Verification, पोलीस व्हेरिफिकेशन - चरित्र प्रमाणपत्र, ")
                            if (type.income_certificates != null) strEsuvidhaTypes.append("Income Certificate, मिळकतीचे प्रमाणपत्र, ")
                            if (type.age_certificates != null) strEsuvidhaTypes.append("Age Nationality and Domicile Certificate, वय, राष्ट्रीयत्व आणि अधिवास प्रमाणपत्र, ")
                            if (type.gazzets != null) strEsuvidhaTypes.append("Gazzet, गँझेट, ")
                            if (type.jyotish_shastra != null) strEsuvidhaTypes.append("Jyotish Shastra, ज्योतिष शास्त्रींना प्रश्न विचारा, ")
                            if (type.cibil_reports != null) strEsuvidhaTypes.append("Cibil Report, सिबिल रिपोर्ट, ")
                            if (type.credit_cards != null) strEsuvidhaTypes.append("Credit Card, क्रेडीट कार्ड, ")
                            if (type.gst_regs != null) strEsuvidhaTypes.append("GST Registration, जीएसटी नोंदणी, ")
                            if (type.all_govt_cards != null) strEsuvidhaTypes.append("All Government Card, सर्व सरकारी कार्ड, ")
                            if (type.farmer_policies != null) strEsuvidhaTypes.append("Farmer Policy, शेतकरी योजना, ")
                            if (type.all_other_cards != null) strEsuvidhaTypes.append("All Other Card, इतर सर्व कार्ड, ")
                            if (type.demat_accounts != null) strEsuvidhaTypes.append("Demat Account, डीमॅट खाते, ")
                            if (type.govt_schemes != null) strEsuvidhaTypes.append("Government Scheme, सरकारी योजना, ")
                        }
                        dateList.add(ESuvidhaDate(date, strEsuvidhaTypes.toString()))
                    }
                    if (dateList.isNotEmpty()) {
                        list.addAll(dateList)
                        adapter?.itemList?.let { adapter?.list_search?.addAll(it) }
                        val df: DateFormat = SimpleDateFormat("dd-MM-yyyy")
                        Collections.sort(list, Comparator { o1, o2 ->
                            return@Comparator df.parse(o2.date.toString())!!.compareTo(df.parse(o1.date.toString()))
                        })
                        notifyAdapter()
                        binding.recyclerView?.visible()
                        binding.tvError?.gone()
                        binding.toolbarLayout.searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String): Boolean { //Do some magic
                                if (adapter != null) adapter?.filter(query)
                                return false
                            }

                            override fun onQueryTextChange(
                                newText: String
                            ): Boolean { //Do some magic
                                if (adapter != null) adapter?.filter(newText)
                                return false
                            }
                        })
                    } else {
                        binding.recyclerView?.gone()
                        binding.tvError?.visible()
                    }
                } else {
                    Log.e("in", "fail response")
                    notifyAdapter()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.progressBar?.gone()
                Log.e("in", "failure")
                notifyAdapter()
            }
        })
    }

    private fun notifyAdapter() {
        binding.progressBar?.gone()
        adapter?.notifyDataSetChanged()
        if (list.size == 0) {
            binding.tvError?.visible()
        } else {
            binding.tvError?.gone()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val item = menu.findItem(R.id.action_search)
        val action_download = menu.findItem(R.id.action_download)
        binding.toolbarLayout.searchView.setMenuItem(item)
        action_download?.isVisible = false
        return true
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
