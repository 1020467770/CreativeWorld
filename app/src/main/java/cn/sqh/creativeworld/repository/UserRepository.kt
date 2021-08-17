package cn.sqh.creativeworld.repository

import androidx.lifecycle.LiveData
import cn.sqh.creativeworld.core.EmptyLiveData
import cn.sqh.creativeworld.core.data.Resource
import cn.sqh.creativeworld.core.data.User
import cn.sqh.creativeworld.core.database.CreativeWorldDatabase
import cn.sqh.creativeworld.core.database.UserDao
import cn.sqh.creativeworld.login.data.model.LoggedInUser
import cn.sqh.creativeworld.login.data.model.LoginResult
import cn.sqh.creativeworld.login.data.model.RegisterResult
import cn.sqh.creativeworld.login.data.model.RegisteringUser
import cn.sqh.creativeworld.network.ApiResponse
import cn.sqh.creativeworld.network.CreativeWorldNetwork
import cn.sqh.creativeworld.network.NetworkBoundResource
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.Dispatchers

object UserRepository : BaseRepository() {

    private val userDao = CreativeWorldDatabase.getDatabase().userDao()

    var loggedInUser: LoggedInUser? = null

    var loggedInToken: String = ""

    fun register(registeringUser: RegisteringUser): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, RegisterResult>() {
            override fun saveCallResult(item: RegisterResult) {
                val registeredUser = item.data
                if (item.code == 200 && registeredUser != null) {
                    registeredUser.user?.let {
                        userDao.insert(it)
                    }
                }
            }

            override fun shouldFetch(data: User?) = true

            override fun loadFromDb(): LiveData<User> = userDao.findByEmail(registeringUser.email)

            override fun createCall(): LiveData<ApiResponse<RegisterResult>> =
                fire_liveData(Dispatchers.IO) {
//                    LogUtils.d(registeringUser)
                    val result = CreativeWorldNetwork.register(
                        registeringUser.username,
                        registeringUser.email,
                        registeringUser.password
                    )
                    ApiResponse.create(result)
                }

        }.asLiveData()
    }

    fun login(email: String, password: String): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, LoginResult>() {
            override fun saveCallResult(item: LoginResult) {
                if (item.code == 200) {
                    loggedInUser = item.data
                    if (loggedInUser != null) {
                        loggedInToken = loggedInUser!!.token
                        userDao.insert(loggedInUser!!.user!!)
                    }
                }
            }

            override fun shouldFetch(data: User?) = true

            override fun loadFromDb(): LiveData<User> = userDao.findByEmail(email)

            override fun createCall(): LiveData<ApiResponse<LoginResult>> =
                fire_liveData(Dispatchers.IO) {
                    val result = CreativeWorldNetwork.login(email, password)
                    ApiResponse.create(result)
                }

            override fun onFetchFailed() {
                loggedInUser = null
                loggedInToken = ""
            }

        }.asLiveData()
    }

}