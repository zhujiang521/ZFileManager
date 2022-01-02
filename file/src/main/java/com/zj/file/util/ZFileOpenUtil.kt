package com.zj.file.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.FileProvider
import com.zj.file.R
import com.zj.file.content.getFileType
import com.zj.file.content.getZFileConfig
import java.io.File


/**
 * 文件打开帮助类
 */
internal object ZFileOpenUtil {

    private const val TXT = "text/plain"
    private const val ZIP = "application/x-zip-compressed"

    // Word
    private const val DOC = "application/msword"

    // excel
    private const val XLS = "application/vnd.ms-excel"

    // ppt
    private const val PPT = "application/vnd.ms-powerpoint"

    // pdf
    private const val PDF = "application/pdf"

    fun openTXT(filePath: String, view: View) {
        open(filePath, TXT, view.context)
    }

    fun openZIP(filePath: String, view: View) {
        open(filePath, ZIP, view.context)
    }

    fun openDOC(filePath: String, view: View) {
        open(filePath, DOC, view.context)
    }

    fun openXLS(filePath: String, view: View) {
        open(filePath, XLS, view.context)
    }

    fun openPPT(filePath: String, view: View) {
        open(filePath, PPT, view.context)
    }

    fun openPDF(filePath: String, view: View) {
        open(filePath, PDF, view.context)
    }

    private fun open(filePath: String, type: String, context: Context) {
        // 腾讯TBS打开失败的话尝试调用本地应用
        try {
            context.commonDialog(R.string.zfile_dialog_preview, R.string.zfile_dialog_preview_tip) {
                try {
                    openDefaultApp(context, filePath, type)
                } catch (e: Exception) {
                    e.printStackTrace()
                    openAppStore(filePath, context)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ZFileLog.e("ZFileConfiguration.authority 未设置？？？")
            openAppStore(filePath, context)
        }
    }

    fun openAppStore(filePath: String, context: Context) {
        context.commonDialog(
            R.string.zfile_dialog_preview,
            R.string.zfile_dialog_open_preview_tip
        ) {
            val fileType = filePath.getFileType()
            try {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("market://search?q=$fileType")
                context.startActivity(i)
            } catch (e: Exception) {
                context.showToast(R.string.zfile_dialog_no_store)
                e.printStackTrace()
            }
        }
    }

    private fun openDefaultApp(
        context: Context,
        filePath: String,
        type: String
    ) {
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            addCategory("android.intent.category.DEFAULT")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val contentUri = FileProvider.getUriForFile(
                context,
                getZFileConfig().authority, File(filePath)
            )
            setDataAndType(contentUri, type)
        })
    }

}