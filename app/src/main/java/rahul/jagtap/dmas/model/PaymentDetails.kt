package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentDetails(
    @SerializedName("paymentPhotoFileName") @Expose var paymentPhotoFileName: String? = "",
    @SerializedName("paymentPhotoDownloadUrl") @Expose var paymentPhotoDownloadUrl: String? = "",
    @SerializedName("downloadUrl") @Expose var downloadUrl: String? = "",
    @SerializedName("createdBy") @Expose var createdBy: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("createdDateTime") @Expose var createdDateTime: String? = ""
) : Parcelable {
    constructor() : this("", "", "", "",  -1, "")
}
@Parcelize
data class ReferralProgram(
    @SerializedName("photoFileName") @Expose var photoFileName: String? = "",
    @SerializedName("photoDownloadUrl") @Expose var photoDownloadUrl: String? = "",
    @SerializedName("downloadUrl") @Expose var downloadUrl: String? = "",
    @SerializedName("createdBy") @Expose var createdBy: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("createdDateTime") @Expose var createdDateTime: String? = ""
) : Parcelable {
    constructor() : this("", "", "", "",  -1, "")
}

@Parcelize
data class AboutTeamImage(
    @SerializedName("photoFileName1") @Expose var photoFileName1: String? = "",
    @SerializedName("photoDownloadUrl1") @Expose var photoDownloadUrl1: String? = "",
    @SerializedName("downloadUrl1") @Expose var downloadUrl1: String? = "",
    @SerializedName("photoFileName2") @Expose var photoFileName2: String? = "",
    @SerializedName("photoDownloadUrl2") @Expose var photoDownloadUrl2: String? = "",
    @SerializedName("downloadUrl2") @Expose var downloadUrl2: String? = "",
    @SerializedName("createdBy") @Expose var createdBy: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("createdDateTime") @Expose var createdDateTime: String? = ""
) : Parcelable {
    constructor() : this("", "", "", "", "", "", "",  -1, "")
}