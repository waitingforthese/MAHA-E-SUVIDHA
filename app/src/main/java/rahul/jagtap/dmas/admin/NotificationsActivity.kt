package rahul.jagtap.dmas.admin

import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.NotificationAdapter
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.utils.Utils

class NotificationsActivity : BaseActivity() {
    private var uid: String? = ""
    private var email: String? = ""
    private val TAG = NotificationsActivity::class.java.simpleName
    var notificationItemList = ArrayList<NotificationItem>()
    var notificationAdapter: NotificationAdapter? = null

    private lateinit var binding: ActivityViewBillsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)

        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_notifications)

        notificationAdapter = NotificationAdapter(mContext, notificationItemList)
        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, true)
        binding.recyclerView?.adapter = notificationAdapter
        setListData()
    }

    private fun setListData() {
        Firebase.database.getReference(Utils.NOTIFICATIONS_TABLE)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding.progressBar?.visible()
                    for (child in dataSnapshot.children) {
                        val bill = child.getValue(NotificationItem::class.java)
                        bill?.let { notificationItemList.add(it) }
                    }
                    binding.progressBar?.gone()
                    notificationAdapter?.notifyDataSetChanged()
                    if (notificationItemList.size == 0) {
                        binding.tvError?.visible()
                    } else {
                        binding.tvError?.gone()
                    }
                    binding.recyclerView.postDelayed({
                        binding.recyclerView.adapter?.itemCount?.minus(1)?.let { binding.recyclerView.scrollToPosition(it) }
                    }, 1200)
                }

                override fun onCancelled(dataSnapshot: DatabaseError) {
                }
            })
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
