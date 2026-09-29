package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationItem(
    @SerializedName("message") @Expose var message: String? = "",
    @SerializedName("createdAt") @Expose var createdAt: String? = "",
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("uid") @Expose var uid: String? = "",
    @SerializedName("billType") @Expose var billType: String? = "",
    @SerializedName("notificationType") @Expose var notificationType: String? = ""
) : Parcelable {
    constructor() : this("", "", "", "", "", "")
}