package rahul.jagtap.dmas.admin

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rahul.jagtap.dmas.widget.MaterialSearchView
import okhttp3.ResponseBody
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.adapter.DayBookAdapter
import rahul.jagtap.dmas.databinding.ActivityDayBookListBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.DayBook
import rahul.jagtap.dmas.user.DayBookActivity
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class DayBookListActivity : BaseActivity() {
    private var uid: String? = ""
    private var email: String? = ""
    private val TAG = DayBookListActivity::class.java.simpleName
    var dayBookList = ArrayList<DayBook>()
    var dayBookAdapter: DayBookAdapter? = null
    lateinit var binding: ActivityDayBookListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDayBookListBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)

        email = app?.preferences?.loggedInUser?.email
        uid = app?.preferences?.loggedInUser?.uid

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarTitle?.text = getString(R.string.txt_day_book_list)
        dayBookList.clear()
        dayBookAdapter = DayBookAdapter(mContext, dayBookList)
        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.recyclerView?.adapter = dayBookAdapter
        dayBookAdapter?.itemClickListener = object : DayBookAdapter.ItemClickListener {
            override fun onItemClick(position: Int, dayBook: DayBook?) {
                startActivity(Intent(mContext, DayBookActivity::class.java).putExtra("userUid", dayBook?.userUid))
            }

            override fun onDeleteClick(position: Int, dayBook: DayBook?) { // Delete DB Record
                dayBook?.userUid?.let { database.child(Utils.DAY_BOOK_TABLE).child(it).removeValue() }
                dayBookList.removeAt(position)
                dayBookAdapter?.notifyItemRemoved(position)
//                toast("Record deleted successfully")
            }
        }
        binding.searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { //Do some magic
                if (dayBookAdapter != null) dayBookAdapter?.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean { //Do some magic
                if (dayBookAdapter != null) dayBookAdapter?.filter(newText)
                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        dayBookList.clear()
        dayBookAdapter?.notifyDataSetChanged()
        setListData()
    }

    private fun setListData() {
        binding.progressBar?.visible()
        app?.apiRequestHelper?.apiService?.dayBooks?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) { //                Log.e("TAG", "onResponse: after")
                binding.progressBar?.gone()
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (json == null || json == "null") {
                        return
                    }

                    val type: Type = object : TypeToken<HashMap<String, DayBook>>() {}.type
                    val map: HashMap<String, DayBook>? = if (!TextUtils.isEmpty(json)) Gson().fromJson(json, type) else null //                    val dayBookListData = if (!TextUtils.isEmpty(json)) Gson().fromJson(json, DayBookListData::class.java) else null
                    dayBookList.clear()
                    map?.values?.forEach {
                        dayBookList.add(it)
                    }
                    dayBookAdapter?.notifyDataSetChanged()
                    if (dayBookList.size == 0) {
                        binding.tvError?.visible()
                    } else {
                        binding.tvError?.gone()
                    }
                } else {
                    Log.e("in", "fail response")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.progressBar?.gone()
            }
        }) //        Firebase.database.getReference(Utils.DAY_BOOK_TABLE).addValueEventListener(object : ValueEventListener {
        //                override fun onDataChange(dataSnapshot: DataSnapshot) {
        //                    progressBar?.visible()
        //                    for (child in dataSnapshot.children) {
        //                        val bill = child.getValue(DayBook::class.java)
        //                        bill?.let { dayBookList.add(it) }
        //                    }
        //                    progressBar?.gone()
        //                    dayBookAdapter?.notifyDataSetChanged()
        //                    if (dayBookList.size == 0) {
        //                        tvError?.visible()
        //                    } else {
        //                        tvError?.gone()
        //                    }
        //                }
        //
        //                override fun onCancelled(dataSnapshot: DatabaseError) {
        //                }
        //            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        val item = menu?.findItem(R.id.action_search)
        binding.searchView.setMenuItem(item)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(mContext, AddDayBookActivity::class.java))
                return true
            }

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
