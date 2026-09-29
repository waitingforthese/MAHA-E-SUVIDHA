package rahul.jagtap.dmas

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import okhttp3.ResponseBody
import rahul.jagtap.dmas.adapter.AccountingMenuAdapter
import rahul.jagtap.dmas.databinding.ActivityAccountingBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.DayBook
import rahul.jagtap.dmas.model.ImageDetails
import rahul.jagtap.dmas.utils.GridDividerDecoration
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AccountingMenuActivity : BaseActivity() {
    var dayBook: DayBook? = null
    private lateinit var accountingAdapter: AccountingMenuAdapter
    private lateinit var menuList: java.util.ArrayList<String>
    private val TAG = AccountingMenuActivity::class.java.simpleName
    lateinit var binding: ActivityAccountingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountingBinding.inflate(layoutInflater)
        val loggedInEmail = app?.preferences?.loggedInUser?.email
        if (screenshotAllowedEmailList.contains(loggedInEmail))
            Utils.disableScreenshot = false
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = "Accounting"

        setMenuGrid()

        database.child(Utils.ACCOUNTING_MENU_IMAGE_TABLE).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val imageDetails = dataSnapshot.getValue(ImageDetails::class.java)
                if (imageDetails != null) {
                    if (!TextUtils.isEmpty(imageDetails.imageDownloadUrl)) {
                        mContext?.let { Glide.with(it).load(imageDetails.imageDownloadUrl).into(binding.imageView1) }
                        binding.imageView1?.visible()
                    }
                }
            }
        })
    }

    private fun setMenuGrid() {
        binding.rvMenu?.apply {
            layoutManager = GridLayoutManager(mContext, 2)
            addItemDecoration(GridDividerDecoration(mContext))
        }
        menuList = ArrayList<String>()

        menuList.add("Scan Bills")
        menuList.add("View Scan Bills")
        menuList.add("Tally Reports")
        menuList.add("Payment Details")
        menuList.add("Reports")
        accountingAdapter = AccountingMenuAdapter(mContext, menuList)
        binding.rvMenu.adapter = accountingAdapter
        getDayBookData()
    }

    private fun getDayBookData() {
        binding.progressBar?.visible()
        app?.preferences?.loggedInUser?.uid?.let { userUid ->
            app?.apiRequestHelper?.apiService?.getDayBookDataByUid(userUid)?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) { //                Log.e("TAG", "onResponse: after")
                    binding.progressBar?.gone()
                    if (response.isSuccessful) {
                        val json = response.body()?.string()
                        if (json == null || json == "null") {
                            return
                        }

                        dayBook = if (!TextUtils.isEmpty(json)) Gson().fromJson(json, DayBook::class.java) else null
                        if (dayBook != null) {
                            menuList.add("Journal")
                            accountingAdapter = AccountingMenuAdapter(mContext, menuList)
                            binding.rvMenu.adapter = accountingAdapter
                        }
                    } else {
                        Log.e("in", "fail response")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    binding.progressBar?.gone()
                }
            })
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
}
