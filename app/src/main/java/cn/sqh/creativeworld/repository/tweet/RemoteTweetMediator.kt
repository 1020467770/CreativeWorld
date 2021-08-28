package cn.sqh.creativeworld.repository.tweet

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import cn.sqh.creativeworld.CreativeWorldApplication
import cn.sqh.creativeworld.core.data.TweetEntity
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.core.database.CreativeWorldDatabase
import cn.sqh.creativeworld.network.service.TweetService
import cn.sqh.creativeworld.utils.isConnectedNetwork
import com.blankj.utilcode.util.LogUtils
import java.lang.Exception

@OptIn(ExperimentalPagingApi::class)
class RemoteTweetMediator(
    val api: TweetService,
    val db: CreativeWorldDatabase
) : RemoteMediator<Int, TweetEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TweetEntity>
    ): MediatorResult {
        try {
            val tweetDao = db.tweetDao()
            val pageKey = when (loadType) {
                //首次访问或调用refresh
                LoadType.REFRESH -> null

                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    LogUtils.d("lastItem=$lastItem")
                    LogUtils.d("isEmpty=${state.isEmpty()}")
                    if (lastItem == null && !state.isEmpty()) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    db.withTransaction {
                        lastItem?.id?.let {
                            tweetDao.getTweetById(it)?.nextPage
                        }
                    }
                }
            }
            //没网络
            if (!CreativeWorldApplication.context.isConnectedNetwork()) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            val page = pageKey ?: 1
            val result = api.getTweets(page)
            val data = result.data

            val endOfPaginationReached = data.isEmpty()

            val tweets = data.map { tweetApiInfo ->
                tweetApiInfo.tweet!!.apply {
                    nextPage = page + 1
                    owner = tweetApiInfo.user
                }
            }
            LogUtils.d("转化后的tweets为${tweets}")


            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    tweetDao.deleteAll()
                }
                tweetDao.insertAll(tweets)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            LogUtils.d(e)
            return MediatorResult.Error(e)
        }
    }

}