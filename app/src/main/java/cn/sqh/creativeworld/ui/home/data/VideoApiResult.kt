package cn.sqh.creativeworld.ui.home.data

import cn.sqh.creativeworld.network.data.ApiMultiResult
import cn.sqh.creativeworld.network.data.ApiSingleResult
import cn.sqh.creativeworld.core.data.User
import cn.sqh.creativeworld.core.data.Video
import com.google.gson.annotations.Expose


sealed class VideoApiModel {

    class VideosResult : ApiMultiResult<VideoApiInfo>()

    class OneVideoResult : ApiSingleResult<VideoApiInfo>()

    data class VideoApiInfo(
        @Expose
        var user: User?,
        @Expose
        var video: Video?
    )


}


