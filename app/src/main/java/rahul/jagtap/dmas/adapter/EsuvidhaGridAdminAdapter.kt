package rahul.jagtap.dmas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rahul.jagtap.dmas.databinding.ItemEsuvidhaGridAdminBinding
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry
import rahul.jagtap.dmas.utils.EsuvidhaServiceRegistry.GridTile
import java.util.Collections

/**
 * Admin list of every grid tile (including hidden ones). Row: edit (tap), enable/disable (switch),
 * and drag-to-reorder (handle). Hidden tiles are dimmed and tagged.
 */
class EsuvidhaGridAdminAdapter(
    private val context: Context?,
    private val items: MutableList<GridTile>,
    private val onEdit: (GridTile) -> Unit,
    private val onToggle: (GridTile, Boolean) -> Unit,
    private val onStartDrag: (RecyclerView.ViewHolder) -> Unit
) : RecyclerView.Adapter<EsuvidhaGridAdminAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemEsuvidhaGridAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = items.size

    /** Called by ItemTouchHelper while dragging to reflect the new visual order. */
    fun onItemMove(from: Int, to: Int) {
        if (from < 0 || to < 0 || from >= items.size || to >= items.size) return
        Collections.swap(items, from, to)
        notifyItemMoved(from, to)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val tile = items[position]
        val b = holder.binding
        b.tvTitle.text = tile.title
        val meta = buildString {
            if (tile.feeText.isNotBlank()) append(tile.feeText)
            if (!tile.enabled) { if (isNotEmpty()) append(" • "); append("Hidden") }
        }
        b.tvMeta.text = if (meta.isBlank()) "Visible" else meta

        // Colourful flat group icon in its original colour (no tint), matching the user grid.
        val fallback = EsuvidhaServiceRegistry.groupIconFor(tile.def)
        ImageViewCompat.setImageTintList(b.ivIcon, null)
        if (tile.iconUrl.isNotBlank() && context != null) {
            Glide.with(context).load(tile.iconUrl).placeholder(fallback).into(b.ivIcon)
        } else {
            b.ivIcon.setImageResource(fallback)
        }

        val dim = if (tile.enabled) 1f else 0.45f
        b.ivIcon.alpha = dim
        b.tvTitle.alpha = dim

        b.swEnabled.setOnCheckedChangeListener(null)
        b.swEnabled.isChecked = tile.enabled
        b.swEnabled.setOnCheckedChangeListener { _, checked -> onToggle(tile, checked) }

        b.ivDrag.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) onStartDrag(holder)
            false
        }
        b.root.setOnClickListener { onEdit(tile) }
    }

    inner class VH(val binding: ItemEsuvidhaGridAdminBinding) : RecyclerView.ViewHolder(binding.root)
}
