package com.zj.file.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.zj.file.R
import com.zj.file.common.ZFileManageDialog
import com.zj.file.content.isNull
import com.zj.file.content.setNeedWH
import com.zj.file.databinding.DialogZfileRenameBinding
import com.zj.file.util.ZFileLog
import com.zj.file.util.showToast

internal class ZFileRenameDialog : ZFileManageDialog(), Runnable {

    private var binding: DialogZfileRenameBinding? = null
    var reanameDown: (String.() -> Unit)? = null
    private var handler: Handler? = null
    private var oldName = "请输入文件名称"

    companion object {

        fun newInstance(oldName: String) = ZFileRenameDialog().apply {
            arguments = Bundle().run {
                putString("oldName", oldName)
                this
            }
        }

    }

    override fun getCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogZfileRenameBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun createDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext(), R.style.ZFile_Common_Dialog).apply {
            window?.setGravity(Gravity.CENTER)
        }

    override fun init(savedInstanceState: Bundle?) {
        oldName = arguments?.getString("oldName") ?: "请输入文件名称"
        handler = Handler(Looper.getMainLooper())
        binding?.zfileDialogRenameEdit?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                rename()
            }
            true
        }
        binding?.zfileDialogRenameEdit?.hint = oldName
        binding?.zfileDialogRenameDown?.setOnClickListener {
            rename()
        }
        binding?.zfileDialogRenameCancel?.setOnClickListener {
            dismiss()
        }
    }

    private fun rename() {
        val newName = binding?.zfileDialogRenameEdit?.text.toString()
        if (newName.isNull()) {
            context?.showToast("请输入文件名")
        } else {
            if (oldName == newName) {
                ZFileLog.e("相同名字，不执行重命名操作")
                dismiss()
            } else {
                reanameDown?.invoke(newName)
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setNeedWH()
        handler?.postDelayed(this, 150)
    }

    override fun run() {
        binding?.zfileDialogRenameEdit?.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding?.zfileDialogRenameEdit, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDestroyView() {
        closeKeyboard()
        handler?.removeCallbacks(this)
        handler?.removeCallbacksAndMessages(null)
        handler = null
        super.onDestroyView()
    }

    private fun closeKeyboard() {
        val m = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val isOpen = m.isActive
        if (isOpen) {
            m.hideSoftInputFromWindow(
                (context as Activity).currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}