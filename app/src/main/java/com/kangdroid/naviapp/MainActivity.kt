package com.kangdroid.naviapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kangdroid.naviapp.view.FileData
import com.kangdroid.naviapp.view.FileRecyclerAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fileRV: RecyclerView by lazy {
            findViewById<RecyclerView>(R.id.rv_test).apply {
                layoutManager = LinearLayoutManager(applicationContext)
            }
        }

        val fileAdapter: FileRecyclerAdapter by lazy {
            FileRecyclerAdapter().also { fileRV.adapter = it }
        }

        findViewById<Button>(R.id.btn_add_file).setOnClickListener {
            fileAdapter.items.add(
                FileData(
                    fileAdapter.items.size.toLong(),
                    "file-name-${fileAdapter.items.size.toLong() + 1}",
                    FileData.FILE_TYPE_FILE,
                    "TOKEN",
                    "modified-time-${fileAdapter.items.size.toLong() + 1}"
                )
            )
            fileAdapter.notifyDataSetChanged()
        }

        findViewById<Button>(R.id.btn_add_folder).setOnClickListener {
            fileAdapter.items.add(
                FileData(
                    fileAdapter.items.size.toLong(),
                    "folder-name-${fileAdapter.items.size.toLong() + 1}/",
                    FileData.FILE_TYPE_FOLDER,
                    "TOKEN",
                    "modified-time-${fileAdapter.items.size.toLong() + 1}"
                )
            )
            fileAdapter.notifyDataSetChanged()
        }
    }
}