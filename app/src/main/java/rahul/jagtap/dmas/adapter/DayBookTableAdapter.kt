package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.BaseListAdapter
import rahul.jagtap.dmas.BaseViewHolder
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.DayBookTableListItemBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.DayBookTableItem
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.DayBookActivity
import java.util.*

class DayBookTableAdapter(var context: Context?, var itemList: ArrayList<DayBookTableItem>? = null) : BaseListAdapter<DayBookTableListItemBinding, DayBookTableItem>(DIFF_CALLBACK) {
    private var loggedInUser: User? = null
    public lateinit var itemClickListener: ItemClickListener
    var list_search: ArrayList<DayBookTableItem> = ArrayList<DayBookTableItem>()
    var hideSrNo = (context as DayBookActivity).hideSrNo
    var hideDate = (context as DayBookActivity).hideDate
    var hideLedger1 = (context as DayBookActivity).hideLedger1
    var hideLedger2 = (context as DayBookActivity).hideLedger2
    var hideDebit = (context as DayBookActivity).hideDebit
    var hideCredit = (context as DayBookActivity).hideCredit
    var hideVouchType = (context as DayBookActivity).hideVouchType
    var hideVouchNo = (context as DayBookActivity).hideVouchNo

    var hideName = (context as DayBookActivity).hideName
    var hideQuantity = (context as DayBookActivity).hideQuantity
    var hideLiterPrice = (context as DayBookActivity).hideLiterPrice
    var hideCgst = (context as DayBookActivity).hideCgst
    var hideSgst = (context as DayBookActivity).hideSgst
    var hideIgst = (context as DayBookActivity).hideIgst
    var hideTaxable = (context as DayBookActivity).hideTaxable
    var cellBgColor: Drawable? = null
    var selectedCellBgColor: Drawable? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
        itemList?.let { list_search.addAll(it) }
        cellBgColor = ContextCompat.getDrawable(context!!, R.drawable.table_content_cell_bg)
        selectedCellBgColor = ContextCompat.getDrawable(context!!, R.drawable.table_content_cell_selected_bg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<DayBookTableListItemBinding, DayBookTableItem> {
        val binding = DayBookTableListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(parent, binding, itemClickListener!!)// RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: BaseViewHolder<DayBookTableListItemBinding, DayBookTableItem>, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvSL.text = "${position + 1}"
        holder.binding.tvDate.text = item?.date
        holder.binding.tvLedger1.text = item?.ledger1
        if (item?.isLedger1Checked == true) {
            holder.binding.tvCredit.background = selectedCellBgColor
        } else {
            holder.binding.tvCredit.background = cellBgColor
        }
        if (item?.isLedger2Checked == true) {
            holder.binding.tvDebit.background = selectedCellBgColor
        } else {
            holder.binding.tvDebit.background = cellBgColor
        }
        holder.binding.tvLedger2.text = item?.ledger2
        holder.binding.tvDebit.text = item?.debit
        holder.binding.tvCredit.text = item?.credit
        holder.binding.tvVoucherType.text = item?.voucherType
        holder.binding.tvVoucherNo.text = item?.voucherNo
        holder.binding.tvStockItemName.text = item?.stockItemName
        holder.binding.tvStockItemQuantity.text = item?.stockItemQuantity
        holder.binding.tvStockItemRate.text = item?.stockItemRate
        holder.binding.tvTaxable.text = item?.taxable
        holder.binding.tvCgst.text = item?.cgst
        holder.binding.tvSgst.text = item?.sgst
        holder.binding.tvIgst.text = item?.igst
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(holder.adapterPosition)
        }
        if (hideSrNo)
            holder.binding.tvSL.gone()
        else
            holder.binding.tvSL.visible()
        if (hideDate)
            holder.binding.tvDate.gone()
        else
            holder.binding.tvDate.visible()
        if (hideLedger1)
            holder.binding.tvLedger1.gone()
        else
            holder.binding.tvLedger1.visible()
        if (hideLedger2)
            holder.binding.tvLedger2.gone()
        else
            holder.binding.tvLedger2.visible()
        if (hideDebit)
            holder.binding.tvDebit.gone()
        else
            holder.binding.tvDebit.visible()
        if (hideCredit)
            holder.binding.tvCredit.gone()
        else
            holder.binding.tvCredit.visible()
        if (hideVouchType)
            holder.binding.tvVoucherType.gone()
        else
            holder.binding.tvVoucherType.visible()
        if (hideVouchNo)
            holder.binding.tvVoucherNo.gone()
        else
            holder.binding.tvVoucherNo.visible()

        if (hideName)
            holder.binding.tvStockItemName.gone()
        else
            holder.binding.tvStockItemName.visible()
        if (hideQuantity)
            holder.binding.tvStockItemQuantity.gone()
        else
            holder.binding.tvStockItemQuantity.visible()
        if (hideLiterPrice)
            holder.binding.tvStockItemRate.gone()
        else
            holder.binding.tvStockItemRate.visible()
        if (hideTaxable)
            holder.binding.tvTaxable.gone()
        else
            holder.binding.tvTaxable.visible()
        if (hideCgst)
            holder.binding.tvCgst.gone()
        else
            holder.binding.tvCgst.visible()
        if (hideSgst)
            holder.binding.tvSgst.gone()
        else
            holder.binding.tvSgst.visible()
        if (hideIgst)
            holder.binding.tvIgst.gone()
        else
            holder.binding.tvIgst.visible()
    }

    fun hideColumns(text: Array<CharSequence>?) {
        this.hideSrNo = text?.contains("Sr No") == false
        this.hideDate = text?.contains("Date") == false
        this.hideLedger1 = text?.contains("Ledger 1") == false
        this.hideLedger2 = text?.contains("Ledger 2") == false
        this.hideDebit = text?.contains("Debit") == false
        this.hideCredit = text?.contains("Credit") == false
        this.hideVouchType = text?.contains("Voucher Type") == false
        this.hideVouchNo = text?.contains("Voucher No") == false
        this.hideName = text?.contains("Stock Item Name") == false
        this.hideQuantity = text?.contains("Stock Item Quantity") == false
        this.hideLiterPrice = text?.contains("Stock Item Rate") == false
        this.hideTaxable = text?.contains("Taxable") == false
        this.hideCgst = text?.contains("CGST") == false
        this.hideSgst = text?.contains("SGST") == false
        this.hideIgst = text?.contains("IGST") == false

        (context as DayBookActivity).hideSrNo = text?.contains("Sr No") == false
        (context as DayBookActivity).hideDate = text?.contains("Date") == false
        (context as DayBookActivity).hideLedger1 = text?.contains("Ledger 1") == false
        (context as DayBookActivity).hideLedger2 = text?.contains("Ledger 2") == false
        (context as DayBookActivity).hideDebit = text?.contains("Debit") == false
        (context as DayBookActivity).hideCredit = text?.contains("Credit") == false
        (context as DayBookActivity).hideVouchType = text?.contains("Voucher Type") == false
        (context as DayBookActivity).hideVouchNo = text?.contains("Voucher No") == false
        (context as DayBookActivity).hideName = text?.contains("Stock Item Name") == false
        (context as DayBookActivity).hideQuantity = text?.contains("Stock Item Quantity") == false
        (context as DayBookActivity).hideLiterPrice = text?.contains("Stock Item Rate") == false
        (context as DayBookActivity).hideTaxable = text?.contains("Taxable") == false
        (context as DayBookActivity).hideCgst = text?.contains("CGST") == false
        (context as DayBookActivity).hideSgst = text?.contains("SGST") == false
        (context as DayBookActivity).hideIgst = text?.contains("IGST") == false
        notifyDataSetChanged()
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(parent: ViewGroup,
        binding: DayBookTableListItemBinding,
        private val listener: ItemClickListener) : BaseViewHolder<DayBookTableListItemBinding, DayBookTableItem>(parent, binding, listener::onItemClick) {

    }

    interface ItemClickListener {
        fun onItemClick(item: DayBookTableItem)

        fun onItemClick(position: Int)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DayBookTableItem>() {
            override fun areItemsTheSame(oldItem: DayBookTableItem, newItem: DayBookTableItem) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: DayBookTableItem, newItem: DayBookTableItem) =
                oldItem == newItem
        }
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
//                val g: DayBookTableItem = list_search.get(i)
//                if (g.itemDescription?.toLowerCase()?.contains(charText) == true || g.ledger1?.toLowerCase()?.contains(charText) == true || g.vouchType?.toLowerCase()?.contains(charText) == true) {
//                    itemList?.add(list_search[i])
//                }
//            }
//        }
//        notifyDataSetChanged()
//    }
}