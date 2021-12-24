package com.zj.file.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.zj.file.R
import com.zj.file.async.ZFileQWAsync
import com.zj.file.common.ZFileFragment
import com.zj.file.content.*
import com.zj.file.ui.adapter.ZFileListAdapter
import com.zj.file.util.ZFileQWUtil
import com.zj.file.util.ZFileUtil
import kotlinx.android.synthetic.main.fragment_zfile_qw.*

internal class ZFileQWFragment : ZFileFragment(R.layout.fragment_zfile_qw) {

    private var qwFileType = ZFileConfiguration.QQ
    // 文件类型
    private var type = ZFILE_QW_PIC
    private var qwManage = false

    private var qwAdapter: ZFileListAdapter? = null

    companion object {
        fun newInstance(qwFileType: String, type: Int, isManager: Boolean) = ZFileQWFragment().apply {
            arguments = Bundle().run {
                putString(QW_FILE_TYPE_KEY, qwFileType)
                putInt("type", type)
                putBoolean("isManager", isManager)
                this
            }
        }
    }

    override fun initAll() {
        qwFileType = arguments?.getString(QW_FILE_TYPE_KEY) ?: ZFileConfiguration.QQ
        type = arguments?.getInt("type") ?: ZFILE_QW_PIC
        initRecyclerView()
    }

    private fun initRecyclerView() {
        initAdapter()
        zfile_qw_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = qwAdapter
        }
        zfile_qw_bar.visibility = View.VISIBLE

        val qwFileLoadListener = getZFileHelp().getQWFileLoadListener()
        val filterArray = qwFileLoadListener?.getFilterArray(type) ?: ZFileQWUtil.getQWFilterMap()[type]!!
        ZFileQWAsync(qwFileType, type, requireContext()) {
            zfile_qw_bar.visibility = View.GONE
            if (isNullOrEmpty()) {
                qwAdapter?.clear()
                zfile_qw_emptyLayout.visibility = View.VISIBLE
            } else {
                qwAdapter?.setDatas(this)
                zfile_qw_emptyLayout.visibility = View.GONE
            }
        }.start(filterArray)
    }

    private fun initAdapter() {
        if (qwAdapter == null) {
            qwAdapter = ZFileListAdapter(requireContext(), true).run {
                itemClick = { v, _, item ->
                    ZFileUtil.openFile(item.filePath, v)
                }
                qwChangeListener = { isManage, item, isSelect ->
                    if (isManage) {
                        (context as? ZFileQWActivity)?.observer(item toQWBean isSelect)
                    }
                }
                isManage = qwManage
                this
            }
        }
    }

    fun setManager(isManage: Boolean) {
        if (this.qwManage != isManage) {
            this.qwManage = isManage
            qwAdapter?.isManage = isManage
        }
    }

    fun removeLastSelectData(bean: ZFileBean?) {
        qwAdapter?.setQWLastState(bean)
    }

    fun resetAll() {
        qwManage = false
        qwAdapter?.isManage = false
    }

}
