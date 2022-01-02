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
    }

}