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
import rahul.jagtap.dmas.databinding.ItemAllOtherCardBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.AllOtherCard
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.utils.Utils

class AllOtherCardListAdapter(private var context: Context?, var itemList: ArrayList<AllOtherCard>? = null) : RecyclerView.Adapter<AllOtherCardListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_all_other_card, parent, false)
//        return RecyclerViewHolder(v)
        val binding = ItemAllOtherCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvServiceType.text = item?.serviceType
        holder.binding.tvApplicantName.text = item?.applicantName
        holder.binding.tvMobileNo.text = item?.mobileNo
        holder.binding.tvMobileNo.paintFlags = holder.binding.tvMobileNo.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.binding.tvMobileNo?.setOnClickListener {
            itemList!![holder.absoluteAdapterPosition]?.mobileNo?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        }
        holder.binding.tvGatNumber.text = item?.gatNumber
        val strServiceType = item?.serviceType
        if (strServiceType == "शेतकरी / अल्पभुधारक दाखला") {
            holder.binding.llGatNumber?.visible()
        } else
            holder.binding.llGatNumber?.gone()
        if ((strServiceType == "शेतकरी / अल्पभुधारक दाखला" || strServiceType == "नॉनक्रिमीलेयर" || strServiceType == "जातीचा दाखला")) {
            holder.binding.llApplicantSLC?.visible()
        } else {
            holder.binding.llApplicantSLC?.gone()
        }
        if ((strServiceType == "नॉनक्रिमीलेयर")) {
            holder.binding.llApplicantCastCertXerox?.visible()
        } else {
            holder.binding.llApplicantCastCertXerox?.gone()
        }
        if ((strServiceType == "नॉनक्रिमीलेयर" || strServiceType == "EWS State/Central List")) {
            holder.binding.llTehsildarIncomeCert?.visible()
        } else {
            holder.binding.llTehsildarIncomeCert?.gone()
        }
        if (strServiceType == "EWS State/Central List") {
            holder.binding.llFatherSLC?.visible()
            holder.binding.llGrandpaSLC?.visible()
        } else {
            holder.binding.llFatherSLC?.gone()
            holder.binding.llGrandpaSLC?.gone()
        }
        if ((strServiceType == "शेतकरी / अल्पभुधारक दाखला" || strServiceType == "नॉनक्रिमीलेयर" || strServiceType == "EWS State/Central List")) {
            holder.binding.llOtherImp?.visible()
        } else {
            holder.binding.llOtherImp?.gone()
        }
        if ((strServiceType == "जातीचा दाखला")) {
            holder.binding.llVanshaval?.visible()
            holder.binding.llVanshavalPersonCastCert?.visible()
            holder.binding.llLocalEnquiryReport?.visible()
        } else {
            holder.binding.llVanshaval?.gone()
            holder.binding.llVanshavalPersonCastCert?.gone()
            holder.binding.llLocalEnquiryReport?.gone()
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
//            holder.binding.btnPdfDownload.visible()
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
        holder.binding.tvApplicantPhoto.setOnClickListener {
            item?.applicantPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "अर्जदाराचा फोटो", it1) }
        }
        holder.binding.tvAadharCardFront.setOnClickListener {
            item?.aadharFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड पुढील फोटो", it1) }
        }
        holder.binding.tvAadharCardBack.setOnClickListener {
            item?.aadharBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड मागील फोटो", it1) }
        }
        holder.binding.tvRationCardFront.setOnClickListener {
            item?.rationFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "रेशन कार्ड पुढील फोटो", it1) }
        }
        holder.binding.tvRationCardBack.setOnClickListener {
            item?.rationBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "रेशन कार्ड मागील फोटो", it1) }
        }
        holder.binding.tvApplicantSLC.setOnClickListener {
            item?.applicantSLCDownloadUrl?.let { it1 -> openPhoto(holder, "अर्जदाराचा शाळा सोडलेचा दाखला", it1) }
        }
        holder.binding.tvApplicantCastCertXerox.setOnClickListener {
            item?.applicantCastCertXeroxDownloadUrl?.let { it1 -> openPhoto(holder, "अर्जदाराचा जातीचा दाखला झेरॉक्स", it1) }
        }
        holder.binding.tvTehsildarIncomeCert.setOnClickListener {
            item?.tehsildarIncomeCertDownloadUrl?.let { it1 -> openPhoto(holder, "तहसिलदार ३ वर्षे उत्पन्न दाखला", it1) }
        }
        holder.binding.tvFatherSLC.setOnClickListener {
            item?.fatherSLCDownloadUrl?.let { it1 -> openPhoto(holder, "वडील शाळा सोडलेचा दाखला", it1) }
        }
        holder.binding.tvGrandpaSLC.setOnClickListener {
            item?.grandpaSLCDownloadUrl?.let { it1 -> openPhoto(holder, "आजोबा शाळा सोडलेचा दाखला", it1) }
        }
        holder.binding.tvOtherImp.setOnClickListener {
            item?.otherImpDownloadUrl?.let { it1 -> openPhoto(holder, "इतर महत्त्वाचे", it1) }
        }
        holder.binding.tvVanshaval.setOnClickListener {
            item?.vanshavalDownloadUrl?.let { it1 -> openPhoto(holder, "वंशावळ - रू 100 च्या स्टॅम्प वर", it1) }
        }
        holder.binding.tvVanshavalPersonCastCert.setOnClickListener {
            item?.vanshavalPersonCastCertDownloadUrl?.let { it1 -> openPhoto(holder, "वंशावळीतील एका व्यक्तीचा जातीचा दाखला", it1) }
        }
        holder.binding.tvLocalEnquiryReport.setOnClickListener {
            item?.localEnquiryReportDownloadUrl?.let { it1 -> openPhoto(holder, "स्थानिक चौकशी अहवाल - तलाठी, ग्रामसेवक", it1) }
        }
        holder.binding.tvApplicantSignSpecimen.setOnClickListener {
            item?.applicantSignSpecimenDownloadUrl?.let { it1 -> openPhoto(holder, "अर्जदाराच्या सहीचा नमुना", it1) }
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

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemAllOtherCardBinding) : RecyclerView.ViewHolder(binding.root) {

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