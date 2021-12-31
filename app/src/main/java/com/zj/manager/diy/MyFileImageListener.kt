package com.zj.manager.diy

import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.zj.file.content.SVG
import com.zj.file.listener.ZFileImageListener
import com.zj.manager.R
import com.zj.manager.diy.svg.SvgSoftwareLayerSetter
import java.io.File

class MyFileImageListener : ZFileImageListener() {

    /**
     * 图片类型加载
     */
    override fun loadImage(imageView: ImageView, file: File) {
        if (file.name.endsWith(SVG)) {
            Glide.with(imageView.context)
                .`as`(PictureDrawable::class.java)
                .listener(SvgSoftwareLayerSetter())
                .load(file)
                .placeholder(R.drawable.ic_zfile_other)
                .error(R.drawable.ic_zfile_other)
                .into(imageView)
        } else {
            Glide.with(imageView.context)
                .load(file)
                .placeholder(R.drawable.ic_zfile_other)
                .error(R.drawable.ic_zfile_other)
                .into(imageView)
        }
    }
}