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
import rahul.jagtap.dmas.databinding.ItemBusinessPanCardBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.BusinessPanCard
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.BusinessPanCardActivity
import rahul.jagtap.dmas.utils.Utils

class BusinessPanCardListAdapter(private var context: Context?, var itemList: ArrayList<BusinessPanCard>? = null) : RecyclerView.Adapter<BusinessPanCardListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_business_pan_card, parent, false)
//        return RecyclerViewHolder(v)
        val binding = ItemBusinessPanCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvType.text = if (!TextUtils.isEmpty(item?.typeToShow)) item?.typeToShow else item?.type
        holder.binding.tvApplicantName.text = item?.applicantName
        holder.binding.tvFullName.text = item?.fullName
        holder.binding.tvFullAddress.text = item?.fullAddress
        holder.binding.tvStartDate.text = item?.startDate
        holder.binding.tvMobileNumber.text = item?.mobileNo
        holder.binding.tvMobileNumber.paintFlags = holder.binding.tvMobileNumber.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.binding.tvMobileNumber?.setOnClickListener {
            itemList!![holder.absoluteAdapterPosition]?.mobileNo?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        }
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
            }
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
        }
        when (item?.type) {
            BusinessPanCardActivity.TYPE_PROPRIETOR -> {
                holder.binding.llPartnershipDeed.gone()
            }
            BusinessPanCardActivity.TYPE_PARTNERSHIP -> {
                holder.binding.llPartnershipDeed.visible()
            }
            BusinessPanCardActivity.TYPE_TYPE_THREE -> {
                holder.binding.llPartnershipDeed.gone()
            }
        }
        holder.binding.tvAadharCardFront.setOnClickListener {
            item?.aadharFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "अध्यक्ष किव्हा चेअरमन किव्हा सचिव - एकाचा आधार कार्ड समोरून फोटो", it1) }
        }
        holder.binding.tvAadharCardBack.setOnClickListener {
            item?.aadharBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "अध्यक्ष किव्हा चेअरमन किव्हा सचिव - एकाचा आधार कार्ड चा मागील फोटो", it1) }
        }
        holder.binding.tvGpNOCPhoto.setOnClickListener {
            item?.gpnocPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "ग्रामपंचायत ना हरकत दाखला", it1) }
        }
        holder.binding.tvStamp.setOnClickListener {
            item?.stampPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "कोऱ्या पेपर वर चोकोनी शिक्का", it1) }
        }

//        if (TextUtils.isEmpty(item?.regCertPhotoDownloadUrl)) {
//            holder.itemView.llRegPhoto.gone()
//        } else {
//            holder.itemView.llRegPhoto.visible()
//        }
//        if (TextUtils.isEmpty(item?.partnershipDeedPhotoDownloadUrl)) {
//            holder.itemView.llPartnershipDeed.gone()
//        } else {
//            holder.itemView.llPartnershipDeed.visible()
//        }
//
//        holder.itemView.tvRegPhoto.setOnClickListener {
//            item?.regCertPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "नोंदणी प्रमाणपत्र (पर्यायी)", it1) }
//        }
        holder.binding.tvPartnershipDeed.setOnClickListener {
            item?.partnershipDeedPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "पार्टनरशिप डीड", it1) }
        }
        holder.binding.tvPaymentScreenshot.setOnClickListener {
            item?.paymentScreenshotDownloadUrl?.let { it1 -> openPhoto(holder, "पेमेंट स्क्रीनशॉट", it1) }
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

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemBusinessPanCardBinding) : RecyclerView.ViewHolder(binding.root) {

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