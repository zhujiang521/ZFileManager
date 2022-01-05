package com.zj.file.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zj.file.content.getZFileHelp
import com.zj.file.listener.ZFileSelectResultListener
import com.zj.file.ui.viewmodel.ZFileProxyViewModel

internal class ZFileProxyFragment : Fragment() {

    companion object {
        internal const val TAG = "ZFileProxyFragment"
    }

    private lateinit var startActivitylaunch: ActivityResultLauncher<Intent>
    private val mViewModel by viewModels<ZFileProxyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivitylaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                //此处是跳转的result回调方法
                val list = getZFileHelp().getSelectData(it.resultCode, it.data)
                Log.e(TAG, "onCreate: 大爷的:${mViewModel.resultListener}")
                mViewModel.resultListener?.selectResult(list)
            }
    }

    fun jump(data: Intent, resultListener: ZFileSelectResultListener) {
        mViewModel.resultListener = resultListener
        startActivitylaunch.launch(data)
    }

}