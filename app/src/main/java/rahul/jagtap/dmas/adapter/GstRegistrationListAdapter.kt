package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.FullScreenImageActivity
import rahul.jagtap.dmas.databinding.ItemGstRegistrationBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.GstRegistration
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.ShopActActivity
import rahul.jagtap.dmas.utils.Utils

class GstRegistrationListAdapter(private var context: Context?, var itemList: ArrayList<GstRegistration>? = null) : RecyclerView.Adapter<GstRegistrationListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_gst_registration, parent, false)
        val binding = ItemGstRegistrationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvType.text = if (!TextUtils.isEmpty(item?.typeToShow)) item?.typeToShow else item?.type
        holder.binding.tvBusinessName.text = item?.businessName
        holder.binding.tvFullName.text = item?.applicantName
        holder.binding.tvMobileNo.text = item?.customerMobileNo
        holder.binding.tvMobileNo.paintFlags = holder.binding.tvMobileNo.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.binding.tvMobileNo?.setOnClickListener {
            itemList!![holder.absoluteAdapterPosition]?.customerMobileNo?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        }
        holder.binding.tvEmail.text = item?.customerEmail
//        if (!TextUtils.isEmpty(item?.partnershipAgreeDownloadUrl)) {
//            holder.binding.llPartnershipAgree?.visible()
//        } else {
//            holder.binding.llPartnershipAgree?.gone()
//        }
        holder.binding.tvDateTime.text = item?.createdDateTime
        if (loggedInUser?.isAdmin == "1" || loggedInUser?.userType == "2") {
            holder.binding.tvCreatedBy.text = item?.createdBy
            holder.binding.llCreatedBy.visible()
            holder.binding.btnDelete.visible()
            holder.binding.btnDownloadAll.visible()
            holder.binding.btnCreateFolder.visible()
            holder.binding.btnContactCreator.visible()
            holder.binding.btnContactCreator.setOnClickListener {
                itemClickListener?.onCallOrSmsCreator()
            } //            holder.binding.btnPdfDownload.visible()
            holder.binding.btnMarkAsPaidUnPaid.visible()
            holder.binding.llPaymentStatus.visible()
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
            holder.binding.btnDownloadAll.gone()
            holder.binding.btnCreateFolder.gone()
            holder.binding.btnContactCreator.gone()
            holder.binding.btnPdfDownload.gone()
        }
        when (item?.type) {
            ShopActActivity.TYPE_NAVIN -> {
                holder.binding.llPartnershipDeed.gone()
            }
            ShopActActivity.TYPE_DURUSTI -> {
                holder.binding.llPartnershipDeed.visible()
            }
        }
        holder.binding.tvAadharCardFront.setOnClickListener {
            item?.aadharFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड पुढील फोटो", it1) }
        }
        holder.binding.tvAadharCardBack.setOnClickListener {
            item?.aadharBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड मागील फोटो", it1) }
        }
        holder.binding.tvCompanyPanCard.setOnClickListener {
            item?.companyPanCardPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "कस्टमरचे / कंपनी पँण कार्ड", it1) }
        }
        holder.binding.tvOnlineRentalAgree.setOnClickListener {
            item?.onlineRentalAgreeDownloadUrl?.let { it1 -> openPhoto(holder, "ऑनलाईन भाडे करार", it1) }
        }
        holder.binding.tvLightBill.setOnClickListener {
            item?.lightBillDownloadUrl?.let { it1 -> openPhoto(holder, "लाईट बिल", it1) }
        }
//        holder.binding.tvPartnershipAgree.setOnClickListener {
//            item?.partnershipAgreeDownloadUrl?.let { it1 -> openPhoto(holder, "भागीरारी करार इतर (पर्यायी)", it1) }
//        }
        holder.binding.tvUdyamAadhar.setOnClickListener {
            item?.udyamAadharDownloadUrl?.let { it1 -> openPhoto(holder, "उद्यम आधार", it1) }
        }
        holder.binding.tvPartnershipDeed.setOnClickListener {
            item?.udyamAadharDownloadUrl?.let { it1 -> openPhoto(holder, "पार्टनरशिप डीड", it1) }
        }
        holder.binding.tvPaymentScreenshot.setOnClickListener {
            item?.paymentScreenshotDownloadUrl?.let { it1 -> openPhoto(holder, "पेमेंट स्क्रीन शॉट", it1) }
        }
        holder.binding.btnDelete.setOnClickListener {
            Utils.showDialog(context, "तुम्हाला खात्री आहे की रेकॉर्ड हटवायचा आहे?", true) { dialog, which ->
                run {
                    dialog.dismiss()
                    itemClickListener?.onDeleteClick(holder.adapterPosition)
                }
            }
        }
        holder.binding.btnDownloadAll.setOnClickListener {
            itemClickListener?.onDownloadAll(holder.adapterPosition, false)
        }
        holder.binding.btnCreateFolder.setOnClickListener {
            itemClickListener?.onDownloadAll(holder.adapterPosition, true)
        }
        holder.binding.btnPdfDownload.setOnClickListener {
            itemClickListener?.onPdfDownload(holder.adapterPosition)
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
        holder.itemView.context.startActivity(Intent(holder.itemView.context, FullScreenImageActivity::class.java).putExtra("title", title).putExtra("imageUrl", path).putExtra("downloadUrl", path))
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemGstRegistrationBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface ItemClickListener {
        fun onDeleteClick(position: Int)
        fun onDownloadAll(position: Int, isCreateFolderClicked: Boolean)
        fun onMarkAsUnPaid(position: Int)
        fun onMarkAsPaid(position: Int)
        fun onCallOrSms(phoneNo: String)
        fun onCallOrSmsCreator()
        fun onPdfDownload(position: Int)
    }
}