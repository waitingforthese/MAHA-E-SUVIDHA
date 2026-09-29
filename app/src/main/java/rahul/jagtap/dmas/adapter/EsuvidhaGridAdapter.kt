package rahul.jagtap.dmas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rahul.jagtap.dmas.R
import rahul.jagtap.dmas.databinding.ItemEsuvidhaServiceBinding
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry.GridTile

/**
 * Renders the flattened e-suvidha services grid. Each tile shows its (admin-overridable) title and icon;
 * the icon is loaded from the uploaded [GridTile.iconUrl] when present, otherwise the registry default.
 */
class EsuvidhaGridAdapter(
    private val context: Context?,
    private val items: List<GridTile>,
    private val onClick: (GridTile) -> Unit
) : RecyclerView.Adapter<EsuvidhaGridAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEsuvidhaServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tile = items[position]
        holder.binding.tvServiceTitle.text = tile.title
        if (tile.feeText.isNotBlank()) {
            holder.binding.tvServiceFee.text = tile.feeText
            holder.binding.tvServiceFee.visibility = View.VISIBLE
        } else {
            holder.binding.tvServiceFee.visibility = View.GONE
        }
        val iv = holder.binding.ivService
        // Colourful flat group icon (falls back to the monochrome grid icon), matching ESuvidhaMenuActivity.
        val fallback = EsuvidhaServiceRegistry.groupIconFor(tile.def)
        // Show every icon in its original colour (no tint), matching ESuvidhaMenuActivity.
        ImageViewCompat.setImageTintList(iv, null)
        if (tile.iconUrl.isNotBlank() && context != null) {
            Glide.with(context).load(tile.iconUrl).placeholder(fallback).error(fallback).into(iv)
        } else {
            iv.setImageResource(fallback)
        }
        holder.itemView.setOnClickListener { onClick(tile) }
    }

    inner class ViewHolder(val binding: ItemEsuvidhaServiceBinding) : RecyclerView.ViewHolder(binding.root)
}
