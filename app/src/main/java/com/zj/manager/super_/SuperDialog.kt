package com.zj.manager.super_

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zj.file.content.ZFileBean
import com.zj.manager.databinding.DialogSuperBinding

/**
 * 数据已经获取到了，具体怎么操作就交给你了！
 */
class SuperDialog : BottomSheetDialogFragment() {

    companion object {

        const val FILE_BEAN_LIST = "list"

        fun newInstance(list: ArrayList<ZFileBean>) = SuperDialog().apply {
            arguments = Bundle().run {
                putParcelableArrayList(FILE_BEAN_LIST, list)
                this
            }
        }
    }

    private var binding: DialogSuperBinding? = null
    private var superAdapter: SuperAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSuperBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i("ZFileManager", "数据已经获取到了，具体怎么操作就交给你了！")
        binding?.superDownPic?.setOnClickListener {
            dismiss()
        }
        binding?.superCacelPic?.setOnClickListener {
            dismiss()
        }
        val list = arguments?.getParcelableArrayList<ZFileBean>(FILE_BEAN_LIST) ?: arrayListOf()
        superAdapter = SuperAdapter(list)
        binding?.superRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = superAdapter
        }
    }

}