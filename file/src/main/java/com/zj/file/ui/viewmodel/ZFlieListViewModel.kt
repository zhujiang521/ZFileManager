package com.zj.file.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.zj.file.common.ZFileAdapter
import com.zj.file.content.ZFilePathBean
import com.zj.file.ui.adapter.ZFileListAdapter
import com.zj.file.util.ZFileLog
import com.zj.file.util.ZFileUtil

class ZFlieListViewModel : ViewModel() {

    var isFirstLoad = true

    var toManagerPermissionPage = false

    var barShow = false
    lateinit var filePathAdapter: ZFileAdapter<ZFilePathBean>
    var fileListAdapter: ZFileListAdapter? = null

    var index = 0
    var rootPath = "" // 根目录
    var specifyPath: String? = "" // 指定目录
    var nowPath: String? = "" // 当前目录

    var hasPermission = false

    val backList by lazy {
        ArrayList<String>()
    }

    override fun onCleared() {
        ZFileUtil.resetAll()
        super.onCleared()
        ZFileLog.e("ZFlieListViewModel:onCleared")
        fileListAdapter?.reset()
        backList.clear()
    }

}