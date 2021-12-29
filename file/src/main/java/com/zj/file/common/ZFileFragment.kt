package com.zj.file.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

internal abstract class ZFileFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    abstract fun initAll()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return getCreateView(inflater,container)
    }

    abstract fun getCreateView(inflater: LayoutInflater, container: ViewGroup?): View?

    override fun onResume() {
        super.onResume()
        initAll()
    }

}