package com.kangdroid.naviapp.view

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

class FileRecyclerViewHolder(
    itemView: View,
    _onFileSelectedCallback: (FileData) -> Any
) : RecyclerView.ViewHolder(itemView) {
    private val onFileSelectedCallback: (FileData) -> Any = _onFileSelectedCallback
    private val imgFileType: ImageView = itemView.findViewById(R.id.img_file_type)
    private val tvFileName: TextView = itemView.findViewById(R.id.tv_file_name)
    private val tvLastModifiedTime: TextView =
        itemView.findViewById(R.id.tv_last_modified_time)

    private companion object {
        const val SELECT_INTERVAL: Long = 400
        var lastSelectedTime: Long = System.currentTimeMillis()
    }

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
            val now: Long = System.currentTimeMillis()
            if (now - lastSelectedTime > SELECT_INTERVAL) {
                onFileSelectedCallback(fileData)
                lastSelectedTime = now
            }
        }
    }
}

class FileRecyclerAdapter(
    val folder: FileData,
    _onFileSelectedCallback: OnFileSelectedCallback,
    _items: SortedFileList = SortedFileList()
) : RecyclerView.Adapter<FileRecyclerViewHolder>(), MutableList<FileData> by _items {
    private var items: SortedFileList = _items
    private val onFileSelectedCallback: OnFileSelectedCallback = _onFileSelectedCallback

    fun sort(mode: FileSortingMode, reverse: Boolean) {
        var isChanged = false
        if (items.comparator != mode) {
            items.comparator = mode
            isChanged = true
        }
        if (items.isReversed != reverse) {
            items.isReversed = reverse
            isChanged = true
        }
        if (isChanged) {
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileRecyclerViewHolder(view) { fileData -> onFileSelectedCallback(fileData, this) }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FileRecyclerViewHolder, position: Int) =
        holder.bind(items[position])
}

typealias OnFileSelectedCallback = (FileData, FileRecyclerAdapter) -> Unit
