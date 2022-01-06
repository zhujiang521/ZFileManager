package com.zj.file.ui

import android.os.Bundle
import android.view.View
import com.zj.file.common.ZFileActivity
import com.zj.file.content.getZFileHelp
import com.zj.file.content.setStatusBarTransparent
import com.zj.file.content.toFile
import com.zj.file.databinding.ActivityZfileVideoPlayBinding

internal class ZFileVideoPlayActivity : ZFileActivity() {

    companion object{
        const val VIDEO_FILE_PATH = "videoFilePath"
    }

    private lateinit var binding: ActivityZfileVideoPlayBinding

    override fun getContentView(): View {
        binding = ActivityZfileVideoPlayBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun init(savedInstanceState: Bundle?) {
        setStatusBarTransparent()
        val videoPath = intent.getStringExtra(VIDEO_FILE_PATH) ?: ""
        getZFileHelp().getImageLoadListener().loadImage(binding.videoImg, videoPath.toFile())
        binding.videoPlayerButton.setOnClickListener { v ->
            binding.videoPlayer.videoPath = videoPath
            binding.videoPlayer.play()
            v.visibility = View.GONE
            binding.videoImg.visibility = View.GONE
        }
        binding.videoPlayer.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        binding.videoImg.visibility = View.VISIBLE
        super.onBackPressed()
    }
}
