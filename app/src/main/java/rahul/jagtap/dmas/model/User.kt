package rahul.jagtap.dmas.model

import com.google.firebase.database.PropertyName
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("uid") @Expose var uid: String? = "",
    @SerializedName("name") @Expose var name: String? = "",
    @SerializedName("username") @Expose var username: String? = "",
    @SerializedName("shopName") @Expose var shopName: String? = "",
    @SerializedName("isAdminUser")
    @Expose
    @set:PropertyName("isAdminUser")
    @get:PropertyName("isAdminUser") var isAdmin: String? = "",
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("contactNo") @Expose var contactNo: String? = "",
    @SerializedName("address") @Expose var address: String? = "",
    @SerializedName("referrer") @Expose var referrer: String? = "",
    @SerializedName("createdAt") @Expose var createdAt: String? = "",
    @SerializedName("aadharNo") @Expose var aadharNo: String? = "",
    @SerializedName("contactSavedBy") @Expose var contactSavedBy: String? = "",
    @SerializedName("userType") @Expose var userType: String? = "0", // 0 - User, 1 - Admin, 2 - Employee
    @SerializedName("isBlocked") @Expose var isBlocked: String? = "",
    @SerializedName("dailyEntryBlocked") @Expose var isDailyEntryBlocked: String? = null,
    @SerializedName("fcmToken") @Expose var fcmToken: String? = "",
    @SerializedName("dailyEntriesCount") @Expose var dailyEntriesCount: Long? = 0
) : Serializable {
//    constructor() : this("", "","", "", "", "", "")
}