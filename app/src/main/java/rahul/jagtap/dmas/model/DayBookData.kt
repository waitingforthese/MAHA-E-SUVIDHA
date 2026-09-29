package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class DayBookTable(
    @SerializedName("title") @Expose var title: String? = "",
    @SerializedName("subTitle") @Expose var subTitle: String? = "",
    @SerializedName("description") @Expose var description: String? = "",
    @SerializedName("records") @Expose var records: ArrayList<DayBookTableItem>? = null,
) : Parcelable {
}