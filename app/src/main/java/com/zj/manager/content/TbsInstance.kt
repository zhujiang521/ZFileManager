package com.zj.manager.content

import android.content.Context
import android.util.Log
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsDownloader
import com.tencent.smtt.sdk.TbsListener

class TbsInstance {

    fun initX5Environment(context: Context) {
        try {
            val map = HashMap<String, Any>()
            map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
            map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
            if (!QbSdk.getTBSInstalling())
                map[TbsCoreSettings.TBS_SETTINGS_USE_PRIVATE_CLASSLOADER] = true

            QbSdk.initTbsSettings(map)

            QbSdk.setDownloadWithoutWifi(true)
            QbSdk.setTbsListener(object : TbsListener {
                override fun onDownloadFinish(i: Int) {
                    Log.e(
                        TAG, "QbSdk onDownloadFinish -->下载X5内核完成：" + i
                                + " isDownloading:" + TbsDownloader.isDownloading()
                    )
                    //[首次下载]
                    if (i != 100 && !TbsDownloader.isDownloading()) {
                        QbSdk.reset(context)
                        TbsDownloader.startDownload(context)
                    }

                }

                override fun onInstallFinish(i: Int) {
                    Log.e(TAG, "QbSdk onInstallFinish -->安装X5内核进度：$i")
                }

                override fun onDownloadProgress(i: Int) {
                    Log.e(TAG, "QbSdk onDownloadProgress -->下载X5内核进度：$i")
                }
            })

            val preInitCallback = object : QbSdk.PreInitCallback {
                override fun onCoreInitFinished() {
                    Log.e(TAG, "QbSdk onCoreInitFinished")
                }

                override fun onViewInitFinished(arg0: Boolean) {
                    Log.e(
                        TAG, "QbSdk onViewInitFinished -->内核加载 arg0:" + arg0
                                + " isDownloading:" + TbsDownloader.isDownloading()
                    )
                    //[存储合成标记 意义不大]
                    if (!arg0 && !TbsDownloader.isDownloading()) {
                        QbSdk.reset(context)
                        TbsDownloader.startDownload(context)
                    }
                }
            }
            QbSdk.initX5Environment(context, preInitCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {

        const val TAG = "QbSdk"

        @Volatile
        private var tbsInstance: TbsInstance? = null

        fun getInstance(): TbsInstance =
            tbsInstance ?: synchronized(this) {
                tbsInstance ?: createInstance().also {
                    tbsInstance = it
                }
            }

        private fun createInstance(): TbsInstance = TbsInstance()
    }
}
