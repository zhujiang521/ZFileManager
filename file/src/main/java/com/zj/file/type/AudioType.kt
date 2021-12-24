package com.zj.file.type

import android.view.View
import android.widget.ImageView
import com.zj.file.R
import com.zj.file.common.ZFileType
import com.zj.file.content.getZFileConfig
import com.zj.file.content.getZFileHelp

/**
 * 音频文件
 */
open class AudioType : ZFileType() {

    override fun openFile(filePath: String, view: View) {
        getZFileHelp().getFileOpenListener().openAudio(filePath, view)
    }

    override fun loadingFile(filePath: String, pic: ImageView) {
        val resId = getZFileConfig().resources.audioRes
        pic.setImageResource(getRes(resId, R.drawable.ic_zfile_audio))
    }

}