package cn.sqh.creativeworld.core

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class DataBoundPagingAdapter<T : Any, V : ViewDataBinding>(
    diffCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, DataBoundViewHolder<V>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<V> {
        val binding = createBinding(parent)
        return DataBoundViewHolder(binding)
    }

    protected abstract fun createBinding(parent: ViewGroup): V

    override fun onBindViewHolder(holder: DataBoundViewHolder<V>, position: Int) {
        getItem(position)?.let { bind(holder.binding, it) }
        holder.binding.executePendingBindings()
    }

    protected abstract fun bind(binding: V, item: T)
}