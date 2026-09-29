package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.*
import rahul.jagtap.dmas.GovtSchemesInfoActivity
import rahul.jagtap.dmas.admin.esuvidha.ESuvidhaDatesActivity
import rahul.jagtap.dmas.admin.reports.ReportTypesActivity
import rahul.jagtap.dmas.databinding.ItemHomeMenuBinding
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.*
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry

class ESuvidhaMenuAdapter(
    var context: Context?, var itemList: List<String?>? = null,
    private val jyotish_shastra_suchna: String, private val typesMap: HashMap<String, HashMap<String, HashMap<String, String>>>?,
    private val suchnaMap: HashMap<String, String>? = null
) : RecyclerView.Adapter<ESuvidhaMenuAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var isAdmin = false
    var isEmployee = false

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
        isAdmin = app.preferences?.loggedInUser?.isAdmin == "1"
        isEmployee = app.preferences?.loggedInUser?.userType == "2"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_home_menu, parent, false)
        val binding = ItemHomeMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.binding.tvMenuTitle.text = itemList?.get(position)
        when (itemList?.get(position)) {
//            "Journal" -> holder.itemView.ivMenu.setImageResource(R.drawable.ic_view_reports)
//            "Scan Bills" -> holder.itemView.ivMenu.setImageResource(R.drawable.ic_scan_bill)
//            "Accounting" -> holder.itemView.ivMenu.setImageResource(R.drawable.ic_view_reports)
            "ई - सुविधा येथून पाठवा" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_esuvidha)
            "नवनवीन माहिती" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_govt_scheme_white)
            "मिळालेल्या सुविधा" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_view_reports)
//            "Send Report" -> holder.itemView.ivMenu.setImageResource(R.drawable.ic_view_reports)
//            "Notifications" -> holder.itemView.ivMenu.setImageResource(R.drawable.ic_notifications)
//            "Daily Entries" -> holder.itemView.ivMenu.setImageResource(R.drawable.ic_daily_entries)
//            "View Scan Bills" -> holder.itemView.ivMenu.setImageResource(R.drawable.ic_view_reports)
            "पाठविलेल्या सुविधा" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_view_reports)
//            "Block/Unblock User" -> holder.itemView.ivMenu.setImageResource(R.drawable.user_icon)
            "येथून फी भरावी" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_make_payment)
            "ज्योतिष शास्त्रींना प्रश्न विचारा" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_jyotish_shastra)
            "Project Funding" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_project_funding)
            "My Accountant\n(Outsourcing)" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_accounting)
            "खाते बुक\n(स्वतःचा हिशोब स्वतः करा)" -> holder.binding.ivMenu.setImageResource(R.drawable.ic_khate_book)
        }
        // Grouped-service tile: colourful flat icon (per-service colour), shown untinted.
        EsuvidhaServiceRegistry.groupedDefForTitle(itemList?.get(position))?.let { def ->
            holder.binding.ivMenu.setImageResource(EsuvidhaServiceRegistry.groupIconFor(def))
        }
        holder.itemView.setOnClickListener {
            when (itemList?.get(position)) {
//                "Journal" -> {
//                    if (isAdmin) context?.startActivity(Intent(context, DayBookListActivity::class.java))
//                    else {
//                        context?.startActivity(Intent(context, DayBookActivity::class.java).putExtra("dayBook", (context as? AccountingMenuActivity)?.dayBook))
//                    }
//                }
//                "Scan Bills" -> {
//                    if (isAdmin) context?.startActivity(Intent(context, BillDatesActivity::class.java))
//                    else context?.startActivity(Intent(context, AccountingServicesActivity::class.java))
//                }
//                "Accounting" -> {
//                    context?.startActivity(Intent(context, AccountingServicesActivity::class.java))
//                }
                "ई - सुविधा येथून पाठवा" -> {
                    if (isAdmin || isEmployee) context?.startActivity(Intent(context, ESuvidhaDatesActivity::class.java))
                    else context?.startActivity(Intent(context, ESuvidhaServicesGridActivity::class.java))
                }
                "नवनवीन माहिती" -> {
                    context?.startActivity(Intent(context, GovtSchemesInfoActivity::class.java))
                }
                "मिळालेल्या सुविधा" -> {
                    if (isAdmin || isEmployee) context?.startActivity(Intent(context, ReportTypesActivity::class.java))
                    else context?.startActivity(Intent(context, UserReportTypesActivity::class.java).putExtra("isEsuvidha", true))
//                    context?.startActivity(Intent(context, ReportOptionsActivity::class.java).putExtra("dayBook", (context as? AccountingMenuActivity)?.dayBook))
                }
//                "Notifications" -> {
//                    context?.startActivity(Intent(context, NotificationsActivity::class.java))
//                }
//                "Send Report" -> {
//                    context?.startActivity(Intent(context, SendReportActivity::class.java))
//                }
//                "Daily Entries" -> {
//                    context?.startActivity(Intent(context, DailyEntriesActivity::class.java))
//                }
//                "View Scan Bills" -> {
//                    context?.startActivity(Intent(context, BillDatesActivity::class.java))
//                }
                "पाठविलेल्या सुविधा" -> {
                    context?.startActivity(Intent(context, ESuvidhaDatesActivity::class.java))
                }
//                "Block/Unblock User" -> {
//                    context?.startActivity(Intent(context, BlockUnblockUserActivity::class.java))
//                }
                "येथून फी भरावी" -> {
                    context?.startActivity(Intent(context, PaymentDetailsActivity::class.java))
                }
                "ज्योतिष शास्त्रींना प्रश्न विचारा" -> {
                    val bundle = Bundle()
                    bundle.putSerializable("hashMap", typesMap?.get("jyotish_shastra"))
                    context?.startActivity(Intent(context, JyotishShastraActivity::class.java)
                        .putExtra("suchna", jyotish_shastra_suchna)
                        .putExtras(bundle)
                    )
                }
                "Project Funding" -> {
                    context?.startActivity(Intent(context, ProjectFundingActivity::class.java))
                }
                "My Accountant\n(Outsourcing)" -> {
                    context?.startActivity(Intent(context, AccountingMenuActivity::class.java))
                }
                "खाते बुक\n(स्वतःचा हिशोब स्वतः करा)" -> {
                    context?.startActivity(Intent(context, DailyEntriesActivity::class.java))
                }
            }
            // Grouped-service tile -> open the parent screen (user picks the subtype there).
            EsuvidhaServiceRegistry.groupedDefForTitle(itemList?.get(position))?.let { def ->
                context?.let { c -> c.startActivity(EsuvidhaServiceRegistry.buildParentIntent(c, def, typesMap, suchnaMap)) }
            }
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemHomeMenuBinding) : RecyclerView.ViewHolder(binding.root) {
//        var ivMenu: ImageView? = view.findViewById(R.id.ivMenu)
//        var tvMenuTitle: TextView? = view.findViewById(R.id.tvMenuTitle)
    }
}