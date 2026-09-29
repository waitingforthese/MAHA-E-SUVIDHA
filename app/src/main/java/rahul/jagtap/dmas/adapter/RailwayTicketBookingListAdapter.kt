package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.FullScreenImageActivity
import rahul.jagtap.dmas.databinding.ItemRailwayTicketBookingBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.RailwayTicketBooking
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils
import java.util.*

class RailwayTicketBookingListAdapter(var context: Context?, var itemList: ArrayList<RailwayTicketBooking>? = null) : RecyclerView.Adapter<RailwayTicketBookingListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_railway_ticket_booking, parent, false)
        val binding = ItemRailwayTicketBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvFrom.text = item?.rail_booking_from
        holder.binding.tvTo.text = item?.rail_booking_destination
        holder.binding.tvDate.text = item?.rail_booking_date
        holder.binding.tvName.text = item?.rail_booking_name
        holder.binding.tvAge.text = item?.rail_booking_age
        holder.binding.tvMobileNumber.text = item?.rail_booking_mobileNo
        holder.binding.tvMobileNumber.paintFlags = holder.binding.tvMobileNumber.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.binding.tvMobileNumber?.setOnClickListener {
            itemList!![holder.absoluteAdapterPosition]?.rail_booking_mobileNo?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        }
        holder.binding.tvAddress.text = item?.rail_booking_address
        holder.binding.tvDateTime.text = item?.createdDateTime
        if (loggedInUser?.isAdmin == "1" || loggedInUser?.userType == "2") {
            holder.binding.tvCreatedBy.text = item?.createdBy
            holder.binding.llCreatedBy.visible()
            holder.binding.btnDelete.visible()
            holder.binding.btnContactCreator.visible()
            holder.binding.btnContactCreator.setOnClickListener {
                itemClickListener?.onCallOrSmsCreator()
            }
            when (item?.paymentStatus) {
                "1" -> {
                    holder.binding.btnMarkAsPaidUnPaid.text = "Mark as Un-Paid"
                    holder.binding.tvPaymentStatus.text = "PAID"
                }
                "2" -> {
                    holder.binding.btnMarkAsPaidUnPaid.text = "Mark as Paid"
                    holder.binding.tvPaymentStatus.text = "UN-PAID"
                }
                else -> {
                    holder.binding.btnMarkAsPaidUnPaid.text = "Mark as Paid"
                    holder.binding.tvPaymentStatus.text = ""
                }
            }
        } else {
            holder.binding.btnMarkAsPaidUnPaid.gone()
            holder.binding.llPaymentStatus.gone()
            holder.binding.llCreatedBy.gone()
            holder.binding.btnDelete.gone()
            holder.binding.btnContactCreator.gone()
        }
        holder.binding.tvAadharCard.setOnClickListener {
            item?.aadharPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "Aadhar Card Photo", it1) }
        }
        holder.binding.btnDelete.setOnClickListener {
            Utils.showDialog(context, "Are you sure want to delete the record?", true) { dialog, which ->
                run {
                    dialog.dismiss()
                    itemClickListener?.onDeleteClick(holder.adapterPosition)
                }
            }
        }
        holder.binding.btnMarkAsPaidUnPaid.setOnClickListener {
            if (item?.paymentStatus == "1") {
                itemClickListener?.onMarkAsUnPaid(holder.adapterPosition)
            } else {
                itemClickListener?.onMarkAsPaid(holder.adapterPosition)
            }
        }
    }

    private fun openPhoto(holder: RecyclerViewHolder, title: String, path: String) {
        holder.itemView.context.startActivity(Intent(holder.itemView.context, FullScreenImageActivity::class.java)
            .putExtra("title", title)
            .putExtra("imageUrl", path)
            .putExtra("downloadUrl", path)
        )
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemRailwayTicketBookingBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface ItemClickListener {
        fun onDeleteClick(position: Int)
        fun onDownloadAll(position: Int, isCreateFolderClicked: Boolean)
        fun onMarkAsUnPaid(position: Int)
        fun onMarkAsPaid(position: Int)
        fun onCallOrSms(phoneNo: String)
        fun onCallOrSmsCreator()
    }
}