package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class ReportData(
    var map: HashMap<String, HashMap<String, HashMap<String,  HashMap<String, ReportInfo>>>>? = null
) : Parcelable

@Parcelize
data class ReportInfo(
    @SerializedName("createdBy") @Expose var createdBy: String? = "",
    @SerializedName("createdDateTime") @Expose var createdDateTime: String? = "",
    @SerializedName("reportFileName") @Expose var reportFileName: String? = "",
    @SerializedName("reportDownloadUrl") @Expose var reportDownloadUrl: String? = "",
    @SerializedName("reportType") @Expose var reportType: String? = "",
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("uid") @Expose var uid: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
) : Parcelable