package cn.sqh.creativeworld.network.service

import cn.sqh.creativeworld.core.data.UserId
import cn.sqh.creativeworld.login.data.model.LoginSingleResult
import cn.sqh.creativeworld.login.data.model.RegisterResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @FormUrlEncoded
    @POST("api/v1/users/action/login")
    fun login(
        @Field("userEmail") email: String,
        @Field("userPassword") password: String,
    ): Call<LoginSingleResult>

    @FormUrlEncoded
    @POST("api/v1/users/action/register")
    fun register(
        @Field("userName") username: String,
        @Field("userEmail") email: String,
        @Field("userPassword") password: String
    ): Call<RegisterResult>

    @POST("api/v1/users/action/follow/{vlogerId}")
    suspend fun follow(
        @Path("vlogerId") userId: UserId,
        @Header("token") token: String
    )
}