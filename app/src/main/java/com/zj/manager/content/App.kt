package com.zj.manager.content

import android.app.Application
import android.util.Log
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.zj.file.content.getZFileHelp
import com.zj.manager.diy.MyFileImageListener
import com.zj.manager.diy.MyFileTypeListener
import java.util.HashMap

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        getZFileHelp()
            .init(MyFileImageListener())
            .setFileTypeListener(MyFileTypeListener())
        //initQbSdk()
    }

    private fun initQbSdk() {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        initTbsSettings()
        //x5内核初始化接口
        QbSdk.initX5Environment(applicationContext, object : QbSdk.PreInitCallback {
            override fun onViewInitFinished(arg0: Boolean) { //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e("APPApplication", " onViewInitFinished is $arg0")
            }

            override fun onCoreInitFinished() {
                Log.e("APPApplication", " onCoreInitFinished")
            }
        })
    }

    private fun initTbsSettings() {
        val map: MutableMap<String, Any> = HashMap()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
    }


}