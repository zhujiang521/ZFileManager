package com.zj.file.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.Toast

private var mToast: Toast? = null

fun View.showToast(msg: String) {
    context.showToast(msg)
}

fun View.showToast(resId: Int) {
    context.showToast(resId)
}

fun Context?.showToast(text: String?) {
    if (this == null) return
    if (!TextUtils.isEmpty(text)) {
        if (Thread.currentThread() === Looper.getMainLooper().thread) {
            showToast(this, text, Toast.LENGTH_SHORT)
        } else {
            Handler(this.mainLooper).post { showToast(this, text, Toast.LENGTH_SHORT) }
        }
    }
}

fun Context?.showToast(resId: Int) {
    if (this == null) return
    if (Thread.currentThread() === Looper.getMainLooper().thread) {
        showToast(this, resId, Toast.LENGTH_SHORT)
    } else {
        Handler(this.mainLooper).post { showToast(this, resId, Toast.LENGTH_SHORT) }
    }
}

fun Context?.showLongToast(text: String?) {
    if (this == null) return
    if (!TextUtils.isEmpty(text)) {
        if (Thread.currentThread() === Looper.getMainLooper().thread) {
            showToast(this, text, Toast.LENGTH_LONG)
        } else {
            Handler(this.mainLooper).post { showToast(this, text, Toast.LENGTH_LONG) }
        }
    }
}

fun Context?.showLongToast(resId: Int) {
    if (this == null) return
    if (Thread.currentThread() === Looper.getMainLooper().thread) {
        showToast(this, resId, Toast.LENGTH_LONG)
    } else {
        Handler(this.mainLooper).post { showToast(this, resId, Toast.LENGTH_LONG) }
    }
}

private fun showToast(context: Context?, text: String?, duration: Int) {
    if (TextUtils.isEmpty(text)) return
    cancelToast()
    if (mToast == null) {
        mToast = Toast.makeText(context, null as CharSequence?, duration)
    }
    mToast?.apply {
        setText(text)
        this.duration = duration
        show()
    }
}

private fun showToast(context: Context?, res: Int, duration: Int) {
    cancelToast()
    if (mToast == null) {
        mToast = Toast.makeText(context, res, duration)
    } else {
        mToast?.setText(res)
        mToast?.duration = duration
    }
    mToast?.show()
}

fun cancelToast() {
    mToast?.cancel()
}