package com.kangdroid.naviapp.data

import java.text.SimpleDateFormat
import java.util.*

data class FileData(
    var id: Long = 0,
    var fileName: String,
    var fileType: String,
    var token: String,
    var lastModifiedTime: Long
)

enum class FileType {
    Folder, File
}

fun getBriefName(file: FileData): String {
    val toSplit: Char = if (file.fileName.contains('\\')) {
        '\\'
    } else {
        '/'
    }

    val listToken: List<String> = file.fileName.split(toSplit)
    return listToken[listToken.size - 1]
}

fun getFormattedDate(file: FileData): String = SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ss.SSSSS",
    Locale.getDefault()
).format(Date(file.lastModifiedTime))