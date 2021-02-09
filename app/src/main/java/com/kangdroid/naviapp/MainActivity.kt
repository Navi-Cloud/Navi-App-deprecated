package com.kangdroid.naviapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ToggleButton
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kangdroid.naviapp.custom.FileSortingMode
import com.kangdroid.naviapp.data.FileData
import com.kangdroid.naviapp.data.FileType
import com.kangdroid.naviapp.data.getBriefName
import com.kangdroid.naviapp.view.FilePagerAdapter
import com.kangdroid.naviapp.view.FileRecyclerAdapter

class MainActivity : AppCompatActivity() {

    private var sortReverse: Boolean = false
    private lateinit var sortMode: FileSortingMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pages: ViewPager2 = findViewById(R.id.vp_test)

        val pagerAdapter: FilePagerAdapter = FilePagerAdapter(pages).also {
            pages.adapter = it
        }

        val fileRecyclerAdapter = FileRecyclerAdapter(
            FileData(
                0,
                "root",
                FileType.FOLDER,
                "test-token",
                System.currentTimeMillis()
            ), pagerAdapter
        ).apply {
            pagerAdapter.addPage(this)
            pagerAdapter.notifyDataSetChanged()
            add(FileData(1, "TestDirectory", FileType.FOLDER, "test-token", System.currentTimeMillis()))
            notifyDataSetChanged()
        }

        val tabs: TabLayout = findViewById(R.id.tl_test)
        TabLayoutMediator(tabs, pages) { tab, position ->
            tab.text = getBriefName(pagerAdapter.pages[position].folder)
        }.attach()

        findViewById<ToggleButton>(R.id.tgb_asc_dsc).setOnCheckedChangeListener { _, isChecked ->
            sortReverse = isChecked
        }

        val shuffleTypeTGB: ToggleButton = findViewById(R.id.tgb_shuffle_type)
        val sortByLMTTGB: ToggleButton = findViewById(R.id.tgb_name_lmt)

        shuffleTypeTGB.setOnCheckedChangeListener { _, isChecked ->
            when (sortByLMTTGB.isChecked) {
                true -> fileAdapter.setSortingMode(if (isChecked) FileSortingMode.LMT else FileSortingMode.TypedLMT)
                false -> fileAdapter.setSortingMode(if (isChecked) FileSortingMode.Name else FileSortingMode.TypedName)
            }
        }

        sortByLMTTGB.setOnCheckedChangeListener { _, isChecked ->
            when (shuffleTypeTGB.isChecked) {
                true -> fileAdapter.setSortingMode(if (isChecked) FileSortingMode.LMT else FileSortingMode.Name)
                false -> fileAdapter.setSortingMode(if (isChecked) FileSortingMode.TypedLMT else FileSortingMode.TypedName)
            }
        }
    }
}