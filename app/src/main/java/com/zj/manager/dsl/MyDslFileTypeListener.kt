package com.zj.manager.dsl

import com.zj.file.content.JSON
import com.zj.file.content.TXT
import com.zj.file.content.XML
import com.zj.file.listener.ZFileTypeListener
import com.zj.file.type.OtherType
import com.zj.file.util.ZFileHelp
import com.zj.manager.content.Content
import com.zj.manager.diy.ApkType

class MyDslFileTypeListener : ZFileTypeListener() {

    override fun getFileType(filePath: String) =
        when (ZFileHelp.getFileTypeBySuffix(filePath)) {
            TXT, XML, JSON -> OtherType()
            Content.APK -> ApkType()
            else -> super.getFileType(filePath)
        }
}
