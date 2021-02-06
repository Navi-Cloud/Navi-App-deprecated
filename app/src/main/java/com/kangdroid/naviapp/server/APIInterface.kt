package com.kangdroid.naviapp.server

import com.kangdroid.naviapp.data.FileData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface APIInterface {
    @GET("/api/navi/rootToken")
    fun getRootToken(): Call<ResponseBody>

    @GET("/api/navi/findInsideFiles/{token}")
    fun getInsideFiles(@Path("token") token: String): Call<List<FileData>>
}