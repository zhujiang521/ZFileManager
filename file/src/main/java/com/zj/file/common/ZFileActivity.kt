package com.zj.file.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zj.file.R

internal abstract class ZFileActivity : AppCompatActivity(R.layout.fragment_zfile_qw) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        init(savedInstanceState)
    }

    abstract fun getContentView(): Int
    abstract fun init(savedInstanceState: Bundle?)


}