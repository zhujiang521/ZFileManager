package com.zj.file.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.zj.file.listener.ZFileSelectResultListener

class ZFileProxyViewModel:ViewModel() {

    var resultListener: ZFileSelectResultListener? = null

}