package cn.sqh.creativeworld.repository.old

import androidx.lifecycle.liveData
import cn.sqh.creativeworld.network.ApiResponse
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

open class BaseRepository {

    protected fun <T> fire_liveData(
        context: CoroutineContext,
        block: suspend () -> ApiResponse<T>
    ) =
    //这里加的suspend表示所有传入的lambda表达式中的代码拥有挂起函数上下文
        //LiveData{}是一个协程构造方法，会构建一个新协程
        liveData<ApiResponse<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                ApiResponse.create<T>(e)
            }
            emit(result)
        }
}