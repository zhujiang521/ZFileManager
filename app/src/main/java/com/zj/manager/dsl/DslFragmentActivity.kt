package com.zj.manager.dsl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zj.manager.R
import com.zj.manager.diy.MyFileTypeListener
import com.zj.file.content.getZFileHelp

class DslFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dsl_fragment)
        supportFragmentManager.beginTransaction()
            .add(R.id.dsl_fragmentLayout, DslFragment(), "DslFragment")
            .commit()

    }

    override fun onDestroy() {
        super.onDestroy()
        // 该页面演示结束后保证其他页面不受影响
        getZFileHelp().setFileTypeListener(MyFileTypeListener())
    }
}