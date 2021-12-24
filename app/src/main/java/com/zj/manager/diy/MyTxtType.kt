package com.zj.manager.diy

import android.widget.ImageView
import com.zj.manager.R
import com.zj.file.type.TxtType

/**
 * 改变 txt 原本显示的图标
 */
class MyTxtType : TxtType() {

    override fun loadingFile(filePath: String, pic: ImageView) {
        pic.setImageResource(R.drawable.ic_my_txt)
    }

}