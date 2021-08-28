package cn.sqh.creativeworld.repository.tweet

import androidx.paging.PagingData
import cn.sqh.creativeworld.core.data.Resource
import cn.sqh.creativeworld.core.data.SourceResult
import cn.sqh.creativeworld.core.data.TweetEntity
import cn.sqh.creativeworld.core.data.TweetId
import cn.sqh.creativeworld.network.data.PageInfo
import cn.sqh.creativeworld.ui.tweets.data.TweetPreviewModel
import kotlinx.coroutines.flow.Flow

interface ITweetRepository {

    fun getTweetsPreview(
        pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): Flow<PagingData<TweetPreviewModel>>

    suspend fun getTweetInfo(tweetId: TweetId): Flow<SourceResult<TweetPreviewModel>>
}