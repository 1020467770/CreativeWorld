package cn.sqh.creativeworld.login.data.model

import cn.sqh.creativeworld.core.data.User
import com.google.gson.annotations.Expose

data class RegisterResult(
    @Expose
    var code: Int = 400,
    @Expose
    var data: RegisteredUser?,
) {
}

data class RegisteredUser(
    @Expose
    var user: User?
)