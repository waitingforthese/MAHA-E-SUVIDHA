package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.databinding.ItemBillUserBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.User
import java.util.*

class BillUserListAdapter(var context: Context?, var itemList: ArrayList<User>? = null) : RecyclerView.Adapter<BillUserListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var dateListListener: DateListListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_bill_user, parent, false)
//        return RecyclerViewHolder(v)
        val binding = ItemBillUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvUserEmail.text = item?.email
        if (TextUtils.isEmpty(item?.name)) {
            holder.binding.tvName.gone()
        } else {
            holder.binding.tvName.visible()
            holder.binding.tvName.text = item?.name
        }
        if (TextUtils.isEmpty(item?.shopName)) {
            holder.binding.tvShopName.gone()
        } else {
            holder.binding.tvShopName.visible()
            holder.binding.tvShopName.text = "(Shop Name - ${item?.shopName})"
        }
        holder.binding.root.setOnClickListener {
            dateListListener?.onItemClick(holder.adapterPosition)
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemBillUserBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface DateListListener {
        fun onItemClick(position: Int)
    }
}