package cn.sqh.creativeworld.login.data.model

import cn.sqh.creativeworld.core.data.User
import com.google.gson.annotations.Expose

data class LoggedInUser(
    @Expose
    var token: String = "",
    @Expose
    val user: User?,
)