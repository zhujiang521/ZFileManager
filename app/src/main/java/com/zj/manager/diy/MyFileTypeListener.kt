package com.zj.manager.diy

import com.zj.file.content.JSON
import com.zj.file.content.TXT
import com.zj.file.content.XML
import com.zj.file.listener.ZFileTypeListener
import com.zj.file.util.ZFileHelp
import com.zj.manager.content.Content

class MyFileTypeListener : ZFileTypeListener() {

    override fun getFileType(filePath: String) =
        when (ZFileHelp.getFileTypeBySuffix(filePath)) {
            TXT, XML, JSON -> MyTxtType()
            Content.APK -> ApkType()
            else -> super.getFileType(filePath)
        }
}