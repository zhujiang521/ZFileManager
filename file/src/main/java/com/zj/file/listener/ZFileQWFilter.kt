package com.zj.file.listener

import com.zj.file.content.*
import java.io.File
import java.io.FileFilter

internal class ZFileQWFilter(private var filterArray: Array<String>) :
    FileFilter {

    override fun accept(file: File): Boolean {
        filterArray.forEach {
            if (it.isEmpty()) {
                if (acceptOther(file.name) && file.isFile) {
                    return true
                }
            } else {
                if (file.name accept it && file.isFile) {
                    return true
                }
            }
        }
        return false
    }

    // 匹配其他文件类型
    private fun acceptOther(name: String): Boolean {
        val isImage =
            name accept PNG || name accept JPG || name accept JPEG || name accept SVG || name accept GIF
        val isVideo = name accept MP4 || name accept _3GP
        val isAudio = name accept MP3 || name accept AAC || name accept WAV || name accept M4A
        val isTxt = name accept TXT || name accept XML || name accept JSON
        val isDOC = name accept DOC || name accept DOCX
        val isXLS = name accept XLS || name accept XLSX
        val isPPT = name accept PPT || name accept PPTX
        val isPDF = name accept PDF
        return !isImage && !isVideo && !isAudio && !isTxt && !isDOC && !isXLS && !isPPT && !isPDF
    }
}