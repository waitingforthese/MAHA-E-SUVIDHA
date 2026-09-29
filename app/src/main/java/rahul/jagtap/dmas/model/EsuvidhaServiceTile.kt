package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Admin-configurable override for a single tile in the e-suvidha services grid, stored at
 * esuvidha_grid/{itemKey} (mirrored to esuvidha_grid_backup/{itemKey}).
 *
 * A tile is either a standalone service (itemKey == serviceKey, subtypeKey == null) or one subtype of a
 * service that has subtypes (itemKey == "serviceKey__subtypeKey") or a grouped service (itenKey == serviceKey,
 * subtypeKey == null). The actual list of tiles is built at
 * runtime by merging the code [rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry] + the live
 * esuvidha_dynamic_types subtypes + these overrides. Every field is optional so a tile renders from its
 * registry / subtype defaults even before an admin has ever saved an override.
 *
 * - [title]  blank => use the registry default (standalone) or the subtype's type_title.
 * - [iconUrl] blank => use the registry default drawable.
 * - [order]  -1 => keep the natural registry order.
 * - [enabled] controls show/hide in the grid.
 */
@Parcelize
data class EsuvidhaServiceTile(
    @SerializedName("itemKey") @Expose var itemKey: String? = "",
    @SerializedName("serviceKey") @Expose var serviceKey: String? = "",
    @SerializedName("subtypeKey") @Expose var subtypeKey: String? = null,

    @SerializedName("title") @Expose var title: String? = "",
    @SerializedName("iconUrl") @Expose var iconUrl: String? = "",
    // Admin-managed fee for this service/subtype (plain number or "₹150"). Blank => fall back to any fee
    // embedded in the title text. Kept as its own field so it can drive wallet debits later.
    @SerializedName("fee") @Expose var fee: String? = "",
    @SerializedName("order") @Expose var order: Int? = -1,
    @SerializedName("enabled") @Expose var enabled: Boolean? = true,

    @SerializedName("updatedBy") @Expose var updatedBy: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long? = -1,
    @SerializedName("updatedDateTime") @Expose var updatedDateTime: String? = ""
) : Parcelable {
    constructor() : this("", "", null, "", "", "", -1, true, "", -1, "")
}
