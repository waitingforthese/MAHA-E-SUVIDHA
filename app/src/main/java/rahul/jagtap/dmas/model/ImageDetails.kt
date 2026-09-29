package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageDetails(
    @SerializedName("imageFileName") @Expose var imageFileName: String? = "",
    @SerializedName("imageDownloadUrl") @Expose var imageDownloadUrl: String? = "",
    @SerializedName("downloadUrl") @Expose var downloadUrl: String? = "",
    @SerializedName("createdBy") @Expose var createdBy: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("createdDateTime") @Expose var createdDateTime: String? = ""
) : Parcelable {
    constructor() : this("", "", "", "",  -1, "")
}