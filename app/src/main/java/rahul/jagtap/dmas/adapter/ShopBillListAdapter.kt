package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.admin.bills.BillDatesActivity
import rahul.jagtap.dmas.databinding.ItemShopBillBinding
import rahul.jagtap.dmas.model.ShopBill
import rahul.jagtap.dmas.model.User
import java.util.*

class ShopBillListAdapter(
    var context: Context?,
    var itemList: ArrayList<ShopBill>? = null
) :
    RecyclerView.Adapter<ShopBillListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_shop_bill, parent, false)
        val binding = ItemShopBillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvShopName.text = item?.shopName
        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(
                Intent(holder.itemView.context, BillDatesActivity::class.java)
                    .putExtra("uid", item?.uid)
            )
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi")
    constructor(val binding: ItemShopBillBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}