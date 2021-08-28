package cn.sqh.creativeworld.network.service

import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.network.data.PageInfo
import cn.sqh.creativeworld.repository.old.UserRepository
import cn.sqh.creativeworld.ui.comment.data.CommentApiResult
import cn.sqh.creativeworld.ui.home.data.VideoApiModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface VideoService {

    /**
     * 使用LiveData以及线程通信作为DataSource
     */
    @GET("api/v1/videos/action/return/all")
    fun getVideos(
        @Query("page") page: Int = PageInfo.DEFAULT_PAGE_NUM,
        @Query("peerpage") pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): Call<VideoApiModel.VideosResult>

    @GET("api/v1/videos/action/return/single/{videoId}")
    fun getVideoById(@Path("videoId") videoId: VideoId): Call<VideoApiModel.OneVideoResult>

    /**
     * 使用Flow和协程作为DataSource
     */
    @GET("api/v1/videos/action/return/all")
    suspend fun getVideos2(
        @Query("page") page: Int = PageInfo.DEFAULT_PAGE_NUM,
        @Query("peerpage") pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): VideoApiModel.VideosResult

    @Multipart
    @POST("api/v1/videos/action/upload")
    suspend fun uploadVideo(
        @Part("videoName") videoFileName: RequestBody,
        @Part("videoDescribe") videoDesc: RequestBody,
        @Part videoFile: MultipartBody.Part,//videoFile
        @Part coverFile: MultipartBody.Part,//coverFile
        @Header("token") token: String = UserRepository.loggedInToken
    ): VideoApiModel.OneVideoResult

    @GET("api/v1/videos/action/return/single/{videoId}")
    suspend fun getVideoById_suspend(@Path("videoId") videoId: VideoId): VideoApiModel.OneVideoResult

    @GET("api/v1/videos/action/show/comment/{videoId}")
    suspend fun getVideoCommentsByVideoBy(
        @Path("videoId") videoId: VideoId,
        @Query("page") page: Int = PageInfo.DEFAULT_PAGE_NUM,
        @Query("peerpage") pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): CommentApiResult.VideoCommentsResult

    @FormUrlEncoded
    @POST("api/v1/videos/action/comment/{videoId}")
    suspend fun makeComment(
        @Path("videoId") videoId: VideoId,
        @Field("comment") content: String,
        @Header("token") token: String = UserRepository.loggedInToken
    ): CommentApiResult.OneVideoCommentResult

}