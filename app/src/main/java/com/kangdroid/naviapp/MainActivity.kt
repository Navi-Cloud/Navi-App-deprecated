package com.kangdroid.naviapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.util.Log
import android.widget.CompoundButton
import android.widget.ToggleButton
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kangdroid.naviapp.custom.FileSortingMode
import com.kangdroid.naviapp.data.FileData
import com.kangdroid.naviapp.data.FileType
import com.kangdroid.naviapp.data.getBriefName
import com.kangdroid.naviapp.server.ServerManagement
import com.kangdroid.naviapp.view.FilePagerAdapter
import com.kangdroid.naviapp.view.FileRecyclerAdapter
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var pagesVP: ViewPager2
    private val MAIN_UI: String = "MainActivity"
    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.tb_main))
        if (!ServerManagement.initServerCommunication()) {
            Log.wtf("MainActivity", "Server initiation failed!")
            Log.wtf("MainActivity", "This should NOT be happened!")
        }

        pagesVP = findViewById(R.id.vp_test)

        val pagerAdapter: FilePagerAdapter = FilePagerAdapter(pagesVP).also {
            pagesVP.adapter = it
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

        coroutineScope.launch {
            val response: List<FileData> = initData()
            withContext(Dispatchers.Main) {
                FileRecyclerAdapter(
                    FileData(
                        0,
                        "root",
                        FileType.Folder.toString(),
                        "test-token",
                        System.currentTimeMillis()
                    ), pagerAdapter
                ).apply{
                    pagerAdapter.addPage(this)
                    pagerAdapter.notifyDataSetChanged()
                    for (data in response){
                        data.fileName = testString(File(data.fileName).name)
                        Log.e("TESTING", "${File(data.fileName).isAbsolute}")
                        add(data)
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (pagesVP.currentItem > 0) {
            pagesVP.currentItem -= 1
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_action, menu)
        return true
    }
    private fun initData(): List<FileData> {
        val responseList: List<FileData>

        with(ServerManagement) {
            responseList = getInsideFiles(getRootToken()) ?: run {
                Log.e(MAIN_UI, "Error occurred when connecting to server.")
                Log.e(MAIN_UI, "Returning empty list..")
                emptyList<FileData>()
            }
        }
        return responseList
    }

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
