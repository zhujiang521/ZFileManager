package com.zj.file.type

import android.view.View
import android.widget.ImageView
import com.zj.file.R
import com.zj.file.common.ZFileType
import com.zj.file.content.getZFileConfig
import com.zj.file.content.getZFileHelp

/**
 * 其他类型的文件
 */
open class OtherType : ZFileType() {

    override fun openFile(filePath: String, view: View) {
        getZFileHelp().getFileOpenListener().openOther(filePath, view)
    }

    override fun loadingFile(filePath: String, pic: ImageView) {
        pic.setImageResource(getRes(getZFileConfig().resources.otherRes, R.drawable.ic_zfile_other))
    }
}