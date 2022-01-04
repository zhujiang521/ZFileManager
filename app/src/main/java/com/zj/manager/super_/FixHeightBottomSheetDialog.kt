package com.zj.manager.super_

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class FixHeightBottomSheetDialog(context: Context) : BottomSheetDialog(context) {
    private var mContentView: View? = null

    override fun onStart() {
        super.onStart()
        fixHeight()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        mContentView = view
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        mContentView = view
    }

    private fun fixHeight() {
        if (null == mContentView) {
            return
        }
        val parent: View = mContentView?.parent as View
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View>(parent)
        mContentView?.measure(0, 0)
        behavior.peekHeight = mContentView?.measuredHeight ?: 0
        val params = parent.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        parent.layoutParams = params
    }
}
