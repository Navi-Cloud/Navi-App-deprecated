package com.kangdroid.naviapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kangdroid.naviapp.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

data class FileData(
    var id: Long = 0,
    var fileName: String,
    var fileType: FileType,
    var token: String,
    var lastModifiedTime: Long
)

enum class FileType {
    FOLDER, FILE
}

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

class SortedFileList : ArrayList<FileData>() {
    var comparator: FileSortingMode = FileSortingMode.TypedName
        set(value) {
            field = value
            sort()
        }

    var reversed: Boolean = false
        set(value) {
            field = value
            sort()
        }

    private fun sort() {
        if (reversed) {
            sortWith(comparator)
            reverse()
        } else {
            sortWith(comparator)
        }
        println("sorted")
    }

    override fun add(element: FileData): Boolean {
        var done: Boolean = false
        for (i in indices) {
            val compare = comparator.compare(this[i], element)
            if (reversed) {
                if (compare <= 0) {
                    add(i, element)
                    done = true
                    break
                } else {
                    continue
                }
            } else if (compare >= 0) {
                add(i, element)
                done = true
                break
            } else {
                continue
            }
        }
        if (!done) {
            done = super.add(element)
        }
        return done
    }
}

sealed class FileSortingMode : Comparator<FileData> {
    object TypedName : FileSortingMode(),
        Comparator<FileData> by compareBy({ it.fileType }, { it.fileName }, { it.lastModifiedTime })

    object TypedLMT : FileSortingMode(),
        Comparator<FileData> by compareBy({ it.fileType }, { it.lastModifiedTime }, { it.fileName })

    object Name : FileSortingMode(),
        Comparator<FileData> by compareBy({ it.fileName }, { it.lastModifiedTime })

    object LMT : FileSortingMode(),
        Comparator<FileData> by compareBy({ it.lastModifiedTime }, { it.fileName })
}