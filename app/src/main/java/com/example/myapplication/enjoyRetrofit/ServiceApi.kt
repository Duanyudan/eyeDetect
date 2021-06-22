package com.example.myapplication.enjoyRetrofit

import com.example.myapplication.bean.BaseResp
import com.example.myapplication.bean.ProjectTree
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ServiceApi {

    //    https://restapi.amap.com
    @GET("article/list/0/json")
    fun getArticles(@Field("author") author: String): Response<String>

    @GET("/v3/weather/weatherInfo")
    fun getWeather(@Query("city") city: String, @Query("key") key: String): Call<ResponseBody>

    @POST
    @FormUrlEncoded
    fun postWeather(@Field("city") city: String, @Field("key") key: String): Call<ResponseBody>

//    @GET("project/tree/json")
//    suspend fun loadProjectTree(): Deferred<User>

}