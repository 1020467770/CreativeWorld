package cn.sqh.creativeworld.repository.video

import androidx.paging.*
import cn.sqh.creativeworld.core.data.SourceResult
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.core.database.CreativeWorldDatabase
import cn.sqh.creativeworld.core.mapper.Mapper
import cn.sqh.creativeworld.network.service.VideoService
import cn.sqh.creativeworld.repository.old.UserRepository
import cn.sqh.creativeworld.ui.home.data.VideoApiModel
import cn.sqh.creativeworld.ui.home.data.VideoPreviewModel
import cn.sqh.creativeworld.ui.videoDetail.VideoDetailModel
import cn.sqh.creativeworld.utils.toResponseBody
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*

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

    override suspend fun uploadVideo(
        videoTitle: String,
        videoDesc: String,
        videoRealPath: String,
        coverFileBytes: ByteArray
    ): Flow<VideoApiModel.OneVideoResult> {
        return flow<VideoApiModel.OneVideoResult> {
            val result = api.uploadVideo(
                videoTitle.toResponseBody(),
                videoDesc.toResponseBody(),
                createVideoPartBody(videoRealPath),
                createCoverPartBody(coverFileBytes)
            )
            val video = result.data?.video
            video?.owner = result.data?.user
            if (video != null) {
                db.videoDao().insert(video)
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
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

    private fun createVideoPartBody(videoPath: String) = File(videoPath).run {
        MultipartBody.Part.createFormData(
            "videoFile",
            this.name,
            RequestBody.create(MediaType.parse("video/mp4"), this)
        )
    }


    private fun createCoverPartBody(coverBytes: ByteArray) =
        MultipartBody.Part.createFormData(
            "coverFile",
            "pic_${UUID.randomUUID()}.jpg",
            RequestBody.create(MediaType.parse("image/jpg"), coverBytes)
        )
}