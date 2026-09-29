package rahul.jagtap.dmas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import rahul.jagtap.dmas.adapter.GovtSchemeInfoAdapter
import rahul.jagtap.dmas.databinding.ActivityViewBillsBinding
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.GovtSchemeInfo
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Locale


class GovtSchemesInfoActivity : BaseActivity() {
//    private var uid: String? = ""
    var list = ArrayList<GovtSchemeInfo>()
    var adapter: GovtSchemeInfoAdapter? = null

    private lateinit var binding: ActivityViewBillsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBillsBinding.inflate(layoutInflater)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "नवनवीन माहिती"

//        uid = app?.preferences?.loggedInUser?.uid
        adapter = GovtSchemeInfoAdapter(mContext, list, isAdminOrEmployee())

        binding.recyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.recyclerView?.adapter = adapter
        adapter?.itemClickListener = object : GovtSchemeInfoAdapter.ItemClickListener {
            override fun onDeleteClick(position: Int) {
                Utils.showDialog(mContext, "Are you sure want to delete the record?", true) { dialog, which ->
                    run {
                        dialog.dismiss()
                        val selItem = list[position]
                        list?.removeAt(position) // Delete DB Record
                        selItem.pushKey?.let {
                            database.child(Utils.GOVT_SCHEMES_TABLE).child(selItem.pushKey!!).removeValue()
                        }
                        notifyAdapter()
                        toast("Record deleted successfully")
                    }
                }
            }

            override fun onEditClick(position: Int) {
                val selItem = list[position]
                val intent = Intent(mContext, AddGovtSchemeInfoActivity::class.java)
                intent.putExtra("govtSchemeInfo", selItem)
                resultLauncher.launch(intent)
            }
        }
        setEntriesData()
    }

    private fun setEntriesData() {
        binding.progressBar?.visible()
        app?.apiRequestHelper?.apiService?.govtSchemes?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                binding.progressBar?.gone()
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    if (json == null || json == "null") {
                        notifyAdapter()
                        return
                    }
                    val type: Type = object : TypeToken<HashMap<String, GovtSchemeInfo>?>() {}.type
                    val map: HashMap<String, GovtSchemeInfo>? = Gson().fromJson(json, type)
                    val values = map?.values
                    if (!values.isNullOrEmpty()) {
                        list.clear()
                        list.addAll(values)
                        try {
                            val df: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                            Collections.sort(list, Comparator { o1, o2 -> //                                if (o1.createdAt.isNullOrEmpty() || o2.createdAt.isNullOrEmpty()) return@Comparator 1
                                if (o1.createdDateTime == null || o2.createdDateTime == null || o1.createdDateTime!!.isEmpty() || o2.createdDateTime!!.isEmpty()) {
                                    return@Comparator 1
                                }
                                return@Comparator try { // Try parsing as English date
                                    df.parse(o2.createdDateTime.toString())!!.compareTo(df.parse(o1.createdDateTime.toString()))
                                } catch (e: ParseException) { // If parsing as English date fails, try parsing as Marathi date
                                    Log.e("TAG", "ParseException: " + o2.createdDateTime + "||" + o1.createdDateTime)
                                    val marathiDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale("mr"))
                                    marathiDateFormat.parse(o2.createdDateTime.toString())!!.compareTo(marathiDateFormat.parse(o1.createdDateTime.toString()))
                                } catch (e: Exception) { // If parsing as English date fails, try parsing as Marathi date
                                    Log.e("TAG", "Exception: " + o2.createdDateTime + "||" + o1.createdDateTime)
                                    val marathiDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale("mr"))
                                    marathiDateFormat.parse(o2.createdDateTime.toString())!!.compareTo(marathiDateFormat.parse(o1.createdDateTime.toString()))
                                } //                            return@Comparator df.parse(o2.createdAt.toString())!!.compareTo(df.parse(o1.createdAt.toString()))
                            })
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        notifyAdapter()
                        binding.recyclerView?.visible()
                        binding.tvError?.gone()
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
        if (list != null && list.size > 0) {
            adapter?.notifyDataSetChanged()
            binding.recyclerView?.visible()
            binding.tvError?.gone()
        } else {
            binding.recyclerView?.gone()
            binding.tvError?.visible()
        }
    }

    private fun isAdminOrEmployee(): Boolean = app?.preferences?.loggedInUser?.isAdmin == "1" || app?.preferences?.loggedInUser?.userType == "2"

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_govt_scheme, menu)
        val item = menu?.findItem(R.id.action_add)
        item?.isVisible = isAdminOrEmployee()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                val intent = Intent(mContext, AddGovtSchemeInfoActivity::class.java)
                resultLauncher.launch(intent)
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

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            setEntriesData()
        }
    }

    companion object {

    }
}
