package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DayBook(
    @SerializedName("title") @Expose var title: String? = "",
    @SerializedName("fromDate") @Expose var fromDate: String? = "",
    @SerializedName("toDate") @Expose var toDate: String? = "",
    @SerializedName("json") @Expose var json: String? = "",
    @SerializedName("userEmail") @Expose var userEmail: String? = "",
    @SerializedName("userFullName") @Expose var userFullName: String? = "",
    @SerializedName("userContactNo") @Expose var userContactNo: String? = "",
    @SerializedName("userUid") @Expose var userUid: String? = "",
    @SerializedName("createdEmail") @Expose var createdEmail: String? = "",
    @SerializedName("createdBy") @Expose var createdBy: String? = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("createdByUid") @Expose var createdByUid: String? = "",
    @SerializedName("createdDateTime") @Expose var createdDateTime: String? = "",
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long? = 0
) : Parcelable {
    constructor() : this("","", "", "", "", "", "", "",
        "", "", "", "", "", "", -1)
}