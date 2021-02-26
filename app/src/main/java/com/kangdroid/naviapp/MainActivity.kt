package com.kangdroid.naviapp

import android.content.Intent
import android.view.Menu
import android.view.MenuItem

open class MainActivity : FilePagerActivity() {
    override val className: String = "MainActivity"

    override fun initializeContentView() = setContentView(R.layout.activity_main)

    override fun initializeToolbar() = setSupportActionBar(findViewById(R.id.tb_main))

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
        if (pagesVP.currentItem > 0) {
            pagesVP.currentItem -= 1
        } else {
            super.onBackPressed()
        }
    }
}
