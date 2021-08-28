package cn.sqh.creativeworld.login.data.model

import androidx.annotation.StringRes


data class LoginFormState(
    @StringRes val usernameError: Int? = null,
    @StringRes val passwordError: Int? = null,
    val isDataValid: Boolean = false
)