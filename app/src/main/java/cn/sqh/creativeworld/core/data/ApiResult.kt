package cn.sqh.creativeworld.core.data

import com.google.gson.annotations.Expose

open class ApiResult<T>(
    @Expose
    var code: Int = 400,
    @Expose
    var data: T? = null,
    @Expose
    var note: String = ""
)