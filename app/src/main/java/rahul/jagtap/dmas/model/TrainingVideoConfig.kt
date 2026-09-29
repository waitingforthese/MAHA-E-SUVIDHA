package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Admin-configurable Training Video screen content, stored at training_videos/config (mirrored to
 * training_videos_backup/config). Only [screenTitle] and the [videos] list are admin-editable; the
 * hero banner heading/subtitle/chips are hardcoded in the layout. Defaults render a full screen even
 * before an admin has ever saved anything.
 */
@Parcelize
data class TrainingVideoConfig(
    @SerializedName("screenTitle") @Expose var screenTitle: String? = "ट्रेनिंग व्हिडिओ",
    @SerializedName("videos") @Expose var videos: MutableList<TrainingVideo>? = mutableListOf(),

    @SerializedName("updatedBy") @Expose var updatedBy: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long? = -1,
    @SerializedName("updatedDateTime") @Expose var updatedDateTime: String? = ""
) : Parcelable {
    constructor() : this("ट्रेनिंग व्हिडिओ", mutableListOf(), "", -1, "")
}
