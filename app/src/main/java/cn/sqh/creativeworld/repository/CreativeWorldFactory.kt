package cn.sqh.creativeworld.repository

import cn.sqh.creativeworld.core.database.CreativeWorldDatabase
import cn.sqh.creativeworld.core.mapper.*
import cn.sqh.creativeworld.network.service.TweetService
import cn.sqh.creativeworld.network.service.VideoService
import cn.sqh.creativeworld.repository.comment.CommentRepositoryImpl
import cn.sqh.creativeworld.repository.comment.ICommentRepository
import cn.sqh.creativeworld.repository.tweet.ITweetRepository
import cn.sqh.creativeworld.repository.tweet.TweetRepositoryImpl
import cn.sqh.creativeworld.repository.video.IVideoRepository
import cn.sqh.creativeworld.repository.video.VideoRepositoryImpl

object CreativeWorldFactory {

    fun makeVideoFactory(api: VideoService, db: CreativeWorldDatabase): IVideoRepository =
        VideoRepositoryImpl(
            api,
            db,
            VideoEntity2VideoPreviewModelMapper(),
            VideoEntity2VideoDetailModelMapper()
        )

    fun makeTweetFactory(api: TweetService, db: CreativeWorldDatabase): ITweetRepository =
        TweetRepositoryImpl(
            api,
            db,
            TweetEntity2PreViewModelMapper()
        )

    fun makeCommentsFactory(
        tweetApi: TweetService,
        videoApi: VideoService,
        db: CreativeWorldDatabase
    ): ICommentRepository =
        CommentRepositoryImpl(
            tweetApi,
            videoApi,
            db,
            VideoCommentEntity2CommentModelMapper(),
            TweetCommentEntity2CommentModelMapper()
        )

}
