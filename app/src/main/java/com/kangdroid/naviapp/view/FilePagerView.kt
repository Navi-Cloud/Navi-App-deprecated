package com.kangdroid.naviapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.kangdroid.naviapp.R
import com.kangdroid.naviapp.data.FileData

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

    fun updatePage(token: String) {
        val page: FileRecyclerAdapter = cachedPages[token] ?: return    // should raise an exception
        // TODO should use method of FileRecyclerAdapter to update with fresh data
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
        val page: FileRecyclerAdapter = cachedPages[fileData.token] ?: FileRecyclerAdapter(
            fileData,
            this
        ).also { cachePage(it) }
        if (pages.lastIndex <= view.currentItem || pages[view.currentItem + 1].folder.token != fileData.token) {
            insertPage(view.currentItem + 1, page)
            notifyDataSetChanged()
        }
        view.currentItem = view.currentItem + 1
        println(cachedPages)
        println(pages)
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
}