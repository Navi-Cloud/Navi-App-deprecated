package com.kangdroid.naviapp

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kangdroid.naviapp.data.FileData
import com.kangdroid.naviapp.data.getBriefName
import com.kangdroid.naviapp.view.FileRecyclerAdapter

open class MainActivity : FilePagerActivity() {
    override val className: String = "MainActivity"

    private lateinit var fileInteractBSB: BottomSheetBehavior<*>
    private var fileInteractBS = object {
        val fileInteractLL: LinearLayout by lazy { findViewById(R.id.ll_file_interact) }
        val peekLL: LinearLayout by lazy { findViewById(R.id.ll_peek) }
        val infoLL: LinearLayout by lazy { findViewById(R.id.ll_selected_info) }
        val briefNameTV: TextView by lazy { findViewById(R.id.tv_selected_brief_name) }
        val nameTV: TextView by lazy { findViewById(R.id.tv_selected_name) }
        val downloadBTN: Button by lazy { findViewById(R.id.btn_download) }
    }

    override fun initializeContentView() = setContentView(R.layout.activity_main)

    override fun initializeToolbar() = setSupportActionBar(findViewById(R.id.tb_main))

    override fun initializeViews() {
        super.initializeViews()

        fileInteractBSB = BottomSheetBehavior.from(fileInteractBS.fileInteractLL).apply {
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // TODO should implement event listener
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // TODO should implement event listener
                }
            })
            fileInteractBS.peekLL.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            )
            setPeekHeight(fileInteractBS.peekLL.measuredHeight, true)
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        fileInteractBS.downloadBTN.setOnClickListener {
            // TODO should implement download path selection and download
        }
    }


    override fun onFileSelected(fileData: FileData, page: FileRecyclerAdapter): Boolean {
        if (!super.onFileSelected(fileData, page)) {
            if (fileInteractBSB.state == BottomSheetBehavior.STATE_HIDDEN) {
                fileInteractBSB.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            fileInteractBS.briefNameTV.text = getBriefName(fileData)
            fileInteractBS.nameTV.text = fileData.fileName
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_upload -> {
            startActivity(Intent(this, UploadingActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_action, menu)
        return true
    }

    override fun onBackPressed() {
        if (fileInteractBSB.state == BottomSheetBehavior.STATE_EXPANDED) {
            fileInteractBSB.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }
}
