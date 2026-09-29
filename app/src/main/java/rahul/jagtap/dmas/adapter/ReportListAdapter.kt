package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.databinding.ItemReportBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.ReportInfo
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import java.util.*

class ReportListAdapter(
    var context: Context?,
    var itemList: ArrayList<ReportInfo>? = null
) :
    RecyclerView.Adapter<ReportListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null
    var isAdmin = false
    var isEmployee = false

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
        isAdmin = app.preferences?.loggedInUser?.isAdmin == "1"
        isEmployee = app.preferences?.loggedInUser?.userType == "2"
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_report, parent, false)
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvName.text = item?.reportFileName
        if (isAdmin || isEmployee)
            holder.binding.ivDelete.visible()
        else
            holder.binding.ivDelete.gone()
        holder.binding.ivDelete.setOnClickListener {
            Utils.showDialog(context, "Are you sure want to delete the record?", true) { dialog, which ->
                run {
                    dialog.dismiss()
                    itemClickListener?.onDeleteClick(holder.adapterPosition)
                }
            }
        }
        holder.binding.ivDownload.setOnClickListener {
            itemClickListener?.onDownloadClick(holder.adapterPosition)
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi")
    constructor(val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface ItemClickListener {
        fun onDeleteClick(position: Int)
        fun onDownloadClick(position: Int)
    }
}