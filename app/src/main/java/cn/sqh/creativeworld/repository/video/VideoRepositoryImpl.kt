package cn.sqh.creativeworld.repository.video

import androidx.paging.*
import cn.sqh.creativeworld.core.data.SourceResult
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.core.database.CreativeWorldDatabase
import cn.sqh.creativeworld.core.mapper.Mapper
import cn.sqh.creativeworld.network.service.VideoService
import cn.sqh.creativeworld.repository.old.UserRepository
import cn.sqh.creativeworld.ui.home.data.VideoPreviewModel
import cn.sqh.creativeworld.ui.videoDetail.VideoDetailModel
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.RuntimeException

class VideoRepositoryImpl(
    val api: VideoService,
    val db: CreativeWorldDatabase,
    val mapper2PreviewModel: Mapper<Video, VideoPreviewModel>,
    val mapper2DetailModel: Mapper<Video, VideoDetailModel>,
) : IVideoRepository {


    @OptIn(ExperimentalPagingApi::class)
    override fun getVideos(pageSize: Int): Flow<PagingData<VideoPreviewModel>> {
        return Pager(
            config = PagingConfig(pageSize),
            remoteMediator = RemoteVideoMediator(api, db)
        ) {
            db.videoDao().getAllVideos()
        }.flow.map { entityData ->
            entityData.map { mapper2PreviewModel.map(it) }
        }
    }

    override suspend fun getVideoInfo(videoId: VideoId): Flow<SourceResult<VideoDetailModel>> {
        return flow {
            try {
//                emit(SourceResult.Loading(0))
                val videoDao = db.videoDao()
                var videoEntity = videoDao.selectByVideoId(videoId)
                if (videoEntity == null) {
                    //数据库查不到该tweet，采用网络请求
                    val networkTweetInfo = api.getVideoById_suspend(videoId)
                    val videoApiInfo = networkTweetInfo.data
                    if (networkTweetInfo.code == 200 && videoApiInfo != null) {
                        videoEntity = videoApiInfo.video
                        if (videoEntity != null) {
                            videoEntity.owner = videoApiInfo.user
                            videoDao.insert(videoEntity)
                        }
                    } else {
                        emit(SourceResult.Failure(RuntimeException("服务器返回码不为200")))
                    }
                }
                //将Entity转换为视图层需要的模型
                val model = mapper2DetailModel.map(videoEntity!!)
                emit(SourceResult.Success(model))
            } catch (e: Exception) {
                emit(SourceResult.Failure(e))
            }
        }.flowOn(Dispatchers.IO)//在IO线程进行数据库和网络操作
    }

    override suspend fun commentToVideo(
        videoId: VideoId,
        content: String,
        callback: suspend () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val result = api.makeComment(videoId, content)
//            LogUtils.d("result=${result.data}")
            val comment = result.data
            if (comment != null) {
                comment.owner = UserRepository.loggedInUser?.user
                LogUtils.d("要存入的comment=${comment}")
                db.videoCommentDao().insert(comment)
                withContext(Dispatchers.Main) {
                    callback()
                }
            }
        }
    }
}