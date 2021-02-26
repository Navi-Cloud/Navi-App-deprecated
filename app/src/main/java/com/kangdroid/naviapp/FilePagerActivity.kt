package com.kangdroid.naviapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.ToggleButton
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kangdroid.naviapp.custom.FileSortingMode
import com.kangdroid.naviapp.custom.NamedClass
import com.kangdroid.naviapp.data.FileData
import com.kangdroid.naviapp.data.FileType
import com.kangdroid.naviapp.data.getBriefName
import com.kangdroid.naviapp.server.ServerManagement
import com.kangdroid.naviapp.view.FilePagerAdapter
import com.kangdroid.naviapp.view.FileRecyclerAdapter
import kotlinx.coroutines.*
import java.io.File

abstract class FilePagerActivity : AppCompatActivity(), NamedClass {
    protected lateinit var pagesVP: ViewPager2
    protected lateinit var pagerAdapter: FilePagerAdapter
    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeContentView()
        initializeToolbar()

        initializeViews()

        if (!ServerManagement.initServerCommunication()) {
            Log.wtf(className, "Server initiation failed!")
            Log.wtf(className, "This should NOT be happened!")
        }
        initializeRootPage()
    }

    override fun onBackPressed() {
        if (pagesVP.currentItem > 0) {
            pagesVP.currentItem -= 1
        } else {
            super.onBackPressed()
        }
    }

    protected abstract fun initializeContentView()
    protected abstract fun initializeToolbar()

    // returns true when handle is done.
    protected open fun onFileSelected(fileData: FileData, page: FileRecyclerAdapter): Boolean {
        if (fileData.fileType == FileType.Folder.toString()) {
            pagerAdapter.exploreFolder(fileData)
            return true
        }
        return false
    }

    protected open fun initializeViews() {
        pagesVP = findViewById<ViewPager2>(R.id.vp_pages)
        pagerAdapter = FilePagerAdapter(pagesVP, ::onFileSelected).also {
            pagesVP.adapter = it
        }
        pagesVP.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    ViewPager2.SCROLL_STATE_SETTLING -> pagesVP.isUserInputEnabled = false
                    ViewPager2.SCROLL_STATE_IDLE -> pagesVP.isUserInputEnabled = true
                }
            }
        })
        val tabs: TabLayout = findViewById(R.id.tl_path)
        TabLayoutMediator(tabs, pagesVP) { tab, position ->
            tab.text = getBriefName(pagerAdapter.pages[position].folder)
        }.attach()

        val shuffleTypeTGB: ToggleButton = findViewById(R.id.tgb_shuffle_type)
        val sortByLMTTGB: ToggleButton = findViewById(R.id.tgb_name_lmt)
        val reverseTGB: ToggleButton = findViewById(R.id.tgb_asc_dsc)

        val sortListener: (CompoundButton, Boolean) -> Unit = { _, _ ->
            pagerAdapter.sort(
                when (sortByLMTTGB.isChecked) {
                    true -> if (shuffleTypeTGB.isChecked) FileSortingMode.LMT else FileSortingMode.TypedLMT
                    false -> if (shuffleTypeTGB.isChecked) FileSortingMode.Name else FileSortingMode.TypedName
                }, reverseTGB.isChecked
            )
        }

        shuffleTypeTGB.setOnCheckedChangeListener(sortListener)
        sortByLMTTGB.setOnCheckedChangeListener(sortListener)
        reverseTGB.setOnCheckedChangeListener(sortListener)
    }

    private fun initializeRootPage() {
        coroutineScope.launch {
            val response: List<FileData> = getRootData()
            withContext(Dispatchers.Main) {
                FileRecyclerAdapter(
                    FileData(
                        0,
                        "/tmp",
                        FileType.Folder.toString(),
                        "test-token",
                        System.currentTimeMillis()
                    ), ::onFileSelected
                ).apply {
                    pagerAdapter.addPage(this)
                    pagerAdapter.notifyDataSetChanged()
                    for (data in response) {
                        Log.e("TESTING", "${File(data.fileName).isAbsolute}")
                        add(data)
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun getRootData(): List<FileData> {
        val responseList: List<FileData>

        with(ServerManagement) {
            responseList = getInsideFiles(getRootToken()) ?: run {
                Log.e(className, "Error occurred when connecting to server.")
                Log.e(className, "Returning empty list..")
                emptyList<FileData>()
            }
        }
        return responseList
    }
}
