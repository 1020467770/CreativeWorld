package cn.sqh.creativeworld.core.data

sealed class SourceResult<out T> {
    data class Success<out T>(val value: T) : SourceResult<T>()

    data class Loading(val loadingValue: Int) : SourceResult<Nothing>()

    data class Failure(val throwable: Throwable?) : SourceResult<Nothing>()
}

inline fun <reified T> SourceResult<T>.doSuccess(success: (T) -> Unit) {
    if (this is SourceResult.Success) {
        success(value)
    }
}

inline fun <reified T> SourceResult<T>.doLoading(success: (Int) -> Unit) {
    if (this is SourceResult.Loading) {
        success(loadingValue)
    }
}

inline fun <reified T> SourceResult<T>.doFailure(failure: (Throwable?) -> Unit) {
    if (this is SourceResult.Failure) {
        failure(throwable)
    }
}