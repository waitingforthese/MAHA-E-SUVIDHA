package rahul.jagtap.dmas.user

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import com.google.gson.Gson
import okhttp3.ResponseBody
import rahul.jagtap.dmas.extensions.toast
import rahul.jagtap.dmas.BaseActivity
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ActivityReportOptionsBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.DayBook
import rahul.jagtap.dmas.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportOptionsActivity : BaseActivity() {
    private val TAG = ReportOptionsActivity::class.java.simpleName
    var dayBook: DayBook? = null
    lateinit var binding: ActivityReportOptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportOptionsBinding.inflate(layoutInflater)
        if (Utils.disableScreenshot) this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbarTitle?.text = getString(R.string.txt_reports) //        val dayBook = intent.getParcelableExtra<DayBook>("dayBook")
        //        val dayBookString = intent.getStringExtra("dayBook")
        //        val dayBook = if (!TextUtils.isEmpty(dayBookString)) Gson().fromJson(dayBookString, DayBook::class.java) else null
        binding.tvDayBook?.setOnClickListener {
            if (dayBook != null) {
                startActivity(Intent(mContext, DayBookActivity::class.java)
                    .putExtra("showDayRangePopup", true)
                    .putExtra("userUid", dayBook?.userUid))
            } else {
                toast("Report is not available")
            }
        }
        binding.tvPurchaseRegister?.setOnClickListener {
            if (dayBook != null) {
                startActivity(Intent(mContext, DayBookActivity::class.java)
                    .putExtra("showDayRangePopup", true)
                    .putExtra("ledger1ToFilterValue1", "Purchase Account")
                    .putExtra("ledger2ToFilterValue1", "Purchase Account")
                    .putExtra("userUid", dayBook?.userUid))
            } else {
                toast("Report is not available")
            }
        }
        binding.tvSalesRegister?.setOnClickListener {
            if (dayBook != null) {
                startActivity(Intent(mContext, DayBookActivity::class.java)
                    .putExtra("showDayRangePopup", true)
                    .putExtra("ledger1ToFilterValue1", "Sales Account")
                    .putExtra("ledger2ToFilterValue1", "Sales Account")
                    .putExtra("userUid", dayBook?.userUid))
            } else {
                toast("Report is not available")
            }
        }
        binding.tvLedger?.setOnClickListener {
            if (dayBook != null) {
                startActivity(Intent(mContext, DayBookActivity::class.java)
                    .putExtra("showLedger1Popup", true)
                    .putExtra("showLedger2Popup", true)
                    .putExtra("userUid", dayBook?.userUid))
            } else {
                toast("Report is not available")
            }
        }
        binding.tvStockItems?.setOnClickListener {
            if (dayBook != null) {
                startActivity(Intent(mContext, DayBookActivity::class.java)
                    .putExtra("showStockItemNamePopup", true)
                    .putExtra("userUid", dayBook?.userUid))
            } else {
                toast("Report is not available")
            }
        }
        binding.tvGstData?.setOnClickListener {
            if (dayBook != null) {
                startActivity(Intent(mContext, DayBookActivity::class.java)
                    .putExtra("ledger1ToFilterValue1", "Purchase Account")
                    .putExtra("ledger1ToFilterValue2", "Sales Account")
                    .putExtra("ledger2ToFilterValue1", "Purchase Account")
                    .putExtra("ledger2ToFilterValue2", "Sales Account")
                    .putExtra("userUid", dayBook?.userUid))
            } else {
                toast("Report is not available")
            }
        } //        btnShopAct?.setOnClickListener {
        //            startActivity(Intent(mContext, ShopActActivity::class.java))
        //        }
        //        btnUdyamAadhar?.setOnClickListener {
        //            startActivity(Intent(mContext, UdyamAadharActivity::class.java))
        //        }
        //        btnFoodLicense?.setOnClickListener {
        //            startActivity(Intent(mContext, FoodLicenseActivity::class.java))
        //        }
        //        btnProvidentFund?.setOnClickListener {
        //            startActivity(Intent(mContext, ProvidentFundActivity::class.java))
        //        }
        //        btnManualAadharPanCard?.setOnClickListener {
        //            startActivity(Intent(mContext, ManualAadharPanActivity::class.java))
        //        }
        //        btnNepalMoneyTransfer?.setOnClickListener {
        //            startActivity(Intent(mContext, NepalMoneyTransferActivity::class.java))
        //        }
        //        btnRailwayTicketBooking?.setOnClickListener {
        //            startActivity(Intent(mContext, RailwayTicketBookingActivity::class.java))
        //        }
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

    companion object {}
}
