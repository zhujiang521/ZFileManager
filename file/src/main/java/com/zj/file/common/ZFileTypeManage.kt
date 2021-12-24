package com.zj.file.common

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.zj.file.content.ZFileBean
import com.zj.file.content.getZFileHelp
import com.zj.file.type.OtherType

/**
 * 文件类型 管理
 */
internal class ZFileTypeManage {

    private object Builder {
        val MANAGER = ZFileTypeManage()
    }

    companion object {
        fun getTypeManager() = Builder.MANAGER
    }

    var fileType: ZFileType = OtherType()

    fun openFile(filePath: String, view: View) {
        fileType = getFileType(filePath)
        fileType.openFile(filePath, view)
    }

    fun loadingFile(filePath: String, pic: ImageView) {
        fileType = getFileType(filePath)
        fileType.loadingFile(filePath, pic)
    }

    fun infoFile(bean: ZFileBean, context: Context) {
        fileType = getFileType(bean.filePath)
        fileType.infoFile(bean, context)
    }

    fun getFileType(filePath: String) =
            getZFileHelp().getFileTypeListener().getFileType(filePath)

}