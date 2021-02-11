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

fun getBriefName(file: FileData): String = file.fileName.let {
    if (it.contains('/')) it.split('/').let { tokens -> tokens[tokens.size - 1] } else it
}.let {
    if (file.fileType == FileType.Folder.toString()) "$it/" else it
}

fun getFormattedDate(file: FileData): String = SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ss.SSSSS",
    Locale.getDefault()
).format(Date(file.lastModifiedTime))