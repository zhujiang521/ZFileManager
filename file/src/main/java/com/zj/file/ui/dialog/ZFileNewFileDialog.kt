package com.zj.file.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.zj.file.R
import com.zj.file.common.ZFileManageDialog
import com.zj.file.content.*
import com.zj.file.content.setNeedWH
import com.zj.file.databinding.DialogZfileRenameBinding

internal class ZFileNewFileDialog : ZFileManageDialog() {

    private var binding: DialogZfileRenameBinding? = null
    var newFileDown: (Boolean.() -> Unit)? = null
    private var nowPath = ""

    companion object {

        private const val NOW_PATH = "nowPath"

        fun newInstance(nowPath: String?) = ZFileNewFileDialog().apply {
            arguments = Bundle().run {
                putString(NOW_PATH, nowPath)
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
        nowPath = arguments?.getString(NOW_PATH) ?: ""
        binding?.zfileDialogTvTitle?.setText(R.string.zfile_menu_add_file)
        binding?.zfileDialogRenameEdit?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                newFile()
            }
            true
        }
        binding?.zfileDialogRenameEdit?.setHint(R.string.zfile_dialog_rename_hint)
        binding?.zfileDialogRenameDown?.setOnClickListener {
            newFile()
            dismiss()
        }
        binding?.zfileDialogRenameCancel?.setOnClickListener {
            dismiss()
        }
    }

    private fun newFile() {
        getZFileHelp().getFileOperateListener()
            .newFile(
                nowPath, binding?.zfileDialogRenameEdit?.text?.toString() ?: "",
                requireContext(), newFileDown
            )
    }

    override fun onStart() {
        super.onStart()
        setNeedWH()
        binding?.zfileDialogRenameEdit?.showIme()
    }

    override fun onDestroyView() {
        binding?.zfileDialogRenameEdit?.hideIme()
        super.onDestroyView()
    }

}