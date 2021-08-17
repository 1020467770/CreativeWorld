package cn.sqh.creativeworld.login.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.switchMap
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.EmptyLiveData
import cn.sqh.creativeworld.login.data.model.LoggingUser
import cn.sqh.creativeworld.login.data.model.LoginFormState
import cn.sqh.creativeworld.repository.UserRepository


class LoginViewModel() : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginUser = MutableLiveData<LoggingUser?>()
    val loggedInUser = _loginUser.switchMap { loginUser ->
        if (loginUser != null) {
            UserRepository.login(loginUser.email, loginUser.password)
        } else {
            EmptyLiveData.create()
        }
    }

    fun login(loggingUser: LoggingUser?) {
        _loginUser.value = loggingUser
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isEmailValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_email)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return if (email.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            email.isNotBlank()
        }
    }

}