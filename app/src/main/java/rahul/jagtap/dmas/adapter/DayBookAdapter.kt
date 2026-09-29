package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.databinding.ItemDayBookListBinding
import rahul.jagtap.dmas.model.DayBook
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import java.util.*

class DayBookAdapter(
    var context: Context?,
    var itemList: ArrayList<DayBook>? = null
) :
    RecyclerView.Adapter<DayBookAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null
    var list_search: ArrayList<DayBook> = ArrayList<DayBook>()

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
        itemList?.let { list_search.addAll(it) }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_day_book_list, parent, false)
        val binding = ItemDayBookListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvName.text = item?.userFullName
        holder.binding.tvPhone.text = "${item?.userContactNo}(${item?.userEmail})"
        holder.binding.tvUid.text = "Uid: ${item?.userUid}"
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(holder.adapterPosition, itemList?.get(holder.adapterPosition))
        }
        holder.binding.ivDelete.setOnClickListener {
            Utils.showDialog(context, "Are you sure want to delete the record?", true, MaterialDialog.SingleButtonCallback { dialog, which ->
                run {
                    dialog.dismiss()
                    itemClickListener?.onDeleteClick(holder.adapterPosition, itemList?.get(holder.adapterPosition))
                }
            })
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi")
    constructor(val binding: ItemDayBookListBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface ItemClickListener {
        fun onItemClick(position: Int, dayBook: DayBook?)
        fun onDeleteClick(position: Int, dayBook: DayBook?)
    }

    // Filter Class
    fun filter(char: String) {
        var charText = char
        charText = charText.toLowerCase(Locale.getDefault())
        itemList?.clear()
        if (charText.isEmpty()) {
            itemList?.addAll(list_search)
        } else {
            for (i in list_search.indices) {
                val g: DayBook = list_search.get(i)
                if (g.userFullName?.toLowerCase()?.contains(charText) == true || g.userContactNo?.toLowerCase()?.contains(charText) == true ||
                    g.userEmail?.toLowerCase()?.contains(charText) == true || g.userUid?.toLowerCase()?.contains(charText) == true) {
                    itemList?.add(list_search[i])
                }
            }
        }
        notifyDataSetChanged()
    }
}