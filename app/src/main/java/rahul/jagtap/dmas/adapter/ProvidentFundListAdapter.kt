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
import rahul.jagtap.dmas.databinding.ItemProvidentFundBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.ProvidentFund
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.ProvidentFundActivity
import rahul.jagtap.dmas.utils.Utils
import java.util.*

class ProvidentFundListAdapter(var context: Context?, var itemList: ArrayList<ProvidentFund>? = null) : RecyclerView.Adapter<ProvidentFundListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val localInflater = LayoutInflater.from(parent.context)
        val binding = ItemProvidentFundBinding.inflate(localInflater, parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvType.text = if (!TextUtils.isEmpty(item?.typeToShow)) item?.typeToShow else item?.type
        holder.binding.tvApplicantName.text = item?.applicantName
//        holder.binding.tvPersonName.text = item?.personName
        holder.binding.tvPersonMobileNo.text = item?.personMobileNo
        holder.binding.tvPersonMobileNo.paintFlags = holder.binding.tvPersonMobileNo.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.binding.tvPersonMobileNo?.setOnClickListener {
            itemList!![holder.absoluteAdapterPosition]?.personMobileNo?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        }
//        holder.binding.tvPersonEmail.text = item?.personEmail
        holder.binding.tvUanNumber.text = item?.uanNumber
        holder.binding.tvPassword.text = item?.password
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
            ProvidentFundActivity.TYPE_CHECK_PF -> {
                holder.binding.llPassword?.visible()
                holder.binding.llPanCardPhoto?.gone()
                holder.binding.llPassbook?.gone()
                holder.binding.llAadharCardFront?.gone()
                holder.binding.llAadharCardBack?.gone()
            }
            ProvidentFundActivity.TYPE_UAN_ACTIVATE -> {
                holder.binding.llPassword?.gone()
                holder.binding.llPanCardPhoto?.gone()
                holder.binding.llPassbook?.gone()
                holder.binding.llAadharCardFront?.visible()
                holder.binding.llAadharCardBack?.visible()
            }
            ProvidentFundActivity.TYPE_CREATE_PASSWORD -> {
                holder.binding.llPassword?.gone()
                holder.binding.llPanCardPhoto?.gone()
                holder.binding.llPassbook?.gone()
                holder.binding.llAadharCardFront?.gone()
                holder.binding.llAadharCardBack?.gone()
            }
            ProvidentFundActivity.TYPE_KYC -> {
                holder.binding.llPassword?.visible()
                holder.binding.llPanCardPhoto?.visible()
                holder.binding.llPassbook?.visible()
                holder.binding.llAadharCardFront?.visible()
                holder.binding.llAadharCardBack?.visible()
            }
            ProvidentFundActivity.TYPE_PF_TRANSFER -> {
                holder.binding.llPassword?.visible()
                holder.binding.llPanCardPhoto?.gone()
                holder.binding.llPassbook?.gone()
                holder.binding.llAadharCardFront?.gone()
                holder.binding.llAadharCardBack?.gone()
            }
            ProvidentFundActivity.TYPE_MARK_EXIT -> {
                holder.binding.llPassword?.visible()
                holder.binding.llPanCardPhoto?.gone()
                holder.binding.llPassbook?.gone()
                holder.binding.llAadharCardFront?.gone()
                holder.binding.llAadharCardBack?.gone()
            }
            ProvidentFundActivity.TYPE_FILL_WITHDRAWL_FORM -> {
                holder.binding.llPassword?.visible()
                holder.binding.llPanCardPhoto?.gone()
                holder.binding.llPassbook?.visible()
                holder.binding.llAadharCardFront?.gone()
                holder.binding.llAadharCardBack?.gone()
            }
        }
//        if (TextUtils.isEmpty(item?.passportNomineePhotoFileName)) {
//            holder.itemView.tvPassportNomineePhoto?.text = "NA"
//            holder.itemView.tvPassportNomineePhoto.setOnClickListener {
//                Toast.makeText(holder.itemView.context, "Not uploaded", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            holder.itemView.tvPassportNomineePhoto?.text = "Click here to view"
//            holder.itemView.tvPassportNomineePhoto.setOnClickListener {
//                item?.passportNomineePhotoDownloadUrl?.let { it1 -> openPhoto(holder, "Passport Photo(Nominee)", it1) }
//            }
//        }
//        if (TextUtils.isEmpty(item?.aadharNomineePhotoFileName)) {
//            holder.itemView.tvNomineeAadharCardPhoto?.text = "NA"
//            holder.itemView.tvNomineeAadharCardPhoto.setOnClickListener {
//                Toast.makeText(holder.itemView.context, "Not uploaded", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            holder.itemView.tvNomineeAadharCardPhoto?.text = "Click here to view"
//            holder.itemView.tvNomineeAadharCardPhoto.setOnClickListener {
//                item?.aadharNomineePhotoDownloadUrl?.let { it1 -> openPhoto(holder, "Aadhar Card Photo(Nominee)", it1) }
//            }
//        }
//        if (TextUtils.isEmpty(item?.nomineePhotoFileName)) {
//            holder.itemView.tvNomineePhoto?.text = "NA"
//            holder.itemView.tvNomineePhoto.setOnClickListener {
//                Toast.makeText(holder.itemView.context, "Not uploaded", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            holder.itemView.tvNomineePhoto?.text = "Click here to view"
//            holder.itemView.tvNomineePhoto.setOnClickListener {
//                item?.nomineePhotoDownloadUrl?.let { it1 -> openPhoto(holder, "Nominee Photo", it1) }
//            }
//        }
        holder.binding.tvAadharCardFront.setOnClickListener {
            item?.aadharFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "Aadhar Card Front Photo", it1) }
        }
        holder.binding.tvAadharCardBack.setOnClickListener {
            item?.aadharBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "Aadhar Card Back Photo", it1) }
        }
        holder.binding.tvPanCard.setOnClickListener {
            item?.panPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "PAN Card Photo", it1) }
        }
        holder.binding.tvPassbookPhoto.setOnClickListener {
            item?.passbookPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "Bank Passbook Photo", it1) }
        }
//        holder.itemView.tvPassportPhoto.setOnClickListener {
//            item?.passportPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "Passport Photo", it1) }
//        }
        holder.binding.tvPaymentScreenshot.setOnClickListener {
            item?.paymentScreenshotDownloadUrl?.let { it1 -> openPhoto(holder, "Payment Screenshot", it1) }
        }
        holder.binding.btnDelete.setOnClickListener {
            Utils.showDialog(context, "Are you sure want to delete the record?", true) { dialog, which ->
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

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemProvidentFundBinding) : RecyclerView.ViewHolder(binding.root) {

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