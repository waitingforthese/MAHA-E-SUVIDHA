package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.FullScreenImageActivity
import rahul.jagtap.dmas.databinding.ItemBillBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.Bill
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import java.util.*

class BillListAdapter(
    var context: Context?,
    var itemList: ArrayList<Bill>? = null
) :
    RecyclerView.Adapter<BillListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_bill, parent, false)
//        return RecyclerViewHolder(v)
        val binding = ItemBillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvBillType.text = "Bill type: ${item?.billType}"
        holder.binding.tvDateTime.text = item?.createdDateTime
        Glide.with(holder.binding.root).load(item?.downloadUrl).into(holder.binding.ivBill)
        if (loggedInUser?.isAdmin == "1" || loggedInUser?.userType == "2") {
            holder.binding.tvCreatedBy.text = "Created By: ${item?.createdBy}"
            holder.binding.tvCreatedByShopName.text = "Created By Shop Name: ${item?.createdByShopName}"
            holder.binding.tvCreatedBy.visible()
            holder.binding.ivDelete.visible()
        } else {
            holder.binding.tvCreatedBy.gone()
            holder.binding.ivDelete.gone()
        }
        holder.binding.ivBill.setOnClickListener {
            holder.binding.root.context.startActivity(
                Intent(
                    holder.binding.root.context,
                    FullScreenImageActivity::class.java
                )
                    .putExtra("title", item?.billType)
                    .putExtra("imageUrl", item?.downloadUrl)
                    .putExtra("downloadUrl", item?.downloadUrl)
            )
        }
        holder.binding.ivDelete.setOnClickListener {
            Utils.showDialog(context, "Are you sure want to delete the record?", true) { dialog, which ->
                run {
                    dialog.dismiss()
                    itemClickListener?.onDeleteClick(holder.adapterPosition)
                }
            }
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi")
    constructor(val binding: ItemBillBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface ItemClickListener {
        fun onDeleteClick(position: Int)
    }
}