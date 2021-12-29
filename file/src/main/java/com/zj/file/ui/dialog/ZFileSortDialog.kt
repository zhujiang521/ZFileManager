package com.zj.file.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.zj.file.R
import com.zj.file.common.ZFileManageDialog
import com.zj.file.content.setNeedWH
import com.zj.file.databinding.DialogZfileSortBinding

internal class ZFileSortDialog : ZFileManageDialog(), RadioGroup.OnCheckedChangeListener {

    companion object {
        fun newInstance(sortSelectId: Int, sequenceSelectId: Int) = ZFileSortDialog().apply {
            arguments = Bundle().run {
                putInt("sortSelectId", sortSelectId)
                putInt("sequenceSelectId", sequenceSelectId)
                this
            }
        }
    }

    private var binding: DialogZfileSortBinding? = null
    private var sortSelectId = 0
    private var sequenceSelectId = 0

    var checkedChangedListener: ((Int, Int) -> Unit)? = null
    override fun getCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogZfileSortBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun createDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext(), R.style.ZFile_Common_Dialog).apply {
            window?.setGravity(Gravity.CENTER)
        }

    override fun init(savedInstanceState: Bundle?) {
        sortSelectId = arguments?.getInt("sortSelectId", 0) ?: 0
        sequenceSelectId = arguments?.getInt("sequenceSelectId", 0) ?: 0
        check()
        when (sortSelectId) {
            R.id.zfile_sort_by_default -> binding?.zfileSortByDefault?.isChecked = true
            R.id.zfile_sort_by_name -> binding?.zfileSortByName?.isChecked = true
            R.id.zfile_sort_by_date -> binding?.zfileSortByDate?.isChecked = true
            R.id.zfile_sort_by_size -> binding?.zfileSortBySize?.isChecked = true
            else -> binding?.zfileSortByDefault?.isChecked = true
        }
        when (sequenceSelectId) {
            R.id.zfile_sequence_asc -> binding?.zfileSequenceAsc?.isChecked = true
            R.id.zfile_sequence_desc -> binding?.zfileSequenceDesc?.isChecked = true
            else -> binding?.zfileSequenceAsc?.isChecked = true
        }
        binding?.zfileSortGroup?.setOnCheckedChangeListener(this)
        binding?.zfileSequenceGroup?.setOnCheckedChangeListener(this)
        binding?.zfileDialogSortCancel?.setOnClickListener { dismiss() }
        binding?.zfileDialogSortDown?.setOnClickListener {
            checkedChangedListener?.invoke(sortSelectId, sequenceSelectId)
            dismiss()
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (group?.id == R.id.zfile_sortGroup) { // 方式
            sortSelectId = checkedId
            check()
        } else { // 顺序
            sequenceSelectId = checkedId
        }
    }

    private fun check() {
        binding?.zfileSequenceLayout?.visibility =
            if (sortSelectId == R.id.zfile_sort_by_default) View.GONE else View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        setNeedWH()
    }
}