package com.kangdroid.naviapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
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
    private lateinit var pagesVP: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pagesVP = findViewById(R.id.vp_test)

        val pagerAdapter: FilePagerAdapter = FilePagerAdapter(pagesVP).also {
            pagesVP.adapter = it
        }

        FileRecyclerAdapter(
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
            add(
                FileData(
                    1,
                    "TestDirectory1",
                    FileType.FOLDER,
                    "test-token1",
                    System.currentTimeMillis()
                )
            )
            add(
                FileData(
                    2,
                    "TestDirectory2",
                    FileType.FOLDER,
                    "test-token2",
                    System.currentTimeMillis()
                )
            )
            notifyDataSetChanged()
        }

        val tabs: TabLayout = findViewById(R.id.tl_test)
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

    override fun onBackPressed() {
        if (pagesVP.currentItem > 0) {
            pagesVP.currentItem -= 1
        } else {
            super.onBackPressed()
        }
    }
}