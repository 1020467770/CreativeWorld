package cn.sqh.creativeworld.network.service

import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.ui.home.data.PageInfo
import cn.sqh.creativeworld.ui.home.data.VideoApiModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VideoService {

    @GET("api/v1/videos/action/return/all")
    fun getVideos(
        @Query("page") page: Int = PageInfo.DEFAULT_PAGE_NUM,
        @Query("peerpage") pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): Call<VideoApiModel.VideosResult>

    @GET("api/v1/videos/action/return/single/{videoId}")
    fun getVideoById(@Path("videoId") videoId: VideoId): Call<VideoApiModel.OneVideoResult>
}