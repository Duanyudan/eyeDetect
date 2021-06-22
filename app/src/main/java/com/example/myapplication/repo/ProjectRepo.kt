package com.example.myapplication.repo

import com.example.myapplication.bean.ProjectTree
import com.example.myapplication.enjoyRetrofit.EnjoyRetrofit
import com.example.myapplication.enjoyRetrofit.ServiceApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ProjectRepo {
    private lateinit var mService: ServiceApi

    init {
        mService = EnjoyRetrofit.Builder()
            .baseUrl("https://www.wanandroid.com")
            .build().create(ServiceApi::class.java)
    }
//
//    suspend fun loadProjectTree() {
//        return mService.loadProjectTree().
//    }
}