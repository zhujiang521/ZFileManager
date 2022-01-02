package com.zj.manager.diy

import android.view.View
import android.widget.ImageView
import com.zj.file.common.ZFileType
import com.zj.file.util.showToast
import com.zj.manager.R

/**
 * 自定义Apk文件类型
 */
class ApkType : ZFileType() {

    /**
     * 打开文件
     * @param filePath  文件路径
     * @param view      当前视图
     */
    override fun openFile(filePath: String, view: View) {
        view.context.showToast("打开自定义拓展文件")
    }

    /**
     * 加载文件
     * @param filePath 文件路径
     * @param pic      文件展示的图片
     */
    override fun loadingFile(filePath: String, pic: ImageView) {
        pic.setImageResource(R.drawable.ic_zfile_apk)
    }
}