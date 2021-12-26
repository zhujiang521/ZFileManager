package com.zj.manager.diy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zj.manager.R
import kotlinx.android.synthetic.main.activity_sun.*

class SunActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sun)
        sun_back.setOnClickListener {
            onBackPressed()
        }
        sun_phoneBg1.background.alpha = 100
        sun_loginBtn2.background.alpha = 100
//        sun_videoPlayer.sizeType = ZFileVideoPlayer.CENTER_CROP_MODE
//        sun_videoPlayer.videoPlayError = {
//            Toast.makeText(this@SunActivity.applicationContext, "播放失败", Toast.LENGTH_SHORT).show()
//        }
//        sun_videoPlayer.assetsVideoName = "sun.mp4"
//        sun_videoPlayer.post {
//            sun_videoPlayer.play()
//        }
    }

    override fun onResume() {
        super.onResume()
//        if (sun_videoPlayer.isPause()) sun_videoPlayer.play()
    }

    override fun onPause() {
        super.onPause()
//        if (sun_videoPlayer.isPlaying()) sun_videoPlayer.pause()
    }

}