package com.kangdroid.naviapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.kangdroid.naviapp.server.ServerManagement
import com.kangdroid.naviapp.utils.NaviFileUtils
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

class UploadingActivity : FilePagerActivity() {
    override val className: String = "UploadingActivity"
    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private lateinit var fileUri: String
    private lateinit var bufferedReader: BufferedReader

    override fun initializeContentView() {
        setContentView(R.layout.activity_uploading)
        val intent : Intent = Intent(Intent.ACTION_GET_CONTENT).apply{
            type = "*/*"
        }
        startActivityForResult(intent,1)
    }

    override fun initializeToolbar() {
        setSupportActionBar(findViewById(R.id.tb_upload))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.title_upload)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.uploading_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_select_path -> {
            // TODO should implement uploading functionality
            Log.d("PATH", pagerAdapter.pages[pagesVP.currentItem].folder.fileName)
            coroutineScope.launch {
                uploading(pagerAdapter.pages[pagesVP.currentItem].folder.token)
                withContext(Dispatchers.Main) {
                    finish()
                }
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                val inputStream: InputStream = contentResolver.openInputStream(uri) ?: run {
                    Log.e("TEST", "Nothing Found")
                    return
                }
                bufferedReader = BufferedReader(InputStreamReader(inputStream))

                Log.i("TAG", "Uri: $uri")
                fileUri = NaviFileUtils.getPathFromUri(this, uri)
                if (fileUri == NaviFileUtils.ERROR_GETTING_FILENAME) {
                    fileUri = ""
                }
            }
        }
    }

    private fun uploading(uploadPath : String){
        val filename : String = fileUri.substring(fileUri.lastIndexOf("/")+1)

        var file : File = File.createTempFile(filename,null,cacheDir)

        var strReader: String? = null
        while (true) {
            strReader = bufferedReader.readLine()
            if (strReader == null) break
            file.appendText(strReader+"\n")
            Log.e("PRINT", strReader)
        }

        Log.e("!!!!!!!!!!!!!!!","file : ${file.readText()}")
        Log.e("!!!!!!!!!!!!!!!","file : $filename")

        val requestBody : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file)
        val uploadFile : MultipartBody.Part = MultipartBody.Part.createFormData("uploadFile",filename,requestBody)
        var param : HashMap<String,Any> = HashMap()
        with(param){
            put("uploadPath", uploadPath)
        }
        ServerManagement.upload(param, uploadFile)
    }

}