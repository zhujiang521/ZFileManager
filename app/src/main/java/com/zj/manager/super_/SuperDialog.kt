package com.zj.manager.super_

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.zj.file.content.ZFileBean
import com.zj.manager.databinding.DialogSuperBinding

/**
 * 数据已经获取到了，具体怎么操作就交给你了！
 */
class SuperDialog : DialogFragment() {

    companion object {
        fun newInstance(list: ArrayList<ZFileBean>) = SuperDialog().apply {
            arguments = Bundle().run {
                putSerializable("list", list)
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

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext(), com.zj.file.R.style.Zfile_Select_Folder_Dialog).apply {
            window?.setGravity(Gravity.BOTTOM)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i("ZFileManager", "数据已经获取到了，具体怎么操作就交给你了！")
        binding?.superDownPic?.setOnClickListener {
            dismiss()
        }
        binding?.superCacelPic?.setOnClickListener {
            dismiss()
        }
        val list = arguments?.getSerializable("list") as ArrayList<ZFileBean>
        superAdapter = SuperAdapter(list)
        binding?.superRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = superAdapter
        }
    }

    override fun onStart() {
        val display = requireContext().getTDisplay()
        dialog?.window?.setLayout(display[0], display[1])
        super.onStart()
    }

    private fun Context.getTDisplay() = IntArray(2).apply {
        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        manager.defaultDisplay.getSize(point)
        this[0] = point.x
        this[1] = point.y
    }

}