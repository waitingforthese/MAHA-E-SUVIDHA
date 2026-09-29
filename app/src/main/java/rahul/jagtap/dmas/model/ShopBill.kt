package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShopBill(
    @SerializedName("shopName") @Expose var shopName: String? = "",
    @SerializedName("uid") @Expose var uid: String? = "",
    var billList: List<Bill>? = emptyList()
) : Parcelable {
    constructor() : this("", "", null)
}