package cn.sqh.creativeworld.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.DataBoundListAdapter
import cn.sqh.creativeworld.core.DataBoundPagingAdapter
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.core.data.VideoDiff
import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.databinding.ItemHomeVideoLayoutBinding
import cn.sqh.creativeworld.ui.home.data.VideoPreviewModel

class VideoPreviewAdapter2(
    private val onClickListener: VideoViewClickListener
) : DataBoundPagingAdapter<VideoPreviewModel, ItemHomeVideoLayoutBinding>(VideoPreviewModel.diffCallback) {

    interface VideoViewClickListener {
        fun onClick(view: View, videoId: VideoId)
        fun onLongPressed(video: VideoPreviewModel): Boolean
    }

    override fun createBinding(parent: ViewGroup): ItemHomeVideoLayoutBinding {
        val binding = DataBindingUtil.inflate<ItemHomeVideoLayoutBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_home_video_layout,
            parent,
            false
        )
        return binding
    }

    override fun bind(binding: ItemHomeVideoLayoutBinding, item: VideoPreviewModel) {
        binding.video = item
        binding.clickListener = onClickListener
    }


}



