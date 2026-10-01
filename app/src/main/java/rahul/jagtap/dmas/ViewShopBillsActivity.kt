package rahul.jagtap.dmas

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rahul.jagtap.dmas.adapter.ShopBillListAdapter
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.Bill
import rahul.jagtap.dmas.model.ShopBill
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import java.util.ArrayList

class ViewShopBillsActivity : BaseActivity() {
    private var uid: String? = null
    private var isAdmin: String? = null
    private var isEmployee: Boolean = false
    private val shopBillList = ArrayList<ShopBill>()
    private var shopBillListAdapter: ShopBillListAdapter? = null
    private lateinit var binding: ActivityViewBillsBinding
    private val TAG = "ViewShopBillsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
        setContentView(binding.root)

        uid = app?.preferences?.loggedInUser?.uid
        isAdmin = app?.preferences?.loggedInUser?.isAdmin
        isEmployee = app?.preferences?.loggedInUser?.userType == "2"

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_bills)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        shopBillListAdapter = ShopBillListAdapter(mContext, shopBillList)
        binding.recyclerView?.adapter = shopBillListAdapter
        listenForBillsData()
    }

    private fun listenForBillsData() {
        val currentUid = uid
        if (currentUid.isNullOrBlank() && isAdmin != "1" && !isEmployee) {
            showEmptyState()
            return
        }

        binding.progressBar?.visible()
        Firebase.database.getReference(Utils.BILLS_TABLE)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val billsByUid = LinkedHashMap<String, MutableList<Bill>>()

                    // Database structure: bills / date / uid / pushKey / bill fields
                    for (dateSnapshot in snapshot.children) {
                        for (userSnapshot in dateSnapshot.children) {
                            val billUid = userSnapshot.key ?: continue
                            if (isAdmin != "1" && !isEmployee && billUid != currentUid) continue

                            val userBills = billsByUid.getOrPut(billUid) { ArrayList() }
                            for (billSnapshot in userSnapshot.children) {
                                val bill = billSnapshot.getValue(Bill::class.java) ?: continue
                                if (bill.pushKey.isNullOrBlank()) bill.pushKey = billSnapshot.key
                                if (bill.uid.isNullOrBlank()) bill.uid = billUid
                                userBills.add(bill)
                            }
                        }
                    }

                    shopBillList.clear()
                    if (billsByUid.isEmpty()) {
                        showEmptyState()
                        return
                    }

                    // Load shop names asynchronously, then publish the completed list once.
                    var pending = billsByUid.size
                    billsByUid.forEach { (billUid, bills) ->
                        Firebase.database.getReference(Utils.USERS_TABLE).child(billUid)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                    val user = userSnapshot.getValue(User::class.java)
                                    shopBillList.add(ShopBill(user?.shopName.orEmpty(), billUid, bills))
                                    pending--
                                    if (pending == 0) publishList()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e(TAG, "Could not load shop name for $billUid: ${error.message}")
                                    shopBillList.add(ShopBill(billUid, billUid, bills))
                                    pending--
                                    if (pending == 0) publishList()
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Could not load bills: ${error.message}")
                    binding.progressBar?.gone()
                    toast("Bills load failed: ${error.message}")
                    showEmptyState()
                }
            })
    }

    private fun publishList() {
        shopBillList.sortBy { it.shopName?.lowercase() ?: "" }
        binding.progressBar?.gone()
        shopBillListAdapter?.notifyDataSetChanged()
        if (shopBillList.isEmpty()) showEmptyState() else {
            binding.recyclerView?.visible()
            binding.tvError?.gone()
        }
    }

    private fun showEmptyState() {
        binding.progressBar?.gone()
        binding.recyclerView?.gone()
        binding.tvError?.visible()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            Utils.hideSoftKeyboard(this)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
