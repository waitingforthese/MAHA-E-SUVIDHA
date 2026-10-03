package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.EditUserActivity
import rahul.jagtap.dmas.databinding.ItemUserBinding
import rahul.jagtap.dmas.extensions.convertStringToArrayList
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visibleOrGone
import rahul.jagtap.dmas.model.User
import java.util.*

class UserAdapter(
    var context: Context?, var itemList: ArrayList<User>? = null
) : RecyclerView.Adapter<UserAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null
    var isAdmin = false
    var list_search: ArrayList<User> = ArrayList<User>()
    var strSavedByEmail: String = ""
    var isGmailSinged = false

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
        isAdmin = app.preferences?.loggedInUser?.isAdmin == "1"
        itemList?.let { list_search.addAll(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_user, parent, false)
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvName.text = item?.name
        holder.binding.tvShopName.text = item?.shopName
        holder.binding.tvPhone.text = item?.contactNo
        holder.binding.tvPhone.paintFlags = holder.binding.tvPhone.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.binding.tvPhone?.setOnClickListener {
            itemList!![holder.absoluteAdapterPosition]?.contactNo?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        }
        holder.binding.tvEmail.text = item?.email
        holder.binding.tvAddress.text = item?.address
        holder.binding.tvReferrer.text = item?.referrer
        holder.binding.tvCreatedAt.text = item?.createdAt
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(holder.adapterPosition)
        }
        holder.binding.btnEdit?.setOnClickListener {
            context?.startActivity(Intent(context, EditUserActivity::class.java).putExtra("user", itemList?.get(holder.adapterPosition)))
        }
        if (isGmailSinged) {
            holder.binding.btnSaveToGContact.visibleOrGone(item?.contactSavedBy?.convertStringToArrayList()?.contains(strSavedByEmail) == false)
        } else {
            holder.binding.btnSaveToGContact.gone()
        }
        holder.binding.btnSaveToGContact?.setOnClickListener {
            val adapterPosition = holder.absoluteAdapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                holder.binding.btnSaveToGContact.isEnabled = false
                itemClickListener?.saveContactToGoogleContact(adapterPosition)
                holder.binding.btnSaveToGContact.postDelayed({
                    if (holder.absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                        holder.binding.btnSaveToGContact.isEnabled = true
                    }
                }, 1500)
            }
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(
        val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
        fun onCallOrSms(phoneNo: String)
        fun saveContactToGoogleContact(position: Int)
    }

    fun getUserAt(position: Int): User? = itemList?.getOrNull(position)

    fun getSavedByEmail(): String {
        return strSavedByEmail
    }

    fun setSavedByEmail(savedByEmail: String) {
        this.isGmailSinged = true
        this.strSavedByEmail = savedByEmail
        notifyDataSetChanged()
    }

    fun setGmailSignedIn(signedIn: Boolean) {
        this.isGmailSinged = signedIn
        notifyDataSetChanged()
    }

    fun markContactSaved(position: Int, savedBy: String) {
        val user = itemList?.getOrNull(position) ?: return
        user.contactSavedBy = savedBy
        val original = list_search.indexOfFirst { it.uid == user.uid }
        if (original >= 0) list_search[original].contactSavedBy = savedBy
        notifyItemChanged(position)
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
                if (g.name?.toLowerCase()?.contains(charText) == true || g.contactNo?.toLowerCase()?.contains(charText) == true || g.email?.toLowerCase()?.contains(charText) == true || g.address?.toLowerCase()?.contains(charText) == true || g.shopName?.toLowerCase()?.contains(charText) == true) {
                    itemList?.add(list_search[i])
                }
            }
        }
        notifyDataSetChanged()
    }
}
