package com.kangdroid.naviapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kangdroid.naviapp.custom.FileSortingMode
import com.kangdroid.naviapp.data.FileData
import com.kangdroid.naviapp.data.FileType
import com.kangdroid.naviapp.server.ServerManagement
import com.kangdroid.naviapp.view.FileRecyclerAdapter
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private val MAIN_UI: String = "MainActivity"
    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    private val fileRV: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.rv_test).apply {
            layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    private val fileAdapter: FileRecyclerAdapter by lazy {
        FileRecyclerAdapter().also { fileRV.adapter = it }
    }

    override fun onBackPressed() {
        if (fileAdapter.isStackEmpty()) {
            super.onBackPressed()
        } else {
            fileAdapter.backButtonPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fileNameET: EditText = findViewById(R.id.et_file_name)

        fun generateAddListener(type: FileType) = View.OnClickListener {
            if (fileNameET.text.isEmpty())
                return@OnClickListener
            fileAdapter.add(
                FileData(
                    id = fileAdapter.size.toLong(),
                    fileName = fileNameET.text.toString(),
                    fileType = type.toString(),
                    mimeType = "",
                    token = "TEMP_TOKEN",
                    prevToken = "PREV_TOKEN",
                    lastModifiedTime = System.currentTimeMillis(),
                    fileCreatedDate = "FileCreatedDate",
                    fileSize = "20B"
                )
            )
            fileAdapter.notifyDataSetChanged()
        }

        findViewById<Button>(R.id.btn_add_file).setOnClickListener(generateAddListener(FileType.File))
        findViewById<Button>(R.id.btn_add_folder).setOnClickListener(generateAddListener(FileType.Folder))

        findViewById<ToggleButton>(R.id.tgb_acc_dec).setOnCheckedChangeListener { _, isChecked ->
            fileAdapter.reversed = isChecked
        }

        val shuffleTypeTGB: ToggleButton = findViewById(R.id.tgb_shuffle_type)
        val sortByLMTTGB: ToggleButton = findViewById(R.id.tgb_name_lmt)

        shuffleTypeTGB.setOnCheckedChangeListener { _, isChecked ->
            when (sortByLMTTGB.isChecked) {
                true -> fileAdapter.setSortingMode(if (isChecked) FileSortingMode.LMT else FileSortingMode.TypedLMT)
                false -> fileAdapter.setSortingMode(if (isChecked) FileSortingMode.Name else FileSortingMode.TypedName)
            }
        }

        sortByLMTTGB.setOnCheckedChangeListener { _, isChecked ->
            when (shuffleTypeTGB.isChecked) {
                true -> fileAdapter.setSortingMode(if (isChecked) FileSortingMode.LMT else FileSortingMode.Name)
                false -> fileAdapter.setSortingMode(if (isChecked) FileSortingMode.TypedLMT else FileSortingMode.TypedName)
            }
        }

        // Initiate Root Data
        coroutineScope.launch {
            val response: List<FileData> = initData()

            withContext(Dispatchers.Main) {
                for (data in response) {
                    data.fileName = File(data.fileName).name
                    fileAdapter.add(data)
                }
                fileAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * A Blocking call could take huge amount of times...
     * Returns list of ROOT list
     */
    private fun initData(): List<FileData> {
        val responseList: List<FileData>

        with(ServerManagement) {
            responseList = getInsideFiles(getRootToken()) ?: run {
                Log.e(MAIN_UI, "Error occurred when connecting to server.")
                Log.e(MAIN_UI, "Returning empty list..")
                emptyList()
            }
        }
        return responseList
    }
}