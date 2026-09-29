package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.databinding.ItemDailyEntryBinding
import rahul.jagtap.dmas.model.DailyEntry
import rahul.jagtap.dmas.model.User
import java.util.*

class DailyEntryListAdapter(var context: Context?, var itemList: ArrayList<DailyEntry>? = null) : RecyclerView.Adapter<DailyEntryListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null
//    var isAdmin = false

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
//        isAdmin = app.preferences?.loggedInUser?.isAdmin == "1"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_daily_entry, parent, false)
        val binding = ItemDailyEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvDate.text = item?.date
        holder.binding.tvDetails.text = item?.details
        holder.binding.tvCashBank.text = item?.cash_bank
        holder.binding.tvDebitCredit.text = item?.debit_credit
        if (item?.debit_credit == "Debit") {
            holder.binding.tvAmount.text = "- ${item?.amount}"
            holder.binding.tvAmount.setTextColor(Color.RED)
        } else {
            holder.binding.tvAmount.text = "+ ${item?.amount}"
            holder.binding.tvAmount.setTextColor(Color.GREEN)
        }
        holder.itemView.setOnClickListener {
            val list = ArrayList<String>()
            list.add("Edit")
            list.add("Delete")
            list.add("Cancel")
            context?.let {
                MaterialDialog.Builder(it).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                    run {
                        when (position) {
                            0 -> itemClickListener?.onEditClick(holder.adapterPosition)
                            1 -> itemClickListener?.onDeleteClick(holder.adapterPosition)
                            2 -> dialog?.dismiss()
                        }
                        dialog?.dismiss()
                    }
                }.show()
            }
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemDailyEntryBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface ItemClickListener {
        fun onDeleteClick(position: Int)
        fun onEditClick(position: Int)
    }
}