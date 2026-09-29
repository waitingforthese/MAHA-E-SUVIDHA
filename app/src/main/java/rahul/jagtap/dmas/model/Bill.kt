package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Bill(
    @SerializedName("billType") @Expose var billType: String? = "",
    @SerializedName("createdDateTime") @Expose var createdDateTime: String? = "",
    @SerializedName("downloadUrl") @Expose var downloadUrl: String? = "",
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("fileName") @Expose var fileName: String? = "",
    @SerializedName("createdBy") @Expose var createdBy: String? = "",
    @SerializedName("createdByShopName") @Expose var createdByShopName: String? = "",
    @SerializedName("uid") @Expose var uid: String? = "",
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?
) : Parcelable {
    constructor() : this("", "", "", "","",
        "","", "","", -1)
}