package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyEntry(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose var createdBy: String? = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose var uid: String? = "",
    @SerializedName("createdDateTime") @Expose var createdDateTime: String? = "",
    @SerializedName("cash_bank") @Expose var cash_bank: String? = "",
    @SerializedName("date") @Expose var date: String? = "",
    @SerializedName("details") @Expose var details: String? = "",
    @SerializedName("debit_credit") @Expose var debit_credit: String? = "",
    @SerializedName("amount") @Expose var amount: String? = "",
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long? = 0
) : Parcelable {
    constructor() : this("", "", "", "", "",
        "", "", "", "", "", "", -1)
}