package com.zj.file.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.zj.file.content.QW_FILE_TYPE_KEY
import com.zj.file.content.ZFILE_QW_PIC
import com.zj.file.content.ZFileConfiguration

internal abstract class ZFileFragment : Fragment() {

    // 文件类型
    protected var type = ZFILE_QW_PIC
    protected var qwFileType = ZFileConfiguration.QQ
    private var isFirstLoad = true

    abstract fun initAll()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return getCreateView(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        type = arguments?.getInt("type") ?: ZFILE_QW_PIC
        qwFileType = arguments?.getString(QW_FILE_TYPE_KEY) ?: ZFileConfiguration.QQ
    }

    abstract fun getCreateView(inflater: LayoutInflater, container: ViewGroup?): View?

    override fun onResume() {
        super.onResume()
        if (type == arguments?.getInt("type") ?: ZFILE_QW_PIC && !isFirstLoad) {
            return
        }
        initAll()
        isFirstLoad = false
    }

}