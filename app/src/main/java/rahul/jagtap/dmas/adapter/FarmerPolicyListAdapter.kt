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
import rahul.jagtap.dmas.App
import rahul.jagtap.dmas.FullScreenImageActivity
import rahul.jagtap.dmas.databinding.ItemFarmerPolicyBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.FarmerPolicy
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.FarmerPolicyActivity
import rahul.jagtap.dmas.utils.Utils

class FarmerPolicyListAdapter(
    private var context: Context?, var itemList: ArrayList<FarmerPolicy>? = null
) : RecyclerView.Adapter<FarmerPolicyListAdapter.RecyclerViewHolder>() {
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerViewHolder { //        val localInflater = LayoutInflater.from(parent.context)
        //        val v = localInflater.inflate(R.layout.item_farmer_policy, parent, false)
        val binding = ItemFarmerPolicyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvPolicy.text = if (!TextUtils.isEmpty(item?.serviceType)) item?.serviceType
        else if (!TextUtils.isEmpty(item?.typeToShow)) item?.typeToShow
        else item?.type
        holder.binding.llApplicantName.visibility = if (item?.applicantName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llSaatBaaraLinkMobileNo.visibility = if (item?.saatBaaraLinkMobileNo.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llMobileNo.visibility = if (item?.aadharLinkedMobileNo.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llTextbox4.visibility = if (item?.strTextbox4.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llTextbox5.visibility = if (item?.strTextbox5.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llDatebox.visibility = if (item?.strDatebox.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llAadharCardFront.visibility = if (item?.aadharFrontPhotoFileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llAadharCardBack.visibility = if (item?.aadharBackPhotoFileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llLand8APhoto.visibility = if (item?.land8APhotoFileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llPanPhoto.visibility = if (item?.panPhotoFileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llCustomer8APhoto.visibility = if (item?.customer8APhotoFileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llOther8AOtherInfo.visibility = if (item?.other8AOtherInfoFileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llQuotationPhoto.visibility = if (item?.quotationFileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llCastCertPhoto.visibility = if (item?.castCertFileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llConsentLetterPhoto.visibility = if (item?.consentLetterFileName.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.binding.llPaymentScreenshot.visibility = if (item?.paymentScreenshotFileName.isNullOrEmpty()) View.GONE else View.VISIBLE

        holder.binding.tvApplicantNameLabel.text = item?.applicantNameHint
        holder.binding.tvSaatBaaraLinkMobileNoLabel.text = item?.saatBaaraLinkMobileNoHint
        holder.binding.tvMobileNoLabel.text = item?.aadharLinkedMobileNoHint
        holder.binding.tvTextbox4Label.text = item?.strTextbox4Hint
        holder.binding.tvTextbox5Label.text = item?.strTextbox5Hint
        holder.binding.tvDateboxLabel.text = item?.strDateboxHint
        holder.binding.tvAadharCardFrontLabel.text = item?.aadharFrontPhotoHint
        holder.binding.tvAadharCardBackLabel.text = item?.aadharBackPhotoHint
        holder.binding.tvLand8APhotoLabel.text = item?.land8APhotoHint
        holder.binding.tvPanPhotoLabel.text = item?.panPhotoHint
        holder.binding.tvCustomer8APhotoLabel.text = item?.customer8APhotoHint
        holder.binding.tvOther8AOtherInfoLabel.text = item?.other8AOtherInfoHint
        holder.binding.tvQuotationPhotoLabel.text = item?.quotationHint
        holder.binding.tvCastCertPhotoLabel.text = item?.castCertHint
        holder.binding.tvConsentLetterPhotoLabel.text = item?.consentLetterHint
        holder.binding.tvPaymentScreenshotLabel.text = item?.paymentScreenshotHint

        holder.binding.tvApplicantName.text = item?.applicantName
        holder.binding.tvSaatBaaraLinkMobileNo.text = item?.saatBaaraLinkMobileNo
        holder.binding.tvMobileNo.text = item?.aadharLinkedMobileNo
        holder.binding.tvTextbox4.text = item?.strTextbox4
        holder.binding.tvTextbox5.text = item?.strTextbox5
        holder.binding.tvDatebox.text = item?.strDatebox
        holder.binding.tvMobileNo.paintFlags = holder.binding.tvMobileNo.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.binding.tvMobileNo?.setOnClickListener {
            itemList!![holder.absoluteAdapterPosition].aadharLinkedMobileNo?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        }
        if (!TextUtils.isEmpty(item?.cropsToInsure)) {
            holder.binding.llCropsToInsure.visible()
            holder.binding.tvCropsToInsure.text = item?.cropsToInsure
        } else holder.binding.llCropsToInsure.gone()
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
//        when (item?.type) {
//            FarmerPolicyActivity.TYPE_PIK_VIMA -> {
//                holder.binding.llQuotationPhoto?.gone()
//                holder.binding.llCastCertPhoto?.gone()
//                holder.binding.llConsentLetterPhoto?.gone()
//                holder.binding.llLand8APhoto?.visible()
//                holder.binding.llPanPhoto?.gone()
//            }
//
//            FarmerPolicyActivity.TYPE_HARITGRUH -> {
//                holder.binding.llQuotationPhoto?.visible()
//                holder.binding.llCastCertPhoto?.visible()
//                holder.binding.llConsentLetterPhoto?.gone()
//                holder.binding.llLand8APhoto?.visible()
//                holder.binding.llPanPhoto?.gone()
//            }
//
//            FarmerPolicyActivity.TYPE_KANDA_CHAL -> {
//                holder.binding.llQuotationPhoto?.visible()
//                holder.binding.llCastCertPhoto?.gone()
//                holder.binding.llConsentLetterPhoto?.gone()
//                holder.binding.llLand8APhoto?.visible()
//                holder.binding.llPanPhoto?.gone()
//            }
//
//            FarmerPolicyActivity.TYPE_THHIBAK -> {
//                holder.binding.llQuotationPhoto?.visible()
//                holder.binding.llCastCertPhoto?.visible()
//                holder.binding.llConsentLetterPhoto?.visible()
//                holder.binding.llLand8APhoto?.visible()
//                holder.binding.llPanPhoto?.gone()
//            }
//
//            FarmerPolicyActivity.TYPE_KISAN_CC -> {
//                holder.binding.llQuotationPhoto?.gone()
//                holder.binding.llCastCertPhoto?.gone()
//                holder.binding.llConsentLetterPhoto?.gone()
//                holder.binding.llLand8APhoto?.visible()
//                holder.binding.llPanPhoto?.gone()
//            }
//
//            FarmerPolicyActivity.TYPE_ENGINEERING -> {
//                holder.binding.llQuotationPhoto?.visible()
//                holder.binding.llCastCertPhoto?.visible()
//                holder.binding.llConsentLetterPhoto?.gone()
//                holder.binding.llLand8APhoto?.visible()
//                holder.binding.llPanPhoto?.gone()
//            }
//
//            FarmerPolicyActivity.TYPE_SHRAM_YOGI -> {
//                holder.binding.llQuotationPhoto?.gone()
//                holder.binding.llCastCertPhoto?.gone()
//                holder.binding.llConsentLetterPhoto?.gone()
//                holder.binding.llLand8APhoto?.gone()
//                holder.binding.llPanPhoto?.visible()
//            }
//
//            FarmerPolicyActivity.TYPE_TYPE_1 -> {
//                holder.binding.llQuotationPhoto?.gone()
//                holder.binding.llCastCertPhoto?.gone()
//                holder.binding.llConsentLetterPhoto?.gone()
//                holder.binding.llLand8APhoto?.visible()
//                holder.binding.llPanPhoto?.gone()
//            }
//
//            FarmerPolicyActivity.TYPE_TYPE_2 -> {
//                holder.binding.llQuotationPhoto?.gone()
//                holder.binding.llCastCertPhoto?.gone()
//                holder.binding.llConsentLetterPhoto?.gone()
//                holder.binding.llLand8APhoto?.visible()
//                holder.binding.llPanPhoto?.gone()
//            }
//        }
        holder.binding.tvAadharCardFront.setOnClickListener {
            item?.aadharFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, item?.aadharFrontPhotoHint.toString(), it1) }
        }
        holder.binding.tvAadharCardBack.setOnClickListener {
            item?.aadharBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, item?.aadharBackPhotoHint.toString(), it1) }
        }
        holder.binding.tvLand8APhoto.setOnClickListener {
            item?.land8APhotoDownloadUrl?.let { it1 -> openPhoto(holder, item?.land8APhotoHint.toString(), it1) }
        }
        holder.binding.tvPanPhoto.setOnClickListener {
            item?.panPhotoDownloadUrl?.let { it1 -> openPhoto(holder, item?.panPhotoHint.toString(), it1) }
        }
        holder.binding.tvCustomer8APhoto.setOnClickListener {
            item?.customer8APhotoDownloadUrl?.let { it1 -> openPhoto(holder, item?.customer8APhotoHint.toString(), it1) }
        }
        holder.binding.tvOther8AOtherInfo.setOnClickListener {
            item?.other8AOtherInfoDownloadUrl?.let { it1 -> openPhoto(holder, item?.other8AOtherInfoHint.toString(), it1) }
        }
        holder.binding.tvPaymentScreenshot.setOnClickListener {
            item?.paymentScreenshotDownloadUrl?.let { it1 -> openPhoto(holder, item?.paymentScreenshotHint.toString(), it1) }
        }
        holder.binding.tvQuotationPhoto.setOnClickListener {
            item?.quotationDownloadUrl?.let { it1 -> openPhoto(holder, item?.quotationHint.toString(), it1) }
        }
        holder.binding.tvCastCertPhoto.setOnClickListener {
            item?.castCertDownloadUrl?.let { it1 -> openPhoto(holder, item?.castCertHint.toString(), it1) }
        }
        holder.binding.tvConsentLetterPhoto.setOnClickListener {
            item?.consentLetterDownloadUrl?.let { it1 -> openPhoto(holder, item?.consentLetterHint.toString(), it1) }
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

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(
        val binding: ItemFarmerPolicyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

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