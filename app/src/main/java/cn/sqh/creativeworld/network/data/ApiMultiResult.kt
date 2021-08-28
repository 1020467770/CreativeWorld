package cn.sqh.creativeworld.network.data

import com.google.gson.annotations.Expose

open class ApiMultiResult<T>(
    @Expose
    var code: Int = 400,
    @Expose
    var data: List<T> = emptyList(),
    @Expose
    var pageInfo: PageInfo = PageInfo()
) {
}