package cn.sqh.creativeworld.core.di

import cn.sqh.creativeworld.network.service.TweetService
import cn.sqh.creativeworld.network.service.UserService
import cn.sqh.creativeworld.network.service.VideoService
import com.blankj.utilcode.util.LogUtils
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetWorkModule {

    private const val BASE_URL = "http://159.75.31.178:5000/"

    private val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .build()
                LogUtils.d("测试发送网络请求${request}")
//                LogUtils.d("网络请求头${request.headers()}")
                chain.proceed(request)
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideVideoService(retrofit: Retrofit): VideoService {
        return retrofit.create(VideoService::class.java)
    }

    @Provides
    @Singleton
    fun provideTweetService(retrofit: Retrofit): TweetService {
        return retrofit.create(TweetService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }
}
