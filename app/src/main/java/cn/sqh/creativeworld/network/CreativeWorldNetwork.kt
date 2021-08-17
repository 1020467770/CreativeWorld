package cn.sqh.creativeworld.network

import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.network.service.UserService
import cn.sqh.creativeworld.network.service.VideoService
import com.blankj.utilcode.util.LogUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object CreativeWorldNetwork {

    private val mUserService = ServiceCreator.create<UserService>()
    private val mVideoService = ServiceCreator.create<VideoService>()

    suspend fun login(email: String, password: String) = mUserService.login(email, password).await()
    suspend fun register(username: String, email: String, password: String) =
        mUserService.register(username, email, password).await()

    suspend fun getVideos(page: Int, pageSize: Int) =
        mVideoService.getVideos(page, pageSize).await()

    suspend fun getVideoById(videoId: VideoId) = mVideoService.getVideoById(videoId).await()

    private suspend fun <T> Call<T>.await(): Response<T> {
        return suspendCoroutine { continuation -> //suspendCoroutine会把当前协程立即挂起，而lambda表达式的代码则会在普通线程中执行
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    LogUtils.d("onSuccessResponse: ${body}")
//                    LogUtils.d("onSuccessResponse: ${response.raw().body()?.string()}")
                    continuation.resume(response)
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    t.printStackTrace()
                    LogUtils.d("onFailedResponse: ${t}")
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}