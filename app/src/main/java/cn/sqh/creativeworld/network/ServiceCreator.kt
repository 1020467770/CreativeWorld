package cn.sqh.creativeworld.network


import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * 老板Service工厂单例类，
 * 现在通过Hilt注入提供Service，减少不必要的依赖
 */
object ServiceCreator {

    private const val BASE_URL = "http://159.75.31.178:5000/"

    private val gson =  GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)


}