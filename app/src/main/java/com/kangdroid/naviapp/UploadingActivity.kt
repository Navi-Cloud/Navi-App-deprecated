package com.kangdroid.naviapp

import android.util.Log
import android.view.Menu
import android.view.MenuItem

class UploadingActivity : FilePagerActivity() {
    override val className: String = "UploadingActivity"

    override fun initializeContentView() = setContentView(R.layout.activity_uploading)

    override fun initializeToolbar() {
        setSupportActionBar(findViewById(R.id.tb_upload))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.title_upload)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.uploading_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_select_path -> {
            // TODO should implement uploading functionality
            Log.d("PATH", pagerAdapter.pages[pagesVP.currentItem].folder.fileName)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}