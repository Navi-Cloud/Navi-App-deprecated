package com.kangdroid.naviapp.server

import com.kangdroid.naviapp.data.FileData
import com.kangdroid.naviapp.data.LoginRequest
import com.kangdroid.naviapp.data.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface APIInterface {
    @GET("/api/navi/root-token")
    fun getRootToken(@HeaderMap headerMap: HashMap<String, Any>): Call<ResponseBody>

    @GET("/api/navi/files/list/{token}")
    fun getInsideFiles(@HeaderMap headerMap: HashMap<String, Any>, @Path("token") token: String): Call<List<FileData>>

    @Multipart
    @POST("/api/navi/files")
    fun upload(@HeaderMap headerMap: HashMap<String, Any>, @PartMap par : HashMap<String,Any>, @Part files: MultipartBody.Part) : Call<ResponseBody>

    @GET("api/navi/files/{token}")
    fun download(@HeaderMap headerMap: HashMap<String, Any>, @Path("token") token : String) : Call<ResponseBody>

    @POST("/api/navi/login")
    fun loginUser( @Body userLoginRequest : LoginRequest ): Call<ResponseBody>

    @POST("/api/navi/join")
    fun register( @Body userRegisterRequest : RegisterRequest ) : Call<ResponseBody>
}