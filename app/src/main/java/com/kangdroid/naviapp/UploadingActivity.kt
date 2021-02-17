package com.kangdroid.naviapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class UploadingActivity : MainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.uploading_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_select_path -> {
            // TODO should implement uploading functionality
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun setLayout() = setContentView(R.layout.activity_uploading)

    override fun setToolbar() {
        setSupportActionBar(findViewById(R.id.tb_upload))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.title_upload)
    }
}