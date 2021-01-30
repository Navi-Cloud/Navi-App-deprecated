package com.kangdroid.naviapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kangdroid.naviapp.R

data class FileData(
        var id: Long = 0,
        var fileName: String,
        var fileType: FileType,
        var nextToken: String,
        var lastModifiedTime: String
)

enum class FileType {
    FILE, FOLDER
}

class FileRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imgFileType: ImageView = itemView.findViewById<ImageView>(R.id.img_file_type)
    private val tvFileName: TextView = itemView.findViewById<TextView>(R.id.tv_file_name)
    private val tvLastModifiedTime: TextView =
            itemView.findViewById<TextView>(R.id.tv_last_modified_time)

    fun bind(fileData: FileData) {
        imgFileType.setImageResource(
                when (fileData.fileType) {
                    FileType.FILE -> R.drawable.ic_common_file_24
                    FileType.FOLDER -> R.drawable.ic_common_folder_24
                }
        )
        tvFileName.text = fileData.fileName
        tvLastModifiedTime.text = fileData.lastModifiedTime
    }
}

class FileRecyclerAdapter(_items: MutableList<FileData> = mutableListOf<FileData>()) :
        RecyclerView.Adapter<FileRecyclerViewHolder>(), MutableCollection<FileData> by _items {
    private var items: MutableList<FileData> = _items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FileRecyclerViewHolder, position: Int) =
            holder.bind(items[position])
}
