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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class FileRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imgFileType: ImageView = itemView.findViewById(R.id.img_file_type)
    private val tvFileName: TextView = itemView.findViewById(R.id.tv_file_name)
    private val tvLastModifiedTime: TextView =
        itemView.findViewById(R.id.tv_last_modified_time)

    fun bind(fileData: FileData) {
        imgFileType.setImageResource(
            when (fileData.fileType) {
                FileType.File.toString() -> R.drawable.ic_common_file_24
                FileType.Folder.toString() -> R.drawable.ic_common_folder_24
                else -> R.drawable.ic_common_error_24
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

    override fun onBindViewHolder(holder: FileRecyclerViewHolder, position: Int) =
        holder.bind(items[position])
}

