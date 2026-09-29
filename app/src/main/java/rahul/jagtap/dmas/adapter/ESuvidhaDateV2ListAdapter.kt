package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.databinding.ItemShopBillBinding
import rahul.jagtap.dmas.model.EsuvidhaInfo
import rahul.jagtap.dmas.model.User
import java.util.*

class ESuvidhaDateV2ListAdapter(var context: Context?, var itemList: ArrayList<EsuvidhaInfo>? = null) : RecyclerView.Adapter<ESuvidhaDateV2ListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var dateListListener: DateListListener? = null
    var list_search: ArrayList<EsuvidhaInfo> = ArrayList<EsuvidhaInfo>()

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
        itemList?.let { list_search.addAll(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
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
        holder.binding.tvShopName.text = item?.strDate
        holder.itemView.setOnClickListener {
            dateListListener?.onItemClick(holder.adapterPosition)
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemShopBillBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface DateListListener {
        fun onItemClick(position: Int)
    }

    // Filter Class
//    fun filter(char: String) {
//        var charText = char
//        charText = charText.toLowerCase(Locale.getDefault())
//        itemList?.clear()
//        if (charText.isEmpty()) {
//            itemList?.addAll(list_search)
//        } else {
//            for (i in list_search.indices) {
//                val g: ESuvidhaDate = list_search[i]
//                if (g.suvidhaTypes?.toLowerCase()?.contains(charText) == true) {
//                    itemList?.add(list_search[i])
//                }
//            }
//        }
//        notifyDataSetChanged()
//        val df: DateFormat = SimpleDateFormat("dd-MM-yyyy")
//        Collections.sort(itemList, Comparator { o1, o2 ->
//            return@Comparator df.parse(o2.date.toString())!!.compareTo(df.parse(o1.date.toString()))
//        })
//    }
}