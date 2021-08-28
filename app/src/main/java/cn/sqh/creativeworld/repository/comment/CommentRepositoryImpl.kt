package cn.sqh.creativeworld.repository.comment

import androidx.paging.*
import cn.sqh.creativeworld.core.data.*
import cn.sqh.creativeworld.core.database.CreativeWorldDatabase
import cn.sqh.creativeworld.core.mapper.TweetCommentEntity2CommentModelMapper
import cn.sqh.creativeworld.core.mapper.VideoCommentEntity2CommentModelMapper
import cn.sqh.creativeworld.network.service.TweetService
import cn.sqh.creativeworld.network.service.VideoService
import cn.sqh.creativeworld.ui.comment.data.CommentModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CommentRepositoryImpl(
    val tweetApi: TweetService,
    val videoApi: VideoService,
    val db: CreativeWorldDatabase,
    val videoMapper2CommentModel: VideoCommentEntity2CommentModelMapper,
    val tweetMapper2CommentModel: TweetCommentEntity2CommentModelMapper,
) : ICommentRepository {

    @ExperimentalPagingApi
    override fun getVideoComments(
        videoId: VideoId,
        pageSize: Int
    ): Flow<PagingData<CommentModel>> {
        return Pager(
            config = PagingConfig(pageSize),
            remoteMediator = RemoteVideoCommentMediator(videoApi, videoId, db)
        ) {
            db.videoCommentDao().getAllVideoComments()
        }.flow.map { commentEntity ->
            commentEntity.map { videoMapper2CommentModel.map(it) }
        }
    }

    @ExperimentalPagingApi
    override fun getTweetComments(
        tweetId: TweetId,
        pageSize: Int
    ): Flow<PagingData<CommentModel>> {
        return Pager(
            config = PagingConfig(pageSize),
            remoteMediator = RemoteTweetCommentMediator(tweetApi, tweetId, db)
        ) {
            db.tweetCommentDao().getAllTweetComments()
        }.flow.map { commentEntity ->
            commentEntity.map { tweetMapper2CommentModel.map(it) }
        }
    }


}