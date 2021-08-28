package cn.sqh.creativeworld.ui.common.pagingFooter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.DataBoundPagingAdapter
import cn.sqh.creativeworld.core.DataBoundViewHolder
import com.blankj.utilcode.util.LogUtils

class FooterAdapter<T : Any, V : ViewDataBinding>(val adapter: DataBoundPagingAdapter<T, V>) :
    LoadStateAdapter<NetworkItemStateViewHolder>() {
    override fun onBindViewHolder(holder: NetworkItemStateViewHolder, loadState: LoadState) {
        val params = holder.itemView.layoutParams
        if (params is StaggeredGridLayoutManager.LayoutParams) {
            params.isFullSpan = true
        }
        LogUtils.d("现在的LoadState=$loadState")
        holder.bindTo(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): NetworkItemStateViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_network_state, parent, false)
        LogUtils.d("创建时的LoadState=$loadState")
        return NetworkItemStateViewHolder(view) { adapter.retry() }
    }
}