package com.zj.file.common

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

internal abstract class ZFileFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    abstract fun initAll()

    override fun onResume() {
        super.onResume()
        initAll()
    }

}