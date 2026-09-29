package rahul.jagtap.dmas.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class BillData(
    var map: HashMap<String, HashMap<String, HashMap<String, Bill>>>? = null
) : Parcelable {
}

