package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.FullScreenImageActivity
import rahul.jagtap.dmas.databinding.ItemGovtSchemeBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.GovtScheme
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.AllGovtCardsActivity
import rahul.jagtap.dmas.user.GovtSchemesActivity
import rahul.jagtap.dmas.utils.Utils
import java.util.*

class GovtSchemeListAdapter(
    var context: Context?, var itemList: ArrayList<GovtScheme>? = null
) : RecyclerView.Adapter<GovtSchemeListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerViewHolder { //        val localInflater = LayoutInflater.from(parent.context)
        //        val v = localInflater.inflate(R.layout.item_govt_scheme, parent, false)
        val binding = ItemGovtSchemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvServiceType.text = if (!TextUtils.isEmpty(item?.serviceType)) item?.serviceType
        else if (!TextUtils.isEmpty(item?.typeToShow)) item?.typeToShow
        else item?.type
        holder.binding.llTextbox1.visibility = if (item?.textbox1.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llTextbox2.visibility = if (item?.textbox2.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llTextbox3.visibility = if (item?.textbox3.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llTextbox4.visibility = if (item?.textbox4.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llTextbox5.visibility = if (item?.textbox5.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llDatebox.visibility = if (item?.datebox.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llAttachment1.visibility = if (item?.attachment1FileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llAttachment2.visibility = if (item?.attachment2FileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llAttachment3.visibility = if (item?.attachment3FileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llAttachment4.visibility = if (item?.attachment4FileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llAttachment5.visibility = if (item?.attachment5FileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llAttachment6.visibility = if (item?.attachment6FileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llAttachment7.visibility = if (item?.attachment7FileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llAttachment8.visibility = if (item?.attachment8FileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llAttachment9.visibility = if (item?.attachment9FileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llAttachment10.visibility = if (item?.attachment10FileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.tvTextbox1Label.text = item?.textbox1Hint
        holder.binding.tvTextbox2Label.text = item?.textbox2Hint
        holder.binding.tvTextbox3Label.text = item?.textbox3Hint
        holder.binding.tvTextbox4Label.text = item?.textbox4Hint
        holder.binding.tvTextbox5Label.text = item?.textbox5Hint
        holder.binding.tvDateboxLabel.text = item?.dateboxHint
        holder.binding.tvTextbox1.text = item?.textbox1
        holder.binding.tvTextbox2.text = item?.textbox2
        holder.binding.tvTextbox3.text = item?.textbox3
        holder.binding.tvTextbox4.text = item?.textbox4
        holder.binding.tvTextbox5.text = item?.textbox5
        holder.binding.tvDatebox.text = item?.datebox
        holder.binding.tvAttachment1Label.text = item?.attachment1Hint
        holder.binding.tvAttachment2Label.text = item?.attachment2Hint
        holder.binding.tvAttachment3Label.text = item?.attachment3Hint
        holder.binding.tvAttachment4Label.text = item?.attachment4Hint
        holder.binding.tvAttachment5Label.text = item?.attachment5Hint
        holder.binding.tvAttachment6Label.text = item?.attachment6Hint
        holder.binding.tvAttachment7Label.text = item?.attachment7Hint
        holder.binding.tvAttachment8Label.text = item?.attachment8Hint
        holder.binding.tvAttachment9Label.text = item?.attachment9Hint
        holder.binding.tvAttachment10Label.text = item?.attachment10Hint
        holder.binding.tvTextbox3.paintFlags = holder.binding.tvTextbox3.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.binding.tvTextbox3?.setOnClickListener {
            itemList!![holder.absoluteAdapterPosition]?.textbox3?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        }
        //        holder.binding.tvApplicantName.text = item?.applicantName
        //        holder.binding.tvBusinessName.text = item?.businessName
        //        holder.binding.tvBusinessAddress.text = item?.businessAddress
        //        holder.binding.tvBusinessNature.text = item?.businessNature
        //        holder.binding.tvOwnerMobileNo.text = item?.ownerMobileNo
        //        holder.binding.tvOwnerMobileNo.paintFlags = holder.binding.tvOwnerMobileNo.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        //        holder.binding.tvOwnerMobileNo?.setOnClickListener {
        //            itemList!![holder.absoluteAdapterPosition]?.ownerMobileNo?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        //        }
        //        holder.binding.tvOwnerEmail.text = item?.ownerEmail
        //        holder.binding.tvOwnerCast.text = item?.ownerCast
        //        holder.binding.tvLoanAmt.text = item?.loanAmt
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
        } //        if (!TextUtils.isEmpty(item?.schoolLCDownloadUrl)) {
        //            holder.binding.llSchoolLC.visible()
        //        } else {
        //            holder.binding.llSchoolLC.gone()
        //        }
        //        if (!TextUtils.isEmpty(item?.birthCertDownloadUrl)) {
        //            holder.binding.llBirthCert.visible()
        //        } else {
        //            holder.binding.llBirthCert.gone()
        //        }
        //        if (!TextUtils.isEmpty(item?.incomeCertDownloadUrl)) {
        //            holder.binding.llIncomeCert.visible()
        //        } else {
        //            holder.binding.llIncomeCert.gone()
        //        }
        //        when (item?.type) {
        //            GovtSchemesActivity.TYPE_NEW_SCHEME_1 -> {
        //                holder.binding.llSchoolLC.gone()
        //                holder.binding.llBirthCert.gone()
        //                holder.binding.llIncomeCert.gone()
        //                holder.binding.llCastCert.gone()
        //                holder.binding.llProjectReport.gone()
        //            }
        //            GovtSchemesActivity.TYPE_NEW_SCHEME_2 -> {
        //                holder.binding.llSchoolLC.visible()
        //                holder.binding.llBirthCert.visible()
        //                holder.binding.llIncomeCert.gone()
        //                holder.binding.llCastCert.gone()
        //                holder.binding.llProjectReport.gone()
        //            }
        //            GovtSchemesActivity.TYPE_CMEGP -> {
        //                holder.binding.llSchoolLC.visible()
        //                holder.binding.llBirthCert.visible()
        //                holder.binding.llIncomeCert.gone()
        //                holder.binding.llCastCert.gone()
        //                holder.binding.llProjectReport.gone()
        //            }
        //            GovtSchemesActivity.TYPE_ANNASAHEB_PATIL -> {
        //                holder.binding.llSchoolLC.visible()
        //                holder.binding.llBirthCert.gone()
        //                holder.binding.llIncomeCert.visible()
        //                holder.binding.llCastCert.gone()
        //                holder.binding.llProjectReport.gone()
        //            }
        //            GovtSchemesActivity.TYPE_OBC_MAHAMANDAL -> {
        //                holder.binding.llSchoolLC.visible()
        //                holder.binding.llBirthCert.gone()
        //                holder.binding.llIncomeCert.gone()
        //                holder.binding.llCastCert.visible()
        //                holder.binding.llProjectReport.visible()
        //            }
        //            GovtSchemesActivity.TYPE_CAST_MAHAMANDAL -> {
        //                holder.binding.llSchoolLC.visible()
        //                holder.binding.llBirthCert.gone()
        //                holder.binding.llIncomeCert.gone()
        //                holder.binding.llCastCert.visible()
        //                holder.binding.llProjectReport.visible()
        //            }
        //            GovtSchemesActivity.TYPE_TYPE_THREE -> {
        //                holder.binding.llSchoolLC.visible()
        //                holder.binding.llBirthCert.gone()
        //                holder.binding.llIncomeCert.gone()
        //                holder.binding.llCastCert.gone()
        //                holder.binding.llProjectReport.gone()
        //            }
        //        }
        holder.binding.tvAttachment1.setOnClickListener { item?.attachment1DownloadUrl?.let { it1 -> openPhoto(holder, item.attachment1Hint.toString(), it1) } }
        holder.binding.tvAttachment2.setOnClickListener { item?.attachment2DownloadUrl?.let { it1 -> openPhoto(holder, item.attachment2Hint.toString(), it1) } }
        holder.binding.tvAttachment3.setOnClickListener { item?.attachment3DownloadUrl?.let { it1 -> openPhoto(holder, item.attachment3Hint.toString(), it1) } }
        holder.binding.tvAttachment4.setOnClickListener { item?.attachment4DownloadUrl?.let { it1 -> openPhoto(holder, item.attachment4Hint.toString(), it1) } }
        holder.binding.tvAttachment5.setOnClickListener { item?.attachment5DownloadUrl?.let { it1 -> openPhoto(holder, item.attachment5Hint.toString(), it1) } }
        holder.binding.tvAttachment6.setOnClickListener { item?.attachment6DownloadUrl?.let { it1 -> openPhoto(holder, item.attachment6Hint.toString(), it1) } }
        holder.binding.tvAttachment7.setOnClickListener { item?.attachment7DownloadUrl?.let { it1 -> openPhoto(holder, item.attachment7Hint.toString(), it1) } }
        holder.binding.tvAttachment8.setOnClickListener { item?.attachment8DownloadUrl?.let { it1 -> openPhoto(holder, item.attachment8Hint.toString(), it1) } }
        holder.binding.tvAttachment9.setOnClickListener { item?.attachment9DownloadUrl?.let { it1 -> openPhoto(holder, item.attachment9Hint.toString(), it1) } }
        holder.binding.tvAttachment10.setOnClickListener { item?.attachment10DownloadUrl?.let { it1 -> openPhoto(holder, item.attachment10Hint.toString(), it1) } }
        //        holder.binding.tvAadharCardFront.setOnClickListener {
        //            item?.aadharFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड च्या पुढील बाजूचा फोटो", it1) }
        //        }
        //        holder.binding.tvAadharCardBack.setOnClickListener {
        //            item?.aadharBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड च्या मागील बाजूचा फोटो", it1) }
        //        }
        //        holder.binding.tvPassbookPhoto.setOnClickListener {
        //            item?.passbookPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "बँक पासबुक फोटो", it1) }
        //        }
        //        holder.binding.tvPanCard.setOnClickListener {
        //            item?.panPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "पॅन कार्ड फोटो", it1) }
        //        }
        //        holder.binding.tvRationCardFront.setOnClickListener {
        //            item?.rationFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "रेशन कार्ड च्या पुढील बाजूचा फोटो", it1) }
        //        }
        //        holder.binding.tvRationCardBack.setOnClickListener {
        //            item?.rationBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "रेशन कार्ड च्या मागील बाजूचा फोटो", it1) }
        //        }
        //        holder.binding.tvSignPhoto.setOnClickListener {
        //            item?.signPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "कोर्या कागदावर सही नमुना फोटो", it1) }
        //        }
        //        holder.binding.tvPassportPhoto.setOnClickListener {
        //            item?.passportPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "पासपोर्ट साईझ फोटो चा फोटो", it1) }
        //        }
        //        holder.binding.tvSchoolLC.setOnClickListener {
        //            item?.schoolLCDownloadUrl?.let { it1 -> openPhoto(holder, "शाळा सोडल्याचा दाखला/मार्क शीट", it1) }
        //        }
        //        holder.binding.tvBirthCert.setOnClickListener {
        //            item?.birthCertDownloadUrl?.let { it1 -> openPhoto(holder, "जन्म दाखला/दोमासाईल", it1) }
        //        }
        //        holder.binding.tvIncomeCert.setOnClickListener {
        //            item?.incomeCertDownloadUrl?.let { it1 -> openPhoto(holder, "उत्पन्नाचा दाखला", it1) }
        //        }
        //        holder.binding.tvCastCert.setOnClickListener {
        //            item?.castCertDownloadUrl?.let { it1 -> openPhoto(holder, "जातीचा दाखला", it1) }
        //        }
        //        holder.binding.tvProjectReport.setOnClickListener {
        //            item?.projectReportDownloadUrl?.let { it1 -> openPhoto(holder, "प्रोजेक्ट रिपोर्ट", it1) }
        //        }
        //        holder.binding.tvPaymentScreenshot.setOnClickListener {
        //            item?.paymentScreenshotDownloadUrl?.let { it1 -> openPhoto(holder, "पेमेंट स्क्रीनशॉट", it1) }
        //        }
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

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(
        val binding: ItemGovtSchemeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

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