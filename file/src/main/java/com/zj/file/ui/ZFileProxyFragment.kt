package com.zj.file.ui

import android.content.Intent
import androidx.fragment.app.Fragment
import com.zj.file.content.getZFileHelp
import com.zj.file.listener.ZFileSelectResultListener

internal class ZFileProxyFragment : Fragment() {

    companion object {
        internal const val TAG = "ZFileProxyFragment"
    }

    private var resultListener: ZFileSelectResultListener? = null

    fun jump(requestCode: Int, data: Intent, resultListener: ZFileSelectResultListener) {
        this.resultListener = resultListener
        startActivityForResult(data, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val list = getZFileHelp().getSelectData(requestCode, resultCode, data)
        resultListener?.selectResult(list)
    }

}