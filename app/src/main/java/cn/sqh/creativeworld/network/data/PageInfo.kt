package cn.sqh.creativeworld.network.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PageInfo(
    @Expose
    var dataLength: Int = 0,
    @Expose
    var ifhavebefore: Boolean = false,
    @Expose
    var ifhavenext: Boolean = false,
    @Expose
    var maxPage: Int = 0,
    @Expose
    var minPage: Int = 0,
    @Expose
    @SerializedName("nowpage")
    var nowPage: Int = 0,
    @Expose
    @SerializedName("peerpage")
    var pageSize: Int = 0,
) {
    companion object {
        const val DEFAULT_PAGE_NUM = 1
        const val DEFAULT_PAGE_SIZE = 5
    }
}