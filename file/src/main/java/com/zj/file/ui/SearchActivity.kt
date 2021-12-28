package com.zj.file.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import com.zj.file.R
import com.zj.file.common.ZFileActivity
import com.zj.file.content.*
import com.zj.file.ui.adapter.ZFileListAdapter
import com.zj.file.util.ZFileUtil
import com.zj.file.util.showToast
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_zfile_list.*

internal class SearchActivity : ZFileActivity(), View.OnClickListener {

    companion object {
        private const val MAX_SEARCH_KEY_LENGTH = 20
    }

    private var fileListAdapter: ZFileListAdapter? = null

    override fun getContentView() = R.layout.activity_search

    override fun init(savedInstanceState: Bundle?) {
        setStatusBarTransparent()
        // 状态栏反色
        setAndroidNativeLightStatusBar()
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
        search_edit_view.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val text = search_edit_view.text
                if (text.isNotEmpty()) {
                    ZFileUtil.getList(this) {
                        val list = this?.filter { it.fileName.contains(text) }?.filter { it.isFile }
                        if (list.isNullOrEmpty()) {
                            search_list.visibility = View.INVISIBLE
                            search_list_emptyLayout.visibility = View.VISIBLE
                        } else {
                            search_list.visibility = View.VISIBLE
                            search_list_emptyLayout.visibility = View.INVISIBLE
                            fileListAdapter?.setDatas(list.toMutableList())
                        }
                    }
                } else {
                    search_list_emptyLayout.visibility = View.INVISIBLE
                    showToast("搜索关键字不能为空")
                }
            }
            true
        }
        initListRecyclerView()
    }

    private fun initListRecyclerView() {
        fileListAdapter = ZFileListAdapter(this).run {
            itemClick = { v, _, item ->
                ZFileUtil.openFile(item.filePath, v)
            }
            this
        }
        search_list.adapter = fileListAdapter
    }

    override fun onResume() {
        super.onResume()
        search_edit_view.showIme()
    }

    override fun onPause() {
        super.onPause()
        search_edit_view.hideIme()
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