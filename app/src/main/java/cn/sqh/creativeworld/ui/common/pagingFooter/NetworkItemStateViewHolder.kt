package cn.sqh.creativeworld.ui.common.pagingFooter

import android.view.View
import androidx.core.view.isVisible
import androidx.paging.LoadState
import cn.sqh.creativeworld.core.DataBoundViewHolder
import cn.sqh.creativeworld.databinding.ItemNetworkStateBinding

class NetworkItemStateViewHolder(view: View, private val retryCallback: () -> Unit) :
    DataBoundViewHolder<ItemNetworkStateBinding>(ItemNetworkStateBinding.bind(view)) {
    fun bindTo(loadState: LoadState) {
        binding.apply {
            progressBar.isVisible = loadState is LoadState.Loading
            // 加载失败，显示并点击重试按钮
            retryButton.isVisible = loadState is LoadState.Error
            retryButton.setOnClickListener { retryCallback() }
            // 加载失败显示错误原因
            errorMsg.isVisible = !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
            errorMsg.text = (loadState as? LoadState.Error)?.error?.message
            executePendingBindings()
        }
    }
}