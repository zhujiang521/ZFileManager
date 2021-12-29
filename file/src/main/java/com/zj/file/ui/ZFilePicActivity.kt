package com.zj.file.ui

import android.os.Bundle
import android.view.View
import com.zj.file.common.ZFileActivity
import com.zj.file.common.ZFileTypeManage
import com.zj.file.content.setStatusBarTransparent
import com.zj.file.databinding.ActivityZfilePicBinding

internal class ZFilePicActivity : ZFileActivity() {

    private lateinit var binding: ActivityZfilePicBinding

    override fun getContentView(): View {
        binding = ActivityZfilePicBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun init(savedInstanceState: Bundle?) {
        setStatusBarTransparent()
        val filePath = intent.getStringExtra("picFilePath") ?: ""
        ZFileTypeManage.getTypeManager().loadingFile(filePath, binding.zfilePicShow)
        binding.zfilePicShow.setOnClickListener { onBackPressed() }
    }
}
