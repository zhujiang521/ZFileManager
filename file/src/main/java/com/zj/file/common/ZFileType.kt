package com.zj.file.common

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.zj.file.content.ZFILE_DEFAULT
import com.zj.file.content.ZFileBean
import com.zj.file.content.getZFileHelp

/**
 * 可扩展成自定义的文件类型
 */
abstract class ZFileType {

    /**
     * 打开文件
     * @param filePath  文件路径
     * @param view      当前视图  (view.context as? xxxActivity)?.let {  具体逻辑实现  }
     */
    abstract fun openFile(filePath: String, view: View)

    /**
     * 加载文件
     * @param filePath 文件路径
     * @param pic      文件展示的图片
     */
    abstract fun loadingFile(filePath: String, pic: ImageView)

    /**
     * 文件详情
     */
    open fun infoFile(bean: ZFileBean, context: Context) {
        getZFileHelp().getFileOperateListener().fileInfo(bean, context)
    }

    protected fun getRes(res: Int, defaultRes: Int) = if (res == ZFILE_DEFAULT) defaultRes else res
}