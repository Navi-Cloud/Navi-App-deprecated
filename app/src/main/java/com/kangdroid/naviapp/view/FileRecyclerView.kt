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
import com.kangdroid.naviapp.data.FileType
import com.kangdroid.naviapp.data.getBriefName
import com.kangdroid.naviapp.data.getFormattedDate
import com.kangdroid.naviapp.server.ServerManagement
import kotlinx.coroutines.*
import java.io.File

class FileRecyclerViewHolder(itemView: View, private val pagerAdapter: FilePagerAdapter) :
    RecyclerView.ViewHolder(itemView) {
    private val imgFileType: ImageView = itemView.findViewById(R.id.img_file_type)
    private val tvFileName: TextView = itemView.findViewById(R.id.tv_file_name)
    private val tvLastModifiedTime: TextView =
        itemView.findViewById(R.id.tv_last_modified_time)
    private val ADAPTER_TAG: String = "FileRecyclerAdapter"

    fun bind(fileData: FileData) {
        imgFileType.setImageResource(
            when (fileData.fileType) {
                FileType.File.toString() -> R.drawable.ic_common_file_24
                FileType.Folder.toString() -> R.drawable.ic_common_folder_24
                else -> R.drawable.ic_common_error_24
            }
        )
        tvFileName.text = getBriefName(fileData)
        tvLastModifiedTime.text = getFormattedDate(fileData)

        itemView.setOnClickListener {
            when (fileData.fileType) {
                FileType.File.toString() -> return@setOnClickListener // TODO should implement this too
                FileType.Folder.toString() -> pagerAdapter.exploreFolder(fileData)
            }
        }
    }

/*    private fun requestDataWithToken(inputToken: String) {
        coroutineScope.launch {
            val listRequest: List<FileData> =
                ServerManagement.getInsideFiles(inputToken) ?: run {
                    Log.e(ADAPTER_TAG, "Error occurred when connecting to server.")
                    Log.e(ADAPTER_TAG, "Returning empty list..")
                    emptyList<FileData>()
                }
                withContext(Dispatchers.Main) {
                    for (data in listRequest) {
                        data.fileName = File(data.fileName).name
                        add(data)
                    }
                    notifyDataSetChanged()
                }
            }
        }*/
}

class FileRecyclerAdapter(
    val folder: FileData,
    val pagerAdapter: FilePagerAdapter,
    _items: SortedFileList = SortedFileList()
) :
    RecyclerView.Adapter<FileRecyclerViewHolder>(), MutableList<FileData> by _items {
    private var items: SortedFileList = _items

    fun sort(mode: FileSortingMode, reverse: Boolean) {
        var isChanged: Boolean = false
        if (items.comparator != mode) {
            items.comparator = mode
            isChanged = true
        }
        if (items.isReversed != reverse) {
            items.isReversed = reverse
            isChanged = true
        }
        if (isChanged){
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileRecyclerViewHolder(view, pagerAdapter)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FileRecyclerViewHolder, position: Int) =
        holder.bind(items[position])
}

