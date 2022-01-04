package com.zj.file.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.zj.file.R
import com.zj.file.databinding.DialogPermissionBinding
import com.zj.file.databinding.DialogProgressBinding

/**
 * 普通Dialog
 *
 * @param title 标题
 * @param content 内容
 * @param cancel 左边文字
 * @param finish 右边文字
 * @param cancelListener 左边点击事件
 * @param finishListener 右边点击事件
 */
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

/**
 * 等待框
 *
 * @param content 显示内容资源id
 * @param cancelable 是否可以返回关闭 true 可以关闭， false不可关闭
 */
fun Context.progressDialog(
    content: Int = R.string.zfile_qw_loading,
    cancelable: Boolean = false
): Dialog {
    val builder = AlertDialog.Builder(this)
    val binding: DialogProgressBinding =
        DialogProgressBinding.inflate(LayoutInflater.from(this))
    builder.setView(binding.root)
    val dialog = builder.create()
    binding.apply {
        dialogTvContent.setText(content)
    }
    dialog.setCancelable(cancelable)
    return dialog
}