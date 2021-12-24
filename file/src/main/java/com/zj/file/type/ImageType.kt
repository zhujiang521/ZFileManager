package com.zj.file.type

import android.view.View
import android.widget.ImageView
import com.zj.file.common.ZFileType
import com.zj.file.content.getZFileHelp
import com.zj.file.content.toFile

/**
 * 图片文件
 */
open class ImageType : ZFileType() {

    override fun openFile(filePath: String, view: View) {
        getZFileHelp().getFileOpenListener().openImage(filePath, view)
    }

    override fun loadingFile(filePath: String, pic: ImageView) {
        getZFileHelp().getImageLoadListener().loadImage(pic, filePath.toFile())
    }

}