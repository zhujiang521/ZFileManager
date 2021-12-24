package com.zj.file.ui

import android.os.Bundle
import android.view.View
import com.zj.file.R
import com.zj.file.common.ZFileActivity
import com.zj.file.content.getZFileHelp
import com.zj.file.content.setStatusBarTransparent
import com.zj.file.content.toFile
import kotlinx.android.synthetic.main.activity_zfile_video_play.*

internal class ZFileVideoPlayActivity : ZFileActivity() {

    override fun getContentView() = R.layout.activity_zfile_video_play

    override fun init(savedInstanceState: Bundle?) {
        setStatusBarTransparent()
        val videoPath = intent.getStringExtra("videoFilePath") ?: ""
        getZFileHelp().getImageLoadListener().loadImage(video_img, videoPath.toFile())
        videoPlayer_button.setOnClickListener { v ->
            video_player.videoPath = videoPath
            video_player.play()
            v.visibility = View.GONE
            video_img.visibility = View.GONE
        }
        video_player.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        video_img.visibility = View.VISIBLE
        super.onBackPressed()
    }
}
