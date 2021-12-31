package com.zj.file.util

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.content.FileProvider
import com.tencent.smtt.sdk.QbSdk
import com.zj.file.R
import com.zj.file.content.getZFileConfig
import com.zj.file.content.isDarkMode
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
        openOtherFile(context, filePath) {
            // 腾讯TBS打开失败的话尝试调用本地应用
            try {
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
            } catch (e: Exception) {
                e.printStackTrace()
                ZFileLog.e("ZFileConfiguration.authority 未设置？？？")
                context.showToast(R.string.zfile_open_file_fail)
            }
        }
    }

    fun openOtherFile(context: Context, path: String, failedListener: () -> Unit) {
        QbSdk.canOpenFile(context, path) { canOpenFile ->
            if (canOpenFile) {
                ZFileLog.e("APPApplication", "openOtherFile: canOpenFile:$canOpenFile")
                openFile(context, path)
            } else {
                failedListener()
            }
        }
    }

    private fun openFile(context: Context, path: String) {
        val params: HashMap<String, String> = HashMap()
        //“0”表示文件查看器使用默认的UI 样式。“1”表示文件查看器使用微信的UI 样式。不设置此key或设置错误值，则为默认UI 样式。
        params["style"] = "1"
        //“true”表示是进入文件查看器，如果不设置或设置为“false”，则进入miniqb 浏览器模式。不是必须设置项
        params["local"] = "true"
        //定制文件查看器的顶部栏背景色。格式为“#xxxxxx”，例“#2CFC47”;不设置此key 或设置错误值，则为默认UI 样式。
        if (context.isDarkMode()) {
            // 深色模式
            params["topBarBgColor"] = "#000000"
        } else {
            // 浅色模式
            params["topBarBgColor"] = "#03A9F4"
        }
        QbSdk.openFileReader(context, path, params) { }
    }

}