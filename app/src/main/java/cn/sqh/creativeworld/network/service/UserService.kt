package cn.sqh.creativeworld.network.service

import cn.sqh.creativeworld.login.data.model.LoginResult
import cn.sqh.creativeworld.login.data.model.RegisterResult
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserService {

    @FormUrlEncoded
    @POST("api/v1/users/action/login")
    fun login(
        @Field("userEmail") email: String,
        @Field("userPassword") password: String,
    ): Call<LoginResult>

    @FormUrlEncoded
    @POST("api/v1/users/action/register")
    fun register(
        @Field("userName") username: String,
        @Field("userEmail") email: String,
        @Field("userPassword") password: String
    ): Call<RegisterResult>
}