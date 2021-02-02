package com.kangdroid.naviapp.data

data class FileData(
    var id: Long = 0,
    var fileName: String,
    var fileType: FileType,
    var token: String,
    var lastModifiedTime: Long
)

enum class FileType {
    FOLDER, FILE
}