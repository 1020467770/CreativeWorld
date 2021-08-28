package cn.sqh.creativeworld.login.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.EmptyLiveData
import cn.sqh.creativeworld.login.data.model.RegisterFormState
import cn.sqh.creativeworld.login.data.model.RegisteringUser
import cn.sqh.creativeworld.repository.old.UserRepository

class RegisterViewModel : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    private val _registerUser = MutableLiveData<RegisteringUser?>()
    val registerUser = _registerUser.switchMap { registerUser ->
        if (registerUser != null) {
            UserRepository.register(registerUser)
        } else {
            EmptyLiveData.create()
        }
    }

    fun register(registeringUser: RegisteringUser) {
        _registerUser.value = registeringUser
    }

    fun registerDataChanged(registeringUser: RegisteringUser) {
        if (!isUserNameValid(registeringUser.username)) {
            _registerForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        } else if (!isEmailValid(registeringUser.email)) {
            _registerForm.value = RegisterFormState(emailError = R.string.invalid_email)
        } else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }

    private fun isEmailValid(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    /**
     * 之后可以用来判断用户名是否重复
     */
    private fun isUserNameValid(username: String) = username.length > 5

}