package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ItemGovtSchemeInfoBinding
import rahul.jagtap.dmas.extensions.copyToClipboard
import rahul.jagtap.dmas.model.GovtSchemeInfo
import rahul.jagtap.dmas.model.User
import java.util.*

class GovtSchemeInfoAdapter(
    var context: Context?, var itemList: ArrayList<GovtSchemeInfo>? = null, var adminOrEmployee: Boolean
) : RecyclerView.Adapter<GovtSchemeInfoAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null
//    var orangeColor = -1
//    var yellowColor = -1
//    var blueColor = -1
//    var pinkColor = -1
//    var goldenColor = -1
    var orangeBg: Drawable? = null
    var yellowBg: Drawable? = null
    var blueBg: Drawable? = null
    var pinkBg: Drawable? = null
    var goldenBg: Drawable? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
        if (context != null) {
            orangeBg = ContextCompat.getDrawable(context!!, R.drawable.btn_bg_primary)
            yellowBg = ContextCompat.getDrawable(context!!, R.drawable.btn_bg_yellow)
            blueBg = ContextCompat.getDrawable(context!!, R.drawable.btn_bg_blue)
            pinkBg = ContextCompat.getDrawable(context!!, R.drawable.btn_bg_pink)
            goldenBg = ContextCompat.getDrawable(context!!, R.drawable.btn_bg_golden)
//            orangeColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
//            yellowColor = ContextCompat.getColor(context!!, R.color.esuvidha_yellow)
//            blueColor = ContextCompat.getColor(context!!, R.color.esuvidha_blue)
//            pinkColor = ContextCompat.getColor(context!!, R.color.esuvidha_pink)
//            goldenColor = ContextCompat.getColor(context!!, R.color.esuvidha_golden)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = ItemGovtSchemeInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvTitle.text = item?.title
        holder.binding.tvSchemeInfo.text = item?.schemeInfo
        holder.binding.tvDateTime.text = item?.createdDateTime
//        holder.binding.tvTitle?.background = titleBgDrawable
        when(item?.titleColor) {
            "Orange" -> holder.binding.tvTitle?.background = orangeBg
            "Yellow" -> holder.binding.tvTitle?.background = yellowBg //holder.binding.tvTitle?.setBackgroundColor(yellowColor)
            "Blue" -> holder.binding.tvTitle?.background = blueBg //holder.binding.tvTitle?.setBackgroundColor(blueColor)
            "Pink" -> holder.binding.tvTitle?.background = pinkBg //holder.binding.tvTitle?.setBackgroundColor(pinkColor)
            "Golden" -> holder.binding.tvTitle?.background = goldenBg //holder.binding.tvTitle?.setBackgroundColor(goldenColor)
        }
        holder.itemView.setOnClickListener {
            if (adminOrEmployee) {
                val obj = itemList?.get(holder.absoluteAdapterPosition)
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
                                2 -> itemView?.context?.let { it1 -> obj?.schemeInfo.toString().copyToClipboard(it1) }
                                3 -> dialog?.dismiss()
                            }
                            dialog?.dismiss()
                        }
                    }.show()
                }
            }
        }
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemGovtSchemeInfoBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface ItemClickListener {
        fun onDeleteClick(position: Int)
        fun onEditClick(position: Int)
    }
}