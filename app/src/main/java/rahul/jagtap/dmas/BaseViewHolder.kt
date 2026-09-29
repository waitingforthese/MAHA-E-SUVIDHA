package rahul.jagtap.dmas

import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<V : ViewBinding, T>(
    val binding: V,
    private val listener: ((T) -> Unit)?
) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {
//val binding = ItemDrivingLearningLicenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//    constructor(parent: ViewGroup, @LayoutRes layoutId: Int, listener: ((T) -> Unit)? = null, contextThemeWrapper: ContextThemeWrapper? = null) :
    constructor(parent: ViewGroup, binding: V, listener: ((T) -> Unit)? = null, contextThemeWrapper: ContextThemeWrapper? = null) :
            this(binding,
//            contextThemeWrapper?.let {
//                        LayoutInflater.from(parent.context).cloneInContext(contextThemeWrapper).inflate(layoutId, parent, false)
//                    } ?: LayoutInflater.from(parent.context).inflate(layoutId, parent, false),
                listener
            ) {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    protected val lifecycleRegistry by lazy { LifecycleRegistry(this) }
//    override val lifecycle: Lifecycle
//        get() = lifecycleRegistry

    override fun getLifecycle() = lifecycleRegistry

    open fun onBind(item: T) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        itemView.setOnClickListener {
            listener?.invoke(item)
//            onItemClick(item)
        }
    }

    protected open fun onItemClick(item: T) = Unit

    open fun onViewRecycled() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }
}