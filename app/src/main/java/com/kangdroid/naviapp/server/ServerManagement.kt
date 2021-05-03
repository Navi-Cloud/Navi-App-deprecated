package com.kangdroid.naviapp.server

import android.os.Environment
import android.util.Log
import com.kangdroid.naviapp.BuildConfig
import com.kangdroid.naviapp.data.FileData
import com.kangdroid.naviapp.data.LoginRequest
import com.kangdroid.naviapp.data.RegisterRequest
import okhttp3.Headers
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URLDecoder

object ServerManagement {

    private val TAG_SERVER_MANAGEMENT = "ServerManagement"
    private var retroFit: Retrofit? = null
    private var api: APIInterface? = null
    // Variable/Value Initiation Ends

    /**
     * initServerCommunication: Initiate basic API/Retrofit
     * Returns true if both retroFit/api is NOT-NULL,
     * false when either of retrofit/api is null
     */
    fun initServerCommunication(): Boolean {
        //val serverAddress: String = BuildConfig.SERVER_URL
        //val serverPort: String = BuildConfig.SERVER_PORT
        val serverAddress: String = "" // IP Addr
        val serverPort: String = "8080"
        retroFit = try {
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

        api = retroFit?.create(APIInterface::class.java) ?: run {
            Log.e(
                TAG_SERVER_MANAGEMENT,
                "Server is NOT initiated, therefore api will not be implemented."
            )
            null
        }

        return (retroFit != null && api != null)
    }

    fun getRootToken(headers: HashMap<String,Any>): String {
        val tokenFunction: Call<ResponseBody>? = api?.getRootToken(headers)
        val response: Response<ResponseBody>? = try {
            tokenFunction?.execute()
        } catch (e: Exception) {
            Log.e(TAG_SERVER_MANAGEMENT, "Error when getting root token from server.")
            Log.e(TAG_SERVER_MANAGEMENT, e.stackTraceToString())
            null
        }
        Log.d("Response", response?.body()?.string() ?: response?.errorBody()!!.string())
        return response?.body()?.string() ?: ""
    }

    /**
     * getInsideFiles: Get list of files/directories based on requested token
     * Param: The target token to request
     * Returns: List of FileResponseDTO[The Response] - could be empty.
     * Returns: NULL when error occurred.
     */
    fun getInsideFiles(headers: HashMap<String,Any>, requestToken: String): List<FileData>? {
        val insiderFunction: Call<List<FileData>>? = api?.getInsideFiles(headers, requestToken)
        val response: Response<List<FileData>>? = try {
            insiderFunction?.execute()
        } catch (e: Exception) {
            Log.e(TAG_SERVER_MANAGEMENT, "Error when getting directory list from server.")
            Log.e(TAG_SERVER_MANAGEMENT, e.stackTraceToString())
            null
        }
        return response?.body()
    }

    fun upload(headers: HashMap<String,Any>, Param : HashMap<String,Any>, file: MultipartBody.Part) : String {
        val uploading: Call<ResponseBody>? = api?.upload(headers, Param, file)

        Log.d("UPLOAD","$uploading")
        val response: Response<ResponseBody>? = try{
            uploading?.execute()
        }catch (e:Exception){
            Log.e(TAG_SERVER_MANAGEMENT, "Error when uploading File.")
            Log.e(TAG_SERVER_MANAGEMENT, e.stackTraceToString())
            null
        }
        Log.i("upload", "SUCEEEEEDDDDD")
        return response?.body()?.string() ?: ""
    }

    fun download(headers: HashMap<String,Any>, token: String) {
        val downloading : Call<ResponseBody> ?= api?.download(headers, token)
        val response: Response<ResponseBody>?=try{
            downloading?.execute()
        }catch (e: Exception){
            Log.e(TAG_SERVER_MANAGEMENT, "Error when downloading File.")
            Log.e(TAG_SERVER_MANAGEMENT, e.stackTraceToString())
            null
        }
        if (response != null) {
            var header : String? = response.headers().get("Content-Disposition")
            header = URLDecoder.decode(header,"UTF-8")
            var fileName : String? = header?.replace("attachment; filename=\"", "")
            fileName = fileName?.substring(fileName.lastIndexOf("/")+1,fileName.length-1)
            Log.e("SERVERMANAGE", "fileName : $fileName")
            Log.e("SERVERMANAGE", "Content : ${response.body().toString()}")

            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                val pa : String = Environment.getExternalStorageDirectory().toString() + "/Download"
                val file = File(pa, fileName)
                try{

                    val fileReader = ByteArray(4096)
                    val fileSize: Long ?= response.body()?.contentLength()
                    var fileSizeDownloaded: Long = 0

                    val inputStream : InputStream? = response.body()?.byteStream()
                    val outputStream = FileOutputStream(file)

                    while (true) {
                        val read: Int ?= inputStream?.read(fileReader)
                        if (read == -1) {
                            break
                        }
                        if (read != null) {
                            outputStream.write(fileReader, 0, read)
                            Log.e("DOWNLOAD", "outputstream : $outputStream")
                            fileSizeDownloaded += read.toLong()
                        }
                        Log.e("DOWNLOAD", "file download: $fileSizeDownloaded of $fileSize")
                    }
                    outputStream.flush()
                }catch (e:Exception){
                    Log.e("External Storage", "External Storage is not ready.")
                    Log.e("External Storage", e.stackTraceToString())
                }
            }
        }
    }

    fun login(userLoginRequest: LoginRequest): String {
        val loginUserRequest: Call<ResponseBody>? = api?.loginUser(userLoginRequest)

        val response : Response<ResponseBody> = runCatching {
            loginUserRequest?.execute()
        }.getOrNull() ?: return ""

        return if (response.isSuccessful) {
            response.body()?.string()
        } else {
            Log.d("ServerManagement.login", response.errorBody()?.string()!!)
            "-1"
        } ?: ""
    }

    fun register(userRegisterRequest: RegisterRequest) : String {
        val registerUserReqeust : Call<ResponseBody> ?= api?.register(userRegisterRequest)

        val response : Response<ResponseBody> = runCatching {
            registerUserReqeust?.execute()
        }.getOrNull() ?: return ""

        return if (response.isSuccessful) {
            response.body()?.string()
        } else {
            response.errorBody()?.string()
        } ?: ""
    }
}