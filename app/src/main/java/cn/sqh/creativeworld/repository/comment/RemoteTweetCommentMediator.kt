package cn.sqh.creativeworld.repository.comment

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import cn.sqh.creativeworld.CreativeWorldApplication
import cn.sqh.creativeworld.core.data.*
import cn.sqh.creativeworld.core.database.CreativeWorldDatabase
import cn.sqh.creativeworld.network.service.TweetService
import cn.sqh.creativeworld.network.service.VideoService
import cn.sqh.creativeworld.utils.isConnectedNetwork
import com.blankj.utilcode.util.LogUtils
import java.lang.Exception

@OptIn(ExperimentalPagingApi::class)
class RemoteTweetCommentMediator(
    val api: TweetService,
    val tweetId: TweetId,
    val db: CreativeWorldDatabase
) : RemoteMediator<Int, TweetCommentEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TweetCommentEntity>
    ): MediatorResult {
        try {
            val tweetCommentDao = db.tweetCommentDao()
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
                            tweetCommentDao.getTweetCommentById(it)?.nextPage
                        }
                    }
                }
            }
            if (!CreativeWorldApplication.context.isConnectedNetwork()) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            val page = pageKey ?: 1
            val result = api.getTweetCommentsByTweetBy(tweetId, page)
            val data = result.data

            val endOfPaginationReached = data.isEmpty()

            val tweetCommentEntities = data.map { tweetCommentApiInfo ->
                tweetCommentApiInfo.comment.apply {
                    nextPage = page + 1
                    owner = tweetCommentApiInfo.user
                }
            }
            LogUtils.d("转化后的tweetCommentEntities为${tweetCommentEntities}")


            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    tweetCommentDao.deleteAll()
                }
                tweetCommentDao.insertAll(tweetCommentEntities)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            LogUtils.d(e)
            return MediatorResult.Error(e)
        }
    }

}