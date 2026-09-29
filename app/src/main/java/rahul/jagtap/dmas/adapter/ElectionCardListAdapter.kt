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
import rahul.jagtap.dmas.databinding.ItemElectionCardBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.ElectionCard
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.ElectionCardActivity
import rahul.jagtap.dmas.utils.Utils

class ElectionCardListAdapter(private var context: Context?, var itemList: ArrayList<ElectionCard>? = null) : RecyclerView.Adapter<ElectionCardListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_election_card, parent, false)
        val binding = ItemElectionCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvApplicantName.text = item?.applicantName
        holder.binding.tvType.text = if (!TextUtils.isEmpty(item?.typeToShow)) item?.typeToShow else item?.type
//        holder.binding.tvFullName.text = item?.fullName
        holder.binding.tvEmail.text = item?.email
        holder.binding.tvVidhansabha.text = item?.vidhansabha
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
            ElectionCardActivity.TYPE_NAVIN -> {
                holder.binding.llElectionCardFront?.visible()
                holder.binding.llElectionCardBack?.visible()
                holder.binding.llOldElectionCardFront?.gone()
                holder.binding.llOldElectionCardBack?.gone()
//                holder.binding.tvCorrectionProofPhoto?.gone()
            }
            ElectionCardActivity.TYPE_DURUSTI -> {
                holder.binding.llElectionCardFront?.gone()
                holder.binding.llElectionCardBack?.gone()
                holder.binding.llOldElectionCardFront?.visible()
                holder.binding.llOldElectionCardBack?.visible()
//                holder.binding.tvCorrectionProofPhoto?.visible()
            }
            else -> {
                holder.binding.llElectionCardFront?.gone()
                holder.binding.llElectionCardBack?.gone()
                holder.binding.llOldElectionCardFront?.visible()
                holder.binding.llOldElectionCardBack?.visible()
//                holder.binding.tvCorrectionProofPhoto?.gone()
            }
        }
        holder.binding.tvAadharCardFront.setOnClickListener {
            item?.aadharFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "ग्राहकाच्या आधार कार्ड चा समोरून फोटो", it1) }
        }
        holder.binding.tvAadharCardBack.setOnClickListener {
            item?.aadharBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "ग्राहकाच्या आधार कार्ड चा मागील फोटो", it1) }
        }
        holder.binding.tvPassportPhoto.setOnClickListener {
            item?.passportPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "ग्राहकाचा पासपोर्ट फोटो चा फोटो", it1) }
        }
        holder.binding.tvElectionCardFront.setOnClickListener {
            item?.electionCardFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "घरातील एका व्यक्तीचे इलेक्शन कार्ड चा समोरून फोटो", it1) }
        }
        holder.binding.tvElectionCardBack.setOnClickListener {
            item?.electionCardBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "घरातील एका व्यक्तीचे इलेक्शन कार्ड चा मागील बाजूचा फोटो", it1) }
        }
        holder.binding.tvOldElectionCardFront.setOnClickListener {
            item?.oldElectionCardFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "जुन्या इलेक्शन कार्ड चा फोटो समोरून", it1) }
        }
        holder.binding.tvOldElectionCardBack.setOnClickListener {
            item?.oldElectionCardBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "जुन्या इलेक्शन कार्ड चा मागील बाजूचा फोटो", it1) }
        }
//        holder.binding.tvCorrectionProofPhoto.setOnClickListener {
//            item?.correctionProofPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "दुरुस्ती साठी योग्य पुरावा(पर्यायी)", it1) }
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

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemElectionCardBinding) : RecyclerView.ViewHolder(binding.root) {

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