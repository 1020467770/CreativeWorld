package cn.sqh.creativeworld.network.data

import com.google.gson.annotations.Expose

open class ApiSingleResult<T>(
    @Expose
    var code: Int = 400,
    @Expose
    var data: T? = null,
    @Expose
    var note: String = ""
)