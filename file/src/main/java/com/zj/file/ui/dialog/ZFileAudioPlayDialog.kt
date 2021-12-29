package com.zj.file.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.*
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.zj.file.R
import com.zj.file.common.ZFileManageDialog
import com.zj.file.content.setNeedWH
import com.zj.file.databinding.DialogZfileAudioPlayBinding
import com.zj.file.util.ZFileOtherUtil
import java.lang.ref.WeakReference

internal class ZFileAudioPlayDialog : ZFileManageDialog(), SeekBar.OnSeekBarChangeListener, Runnable {

    companion object {
        fun getInstance(filePath: String) = ZFileAudioPlayDialog().apply {
            arguments = Bundle().apply { putString("filePath", filePath) }
        }
    }

    private var binding: DialogZfileAudioPlayBinding? = null
    private val UNIT = -1
    private val PLAY = 0
    private val PAUSE = 1

    private var playerState = UNIT

    private var audioHandler: AudioHandler? = null
    private var mediaPlayer: MediaPlayer? = null

    private var beginTime: Long = 0
    private var falgTime: Long = 0
    private var pauseTime: Long = 0

    override fun getCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogZfileAudioPlayBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun createDialog(savedInstanceState: Bundle?) = Dialog(requireContext(), R.style.ZFile_Common_Dialog).apply {
        window?.setGravity(Gravity.CENTER)
    }

    override fun init(savedInstanceState: Bundle?) {
        audioHandler = AudioHandler(this)
        initPlayer()
        binding?.dialogZfileAudioPlay?.setOnClickListener {
            when (playerState) {
                PAUSE -> {
                    startPlay()
                    falgTime = SystemClock.elapsedRealtime()
                    beginTime = falgTime - binding?.dialogZfileAudioBar?.progress!!
                    binding?.dialogZfileAudioNowTime?.base = beginTime
                    binding?.dialogZfileAudioNowTime?.start()
                }
                PLAY -> {
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.pause()
                        playerState = PAUSE
                        binding?.dialogZfileAudioNowTime?.stop()
                        pauseTime = SystemClock.elapsedRealtime()
                        binding?.dialogZfileAudioPlay?.setImageResource(R.drawable.zfile_play)
                    }
                }
                else -> {
                    initPlayer()
                }
            }
        }
        binding?.dialogZfileAudioBar?.setOnSeekBarChangeListener(this)
        binding?.dialogZfileAudioName?.text = arguments?.getString("filePath")?.let {
            it.substring(it.lastIndexOf("/") + 1, it.length)
        }
    }

    override fun onStart() {
        super.onStart()
        setNeedWH()
    }

    private fun initPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(arguments?.getString("filePath"))
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener { play ->
            binding?.dialogZfileAudioBar?.max = play.duration
            audioHandler?.post(this)
            binding?.dialogZfileAudioCountTime?.text = ZFileOtherUtil.secToTime(play.duration / 1000)

            // 设置运动时间
            falgTime = SystemClock.elapsedRealtime()
            pauseTime = 0
            binding?.dialogZfileAudioNowTime?.base = falgTime
            binding?.dialogZfileAudioNowTime?.start()

            startPlay()
        }
        mediaPlayer?.setOnCompletionListener {
            stopPlay()
            binding?.dialogZfileAudioBar?.isEnabled = false
            binding?.dialogZfileAudioBar?.progress = 0
            binding?.dialogZfileAudioNowTime?.base = SystemClock.elapsedRealtime()
            binding?.dialogZfileAudioNowTime?.start()
            binding?.dialogZfileAudioNowTime?.stop()
        }
    }

    // 开始播放
    private fun startPlay() {
        mediaPlayer?.start()
        playerState = PLAY
        binding?.dialogZfileAudioPlay?.setImageResource(R.drawable.zfile_pause)
        binding?.dialogZfileAudioBar?.isEnabled = true
    }

    // 停止播放
    private fun stopPlay() {
        binding?.dialogZfileAudioPlay?.setImageResource(R.drawable.zfile_play)
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = UNIT
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser && mediaPlayer != null) {
            mediaPlayer?.seekTo(progress)
            falgTime = SystemClock.elapsedRealtime()
            beginTime = falgTime - seekBar.progress
            binding?.dialogZfileAudioNowTime?.base = beginTime
            binding?.dialogZfileAudioNowTime?.start()
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
    override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (mediaPlayer?.isPlaying == true) mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = UNIT
        audioHandler?.removeMessages(0)
        audioHandler?.removeCallbacks(this)
        audioHandler?.removeCallbacksAndMessages(null)
        audioHandler = null
    }

    override fun run() {
        // 获得歌曲现在播放位置并设置成播放进度条的值
        if (mediaPlayer != null) {
            audioHandler?.sendEmptyMessage(0)
            audioHandler?.postDelayed(this, 100)
        }
    }

    class AudioHandler(dialog: ZFileAudioPlayDialog) : Handler(Looper.getMainLooper()) {
        private val week: WeakReference<ZFileAudioPlayDialog> by lazy {
            WeakReference<ZFileAudioPlayDialog>(dialog)
        }

        override fun handleMessage(msg: Message) {
            week.get()?.binding?.dialogZfileAudioBar?.progress = week.get()?.mediaPlayer?.currentPosition ?: 0
        }
    }

}