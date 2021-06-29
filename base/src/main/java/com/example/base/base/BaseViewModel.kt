package com.example.base.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.base.loadstate.LoadState
import com.example.base.net.ApiException
import com.example.base.net.ResponseResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BaseViewModel : ViewModel(), LifecycleObserver {
    val loadState: MutableLiveData<LoadState> = MutableLiveData()

    fun runUIThread(callback: (() -> Unit)?) {
        viewModelScope.launch(Dispatchers.Main) {
            callback?.invoke()
        }
    }

    fun <T> request(
        block: suspend CoroutineScope.() -> T,
        success: (T) -> Unit,
        error: (ApiException) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {

        }
    }

    suspend inline fun <T> loadStateCall(crossinline call: suspend CoroutineScope.() -> ResponseResult<T>): ResponseResult<T> {
        return withContext(Dispatchers.IO) {
            var result = ResponseResult<T>()
            try {
                result.dataState = LoadState.LOADING
                result = call()
                if (result.errorCode == 0) {
                    if (result.data == null || (result.data is List<*> && (result.data as List<*>).size == 0)) {
                        result.dataState = LoadState.NO_DATA
                    } else {
                        result.dataState = LoadState.SUCCESS
                    }
                }
                //
                if (result.errorCode == ApiException.CODE_AUTH_INVALID) {
//                跳转业务逻辑
                }
            } catch (e: Throwable) {
                result.dataState = LoadState.ERROR
                return@withContext ApiException.build(e).toResponse<T>()
            }
            return@withContext result
        }
    }
}