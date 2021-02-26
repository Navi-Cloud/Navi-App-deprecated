package com.kangdroid.naviapp.server

import com.kangdroid.naviapp.data.FileData
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface APIInterface {
    @GET("/api/navi/rootToken")
    fun getRootToken(): Call<ResponseBody>
    @GET("/api/navi/findInsideFiles/{token}")
    fun getInsideFiles(@Path("token") token: String): Call<List<FileData>>

    @Multipart
    @POST("/api/navi/fileUpload")
    fun upload(@PartMap par : HashMap<String,Any>, @Part files: MultipartBody.Part) : Call<ResponseBody>
}