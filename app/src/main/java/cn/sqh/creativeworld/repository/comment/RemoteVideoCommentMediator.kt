package cn.sqh.creativeworld.repository.comment

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import cn.sqh.creativeworld.CreativeWorldApplication
import cn.sqh.creativeworld.core.data.TweetEntity
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.core.data.VideoCommentEntity
import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.core.database.CreativeWorldDatabase
import cn.sqh.creativeworld.network.service.TweetService
import cn.sqh.creativeworld.network.service.VideoService
import cn.sqh.creativeworld.utils.isConnectedNetwork
import com.blankj.utilcode.util.LogUtils
import java.lang.Exception

@OptIn(ExperimentalPagingApi::class)
class RemoteVideoCommentMediator(
    val api: VideoService,
    val videoId: VideoId,
    val db: CreativeWorldDatabase
) : RemoteMediator<Int, VideoCommentEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, VideoCommentEntity>
    ): MediatorResult {
        try {
            val videoCommentDao = db.videoCommentDao()
            val pageKey = when (loadType) {
                LoadType.REFRESH -> null

                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null && !state.isEmpty()) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    db.withTransaction {
                        lastItem?.id?.let {
                            videoCommentDao.getVideoCommentById(it)?.nextPage
                        }
                    }
                }
            }
            if (!CreativeWorldApplication.context.isConnectedNetwork()) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            val page = pageKey ?: 1
            val result = api.getVideoCommentsByVideoBy(videoId, page)
            val data = result.data

            val endOfPaginationReached = data.isEmpty()

            val videoCommentEntities = data.map { videoCommentApiInfo ->
                videoCommentApiInfo.comment.apply {
                    nextPage = page + 1
                    owner = videoCommentApiInfo.user
                }
            }
            LogUtils.d("转化后的videoCommentEntities为${videoCommentEntities}")


            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    videoCommentDao.deleteAll()
                }
                videoCommentDao.insertAll(videoCommentEntities)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            LogUtils.d(e)
            return MediatorResult.Error(e)
        }
    }

}