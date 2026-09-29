package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Admin-configurable Contact Us content, stored at contact_us/admin (mirrored to
 * contact_us_backup/admin). Every field has a sensible default so the screen renders
 * fully even before an admin has ever saved anything.
 */
@Parcelize
data class ContactConfig(
    @SerializedName("introTitle") @Expose var introTitle: String? = "We're Here to Help!",
    @SerializedName("introText") @Expose var introText: String? = "Send us the details and we will be right there at your service.",

    @SerializedName("marketingLabel") @Expose var marketingLabel: String? = "Marketing Team",
    @SerializedName("marketingAddress") @Expose var marketingAddress: String? = "17/8 Gauri Shankar Society, Dhankwadi, Pune - 411043",
    @SerializedName("backOfficeLabel") @Expose var backOfficeLabel: String? = "Back Office",
    @SerializedName("backOfficeAddress") @Expose var backOfficeAddress: String? = "Office No-01 Nagawade Estate, IOCL Pump, Kashti 414701",

    @SerializedName("phone1") @Expose var phone1: String? = "+91 9552789899",
    @SerializedName("phone2") @Expose var phone2: String? = "02487 231111",
    @SerializedName("whatsappNumber") @Expose var whatsappNumber: String? = "919552789899",
    @SerializedName("whatsappLabel") @Expose var whatsappLabel: String? = "येथे क्लिक करा - कस्टमर केअरशी बोला",

    @SerializedName("email") @Expose var email: String? = "jpdmas@gmail.com",
    @SerializedName("website") @Expose var website: String? = "",

    @SerializedName("youtubeUrl") @Expose var youtubeUrl: String? = "https://youtube.com/@maha_e_suvidha?si=0gH0fRjGTQnj_diW",
    @SerializedName("instagramUrl") @Expose var instagramUrl: String? = "https://www.instagram.com/jagtap.patil.rahul/?hl=en",
    @SerializedName("facebookUrl") @Expose var facebookUrl: String? = "https://www.facebook.com/rahul.jagtappatil",

    @SerializedName("trustTitle") @Expose var trustTitle: String? = "Your Trust, Our Priority",
    @SerializedName("trustSubtitle") @Expose var trustSubtitle: String? = "Fast Support • Trusted Service • Always With You",

    @SerializedName("updatedBy") @Expose var updatedBy: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long? = -1,
    @SerializedName("updatedDateTime") @Expose var updatedDateTime: String? = ""
) : Parcelable {
    constructor() : this(
        "We're Here to Help!", "Send us the details and we will be right there at your service.",
        "Marketing Team", "17/8 Gauri Shankar Society, Dhankwadi, Pune - 411043",
        "Back Office", "Office No-01 Nagawade Estate, IOCL Pump, Kashti 414701",
        "+91 9552789899", "02487 231111", "919552789899", "येथे क्लिक करा - कस्टमर केअरशी बोला",
        "jpdmas@gmail.com", "",
        "https://youtube.com/@maha_e_suvidha?si=0gH0fRjGTQnj_diW",
        "https://www.instagram.com/jagtap.patil.rahul/?hl=en",
        "https://www.facebook.com/rahul.jagtappatil",
        "Your Trust, Our Priority", "Fast Support • Trusted Service • Always With You",
        "", -1, ""
    )
}
