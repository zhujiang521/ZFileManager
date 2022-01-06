package com.zj.file.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.zj.file.R
import com.zj.file.common.ZFileAdapter
import com.zj.file.common.ZFileManageDialog
import com.zj.file.common.ZFileViewHolder
import com.zj.file.content.*
import com.zj.file.databinding.DialogZfileSelectFolderBinding
import com.zj.file.util.ZFileUtil

internal class ZFileSelectFolderDialog : ZFileManageDialog() {

    companion object {

        private const val SELECT_FOLDER_TYPE = "type"

        fun newInstance(type: String) = ZFileSelectFolderDialog().apply {
            arguments = Bundle().run {
                putString(SELECT_FOLDER_TYPE, type)
                this
            }
        }
    }

    private var binding: DialogZfileSelectFolderBinding? = null
    private var tipStr = ""
    private var filePath: String? = ""
    private var isOnlyFolder = false
    private var isOnlyFile = false
    private var folderAdapter: ZFileAdapter<ZFileBean>? = null

    var selectFolder: (String.() -> Unit)? = null

    private val backList by lazy {
        ArrayList<String>()
    }

    /** 返回当前的路径 */
    private fun getThisFilePath() = if (backList.isEmpty()) null else backList[backList.size - 1]

    override fun getCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogZfileSelectFolderBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun createDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext(), R.style.Zfile_Select_Folder_Dialog).apply {
            window?.setGravity(Gravity.BOTTOM)
        }

    override fun init(savedInstanceState: Bundle?) {
        tipStr = arguments?.getString(SELECT_FOLDER_TYPE) ?: getString(R.string.zfile_menu_copy)
        // 先保存之前用户配置的数据
        filePath = getZFileConfig().filePath
        isOnlyFile = getZFileConfig().isOnlyFile
        isOnlyFolder = getZFileConfig().isOnlyFolder
        binding?.zfileSelectFolderClosePic?.setOnClickListener {
            dismiss()
        }
        binding?.zfileSelectFolderDownPic?.setOnClickListener {
            selectFolder?.invoke(if (getZFileConfig().filePath.isNullOrEmpty()) SD_ROOT else getZFileConfig().filePath!!)
            recoverData()
            dismiss()
        }
        binding?.zfileSelectFolderTitle?.text = getString(R.string.zfile_dialog_to_root, tipStr)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        folderAdapter =
            object : ZFileAdapter<ZFileBean>(requireContext(), R.layout.item_zfile_list_folder) {
                override fun bindView(holder: ZFileViewHolder, item: ZFileBean, position: Int) {
                    holder.apply {
                        setText(R.id.item_zfile_list_folderNameTxt, item.fileName)
                        setImageRes(R.id.item_zfile_list_folderPic, R.drawable.ic_zfile_folder)
                        setBgColor(R.id.item_zfile_list_folder_line, lineColor)
                        setVisibility(R.id.item_zfile_list_folder_line, position < itemCount - 1)
                    }
                }
            }
        folderAdapter?.itemClick = { _, _, item ->
            getZFileConfig().filePath = item.filePath
            backList.add(item.filePath)
            getData()
        }
        val lp = binding?.zfileSelectFolderRecyclerView?.layoutParams as LinearLayout.LayoutParams
        lp.apply {
            bottomMargin = requireContext().getStatusBarHeight()
        }
        binding?.zfileSelectFolderRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = folderAdapter
            layoutParams = lp
        }
        getZFileConfig().apply {
            isOnlyFile = false
            isOnlyFolder = true
            filePath = ""
        }
        getData()
    }

    private fun getData() {
        val filePath = getZFileConfig().filePath
        if (filePath.isNullOrEmpty() || filePath == SD_ROOT) {
            binding?.zfileSelectFolderTitle?.text = getString(R.string.zfile_dialog_to_root, tipStr)
        } else {

            binding?.zfileSelectFolderTitle?.text =
                getString(R.string.zfile_dialog_to_path, tipStr, filePath.toFile().name)
        }
        ZFileUtil.getList(requireContext()) {
            if (isNullOrEmpty()) {
                folderAdapter?.clear()
            } else {
                folderAdapter?.setDatas(this)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        val path = getThisFilePath()
        if (path == SD_ROOT || path.isNullOrEmpty()) { // 根目录
            dismiss()
        } else { // 返回上一级
            backList.removeAt(backList.size - 1)
            getZFileConfig().filePath = getThisFilePath()
            getData()
        }
        return true
    }

    private fun recoverData() {
        // 恢复之前用户配置的数据
        getZFileConfig().filePath = filePath
        getZFileConfig().isOnlyFile = isOnlyFile
        getZFileConfig().isOnlyFolder = isOnlyFolder
    }

    override fun onStart() {
        val display = requireContext().getZDisplay()
        dialog?.window?.setLayout(display[0], display[1])
        super.onStart()
    }


}