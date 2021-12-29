package com.zj.file.common

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.zj.file.R

internal abstract class ZFileManageDialog :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?) = createDialog(savedInstanceState)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return getCreateView(inflater,container)
    }

    abstract fun getCreateView(inflater: LayoutInflater, container: ViewGroup?): View?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(savedInstanceState)
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN)
                onBackPressed()
            else false
        }
    }

    abstract fun createDialog(savedInstanceState: Bundle?): Dialog
    abstract fun init(savedInstanceState: Bundle?)

    /**
     * 返回true 拦截，否则销毁
     */
    open fun onBackPressed() = false

}