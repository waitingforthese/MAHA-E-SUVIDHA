package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * A single training video row in the Training Video screen. [youtubeUrl] may be any YouTube URL
 * form (watch/youtu.be/shorts/embed); the video id is parsed via Utils.extractYoutubeId. [duration]
 * is admin-typed free text (e.g. "4:10") shown as a badge on the thumbnail; blank hides the badge.
 */
@Parcelize
data class TrainingVideo(
    @SerializedName("title") @Expose var title: String? = "",
    @SerializedName("description") @Expose var description: String? = "",
    @SerializedName("youtubeUrl") @Expose var youtubeUrl: String? = "",
    @SerializedName("duration") @Expose var duration: String? = "",
    @SerializedName("visible") @Expose var visible: Boolean = true
) : Parcelable {
    constructor() : this("", "", "", "", true)
}
