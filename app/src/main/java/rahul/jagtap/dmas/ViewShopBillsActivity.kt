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
import java.util.*

class ViewShopBillsActivity : BaseActivity() {
    private var uid: String? = ""
    private var isAdmin: String? = ""
    private var isEmployee: Boolean = false
    var shopBillList = ArrayList<ShopBill>()
    var shopBillListAdapter: ShopBillListAdapter? = null
    var shopName = ""

    private lateinit var binding: ActivityViewBillsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)

        uid = app?.preferences?.loggedInUser?.uid
        isAdmin = app?.preferences?.loggedInUser?.isAdmin
        isEmployee = app?.preferences?.loggedInUser?.userType == "2"

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_bills)

        binding.recyclerView?.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        shopBillListAdapter = ShopBillListAdapter(mContext, shopBillList)
        binding.recyclerView?.adapter = shopBillListAdapter
        listenForBillsData()
    }

    private fun listenForBillsData() {
        if (isAdmin == "1" || isEmployee) {
            Firebase.database.getReference(Utils.BILLS_TABLE)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        setAdminBillsData(dataSnapshot)
                    }

                    override fun onCancelled(dataSnapshot: DatabaseError) {
                    }
                })
        } else {
            Firebase.database.getReference(Utils.BILLS_TABLE).child(uid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        setUserBillsData(dataSnapshot, uid!!)
                    }

                    override fun onCancelled(dataSnapshot: DatabaseError) {
                    }
                })
        }
    }

    private fun setUserBillsData(dataSnapshot: DataSnapshot, uid: String) {
        binding.progressBar?.visible()
        getShopName(uid)
        val billList = ArrayList<Bill>()
        for (child in dataSnapshot.children) {
            val bill = child.getValue(Bill::class.java)
            bill?.let { billList.add(it) }
        }
        shopBillList.add(ShopBill(shopName, uid, billList))
        notifyAdapter()
    }

    private fun setAdminBillsData(dataSnapshot: DataSnapshot) {
        binding.progressBar?.visible()
        for (snapShot in dataSnapshot.children) {
            val uid: String? = snapShot.key
            Log.e("uid key", "$uid")
            if (uid != null) {
                getShopName(uid)
                val billList = ArrayList<Bill>()
                for (child in snapShot.children) {
                    val bill = child.getValue(Bill::class.java)
                    bill?.let { billList.add(it) }
                }
                shopBillList.add(ShopBill(shopName, uid, billList))
            }
        }
        notifyAdapter()
    }

    private fun getShopName(uid: String) {
        shopName = ""
        database.child(Utils.USERS_TABLE).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    shopName = user?.shopName ?: ""
                }
            })
    }

    private fun notifyAdapter() {
        binding.progressBar?.gone()
        shopBillListAdapter?.notifyDataSetChanged()
        if (shopBillList.size == 0) {
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

    companion object {

    }
}
