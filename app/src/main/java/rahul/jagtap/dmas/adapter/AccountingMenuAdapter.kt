package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.*
import rahul.jagtap.dmas.admin.BlockUnblockUserActivity
import rahul.jagtap.dmas.admin.DayBookListActivity
import rahul.jagtap.dmas.admin.bills.BillDatesActivity
import rahul.jagtap.dmas.admin.NotificationsActivity
import rahul.jagtap.dmas.admin.SendReportActivity
import rahul.jagtap.dmas.admin.reports.ReportTypesActivity
import rahul.jagtap.dmas.databinding.ItemHomeMenuBinding
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.*

class AccountingMenuAdapter(var context: Context?, var itemList: List<String?>? = null) : RecyclerView.Adapter<AccountingMenuAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var isAdmin = false
    var isEmployee = false

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
        isAdmin = app.preferences?.loggedInUser?.isAdmin == "1"
        isEmployee = app.preferences?.loggedInUser?.isAdmin == "2"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
        val binding = ItemHomeMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)

//        val v = localInflater.inflate(R.layout.item_home_menu, parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.binding.tvMenuTitle.text = itemList?.get(position)
        when (itemList?.get(position)) {
            "Journal" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_view_reports)
            "Scan Bills" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_scan_bill)
            "Accounting" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_view_reports)
//            "E-Suvidha" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_online_suvidha)
            "Tally Reports" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_accounting)
            "Reports" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_view_reports)
            "Send Report" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_view_reports)
            "Notifications" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_notifications)
            "Daily Entries" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_daily_entries)
            "View Scan Bills" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_view_reports)
//            "View Scan E-Suvidha" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_view_reports)
            "Block/Unblock User" -> holder.binding.ivMenu.setImageResource(R.drawable.user_icon)
            "Payment Details" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_payment_details)
        }
        holder.binding.root.setOnClickListener {
            when (itemList?.get(position)) {
                "Journal" -> {
                    if (isAdmin || isEmployee) context?.startActivity(Intent(context, DayBookListActivity::class.java))
                    else {
                        context?.startActivity(Intent(context, DayBookActivity::class.java).putExtra("userUid", (context as? AccountingMenuActivity)?.dayBook?.userUid))
                    }
                }
                "Scan Bills" -> {
                    if (isAdmin || isEmployee) context?.startActivity(Intent(context, BillDatesActivity::class.java))
                    else context?.startActivity(Intent(context, AccountingServicesActivity::class.java))
                }
                "Accounting" -> {
                    context?.startActivity(Intent(context, AccountingServicesActivity::class.java))
                }
//                "E-Suvidha" -> {
//                    if (isAdmin) context?.startActivity(Intent(context, ESuvidhaDatesActivity::class.java))
//                    else context?.startActivity(Intent(context, OnlineESuvidhaOptionsActivity::class.java))
//                }
                "Tally Reports" -> {
//                    if (isAdmin) context?.startActivity(Intent(context, ReportTypesActivity::class.java))
//                    else context?.startActivity(Intent(context, UserReportTypesActivity::class.java))
                    context?.startActivity(Intent(context, ReportOptionsActivity::class.java))
                }
                "Reports" -> {
                    if (isAdmin || isEmployee) context?.startActivity(Intent(context, ReportTypesActivity::class.java))
                    else context?.startActivity(Intent(context, UserReportTypesActivity::class.java).putExtra("isAccounting", true))
                    //                    context?.startActivity(Intent(context, ReportOptionsActivity::class.java).putExtra("dayBook", (context as? AccountingMenuActivity)?.dayBook))
                }
                "Notifications" -> {
                    context?.startActivity(Intent(context, NotificationsActivity::class.java))
                }
                "Send Report" -> {
                    context?.startActivity(Intent(context, SendReportActivity::class.java))
                }
                "Daily Entries" -> {
                    context?.startActivity(Intent(context, DailyEntriesActivity::class.java))
                }
                "View Scan Bills" -> {
                    context?.startActivity(Intent(context, BillDatesActivity::class.java))
                }
//                "View Scan E-Suvidha" -> {
//                    context?.startActivity(Intent(context, ESuvidhaDatesActivity::class.java))
//                }
                "Block/Unblock User" -> {
                    context?.startActivity(Intent(context, BlockUnblockUserActivity::class.java))
                }
                "Payment Details" -> {
                    context?.startActivity(Intent(context, PaymentDetailsActivity::class.java))
                }
            }
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(
        val binding: ItemHomeMenuBinding
    ) : RecyclerView.ViewHolder(binding.root) {
//        var ivMenu: ImageView? = view.findViewById(R.id.ivMenu)
//        var tvMenuTitle: TextView? = view.findViewById(R.id.tvMenuTitle)
    }
}