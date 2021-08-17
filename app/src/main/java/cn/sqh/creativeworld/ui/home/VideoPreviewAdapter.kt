package cn.sqh.creativeworld.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.DataBoundListAdapter
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.core.data.VideoDiff
import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.databinding.ItemHomeVideoLayoutBinding

class VideoPreviewAdapter(
    private val onClickListener: VideoViewClickListener
) : DataBoundListAdapter<Video, ItemHomeVideoLayoutBinding>(VideoDiff) {

    interface VideoViewClickListener {
        fun onClick(view: View, videoId: VideoId)
        fun onLongPressed(video: Video): Boolean
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

    override fun bind(binding: ItemHomeVideoLayoutBinding, item: Video) {
        binding.video = item
        binding.clickListener = onClickListener
    }


}


