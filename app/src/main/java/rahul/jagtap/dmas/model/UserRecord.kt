package rahul.jagtap.dmas.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRecord(
    var string: String? = "",
    var path: String? = ""
) : Parcelable {
}