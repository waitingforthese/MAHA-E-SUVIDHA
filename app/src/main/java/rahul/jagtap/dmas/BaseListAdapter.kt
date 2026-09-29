package rahul.jagtap.dmas

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

abstract class BaseListAdapter<V : ViewBinding, T>(diffUtilItemCallback: DiffUtil.ItemCallback<T>) : ListAdapter<T, BaseViewHolder<V, T>>(diffUtilItemCallback) {

    override fun onBindViewHolder(holder: BaseViewHolder<V, T>, position: Int) = holder.onBind(getItem(position))

    override fun onViewRecycled(holder: BaseViewHolder<V, T>) {
        super.onViewRecycled(holder)
        holder.onViewRecycled()
    }
}