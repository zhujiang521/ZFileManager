package com.zj.file.ui.viewmodel

import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zj.file.content.ZFileBean
import com.zj.file.util.ZFileUtil

class ZFileQWViewModel : ViewModel() {

    var type: String? = null
    var toManagerPermissionPage = false
    val list = ArrayList<Fragment>()
    lateinit var adapter: FragmentStateAdapter

    val selectArray by lazy {
        ArrayMap<String, ZFileBean>()
    }

    var isManage = false

    override fun onCleared() {
        super.onCleared()
        isManage = false
        selectArray.clear()
        ZFileUtil.resetAll()
    }

}
