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
import rahul.jagtap.dmas.databinding.ItemPassportBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.Passport
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.PassportActivity
import rahul.jagtap.dmas.utils.Utils

class PassportListAdapter(private var context: Context?, var itemList: ArrayList<Passport>? = null) : RecyclerView.Adapter<PassportListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_passport, parent, false)
        val binding = ItemPassportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvType.text = if (!TextUtils.isEmpty(item?.typeToShow)) item?.typeToShow else item?.type
        holder.binding.tvApplicantName.text = item?.applicantName
//        holder.binding.tvDob.text = Utils.ymdTodmy(item?.dob)
        holder.binding.tvEmail.text = item?.applicantEmail
        holder.binding.tvMobileNo.text = item?.mobileNo
        holder.binding.tvMobileNo.paintFlags = holder.binding.tvMobileNo.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.binding.tvMobileNo?.setOnClickListener {
            itemList!![holder.absoluteAdapterPosition]?.mobileNo?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        }
//        holder.binding.tvAadharNo.text = item?.aadharNo
        holder.binding.tvBirthPlace.text = item?.birthPlace
        holder.binding.tvEducation.text = item?.education
        holder.binding.tvEmployment.text = item?.employment
        holder.binding.tvMaritalStatus.text = item?.maritalStatus
        holder.binding.tvAddress.text = item?.address
        holder.binding.tvFatherName.text = item?.fatherName
        holder.binding.tvMotherName.text = item?.motherName
        holder.binding.tvEmergencyName.text = item?.emergencyName
        holder.binding.tvEmergencyMobileNo.text = item?.emergencyMobileNo
        holder.binding.tvEmergencyMobileNo.paintFlags = holder.binding.tvEmergencyMobileNo.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.binding.tvEmergencyMobileNo?.setOnClickListener {
            itemList!![holder.absoluteAdapterPosition]?.emergencyMobileNo?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        }
        holder.binding.tvHusbandWifeName.text = item?.husbandWifeName
        holder.binding.tvDateTime.text = item?.createdDateTime
        if (loggedInUser?.isAdmin == "1" || loggedInUser?.userType == "2") {
            holder.binding.tvCreatedBy.text = item?.createdBy
            holder.binding.llCreatedBy.visible()
            holder.binding.btnDelete.visible()
            holder.binding.btnExcelDownload.visible()
            holder.binding.btnDownloadAll.visible()
            holder.binding.btnCreateFolder.visible()
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
            holder.binding.btnExcelDownload.gone()
            holder.binding.btnDownloadAll.gone()
            holder.binding.btnCreateFolder.gone()
            holder.binding.btnContactCreator.gone()
        }
        holder.binding.btnExcelDownload.setOnClickListener {
            itemClickListener?.onExcelDownload(itemList!![holder.absoluteAdapterPosition])
        }
        when (item?.type) {
            PassportActivity.TYPE_DURUSTI -> {
                holder.binding.llOldPassportPhoto.visible()
                holder.binding.llOldPassportFileNoPhoto.visible()
            }
            PassportActivity.TYPE_NAVIN -> {
                holder.binding.llOldPassportPhoto.gone()
                holder.binding.llOldPassportFileNoPhoto.gone()
            }
        }
//        if (item?.type == "दुरुस्ती") {
//            holder.binding.llOldPanCardPhoto.visible()
//            holder.binding.tvOldPanCardPhoto.setOnClickListener {
//                item?.oldPanCardDownloadUrl?.let { it1 -> openPhoto(holder, "जुने पॅन कार्ड फोटो", it1) }
//            }
//        } else {
//            holder.binding.llOldPanCardPhoto.gone()
//        }
//        holder.binding.tvPassportPhoto.setOnClickListener {
//            item?.passportPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "पासपोर्ट फोटो", it1) }
//        }
        holder.binding.tvAadharCardFront.setOnClickListener {
            item?.aadharFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड पुढील फोटो", it1) }
        }
        holder.binding.tvAadharCardBack.setOnClickListener {
            item?.aadharBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड मागील फोटो", it1) }
        }
        holder.binding.tvPanCard.setOnClickListener {
            item?.panDownloadUrl?.let { it1 -> openPhoto(holder, "पॅन कार्ड फोटो", it1) }
        }
//        holder.binding.tvSignPhoto.setOnClickListener {
//            item?.signDownloadUrl?.let { it1 -> openPhoto(holder, "स्वाक्षरी फोटो", it1) }
//        }
        holder.binding.tvOldPassportPhoto.setOnClickListener {
            item?.oldPassportPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "जुना पासपोर्ट फोटो", it1) }
        }
        holder.binding.tvOldPassportFileNoPhoto.setOnClickListener {
            item?.oldPassportFileNoPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "जुना पासपोर्ट फोटो(फाईल नं)", it1) }
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

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemPassportBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface ItemClickListener {
        fun onDeleteClick(position: Int)
        fun onDownloadAll(position: Int, isCreateFolderClicked: Boolean)
        fun onMarkAsUnPaid(position: Int)
        fun onMarkAsPaid(position: Int)
        fun onCallOrSms(phoneNo: String)
        fun onCallOrSmsCreator()
        fun onExcelDownload(passport: Passport)
    }
}