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

class FileRecyclerViewHolder(itemView: View, private val pagerAdapter: FilePagerAdapter) : RecyclerView.ViewHolder(itemView) {
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
        tvFileName.text = getBriefName(fileData)
        tvLastModifiedTime.text = getFormattedDate(fileData)

        itemView.setOnClickListener {
            when(fileData.fileType) {
                FileType.FILE -> return@setOnClickListener // TODO should implement this too
                FileType.FOLDER -> pagerAdapter.explorePage(FileRecyclerAdapter(fileData, pagerAdapter))
            }
        }
    }
}

class FileRecyclerAdapter(val folder: FileData, private val pagerAdapter: FilePagerAdapter, _items: SortedFileList = SortedFileList()) :
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
        return FileRecyclerViewHolder(view, pagerAdapter)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FileRecyclerViewHolder, position: Int) =
        holder.bind(items[position])
}

