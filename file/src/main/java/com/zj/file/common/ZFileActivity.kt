package com.zj.file.common

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

internal abstract class ZFileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        init(savedInstanceState)
    }

    abstract fun getContentView(): View

    abstract fun init(savedInstanceState: Bundle?)


}