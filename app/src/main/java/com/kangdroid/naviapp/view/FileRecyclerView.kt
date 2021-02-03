package com.kangdroid.naviapp.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kangdroid.naviapp.R
import com.kangdroid.naviapp.custom.FileSortingMode
import com.kangdroid.naviapp.custom.SortedFileList
import com.kangdroid.naviapp.data.FileData
import com.kangdroid.naviapp.data.FileResponseDTO
import com.kangdroid.naviapp.data.FileType
import com.kangdroid.naviapp.server.ServerManagement
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class FileRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imgFileType: ImageView = itemView.findViewById(R.id.img_file_type)
    private val tvFileName: TextView = itemView.findViewById(R.id.tv_file_name)
    private val tvLastModifiedTime: TextView =
        itemView.findViewById(R.id.tv_last_modified_time)

    fun bind(fileData: FileData) {
        imgFileType.setImageResource(
            when (fileData.fileType) {
                FileType.FILE -> R.drawable.ic_common_file_24
                FileType.FOLDER -> R.drawable.ic_common_folder_24
            }
        )
        tvFileName.text = fileData.fileName
        tvLastModifiedTime.text = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSSSS",
            Locale.getDefault()
        ).format(Date(fileData.lastModifiedTime))
    }
}

class FileRecyclerAdapter(_items: SortedFileList = SortedFileList()) :
    RecyclerView.Adapter<FileRecyclerViewHolder>(), MutableList<FileData> by _items {
    private val tokenStack: Stack<String> = Stack()
    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private var items: SortedFileList = _items

    var reversed: Boolean
        get() = items.reversed
        set(value) {
            items.reversed = value
            notifyDataSetChanged()
        }

    fun setSortingMode(mode: FileSortingMode) {
        items.comparator = mode
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FileRecyclerViewHolder, position: Int) {
        holder.itemView.setOnClickListener { v ->
            val testObject = items[holder.adapterPosition]
            Log.e("TEST", "Touched: ${holder.adapterPosition}")
            Log.e("TEST", "File Name: ${testObject.fileName}")
            Log.e("TEST", "File Type: ${testObject.fileType}")
            Log.e("TEST", "Token: ${testObject.token}")
            Log.e("TEST", "Token: ${testObject.prevToken}")

            if (testObject.fileType == FileType.FOLDER) {
                tokenStack.push(testObject.prevToken)
                clear()
                notifyDataSetChanged()
                requestDataWithToken(testObject.token)
            }
        }
        holder.bind(items[position])
    }

    fun backButtonPressed() {
        // PrevToken
        val prevToken: String = tokenStack.pop()
        clear()
        notifyDataSetChanged()
        requestDataWithToken(prevToken)
    }

    private fun requestDataWithToken(inputToken: String) {
        coroutineScope.launch {
            val listRequest: List<FileResponseDTO> = ServerManagement.getInsideFiles(inputToken) ?: emptyList()

            withContext(Dispatchers.Main) {
                for (data in listRequest) {
                    add(
                        data.toFileData()
                    )
                }
                notifyDataSetChanged()
            }
        }
    }
}

