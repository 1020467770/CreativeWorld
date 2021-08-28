package cn.sqh.creativeworld.ui.home.data

import androidx.recyclerview.widget.DiffUtil
import cn.sqh.creativeworld.core.data.VideoId


/**
 * UI层使用的Model，与DataSource层做出区分，可以保证DataSource的数据发生改变时
 * UI层不受影响，解耦合。
 */
data class VideoPreviewModel(
    val id: VideoId,
    val videoName: String,
    val uploaderName: String,
    val playNumber: Long = 0,
    val likerNumber: Long = 0,
    val previewImageUrl: String,
    val profileImageUrl: String
) {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<VideoPreviewModel>() {
            override fun areItemsTheSame(
                oldItem: VideoPreviewModel,
                newItem: VideoPreviewModel
            ): Boolean = oldItem.id == newItem.id
                    && oldItem.videoName == newItem.videoName

            override fun areContentsTheSame(
                oldItem: VideoPreviewModel,
                newItem: VideoPreviewModel
            ): Boolean = oldItem == newItem

        }
    }
}