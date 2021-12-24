package com.zj.file.type

import android.view.View
import android.widget.ImageView
import com.zj.file.R
import com.zj.file.common.ZFileType
import com.zj.file.content.getZFileConfig
import com.zj.file.content.getZFileHelp

/**
 * PDF文件
 */
open class PdfType : ZFileType() {

    override fun openFile(filePath: String, view: View) {
        getZFileHelp().getFileOpenListener().openPDF(filePath, view)
    }

    override fun loadingFile(filePath: String, pic: ImageView) {
        pic.setImageResource(getRes(getZFileConfig().resources.pdfRes, R.drawable.ic_zfile_pdf))
    }
}