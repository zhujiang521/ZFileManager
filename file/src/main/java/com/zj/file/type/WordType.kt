package com.zj.file.type

import android.view.View
import android.widget.ImageView
import com.zj.file.R
import com.zj.file.common.ZFileType
import com.zj.file.content.getZFileConfig
import com.zj.file.content.getZFileHelp

/**
 * word文件
 */
open class WordType : ZFileType() {

    override fun openFile(filePath: String, view: View) {
        getZFileHelp().getFileOpenListener().openDOC(filePath, view)
    }

    override fun loadingFile(filePath: String, pic: ImageView) {
        pic.setImageResource(getRes(getZFileConfig().resources.wordRes, R.drawable.ic_zfile_word))
    }
}