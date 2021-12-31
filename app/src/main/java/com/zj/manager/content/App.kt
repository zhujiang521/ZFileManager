package com.zj.manager.content

import android.app.Application
import com.zj.file.content.getZFileHelp
import com.zj.manager.diy.MyFileImageListener
import com.zj.manager.diy.MyFileTypeListener

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        getZFileHelp()
            .init(MyFileImageListener())
            .setFileTypeListener(MyFileTypeListener())
        initQbSdk()
    }

    private fun initQbSdk() {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        TbsInstance.getInstance().initX5Environment(applicationContext)
    }

}