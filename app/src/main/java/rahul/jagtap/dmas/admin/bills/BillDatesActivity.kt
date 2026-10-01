package rahul.jagtap.dmas.admin.bills

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*


class BillDatesActivity : BaseActivity() {
    private var billData: BillData? = null
    private var uid: String? = ""
    private var isAdmin: String? = ""
    private var requestedUid: String? = null
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
        requestedUid = intent.getStringExtra("uid")

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
                    val targetUid = requestedUid ?: uid
                    if (requestedUid == null && (isAdmin == "1" || app?.preferences?.loggedInUser?.userType == "2")) {
                        startActivity(Intent(mContext, BillUsersActivity::class.java).putExtra("usersData", usersData))
                    } else {
                        val selectedBills: HashMap<String, Bill>? = if (targetUid.isNullOrBlank()) null else usersData[targetUid]
                        if (selectedBills != null) {
                            startActivity(Intent(mContext, BillsActivity::class.java).putExtra("billData", selectedBills))
                        } else toast("No records found.")
                    }
                } else toast("No records found.")
            }
        }
        setAdminBillsData()
    }

    private fun setAdminBillsData() {
        binding.progressBar?.visible()
        binding.recyclerView?.gone()
        binding.tvError?.gone()

        database.child(Utils.BILLS_TABLE).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                billData = BillData()
                val dateMap = HashMap<String, HashMap<String, HashMap<String, Bill>>>()
                for (dateSnapshot in snapshot.children) {
                    val dateKey = dateSnapshot.key ?: continue
                    val usersMap = HashMap<String, HashMap<String, Bill>>()
                    for (userSnapshot in dateSnapshot.children) {
                        val userKey = userSnapshot.key ?: continue
                        if (requestedUid != null && requestedUid != userKey) continue
                        if (isAdmin != "1" && app?.preferences?.loggedInUser?.userType != "2" && userKey != uid) continue

                        val billsMap = HashMap<String, Bill>()
                        for (billSnapshot in userSnapshot.children) {
                            val bill = billSnapshot.getValue(Bill::class.java) ?: continue
                            if (bill.uid.isNullOrBlank()) bill.uid = userKey
                            if (bill.pushKey.isNullOrBlank()) bill.pushKey = billSnapshot.key
                            billsMap[billSnapshot.key ?: continue] = bill
                        }
                        if (billsMap.isNotEmpty()) usersMap[userKey] = billsMap
                    }
                    if (usersMap.isNotEmpty()) dateMap[dateKey] = usersMap
                }
                billData?.map = dateMap

                list.clear()
                list.addAll(dateMap.keys.sortedWith(Comparator { first, second ->
                    parseBillDate(second).compareTo(parseBillDate(first))
                }))
                notifyAdapter()
                if (list.isEmpty()) {
                    binding.recyclerView?.gone()
                    binding.tvError?.visible()
                } else {
                    binding.recyclerView?.visible()
                    binding.tvError?.gone()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar?.gone()
                binding.recyclerView?.gone()
                binding.tvError?.visible()
                Log.e("BillDatesActivity", "Unable to load bills", error.toException())
                toast("Bills load झाले नाहीत. कृपया पुन्हा प्रयत्न करा.")
            }
        })
    }

    private fun parseBillDate(value: String): Date {
        val formats = arrayOf("dd-MM-yy", "dd-MM-yyyy")
        for (pattern in formats) {
            val format = SimpleDateFormat(pattern, Locale.ENGLISH).apply { isLenient = false }
            val position = ParsePosition(0)
            val parsed = format.parse(value, position)
            if (parsed != null && position.index == value.length) return parsed
        }
        return Date(0)
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
