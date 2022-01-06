package com.zj.file.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.zj.file.R
import com.zj.file.common.ZFileManageDialog
import com.zj.file.common.ZFileType
import com.zj.file.common.ZFileTypeManage
import com.zj.file.content.ZFileBean
import com.zj.file.content.ZFileInfoBean
import com.zj.file.content.setNeedWH
import com.zj.file.databinding.DialogZfileInfoBinding
import com.zj.file.type.AudioType
import com.zj.file.type.ImageType
import com.zj.file.type.VideoType
import com.zj.file.util.ZFileOtherUtil
import java.lang.ref.WeakReference

internal class ZFileInfoDialog : ZFileManageDialog(), Runnable {

    companion object {

        private const val FILE_INFO_FILE_BEAN = "fileBean"

        fun newInstance(bean: ZFileBean) = ZFileInfoDialog().apply {
            arguments = Bundle().apply { putParcelable(FILE_INFO_FILE_BEAN, bean) }
        }
    }

    private var binding: DialogZfileInfoBinding? = null
    private var handler: InfoHandler? = null
    private lateinit var thread: Thread
    private var filePath = ""
    private lateinit var fileType: ZFileType

    override fun getCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogZfileInfoBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun createDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext(), R.style.ZFile_Common_Dialog).apply {
            window?.setGravity(Gravity.CENTER)
        }

    override fun init(savedInstanceState: Bundle?) {
        val bean = arguments?.getParcelable(FILE_INFO_FILE_BEAN) ?: ZFileBean()
        filePath = bean.filePath
        fileType = ZFileTypeManage.getTypeManager().getFileType(bean.filePath)
        handler = InfoHandler(this)
        thread = Thread(this)
        thread.start()

        binding?.zfileDialogInfoFileName?.text = bean.fileName
        binding?.zfileDialogInfoFileType?.text = bean.filePath.run {
            substring(lastIndexOf(".") + 1, length)
        }
        binding?.zfileDialogInfoFileDate?.text = bean.date
        binding?.zfileDialogInfoFileSize?.text = bean.size
        binding?.zfileDialogInfoFilePath?.text = bean.filePath

        binding?.zfileDialogInfoMoreBox?.setOnClickListener {
            binding?.zfileDialogInfoMoreLayout?.isVisible =
                binding?.zfileDialogInfoMoreBox?.isChecked == true
        }
        when (fileType) {
            is ImageType -> {
                binding?.zfileDialogInfoMoreBox?.visibility = View.VISIBLE
                binding?.zfileDialogInfoFileDurationLayout?.visibility = View.GONE
                binding?.zfileDialogInfoFileOther?.setText(R.string.zfile_info_no)
                val wh = ZFileOtherUtil.getImageWH(filePath)
                binding?.zfileDialogInfoFileFBL?.text = String.format("%d * %d", wh[0], wh[1])
            }
            is AudioType -> {
                binding?.zfileDialogInfoMoreBox?.visibility = View.VISIBLE
                binding?.zfileDialogInfoFileFBLLayout?.visibility = View.GONE
                binding?.zfileDialogInfoFileOther?.setText(R.string.zfile_info_no)
            }
            is VideoType -> {
                binding?.zfileDialogInfoMoreBox?.visibility = View.VISIBLE
                binding?.zfileDialogInfoFileOther?.setText(R.string.zfile_info_no)
            }
            else -> {
                binding?.zfileDialogInfoMoreBox?.visibility = View.GONE
                binding?.zfileDialogInfoMoreLayout?.visibility = View.GONE
            }
        }
        binding?.zfileDialogInfoDown?.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        setNeedWH()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler?.removeMessages(0)
        handler?.removeCallbacks(this)
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }

    override fun run() {
        if (fileType !is AudioType && fileType !is VideoType) return
        handler?.sendMessage(Message().apply {
            what = 0
            obj = ZFileOtherUtil.getMultimediaInfo(filePath, fileType is VideoType)
        })
    }

    class InfoHandler(dialog: ZFileInfoDialog) : Handler(Looper.getMainLooper()) {
        private val week: WeakReference<ZFileInfoDialog> by lazy {
            WeakReference<ZFileInfoDialog>(dialog)
        }

        override fun handleMessage(msg: Message) {
            if (msg.what != 0) return
            val bean = msg.obj as ZFileInfoBean
            week.get()?.apply {
                when (fileType) {
                    is AudioType -> {
                        binding?.zfileDialogInfoFileDuration?.text = bean.duration
                    }
                    is VideoType -> {
                        binding?.zfileDialogInfoFileDuration?.text = bean.duration
                        binding?.zfileDialogInfoFileFBL?.text =
                            String.format("%s * %s", bean.width, bean.height)
                    }
                }
            }
        }
    }
}