package com.zj.manager.diy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zj.file.content.setAndroidNativeLightStatusBar
import com.zj.file.content.setStatusBarTransparent
import com.zj.manager.databinding.ActivitySunBinding

class SunActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySunBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()
        setAndroidNativeLightStatusBar()
        binding = ActivitySunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            sunBack.setOnClickListener {
                onBackPressed()
            }
            sunPhoneBg1.background.alpha = 100
            sunLoginBtn2.background.alpha = 100
        }
    }

}