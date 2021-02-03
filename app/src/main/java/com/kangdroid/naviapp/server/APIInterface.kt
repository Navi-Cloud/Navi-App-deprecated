package com.kangdroid.naviapp.server

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface APIInterface {
    @GET("/api/navi/rootToken")
    fun getRootToken(): Call<ResponseBody>
}