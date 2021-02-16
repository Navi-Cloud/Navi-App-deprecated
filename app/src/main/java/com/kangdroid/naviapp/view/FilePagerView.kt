package com.kangdroid.naviapp.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.kangdroid.naviapp.R
import com.kangdroid.naviapp.custom.FileSortingMode
import com.kangdroid.naviapp.data.FileData
import com.kangdroid.naviapp.data.FileType
import com.kangdroid.naviapp.server.ServerManagement
import kotlinx.coroutines.*
import java.io.File

class FilePagerViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val filesRV: RecyclerView = itemView.findViewById<RecyclerView>(R.id.rv_files).apply {
        layoutManager = LinearLayoutManager(context)
    }

    fun bind(files: FileRecyclerAdapter) {
        filesRV.adapter = files
    }
}

class FilePagerAdapter(private val view: ViewPager2) : RecyclerView.Adapter<FilePagerViewHolder>() {
    private val cachedPages: MutableMap<String, FileRecyclerAdapter> = mutableMapOf()
    val pages: ArrayList<FileRecyclerAdapter> = arrayListOf()
    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private var sortingMode: FileSortingMode = FileSortingMode.TypedName
    private var isReversed: Boolean = false

    init {
        view.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                sortCurrentPage()
                super.onPageSelected(position)
            }
        })
    }

    fun sort(mode: FileSortingMode, reverse: Boolean) {
        sortingMode = mode
        isReversed = reverse
        sortCurrentPage()
    }

    private fun sortCurrentPage() {
        if (view.currentItem < pages.size) {
            pages[view.currentItem].sort(sortingMode, isReversed)
        }
    }

    fun cachePage(page: FileRecyclerAdapter) {
        cachedPages[page.folder.token] = page
    }

    fun addPage(page: FileRecyclerAdapter) {
        pages.add(page)
        if (!cachedPages.containsKey(page.folder.token)) {
            cachePage(page)
        }
    }

    fun insertPage(position: Int, page: FileRecyclerAdapter) {
        while (pages.lastIndex >= position) {
            pages.removeAt(pages.lastIndex)
        }
        addPage(page)
    }

    fun exploreFolder(fileData: FileData) {
        // TODO: Do we really need "this"?
        val doWeReallyNeedThis = this
        coroutineScope.launch {
            val response: List<FileData> = ServerManagement.getInsideFiles(fileData.token) ?: run {
                Log.e("FilePagerAdapter", "Cannot Retrieve list of filedata. Returning empty file data.")
                emptyList()
            }

            withContext(Dispatchers.Main) {
                val page: FileRecyclerAdapter = cachedPages[fileData.token] ?: FileRecyclerAdapter(
                    fileData,
                    doWeReallyNeedThis
                ).also {
                    cachePage(it)
                    for (data in response) {
                        data.fileName = File(data.fileName).name
                        it.add(data)
                    }
                }
                if (pages.lastIndex <= view.currentItem || pages[view.currentItem + 1].folder.token != fileData.token) {
                    insertPage(view.currentItem + 1, page)
                    notifyDataSetChanged()
                }
                view.currentItem = view.currentItem + 1
                println(cachedPages)
                println(pages)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilePagerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_file_recycler, parent, false)
        return FilePagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilePagerViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    fun testString(input: String): String {
        var retString: String = ""
        var i: Int = input.length - 1
        while (i >= 0) {
            if (input[i] == '\\') {
                break
            } else {
                retString += input[i]
            }
            i--
        }
        return retString.reversed()
    }
}