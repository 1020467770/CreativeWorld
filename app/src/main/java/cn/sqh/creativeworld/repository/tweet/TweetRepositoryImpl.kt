package cn.sqh.creativeworld.repository.tweet

import androidx.paging.*
import cn.sqh.creativeworld.core.data.SourceResult
import cn.sqh.creativeworld.core.data.TweetEntity
import cn.sqh.creativeworld.core.data.TweetId
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.core.database.CreativeWorldDatabase
import cn.sqh.creativeworld.core.mapper.Mapper
import cn.sqh.creativeworld.network.service.TweetService
import cn.sqh.creativeworld.ui.home.data.VideoPreviewModel
import cn.sqh.creativeworld.ui.tweets.data.TweetPreviewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.lang.Exception
import java.lang.RuntimeException

class TweetRepositoryImpl(
    val api: TweetService,
    val db: CreativeWorldDatabase,
    val mapper2PreviewModel: Mapper<TweetEntity, TweetPreviewModel>,
) : ITweetRepository {
    @ExperimentalPagingApi
    override fun getTweetsPreview(pageSize: Int): Flow<PagingData<TweetPreviewModel>> {
        return Pager(
            config = PagingConfig(pageSize),
            remoteMediator = RemoteTweetMediator(api, db)
        ) {
            db.tweetDao().getAllTweets()
        }.flow.map { entityData ->
            entityData.map { mapper2PreviewModel.map(it) }
        }
    }

    override suspend fun getTweetInfo(tweetId: TweetId): Flow<SourceResult<TweetPreviewModel>> {
        return flow {
            try {
//                emit(SourceResult.Loading(0))
                val tweetDao = db.tweetDao()
                var tweetEntity = tweetDao.getTweetById(tweetId)
                if (tweetEntity == null) {
                    //数据库查不到该tweet，采用网络请求
                    val networkTweetInfo = api.getTweetById(tweetId)
                    val tweetApiInfo = networkTweetInfo.data
                    if (networkTweetInfo.code == 200 && tweetApiInfo != null) {
                        tweetEntity = tweetApiInfo.tweet
                        if (tweetEntity != null) {
                            tweetEntity.owner = tweetApiInfo.user
                            tweetDao.insert(tweetEntity)
                        }
                    } else {
                        emit(SourceResult.Failure(RuntimeException("服务器返回码不为200")))
                    }
                }
                //将Entity转换为视图层需要的模型
                val model = mapper2PreviewModel.map(tweetEntity!!)
                emit(SourceResult.Success(model))
            } catch (e: Exception) {
                emit(SourceResult.Failure(e))
            }
        }.flowOn(Dispatchers.IO)//在IO线程进行数据库和网络操作
    }
}