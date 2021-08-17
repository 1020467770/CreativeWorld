package cn.sqh.creativeworld.ui.home.data

import cn.sqh.creativeworld.core.data.User
import cn.sqh.creativeworld.core.data.Video
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


sealed class VideoApiModel {

    data class VideosResult(
        @Expose
        var code: Int = 400,
        @Expose
        var data: List<VideoApiInfo>?,
        @Expose
        var pageInfo: PageInfo

    ) : VideoApiModel()

    data class OneVideoResult(
        @Expose
        var code: Int = 400,
        @Expose
        var data: VideoApiInfo?,
        @Expose
        var note: String = ""

    ) : VideoApiModel()

}

data class VideoApiInfo(
    @Expose
    var user: User?,
    @Expose
    var video: Video?
)

data class PageInfo(
    @Expose
    var dataLength: Int,
    @Expose
    var ifhavebefore: Boolean,
    @Expose
    var ifhavenext: Boolean,
    @Expose
    var maxPage: Int,
    @Expose
    var minPage: Int,
    @Expose
    @SerializedName("nowpage")
    var nowPage: Int,
    @Expose
    @SerializedName("peerpage")
    var pageSize: Int,
) {
    companion object {
        const val DEFAULT_PAGE_NUM = 1
        const val DEFAULT_PAGE_SIZE = 5
    }
}