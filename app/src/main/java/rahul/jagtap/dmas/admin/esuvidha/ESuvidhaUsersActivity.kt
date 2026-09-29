package rahul.jagtap.dmas.admin.esuvidha

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.JsonStorage
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.ESuvidhaUserListAdapter
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.ESuvidhaType
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

class ESuvidhaUsersActivity : BaseActivity() {
    private lateinit var hashMap: HashMap<String, ESuvidhaType>
    var list = ArrayList<User>()
    var adapter: ESuvidhaUserListAdapter? = null

    private lateinit var binding: ActivityViewBillsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val json_data = JsonStorage.getJsonString()
        val type: Type = object : TypeToken<HashMap<String, ESuvidhaType>>() {}.type
        hashMap = Gson().fromJson(json_data, type)
//        hashMap = intent.getSerializableExtra("usersData") as HashMap<String, ESuvidhaType>

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = intent.getStringExtra("title") ?: getString(R.string.txt_online_e_suvidha)

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = ESuvidhaUserListAdapter(mContext, list)
        binding.recyclerView?.adapter = adapter

        setUserList()

        adapter?.dateListListener = object : ESuvidhaUserListAdapter.DateListListener {
            override fun onItemClick(position: Int) {
                val uid = list[position].uid
                val eSuvidhaType = hashMap?.get(uid)
                if (eSuvidhaType != null) {
                    startActivity(Intent(mContext, ESuvidhaTypesActivity::class.java)
                        .putExtra("eSuvidhaType", Gson().toJson(eSuvidhaType))
                        .putExtra("creatorJsonString", Gson().toJson(list[position]))
                    )
                } else toast("No records found.")
            }
        }
    }

    fun setUserList() {
        hashMap.keys.forEachIndexed { index, s ->
            database.child(Utils.USERS_TABLE).child(s).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("TAG", "getUser:onCancelled", databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get user value
                    val userInfo = dataSnapshot.getValue(User::class.java)
                    if (userInfo != null) {
                        list.add(userInfo)
                        notifyAdapter()
                    }
                }
            })
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
