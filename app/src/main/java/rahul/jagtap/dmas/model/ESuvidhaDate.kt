package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ESuvidhaDate(
    @SerializedName("date") @Expose var date: String? = "",
    @SerializedName("suvidhaTypes") @Expose var suvidhaTypes: String? = ""
) : Parcelable {
    constructor() : this("", "")
}