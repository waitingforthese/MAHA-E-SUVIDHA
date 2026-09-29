package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.databinding.ItemTextMsgBinding
import rahul.jagtap.dmas.extensions.copyToClipboard
import rahul.jagtap.dmas.model.TextMsg
import rahul.jagtap.dmas.model.User
import java.util.*

class TextMsgListAdapter(var context: Context?, var itemList: ArrayList<TextMsg>? = null) : RecyclerView.Adapter<TextMsgListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_text_msg, parent, false)
        val binding = ItemTextMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvTitle.text = item?.title
        holder.binding.tvTextMsg.text = item?.textMsg
        holder.itemView.setOnClickListener {
            val textMsg = itemList?.get(holder.absoluteAdapterPosition)
            val list = ArrayList<String>()
            list.add("Edit")
            list.add("Delete")
            list.add("Copy Text")
            list.add("Cancel")
            context?.let {
                MaterialDialog.Builder(it).items(list).itemsCallback { dialog: MaterialDialog?, itemView: View?, position: Int, text: CharSequence ->
                    run {
                        when (position) {
                            0 -> itemClickListener?.onEditClick(holder.adapterPosition)
                            1 -> itemClickListener?.onDeleteClick(holder.adapterPosition)
                            2 -> itemView?.context?.let { it1 -> textMsg?.textMsg.toString().copyToClipboard(it1) }
                            3 -> dialog?.dismiss()
                        }
                        dialog?.dismiss()
                    }
                }.show()
            }
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemTextMsgBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface ItemClickListener {
        fun onDeleteClick(position: Int)
        fun onEditClick(position: Int)
    }
}