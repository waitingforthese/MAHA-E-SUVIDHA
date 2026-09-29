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
import rahul.jagtap.dmas.databinding.ItemGazzetBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.Gazzet
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils

class GazzetListAdapter(private var context: Context?, var itemList: ArrayList<Gazzet>? = null) : RecyclerView.Adapter<GazzetListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_gazzet, parent, false)
        val binding = ItemGazzetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvFullName.text = item?.fullName
        holder.binding.tvFatherName.text = item?.fatherName
        holder.binding.tvMobileNo.text = item?.mobileNo
        holder.binding.tvMobileNo.paintFlags = holder.binding.tvMobileNo.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.binding.tvMobileNo?.setOnClickListener {
            itemList!![holder.absoluteAdapterPosition]?.mobileNo?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        }
        holder.binding.tvEmail.text = item?.userEmail
        holder.binding.tvCategory.text = if (!TextUtils.isEmpty(item?.categoryToShow)) item?.categoryToShow else item?.category
        holder.binding.tvAddress.text = item?.address
        holder.binding.tvCertificatePurpose.text = item?.certificatePurpose
        holder.binding.tvAddress.text = item?.address
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
        holder.binding.tvPassportPhoto.setOnClickListener {
            item?.passportPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "पासपोर्ट फोटो", it1) }
        }
        holder.binding.tvAadharCardFront.setOnClickListener {
            item?.aadharFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड च्या पुढील बाजूचा फोटो", it1) }
        }
        holder.binding.tvAadharCardBack.setOnClickListener {
            item?.aadharBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड च्या मागील बाजूचा फोटो", it1) }
        }
        holder.binding.tvRationCardFPhoto.setOnClickListener {
            item?.rationFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "ओरिजिनल रेशन कार्ड कार्डच्या पुढील बाजूचा फोटो", it1) }
        }
        holder.binding.tvRationCardBPhoto.setOnClickListener {
            item?.rationBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "ओरिजिनल रेशन कार्ड कार्डच्या मागील बाजूचा फोटो", it1) }
        }
        holder.binding.tvBonafide.setOnClickListener {
            item?.bonafideDownloadUrl?.let { it1 -> openPhoto(holder, "ओरिजिनल बोनाफाईड किव्हा LC किव्हा SSC/HSC प्रमाणपत्र", it1) }
        }
        holder.binding.tvSignPhoto.setOnClickListener {
            item?.signPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "सही चा नमुना कोऱ्या कागदावर", it1) }
        }
        holder.binding.tvStampHundred.setOnClickListener {
            item?.stampHundredPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "100 चा स्टॅम्प", it1) }
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
        holder.itemView.context.startActivity(Intent(holder.itemView.context, FullScreenImageActivity::class.java)
            .putExtra("title", title)
            .putExtra("imageUrl", path)
            .putExtra("downloadUrl", path)
        )
    }

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemGazzetBinding) : RecyclerView.ViewHolder(binding.root) {

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