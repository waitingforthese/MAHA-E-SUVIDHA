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
import rahul.jagtap.dmas.databinding.ItemFoodLicenseBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.FoodLicense
import rahul.jagtap.dmas.model.User
import rahul.jagtap.dmas.user.FoodLicenseActivity
import rahul.jagtap.dmas.utils.Utils
import java.util.*

class FoodLicenseListAdapter(var context: Context?, var itemList: ArrayList<FoodLicense>? = null) : RecyclerView.Adapter<FoodLicenseListAdapter.RecyclerViewHolder>() {
    private var isAdmin: Boolean = false
    private var loggedInUser: User? = null
    var itemClickListener: ItemClickListener? = null

    init {
        val app = context?.applicationContext as App
        loggedInUser = app.preferences?.loggedInUser
        isAdmin = app.preferences?.loggedInUser?.isAdmin == "1"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val localInflater = LayoutInflater.from(parent.context)
//        val v = localInflater.inflate(R.layout.item_food_license, parent, false)
        val binding = ItemFoodLicenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.binding.tvType.text = if (!TextUtils.isEmpty(item?.typeToShow)) item?.typeToShow else item?.type
        holder.binding.tvApplicantName.text = item?.applicantName
        holder.binding.tvBusinessName.text = item?.businessName
        holder.binding.tvBusinessAddress.text = item?.businessAddress
//        holder.binding.tvOwnerName.text = item?.ownerName
        holder.binding.tvOwnerMobileNo.text = item?.ownerMobileNo
        holder.binding.tvOwnerMobileNo.paintFlags = holder.binding.tvOwnerMobileNo.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.binding.tvOwnerMobileNo?.setOnClickListener {
            itemList!![holder.absoluteAdapterPosition]?.ownerMobileNo?.let { it1 -> itemClickListener?.onCallOrSms(it1) }
        }
        holder.binding.tvOwnerEmail.text = item?.ownerEmail
        holder.binding.tvYearsLicense.text = item?.yearsLicense
        holder.binding.tvProductSales.text = item?.productSales
        holder.binding.tvDateTime.text = item?.createdDateTime
        if (loggedInUser?.isAdmin == "1" || loggedInUser?.userType == "2") {
            holder.binding.tvCreatedBy.text = item?.createdBy
            holder.binding.llCreatedBy.visible()
            holder.binding.btnDownloadAll.visible()
            holder.binding.btnCreateFolder.visible()
            holder.binding.btnDelete.visible()
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
            holder.binding.btnDownloadAll.gone()
            holder.binding.btnCreateFolder.gone()
            holder.binding.btnDelete.gone()
            holder.binding.btnContactCreator.gone()
        }
        when (item?.type) {
            FoodLicenseActivity.TYPE_1_YR -> {
                holder.binding.llBusinessAddress?.visible()
                holder.binding.llOwnerEmail?.visible()
                holder.binding.llProductSales?.visible()
                holder.binding.llAadharFPhoto?.visible()
                holder.binding.llAadharBPhoto?.visible()
                holder.binding.llAddressProofPhoto?.visible()
                holder.binding.llPanPhoto?.visible()
//                holder.binding.llGPNOCPhoto?.visible()
                holder.binding.llPhoto?.visible()
//                holder.binding.llSignPhoto?.visible()
                holder.binding.llUdyamAadharPhoto?.visible()
                holder.binding.llOldFoodLicensePhoto?.gone()
                holder.binding.llRCBookPhoto?.gone()
            }
            FoodLicenseActivity.TYPE_2_YR -> {
                holder.binding.llBusinessAddress?.visible()
                holder.binding.llOwnerEmail?.visible()
                holder.binding.llProductSales?.visible()
                holder.binding.llAadharFPhoto?.visible()
                holder.binding.llAadharBPhoto?.visible()
                holder.binding.llAddressProofPhoto?.visible()
                holder.binding.llPanPhoto?.visible()
//                holder.binding.llGPNOCPhoto?.visible()
                holder.binding.llPhoto?.visible()
//                holder.binding.llSignPhoto?.visible()
                holder.binding.llUdyamAadharPhoto?.visible()
                holder.binding.llOldFoodLicensePhoto.gone()
                holder.binding.llRCBookPhoto.gone()
            }
            FoodLicenseActivity.TYPE_3_YR -> {
                holder.binding.llBusinessAddress?.visible()
                holder.binding.llOwnerEmail?.visible()
                holder.binding.llProductSales?.visible()
                holder.binding.llAadharFPhoto?.visible()
                holder.binding.llAadharBPhoto?.visible()
                holder.binding.llAddressProofPhoto?.visible()
                holder.binding.llPanPhoto?.visible()
//                holder.binding.llGPNOCPhoto?.visible()
                holder.binding.llPhoto?.visible()
//                holder.binding.llSignPhoto?.visible()
                holder.binding.llUdyamAadharPhoto?.visible()
                holder.binding.llOldFoodLicensePhoto.gone()
                holder.binding.llRCBookPhoto.gone()
            }
            FoodLicenseActivity.TYPE_4_YR -> {
                holder.binding.llBusinessAddress?.visible()
                holder.binding.llOwnerEmail?.visible()
                holder.binding.llProductSales?.visible()
                holder.binding.llAadharFPhoto?.visible()
                holder.binding.llAadharBPhoto?.visible()
                holder.binding.llAddressProofPhoto?.visible()
                holder.binding.llPanPhoto?.visible()
//                holder.binding.llGPNOCPhoto?.visible()
                holder.binding.llPhoto?.visible()
//                holder.binding.llSignPhoto?.visible()
                holder.binding.llUdyamAadharPhoto?.visible()
                holder.binding.llOldFoodLicensePhoto.gone()
                holder.binding.llRCBookPhoto.gone()
            }
            FoodLicenseActivity.TYPE_5_YR -> {
                holder.binding.llBusinessAddress?.visible()
                holder.binding.llOwnerEmail?.visible()
                holder.binding.llProductSales?.visible()
                holder.binding.llAadharFPhoto?.visible()
                holder.binding.llAadharBPhoto?.visible()
                holder.binding.llAddressProofPhoto?.visible()
                holder.binding.llPanPhoto?.visible()
//                holder.binding.llGPNOCPhoto?.visible()
                holder.binding.llPhoto?.visible()
//                holder.binding.llSignPhoto?.visible()
                holder.binding.llUdyamAadharPhoto?.visible()
                holder.binding.llOldFoodLicensePhoto.gone()
                holder.binding.llRCBookPhoto.gone()
            }
            FoodLicenseActivity.TYPE_RENEW -> {
                holder.binding.llBusinessAddress?.gone()
                holder.binding.llOwnerEmail?.gone()
                holder.binding.llProductSales?.gone()
                holder.binding.llAadharFPhoto?.gone()
                holder.binding.llAadharBPhoto?.gone()
                holder.binding.llAddressProofPhoto?.gone()
                holder.binding.llPanPhoto?.gone()
//                holder.binding.llGPNOCPhoto?.gone()
                holder.binding.llPhoto?.gone()
//                holder.binding.llSignPhoto?.gone()
                holder.binding.llUdyamAadharPhoto?.gone()
                holder.binding.llOldFoodLicensePhoto.visible()
                holder.binding.llRCBookPhoto.visible()
            }
        }
        holder.binding.tvAadharCardFront.setOnClickListener {
            item?.aadharFrontPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड च्या पुढील बाजूचा फोटो", it1) }
        }
        holder.binding.tvAadharCardBack.setOnClickListener {
            item?.aadharBackPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आधार कार्ड च्या मागील बाजूचा फोटो", it1) }
        }
        holder.binding.tvAddressProof.setOnClickListener {
            item?.addressProofPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "ऍड्रेस प्रूफ फोटो", it1) }
        }
        holder.binding.tvPanCard.setOnClickListener {
            item?.panPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "पॅन कार्ड फोटो", it1) }
        }
//        holder.binding.tvGpNOCPhoto.setOnClickListener {
//            item?.gpnocPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "ग्रामपंचायत ना हरकत फोटो", it1) }
//        }
        holder.binding.tvPhoto.setOnClickListener {
            item?.photoDownloadUrl?.let { it1 -> openPhoto(holder, "फोटो", it1) }
        }
//        holder.binding.tvSignPhoto.setOnClickListener {
//            item?.signPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "स्वाक्षरी फोटो", it1) }
//        }
        holder.binding.tvUdyamAadharPhoto.setOnClickListener {
            item?.udyamAadharPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "उद्यम आधार फोटो", it1) }
        }
        holder.binding.tvOldFoodLicensePhoto.setOnClickListener {
            item?.oldFoodLicensePhotoDownloadUrl?.let { it1 -> openPhoto(holder, "जुने फूड लायसेन्स", it1) }
        }
        holder.binding.tvRCBook.setOnClickListener {
            item?.rcBookPhotoDownloadUrl?.let { it1 -> openPhoto(holder, "आर सी बुक", it1) }
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

    inner class RecyclerViewHolder @SuppressLint("RestrictedApi") constructor(val binding: ItemFoodLicenseBinding) : RecyclerView.ViewHolder(binding.root) {

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