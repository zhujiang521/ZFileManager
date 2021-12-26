package com.zj.file.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.zj.file.R
import com.zj.file.common.ZFileActivity
import com.zj.file.content.hideIme
import com.zj.file.content.setStatusBarTransparent
import com.zj.file.content.showIme
import kotlinx.android.synthetic.main.activity_search.*

internal class SearchActivity : ZFileActivity(), View.OnClickListener {

    companion object {
        private const val MAX_SEARCH_KEY_LENGTH = 20
    }

    override fun getContentView() = R.layout.activity_search

    override fun init(savedInstanceState: Bundle?) {
        setStatusBarTransparent()
        search_clear.setOnClickListener(this)
        search_cancel.setOnClickListener(this)
        search_edit_view.addTextChangedListener { text ->
            if (text == null) return@addTextChangedListener
            if (TextUtils.isEmpty(text.toString().trim { it <= ' ' })) {
                search_clear.visibility = View.INVISIBLE
            } else if (text.length < MAX_SEARCH_KEY_LENGTH) {
                search_clear.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        search_edit_view.requestFocus()
        showIme(search_edit_view)
    }

    override fun onPause() {
        super.onPause()
        hideIme(search_edit_view)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        cancelSearch()
    }

    private fun cancelSearch() {
        finish()
        overridePendingTransition(0, R.anim.search_push_out)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.search_clear -> {
                search_edit_view.setText("")
            }
            R.id.search_cancel -> {
                cancelSearch()
            }
        }
    }

}