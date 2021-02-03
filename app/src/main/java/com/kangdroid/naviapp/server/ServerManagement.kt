package com.kangdroid.naviapp.server

import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServerManagement {
    private val serverAddress: String = "192.168.0.46"
    private val serverPort: String = "8080"
    private val TAG_SERVER_MANAGEMENT = "ServerManagement"
    private val retroFit: Retrofit? = try {
        Retrofit.Builder()
            .baseUrl("http://$serverAddress:$serverPort")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    } catch (e: IllegalArgumentException) {
        // Log priority: Error[WTF is not allowed since it might terminate APP]
        Log.e(TAG_SERVER_MANAGEMENT, "FATAL - SERVER INIT FAILED!!")
        Log.e(TAG_SERVER_MANAGEMENT, e.stackTraceToString())
        null
    }
    private val api: APIInterface? = retroFit?.create(APIInterface::class.java) ?: run {
        Log.e(TAG_SERVER_MANAGEMENT, "Server is NOT initiated, therefore api will not be implemented.")
        null
    }

    fun getRootToken(): String {
        val tokenFunction: Call<ResponseBody>? = api?.getRootToken()
        val response: Response<ResponseBody>? = try {
            tokenFunction?.execute()
        } catch (e: Exception) {
            Log.e(TAG_SERVER_MANAGEMENT, "Error when getting root token from server.")
            Log.e(TAG_SERVER_MANAGEMENT, e.stackTraceToString())
            null
        }

        return response?.body()?.string() ?: ""
    }
}