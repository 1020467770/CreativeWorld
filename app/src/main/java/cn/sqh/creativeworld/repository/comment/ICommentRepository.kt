package cn.sqh.creativeworld.repository.comment

import androidx.paging.PagingData
import cn.sqh.creativeworld.core.data.*
import cn.sqh.creativeworld.network.data.PageInfo
import cn.sqh.creativeworld.ui.comment.CommentFragment
import cn.sqh.creativeworld.ui.comment.data.CommentModel
import cn.sqh.creativeworld.ui.tweets.data.TweetPreviewModel
import kotlinx.coroutines.flow.Flow

interface ICommentRepository {

    fun getVideoComments(
        videoId: VideoId,
        pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): Flow<PagingData<CommentModel>>

    fun getTweetComments(
        tweetId: TweetId,
        pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): Flow<PagingData<CommentModel>>


}