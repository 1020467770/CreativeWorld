package cn.sqh.creativeworld.ui.videoDetail

import androidx.recyclerview.widget.DiffUtil
import cn.sqh.creativeworld.core.data.User
import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.ui.home.data.VideoPreviewModel
import java.util.*

/**
 * UI层使用的Model，与DataSource层做出区分，可以保证DataSource的数据发生改变时
 * UI层不受影响，解耦合。
 */
data class VideoDetailModel(
    val id: VideoId,
    var owner: User?,
    var name: String = "",
    var videoCoverUrl: String = "",
    var fansCounts: Int = 0,
    var videoCounts: Int = 0,
    var profileUrl: String = "",
    var uploader: String = "",
    var description: String? = "",
    var playNumber: String = "0",
    var likerNumber: String = "0",
    var dislikeNumber: String = "0",
    var collectorNumber: String = "0",
    var forwarderNumber: String = "0",
    var url: String = "",
    var uploadDate: String = "",
) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<VideoDetailModel>() {
            override fun areItemsTheSame(
                oldItem: VideoDetailModel,
                newItem: VideoDetailModel
            ): Boolean = oldItem.id == newItem.id
                    && oldItem.name == newItem.name

            override fun areContentsTheSame(
                oldItem: VideoDetailModel,
                newItem: VideoDetailModel
            ): Boolean = oldItem == newItem

        }
    }
}