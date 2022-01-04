package com.zj.file.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.zj.file.content.getZFileHelp
import com.zj.file.listener.ZFileSelectResultListener

internal class ZFileProxyFragment : Fragment() {

    companion object {
        internal const val TAG = "ZFileProxyFragment"
    }

    private var resultListener: ZFileSelectResultListener? = null
    private lateinit var startActivitylaunch: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivitylaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                //此处是跳转的result回调方法
                val list = getZFileHelp().getSelectData(it.resultCode, it.data)
                resultListener?.selectResult(list)
            }
    }

    fun jump(data: Intent, resultListener: ZFileSelectResultListener) {
        this.resultListener = resultListener
        startActivitylaunch.launch(data)
    }

}