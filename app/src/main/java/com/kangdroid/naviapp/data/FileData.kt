package com.kangdroid.naviapp.data

import java.io.File

data class FileData(
    var id: Long = 0,
    var fileName: String,
    var fileType: FileType,
    var token: String,
    var prevToken: String,
    var lastModifiedTime: Long
)

// TODO: Match predefined FileData + FileType ENUM Integration
data class FileResponseDTO(
    var id: Long = 0,
    var fileName: String,
    var fileType: String,
    var mimeType: String,
    var token: String,
    var prevToken: String,
    var lastModifiedTime: Long,
    var fileCreatedDate: String,
    var fileSize: String
) {
    fun toFileData(): FileData {
        return FileData(
            id = id,
            fileName = File(fileName).name,
            fileType = if (fileType == "Folder") FileType.FOLDER else FileType.FILE,
            token = token,
            prevToken = prevToken,
            lastModifiedTime = lastModifiedTime
        )
    }
}

enum class FileType {
    FOLDER, FILE
}