package com.zj.manager.diy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zj.file.content.setAndroidNativeLightStatusBar
import com.zj.file.content.setStatusBarTransparent
import com.zj.manager.R
import kotlinx.android.synthetic.main.activity_sun.*

class SunActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setAndroidNativeLightStatusBar()
        setContentView(R.layout.activity_sun)
        sun_back.setOnClickListener {
            onBackPressed()
        }
        sun_phoneBg1.background.alpha = 100
        sun_loginBtn2.background.alpha = 100
    }

}