package cn.sqh.creativeworld.core

import androidx.lifecycle.LiveData

/**
 * 提供空值的LiveData
 */
class EmptyLiveData<T : Any?> private constructor() : LiveData<T>() {
    init {
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> {
            return EmptyLiveData()
        }
    }
}