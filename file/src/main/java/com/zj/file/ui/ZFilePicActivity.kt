package com.zj.file.ui

import android.os.Bundle
import com.zj.file.R
import com.zj.file.common.ZFileActivity
import com.zj.file.common.ZFileTypeManage
import com.zj.file.content.setStatusBarTransparent
import kotlinx.android.synthetic.main.activity_zfile_pic.*

internal class ZFilePicActivity : ZFileActivity() {

    override fun getContentView() = R.layout.activity_zfile_pic

    override fun init(savedInstanceState: Bundle?) {
        setStatusBarTransparent()
        val filePath = intent.getStringExtra("picFilePath") ?: ""
        ZFileTypeManage.getTypeManager().loadingFile(filePath, zfile_pic_show)
        zfile_pic_show.setOnClickListener { onBackPressed() }
    }
}
