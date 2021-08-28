package cn.sqh.creativeworld.repository.video

import androidx.paging.PagingData
import cn.sqh.creativeworld.core.data.SourceResult
import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.network.data.PageInfo
import cn.sqh.creativeworld.ui.home.data.VideoPreviewModel
import cn.sqh.creativeworld.ui.tweets.data.TweetPreviewModel
import cn.sqh.creativeworld.ui.videoDetail.VideoDetailModel
import kotlinx.coroutines.flow.Flow

interface IVideoRepository {

    fun getVideos(
        pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): Flow<PagingData<VideoPreviewModel>>

    suspend fun getVideoInfo(videoId: VideoId): Flow<SourceResult<VideoDetailModel>>

    suspend fun commentToVideo(videoId: VideoId, content: String, callback: suspend () -> Unit)
}