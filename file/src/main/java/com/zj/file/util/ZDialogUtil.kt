package com.zj.file.util

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.zj.file.R
import com.zj.file.databinding.DialogPermissionBinding

fun Context.commonDialog(
    title: Int,
    content: Int,
    cancel: Int = R.string.zfile_cancel,
    finish: Int = R.string.zfile_menu_down,
    cancelListener: (() -> Unit)? = null,
    finishListener: () -> Unit
) {
    val builder = AlertDialog.Builder(this)
    val binding: DialogPermissionBinding =
        DialogPermissionBinding.inflate(LayoutInflater.from(this))
    builder.setView(binding.root)
    val dialog = builder.create()
    binding.apply {
        permissionTvTitle.setText(title)
        permissionTvContent.setText(content)
        permissionBtnCancel.setText(cancel)
        permissionBtnFinish.setText(finish)
        permissionBtnCancel.setOnClickListener {
            dialog.dismiss()
            cancelListener?.invoke()
        }
        permissionBtnFinish.setOnClickListener {
            dialog.dismiss()
            finishListener()
        }
    }
    dialog.show()

}