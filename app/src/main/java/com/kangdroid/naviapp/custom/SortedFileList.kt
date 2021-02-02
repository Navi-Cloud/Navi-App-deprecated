package com.kangdroid.naviapp.custom

import com.kangdroid.naviapp.data.FileData

class SortedFileList : ArrayList<FileData>() {
    var comparator: FileSortingMode = FileSortingMode.TypedName
        set(value) {
            field = value
            sort()
        }

    var reversed: Boolean = false
        set(value) {
            field = value
            sort()
        }

    private fun sort() {
        if (reversed) {
            sortWith(comparator)
            reverse()
        } else {
            sortWith(comparator)
        }
        println("sorted")
    }

    override fun add(element: FileData): Boolean {
        var done: Boolean = false
        for (i in indices) {
            val compare = comparator.compare(this[i], element)
            if (reversed) {
                if (compare <= 0) {
                    add(i, element)
                    done = true
                    break
                } else {
                    continue
                }
            } else if (compare >= 0) {
                add(i, element)
                done = true
                break
            } else {
                continue
            }
        }
        if (!done) {
            done = super.add(element)
        }
        return done
    }
}

sealed class FileSortingMode : Comparator<FileData> {
    object TypedName : FileSortingMode(),
        Comparator<FileData> by compareBy({ it.fileType }, { it.fileName }, { it.lastModifiedTime })

    object TypedLMT : FileSortingMode(),
        Comparator<FileData> by compareBy({ it.fileType }, { it.lastModifiedTime }, { it.fileName })

    object Name : FileSortingMode(),
        Comparator<FileData> by compareBy({ it.fileName }, { it.lastModifiedTime })

    object LMT : FileSortingMode(),
        Comparator<FileData> by compareBy({ it.lastModifiedTime }, { it.fileName })
}