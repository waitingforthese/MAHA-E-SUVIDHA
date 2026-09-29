package rahul.jagtap.dmas.admin.esuvidha.newimpl

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.JsonStorage
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.ESuvidhaDateV2ListAdapter
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.EsuvidhaInfo
import rahul.jagtap.dmas.utils.Utils
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Collections


class ESuvidhaDatesV2Activity : BaseActivity() {
    var list = ArrayList<EsuvidhaInfo>()
    var adapter: ESuvidhaDateV2ListAdapter? = null

    private lateinit var binding: ActivityViewBillsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_online_e_suvidha)

        val json_data = JsonStorage.getJsonString()
        val type: Type = object : TypeToken<ArrayList<EsuvidhaInfo>>() {}.type
        if (!TextUtils.isEmpty(json_data)) list = Gson().fromJson(json_data, type) ?: ArrayList()

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = ESuvidhaDateV2ListAdapter(mContext, list)
        val df: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        Collections.sort(list, Comparator { o1, o2 ->
            return@Comparator df.parse(o2.strDate.toString())!!.compareTo(df.parse(o1.strDate.toString()))
        })
        binding.recyclerView?.adapter = adapter
        adapter?.dateListListener = object : ESuvidhaDateV2ListAdapter.DateListListener {
            override fun onItemClick(position: Int) {
                val esuvidhaInfo = list[position]
                JsonStorage.setUserIdsJsonString(Gson().toJson(esuvidhaInfo.userUidList))
                startActivity(Intent(mContext, ESuvidhaUsersV2Activity::class.java).putExtra("type", intent.getStringExtra("type")).putExtra("title", esuvidhaInfo.strDate.toString()) //                    .putExtra("json_data", Gson().toJson(esuvidhaInfo.userUidList))
                )
            }
        } //        setAdminBillsData()
    }

    //    private fun setAdminBillsData() {
    //        progressBar?.visible()
    //        app?.apiRequestHelper?.apiService?.esuvidha?.enqueue(object : Callback<ResponseBody> {
    //            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
    //                progressBar?.gone()
    //                if (response.isSuccessful) {
    //                    val json = response.body()?.string()
    //                    if (json == null || json == "null") {
    //                        notifyAdapter()
    //                        return
    //                    }
    //                    eSuvidhaData = ESuvidhaData()
    //                    val type: Type = object : TypeToken<HashMap<String, HashMap<String, ESuvidhaType>>?>() {}.type // date, <uid, ESuvidhaType>
    //                    val map: HashMap<String, HashMap<String, ESuvidhaType>> = Gson().fromJson(json, type)
    //                    eSuvidhaData?.map = map
    //                    val dates = eSuvidhaData?.map?.keys?.filterIndexed { index, s ->
    //                        if (isAdmin != "1") eSuvidhaData?.map?.get(s)?.containsKey(uid) == true else true
    //                    }
    //                    val dateList: ArrayList<ESuvidhaDate> = ArrayList()
    //                    dates?.forEach { date ->
    //                        val hashMap = map[date]
    //                        val strEsuvidhaTypes = StringBuilder()
    //                        hashMap?.values?.mapIndexed { _, type ->
    //                            if (type.cibil_reports != null) strEsuvidhaTypes.append("Cibil Report, सिबिल रिपोर्ट, ")
    //                            if (type.age_certificates != null) strEsuvidhaTypes.append("Age Nationality and Domicile Certificate, वय, राष्ट्रीयत्व आणि अधिवास प्रमाणपत्र, ")
    //                            if (type.businessPanCards != null) strEsuvidhaTypes.append("Business Pan Card, व्यवसाय पॅन कार्ड, ")
    //                            if (type.gazzets != null) strEsuvidhaTypes.append("Gazzet, गँझेट, ")
    //                            if (type.driving_learning_licenses != null) strEsuvidhaTypes.append("Driving Learning License, ड्रायविंग लर्निंग लायसेन्स, ")
    //                            if (type.edit_pan_aadhar_cards != null) strEsuvidhaTypes.append("Edit Pan Aadhar Card, फोटो शॉप पँण कार्ड / आधार कार्ड, ")
    //                            if (type.foodLicenses != null) strEsuvidhaTypes.append("Food License, फूड लायसन्स, ")
    //                            if (type.electionCards != null) strEsuvidhaTypes.append("Election Card, इलेक्शन कार्ड, ")
    //                            if (type.jyotish_shastra != null) strEsuvidhaTypes.append("Jyotish Shastra, ज्योतिष शास्त्रींना प्रश्न विचारा, ")
    //                            if (type.panCards != null) strEsuvidhaTypes.append("Pan Card, पॅन कार्ड, ")
    //                            if (type.income_certificates != null) strEsuvidhaTypes.append("Income Certificate, मिळकतीचे प्रमाणपत्र, ")
    //                            if (type.nepalMoneyTransfers != null) strEsuvidhaTypes.append("Nepal Money Transfer, नेपाळ मनी ट्रान्सफर, ")
    //                            if (type.passports != null) strEsuvidhaTypes.append("Passport, पासपोर्ट (नवीन/दुरूस्ती), ")
    //                            if (type.providentFunds != null) strEsuvidhaTypes.append("Provident Fund, प्रोव्हिडंट फंड, ")
    //                            if (type.police_verifications != null) strEsuvidhaTypes.append("Police Verification, पोलीस व्हेरिफिकेशन - चरित्र प्रमाणपत्र, ")
    //                            if (type.railwayTicketBookings != null) strEsuvidhaTypes.append("Railway Ticket Booking, रेल्वे तिकीट बुकिंग, ")
    //                            if (type.shopActs != null) strEsuvidhaTypes.append("Shop Act, शॉप ऍक्ट, ")
    //                            if (type.udyamAadhars != null) strEsuvidhaTypes.append("Udyam Aadhar, उद्यम आधार")
    //                        }
    //                        dateList.add(ESuvidhaDate(date, strEsuvidhaTypes.toString()))
    //                    }
    //                    if (dateList.isNotEmpty()) {
    //                        list.addAll(dateList)
    //                        adapter?.itemList?.let { adapter?.list_search?.addAll(it) }
    //                        val df: DateFormat = SimpleDateFormat("dd-MM-yyyy")
    //                        Collections.sort(list, Comparator { o1, o2 ->
    //                            return@Comparator df.parse(o2.date.toString())!!.compareTo(df.parse(o1.date.toString()))
    //                        })
    //                        notifyAdapter()
    //                        recyclerView?.visible()
    //                        tvError?.gone()
    //                        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
    //                            override fun onQueryTextSubmit(query: String): Boolean { //Do some magic
    //                                if (adapter != null) adapter?.filter(query)
    //                                return false
    //                            }
    //
    //                            override fun onQueryTextChange(newText: String): Boolean { //Do some magic
    //                                if (adapter != null) adapter?.filter(newText)
    //                                return false
    //                            }
    //                        })
    //                    } else {
    //                        recyclerView?.gone()
    //                        tvError?.visible()
    //                    }
    //                } else {
    //                    Log.e("in", "fail response")
    //                    notifyAdapter()
    //                }
    //            }
    //
    //            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
    //                progressBar?.gone()
    //                Log.e("in", "failure")
    //                notifyAdapter()
    //            }
    //        })
    //    }

    private fun notifyAdapter() {
        binding.progressBar?.gone()
        adapter?.notifyDataSetChanged()
        if (list.size == 0) {
            binding.tvError?.visible()
        } else {
            binding.tvError?.gone()
        }
    }

    //    override fun onCreateOptionsMenu(menu: Menu): Boolean {
    //        menuInflater.inflate(R.menu.menu_search, menu)
    //        val item = menu.findItem(R.id.action_search)
    //        val action_download = menu.findItem(R.id.action_download)
    //        searchView.setMenuItem(item)
    //        action_download?.isVisible = false
    //        return true
    //    }

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
