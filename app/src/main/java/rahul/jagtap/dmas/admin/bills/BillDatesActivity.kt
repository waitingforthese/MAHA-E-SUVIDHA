package rahul.jagtap.dmas.admin.bills

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.BillDateListAdapter
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.Bill
import rahul.jagtap.dmas.model.BillData
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class BillDatesActivity : BaseActivity() {
    private var billData: BillData? = null
    private var uid: String? = ""
    private var isAdmin: String? = ""
    var list = ArrayList<String>()

    var adapter: BillDateListAdapter? = null

    private lateinit var binding: ActivityViewBillsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)

        uid = app?.preferences?.loggedInUser?.uid
        isAdmin = app?.preferences?.loggedInUser?.isAdmin

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_accounting_services)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = BillDateListAdapter(mContext, list)
        binding.recyclerView?.adapter = adapter
        adapter?.dateListListener = object : BillDateListAdapter.DateListListener {
            override fun onItemClick(position: Int) {
                val date = list[position]
                val usersData: HashMap<String, HashMap<String, Bill>>? = billData?.map?.get(date)
                if (usersData != null) {
                    if (isAdmin == "1" || app?.preferences?.loggedInUser?.userType == "2") {
                        startActivity(Intent(mContext, BillUsersActivity::class.java).putExtra("usersData", usersData))
                    } else {
                        val billData: HashMap<String, Bill>? = usersData[uid]
                        if (billData != null) {
                            startActivity(Intent(mContext, BillsActivity::class.java).putExtra("billData", billData))
                        } else toast("No records found.")
                    }
                } else toast("No records found.")
            }
        }
        setAdminBillsData()
    }

    private fun setAdminBillsData() {
        binding.progressBar?.visible()
        app?.apiRequestHelper?.apiService?.bills?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                binding.progressBar?.gone()
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (json == null || json == "null") {
                        notifyAdapter()
                        return
                    }
                    billData = BillData()
                    val type: Type = object : TypeToken<HashMap<String, HashMap<String, HashMap<String, Bill>>>?>() {}.type
                    val map: HashMap<String, HashMap<String, HashMap<String, Bill>>> = Gson().fromJson(json, type)
                    billData?.map = map
                    val dateList = billData?.map?.keys?.filterIndexed { index, s ->
                        if (isAdmin != "1" && app?.preferences?.loggedInUser?.userType != "2") billData?.map?.get(s)?.containsKey(uid) == true else true
                    }?.toMutableList()
                    if (dateList != null && dateList.isNotEmpty()) {
                        //                        dateList.sortWith { emp1, emp2 -> emp2.compareTo(emp1) }
                        val df: DateFormat = SimpleDateFormat("dd-MM-yyyy")
                        Collections.sort(dateList, Comparator { o1, o2 ->
                            return@Comparator df.parse(o2).compareTo(df.parse(o1))
                        })
                        list.addAll(dateList)
                        notifyAdapter()
                        binding.recyclerView?.visible()
                        binding.tvError?.gone()
                    } else {
                        binding.recyclerView?.gone()
                        binding.tvError?.visible()
                    }
                } else {
                    Log.e("in", "fail response")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.progressBar?.gone()
                Log.e("in", "failure")
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

    //    private fun setUserBillsData(dataSnapshot: DataSnapshot, uid: String) {
    //        binding.progressBar?.visible()
    //        getShopName(uid)
    //        val billList = ArrayList<Bill>()
    //        for (child in dataSnapshot.children) {
    //            val bill = child.getValue(Bill::class.java)
    //            bill?.let { billList.add(it) }
    //        }
    //        shopBillList.add(ShopBill(shopName, uid, billList))
    //        notifyAdapter()
    //    }

    companion object {

    }
}
