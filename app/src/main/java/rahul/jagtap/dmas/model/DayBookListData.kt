package rahul.jagtap.dmas.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DayBookListData(
    var map: java.util.HashMap<String, DayBook>? = null
) : Parcelable {
}