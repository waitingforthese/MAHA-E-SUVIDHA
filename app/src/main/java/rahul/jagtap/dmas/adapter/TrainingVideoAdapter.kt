package rahul.jagtap.dmas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ItemTrainingVideoBinding
import rahul.jagtap.dmas.databinding.ItemTrainingVideoHeroBinding
import rahul.jagtap.dmas.extensions.gone
import rahul.jagtap.dmas.extensions.visible
import rahul.jagtap.dmas.model.TrainingVideo
import rahul.jagtap.dmas.utils.Utils

/**
 * Training Video list: item 0 is the static hero header, the rest are video cards. [videos] is already
 * filtered to visible rows with a parseable YouTube id (done by the activity), so every card is tappable.
 */
class TrainingVideoAdapter(
    private val context: Context?,
    private val videos: List<TrainingVideo>,
    private val onClick: (TrainingVideo) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val typeHero = 0
    private val typeVideo = 1

    override fun getItemViewType(position: Int): Int = if (position == 0) typeHero else typeVideo

    override fun getItemCount(): Int = videos.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == typeHero) {
            HeroVH(ItemTrainingVideoHeroBinding.inflate(inflater, parent, false))
        } else {
            VideoVH(ItemTrainingVideoBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder !is VideoVH) return
        val video = videos[position - 1]
        val b = holder.binding

        // Auto-number the title so the list reads 1., 2., 3.… regardless of what the admin typed.
        val title = video.title?.trim().orEmpty()
        b.tvTitle.text = "$position. $title"

        val desc = video.description?.trim().orEmpty()
        if (desc.isEmpty()) b.tvDescription.gone() else {
            b.tvDescription.visible()
            b.tvDescription.text = desc
        }

        val duration = video.duration?.trim().orEmpty()
        if (duration.isEmpty()) b.tvDuration.gone() else {
            b.tvDuration.visible()
            b.tvDuration.text = duration
        }

        context?.let {
            val thumb = Utils.youtubeThumbUrl(Utils.extractYoutubeId(video.youtubeUrl))
            Glide.with(it)
                .load(thumb)
                .placeholder(R.drawable.bg_tv_thumb)
                .error(R.drawable.bg_tv_thumb)
                .centerCrop()
                .into(b.ivThumb)
        }

        b.root.setOnClickListener { onClick(video) }
    }

    inner class HeroVH(binding: ItemTrainingVideoHeroBinding) : RecyclerView.ViewHolder(binding.root)
    inner class VideoVH(val binding: ItemTrainingVideoBinding) : RecyclerView.ViewHolder(binding.root)
}
