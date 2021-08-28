package cn.sqh.creativeworld.network.service

import cn.sqh.creativeworld.core.data.TweetId
import cn.sqh.creativeworld.network.data.PageInfo
import cn.sqh.creativeworld.ui.comment.data.CommentApiResult
import cn.sqh.creativeworld.ui.home.data.VideoApiModel
import cn.sqh.creativeworld.ui.tweets.data.TweetApiResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TweetService {

    @GET("api/v1/tweets/action/return/all")
    suspend fun getTweets(
        @Query("page") page: Int,
        @Query("peerpage") pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): TweetApiResult.TweetsResult

    @GET("api/v1/tweets/action/return/single/{tweetId}")
    suspend fun getTweetById(@Path("tweetId") tweetId: TweetId): TweetApiResult.OneTweetResult

    @GET("api/v1/tweets/action/show/comment/{videoId}")
    suspend fun getTweetCommentsByTweetBy(
        @Path("tweetId") tweetId: TweetId,
        @Query("page") page: Int = PageInfo.DEFAULT_PAGE_NUM,
        @Query("peerpage") pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): CommentApiResult.TweetCommentsResult

}