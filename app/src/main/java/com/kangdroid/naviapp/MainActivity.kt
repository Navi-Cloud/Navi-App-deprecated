package com.kangdroid.naviapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ToggleButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kangdroid.naviapp.custom.FileSortingMode
import com.kangdroid.naviapp.data.FileData
import com.kangdroid.naviapp.data.FileType
import com.kangdroid.naviapp.server.ServerManagement
import com.kangdroid.naviapp.view.FileRecyclerAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!ServerManagement.initServerCommunication("192.168.0.46", "8080")) {
            Log.wtf("MainActivity", "Server initiation failed!")
            Log.wtf("MainActivity", "This should NOT be happened!")
        }

        val fileRV: RecyclerView by lazy {
            findViewById<RecyclerView>(R.id.rv_test).apply {
                layoutManager = LinearLayoutManager(applicationContext)
            }
        }

        val fileAdapter: FileRecyclerAdapter by lazy {
            FileRecyclerAdapter().also { fileRV.adapter = it }
        }

        val fileNameET: EditText = findViewById(R.id.et_file_name)

        fun generateAddListener(type: FileType) = View.OnClickListener {
            if (fileNameET.text.isEmpty())
                return@OnClickListener
            fileAdapter.add(
                FileData(
                    id = fileAdapter.size.toLong(),
                    fileName = fileNameET.text.toString(),
                    fileType = type.toString(),
                    mimeType = "Mime Type",
                    token = "TEST_TOKEN",
                    prevToken = "PREV_TOKEN",
                    lastModifiedTime = System.currentTimeMillis(),
                    fileCreatedDate = "FILE_TESTCREATE",
                    fileSize = "2B"
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
    }
}