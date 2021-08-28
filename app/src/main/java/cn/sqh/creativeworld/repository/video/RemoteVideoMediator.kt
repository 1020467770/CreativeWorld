package cn.sqh.creativeworld.repository.video

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import cn.sqh.creativeworld.CreativeWorldApplication
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.core.database.CreativeWorldDatabase
import cn.sqh.creativeworld.network.service.VideoService
import cn.sqh.creativeworld.utils.isConnectedNetwork
import com.blankj.utilcode.util.LogUtils
import java.lang.Exception

@OptIn(ExperimentalPagingApi::class)
class RemoteVideoMediator(
    val api: VideoService,
    val db: CreativeWorldDatabase
) : RemoteMediator<Int, Video>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Video>): MediatorResult {
        try {
            val videoDao = db.videoDao()
            val pageKey = when (loadType) {
                //首次访问或调用refresh
                LoadType.REFRESH -> null

                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null && !state.isEmpty()) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    db.withTransaction {
                        lastItem?.id?.let {
                            videoDao.selectByVideoId(it)?.nextPage
                        }
                    }
                }
            }
            //没网络
            if (!CreativeWorldApplication.context.isConnectedNetwork()) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            val page = pageKey ?: 1
            val result = api.getVideos2(page)
            val data = result.data

            val endOfPaginationReached = !result.pageInfo.ifhavenext
            LogUtils.d("endOfPaginationReached=${endOfPaginationReached}")

            val videos = data.map { videoApiInfo ->
                videoApiInfo.video!!.apply {
                    nextPage = page + 1
                    owner = videoApiInfo.user
                }
            }
            LogUtils.d("转化后的item为${videos}")


            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    videoDao.deleteAll()
                }
                videoDao.insertAll(videos)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            LogUtils.d(e)
            return MediatorResult.Error(e)
        }
    }
}