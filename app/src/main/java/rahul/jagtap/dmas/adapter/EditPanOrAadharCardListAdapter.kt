package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.FullScreenImageActivity
import rahul.jagtap.dmas.databinding.ItemEditPanAadharCardBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.EditPanAadharCard
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils

class EditPanOrAadharCardListAdapter(private var context: Context?, var itemList: ArrayList<EditPanAadharCard>? = null) : RecyclerView.Adapter<EditPanOrAadharCardListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_edit_pan_aadhar_card, parent, false)
        val binding = ItemEditPanAadharCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvPanOrAadharCard.text = item?.panOrAadharCard
        holder.binding.tvPanNumber.text = item?.panNo
        holder.binding.tvFullName.text = item?.fullName
        holder.binding.tvFatherName.text = item?.fatherName
        holder.binding.tvDob.text = Utils.ymdTodmy(item?.dob)
        holder.binding.tvAadharNo.text = item?.aadharNo
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
        if (item?.panOrAadharCard == "पँण कार्ड") {
            holder.binding.llPanNo.visible()
            holder.binding.llOldPanCardPhoto.visible()
            holder.binding.llOldAadharCardPhoto.gone()
            holder.binding.llAadharNo.gone()
            holder.binding.llAddress.gone()
        } else {
            holder.binding.llPanNo.gone()
            holder.binding.llOldPanCardPhoto.gone()
            holder.binding.llOldAadharCardPhoto.visible()
            holder.binding.llAadharNo.visible()
            holder.binding.llAddress.visible()
        }
        holder.binding.tvOldAadharPhoto.setOnClickListener {
            item?.oldAadharCardPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "जुने आधार कार्ड फोटो", it1) }
        }
        holder.binding.tvOldPanPhoto.setOnClickListener {
            item?.oldPanCardPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "जुने पॅन कार्ड फोटो", it1) }
        }
        holder.binding.tvPassportPhoto.setOnClickListener {
            item?.passportPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "पासपोर्ट फोटो", it1) }
        }
//        holder.binding.tvAadharCardFront.setOnClickListener {
//            item?.aadharFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड च्या पुढील बाजूचा फोटो", it1) }
//        }
//        holder.binding.tvAadharCardBack.setOnClickListener {
//            item?.aadharBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड च्या मागील बाजूचा फोटो", it1) }
//        }
//        holder.binding.tvSignPhoto.setOnClickListener {
//            item?.signDownloadUrl?.let { it1 -> openPhoto(holder, "स्वाक्षरी फोटो", it1) }
//        }
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

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemEditPanAadharCardBinding) : RecyclerView.ViewHolder(binding.root) {

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