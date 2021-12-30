package com.zj.manager.diy

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.zj.file.listener.ZFileImageListener
import com.zj.manager.R
import java.io.File

class MyFileImageListener : ZFileImageListener() {

    /**
     * 图片类型加载
     */
    override fun loadImage(imageView: ImageView, file: File) {
        Glide.with(imageView.context)
            .load(file)
            .placeholder(R.drawable.ic_zfile_other)
            .error(R.drawable.ic_zfile_other)
            .into(imageView)
    }
}