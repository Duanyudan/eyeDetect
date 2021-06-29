package com.example.base.net

import com.example.base.loadstate.LoadState
import com.google.gson.annotations.SerializedName

data class ResponseResult<T>(@SerializedName("errorCode") val errorCode: Int?=-1,
                             @SerializedName("errorMsg") val errorMsg: String?="",
                             @SerializedName("data") val data: T?=null,
                             var dataState:LoadState?=null,

)