package com.zj.file.async

import android.content.Context
import com.zj.file.content.ZFileBean
import com.zj.file.content.getZFileHelp

internal class ZFileListAsync(
    context: Context,
    block: MutableList<ZFileBean>?.() -> Unit
) : ZFileAsync(context, block) {

    override fun doingWork(filePath: String?) =
        getZFileHelp().getFileLoadListener().getFileList(getContext(), filePath)

}