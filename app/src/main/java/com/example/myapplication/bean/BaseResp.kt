package com.example.myapplication.bean

data class BaseResp<T> (val data:T,val errorCode:Int,val errorMsg:String)
