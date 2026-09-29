package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.databinding.ItemUserListBinding
import rahul.jagtap.dmas.model.User
import java.util.*

class UserListAdapter(var context: Context?, var itemList: ArrayList<User>? = null) : RecyclerView.Adapter<UserListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null
    var isAdmin = false
    var list_search: ArrayList<User> = ArrayList<User>()

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
        isAdmin = app.preferences?.loggedInUser?.isAdmin == "1"
        itemList?.let { list_search.addAll(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_user_list, parent, false)
        val binding = ItemUserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvName.text = item?.name
        holder.binding.tvPhone.text = "${item?.contactNo}(${item?.email})"
        holder.binding.tvUid.text = "Uid: ${item?.uid}"
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(holder.adapterPosition)
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemUserListBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
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
                val g: User = list_search.get(i)
                if (g.name?.toLowerCase()?.contains(charText) == true || g.contactNo?.toLowerCase()?.contains(charText) == true ||
                    g.email?.toLowerCase()?.contains(charText) == true) {
                    itemList?.add(list_search[i])
                }
            }
        }
        notifyDataSetChanged()
    }
}