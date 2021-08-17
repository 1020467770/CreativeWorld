package cn.sqh.creativeworld.repository

import androidx.lifecycle.LiveData
import cn.sqh.creativeworld.core.data.Resource
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.core.database.CreativeWorldDatabase
import cn.sqh.creativeworld.network.ApiResponse
import cn.sqh.creativeworld.network.CreativeWorldNetwork
import cn.sqh.creativeworld.network.NetworkBoundResource
import cn.sqh.creativeworld.ui.home.data.PageInfo
import cn.sqh.creativeworld.ui.home.data.VideoApiModel
import kotlinx.coroutines.Dispatchers

object VideoRepository : BaseRepository() {

    private val videoDao = CreativeWorldDatabase.getDatabase().videoDao()

    fun getVideos(
        page: Int = PageInfo.DEFAULT_PAGE_NUM,
        pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): LiveData<Resource<List<Video>>> {
        return object : NetworkBoundResource<List<Video>, VideoApiModel.VideosResult>() {
            override fun saveCallResult(item: VideoApiModel.VideosResult) {
                val videoApiInfos = item.data
                if (item.code == 200 && videoApiInfos != null) {
                    videoApiInfos.forEach { videoInfo ->
                        videoInfo.video?.let { video ->
                            video.owner = videoInfo.user
                            videoDao.insert(video)
                        }
                    }
                }
            }

            override fun shouldFetch(data: List<Video>?) = data?.size ?: 0 < pageSize

            override fun loadFromDb(): LiveData<List<Video>> =
                videoDao.findVideosByPage(page, pageSize)

            override fun createCall(): LiveData<ApiResponse<VideoApiModel.VideosResult>> =
                fire_liveData(Dispatchers.IO) {
                    val result = CreativeWorldNetwork.getVideos(page, pageSize)
                    ApiResponse.create(result)
                }
        }.asLiveData()
    }

    fun getVideoById(videoId: VideoId): LiveData<Resource<Video>> {
        return object : NetworkBoundResource<Video, VideoApiModel.OneVideoResult>() {

            override fun saveCallResult(item: VideoApiModel.OneVideoResult) {
                val videoApiInfo = item.data
                if (item.code == 200 && videoApiInfo != null) {
                    videoApiInfo.video.let { video ->
                        if (video != null && videoApiInfo.user != null) {
                            video.owner = videoApiInfo.user
                            videoDao.insert(video)
                        }
                    }
                }
            }

            override fun shouldFetch(data: Video?) = data == null

            override fun loadFromDb(): LiveData<Video> = videoDao.findByVideoId(videoId)

            override fun createCall(): LiveData<ApiResponse<VideoApiModel.OneVideoResult>> =
                fire_liveData(Dispatchers.IO) {
                    val result = CreativeWorldNetwork.getVideoById(videoId)
                    ApiResponse.create(result)
                }
        }.asLiveData()
    }

}