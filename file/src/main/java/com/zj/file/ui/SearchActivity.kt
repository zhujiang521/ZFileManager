package com.zj.file.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import com.zj.file.R
import com.zj.file.common.ZFileActivity
import com.zj.file.content.hideIme
import com.zj.file.content.setAndroidNativeLightStatusBar
import com.zj.file.content.setStatusBarTransparent
import com.zj.file.content.showIme
import com.zj.file.databinding.ActivitySearchBinding
import com.zj.file.ui.adapter.ZFileListAdapter
import com.zj.file.util.ZFileUtil
import com.zj.file.util.showToast

internal class SearchActivity : ZFileActivity(), View.OnClickListener {

    companion object {
        private const val MAX_SEARCH_KEY_LENGTH = 20
    }

    private lateinit var binding: ActivitySearchBinding
    private var fileListAdapter: ZFileListAdapter? = null

    override fun getContentView(): View {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun init(savedInstanceState: Bundle?) {
        setStatusBarTransparent()
        // 状态栏反色
        setAndroidNativeLightStatusBar()
        binding.searchClear.setOnClickListener(this)
        binding.searchCancel.setOnClickListener(this)
        binding.searchEditView.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(text: Editable?) {
                if (text == null) return
                if (TextUtils.isEmpty(text.toString().trim { it <= ' ' })) {
                    binding.searchClear.visibility = View.INVISIBLE
                } else if (text.length < MAX_SEARCH_KEY_LENGTH) {
                    binding.searchClear.visibility = View.VISIBLE
                }
            }
        })
        binding.searchEditView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val text = binding.searchEditView.text
                if (text.isNotEmpty()) {
                    ZFileUtil.getList(this) {
                        val list = this?.filter { it.fileName.contains(text) }?.filter { it.isFile }
                        if (list.isNullOrEmpty()) {
                            binding.searchList.visibility = View.INVISIBLE
                            binding.searchListEmptyLayout.visibility = View.VISIBLE
                        } else {
                            binding.searchList.visibility = View.VISIBLE
                            binding.searchListEmptyLayout.visibility = View.INVISIBLE
                            fileListAdapter?.setDatas(list.toMutableList())
                        }
                    }
                } else {
                    binding.searchListEmptyLayout.visibility = View.INVISIBLE
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
        binding.searchList.adapter = fileListAdapter
    }

    override fun onResume() {
        super.onResume()
        binding.searchEditView.showIme()
    }

    override fun onPause() {
        super.onPause()
        binding.searchEditView.hideIme()
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
                binding.searchEditView.setText("")
            }
            R.id.search_cancel -> {
                cancelSearch()
            }
        }
    }

}