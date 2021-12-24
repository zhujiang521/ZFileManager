package com.zj.file.type

import android.view.View
import android.widget.ImageView
import com.zj.file.common.ZFileType
import com.zj.file.content.getZFileHelp
import com.zj.file.content.toFile

/**
 * 视频文件
 */
open class VideoType : ZFileType() {

    override fun openFile(filePath: String, view: View) {
        getZFileHelp().getFileOpenListener().openVideo(filePath, view)
    }

    override fun loadingFile(filePath: String, pic: ImageView) {
        getZFileHelp().getImageLoadListener().loadVideo(pic, filePath.toFile())
    }
}