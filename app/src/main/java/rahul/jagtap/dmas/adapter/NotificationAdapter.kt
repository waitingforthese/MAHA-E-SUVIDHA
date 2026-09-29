package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.databinding.ItemNotificationBinding
import rahul.jagtap.dmas.model.NotificationItem
import rahul.jagtap.dmas.model.User

class NotificationAdapter(
    var context: Context?, var itemList: ArrayList<NotificationItem>? = null
) : RecyclerView.Adapter<NotificationAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerViewHolder { //        val localInflater = LayoutInflater.from(parent.context)
        //        val v = localInflater.inflate(R.layout.item_notification, parent, false)
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvNotification.text = item?.message
        holder.binding.tvDateTime.text = item?.createdAt
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(
        val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

    }
}