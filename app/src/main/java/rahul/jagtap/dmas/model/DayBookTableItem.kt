package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// "date": "01/04/2021",
//    "ledger1": "Adesh Nagawade",
//    "ledger2": "Sales",
//    "debit": "2253.24",
//    "credit": "307.60",
//    "voucherType": "0",
//    "voucherNo": "Diesel",
//    "stockItemName": "33.00",
//    "stockItemQuantity": "68.28",
//    "stockItemRate": "25.00",
//    "cgst": "25.00",
//    "sgst": "25.00",
//    "igst": "25.00"
@Parcelize
data class DayBookTableItem(
    @SerializedName("date") @Expose var date: String? = "",
    @SerializedName("PERTICULAR-C") @Expose var ledger1: String? = "",
    @SerializedName("PERTICULAR-D") @Expose var ledger2: String? = "",
    @SerializedName("AMT-D") @Expose var debit: String? = "",
    @SerializedName("AMT-C") @Expose var credit: String? = "",
    @SerializedName("voucherType") @Expose var voucherType: String? = "",
    @SerializedName("voucherNo") @Expose var voucherNo: String? = "",
    @SerializedName("stockItemName") @Expose var stockItemName: String? = "",
    @SerializedName("stockItemQuantity") @Expose var stockItemQuantity: String? = "",
    @SerializedName("stockItemRate") @Expose var stockItemRate: String? = "",
    @SerializedName("TAXABLE") @Expose var taxable: String? = "",
    @SerializedName("cgst") @Expose var cgst: String? = "",
    @SerializedName("sgst") @Expose var sgst: String? = "",
    @SerializedName("igst") @Expose var igst: String? = "",
    var isLedger1Checked: Boolean? = false,
    var isLedger2Checked: Boolean? = false,
) : Parcelable {
    constructor() : this("", "", "", "", "",
        "", "", "", "", "", "", "", "")
}

@Parcelize
data class CustomDayBookTableItem(
    @SerializedName("date1") @Expose var date1: String? = "",
    @SerializedName("PERTICULAR-C") @Expose var ledger1: String? = "",
    @SerializedName("date2") @Expose var date2: String? = "",
    @SerializedName("PERTICULAR-D") @Expose var ledger2: String? = "",
    @SerializedName("AMT-D") @Expose var debit: String? = "",
    @SerializedName("AMT-C") @Expose var credit: String? = "",
    @SerializedName("voucherType") @Expose var voucherType: String? = "",
    @SerializedName("voucherNo") @Expose var voucherNo: String? = "",
    @SerializedName("stockItemName") @Expose var stockItemName: String? = "",
    @SerializedName("stockItemQuantity") @Expose var stockItemQuantity: String? = "",
    @SerializedName("stockItemRate") @Expose var stockItemRate: String? = "",
    @SerializedName("TAXABLE") @Expose var taxable: String? = "",
    @SerializedName("cgst") @Expose var cgst: String? = "",
    @SerializedName("sgst") @Expose var sgst: String? = "",
    @SerializedName("igst") @Expose var igst: String? = "",
    var isLedger1Checked: Boolean? = false,
    var isLedger2Checked: Boolean? = false,
) : Parcelable {
    constructor() : this("",  "", ""," ", "", "", "",
        "", "", "", "", "", "", "", "")
}