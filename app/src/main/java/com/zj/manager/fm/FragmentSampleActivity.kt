package com.zj.manager.fm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zj.file.content.ZFileBean
import com.zj.file.content.ZFileConfiguration.Companion.TITLE_CENTER
import com.zj.file.content.getZFileConfig
import com.zj.file.listener.ZFragmentListener
import com.zj.file.ui.ZFileListFragment
import com.zj.file.util.showToast
import com.zj.manager.databinding.ActivityFragmentSample2Binding

class FragmentSampleActivity : AppCompatActivity() {

    companion object {
        fun jump(context: Context) {
            context.startActivity(Intent(context, FragmentSampleActivity::class.java))
        }
    }

    private lateinit var binding: ActivityFragmentSample2Binding
    private val list = arrayListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentSample2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.fs2.visibility = View.GONE
        binding.fs2Vp.visibility = View.VISIBLE
        if (list.isNullOrEmpty()) {
            list.add(BlankFragment())
            list.add(getZFragment())
            list.add(BlankFragment())
        }
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = list.size

            override fun createFragment(position: Int): Fragment = list[position]
        }
        getZFileConfig().apply {
            fragmentTag = getFragmentTagByVP(adapter)
            showBackIcon = false
            titleGravity = TITLE_CENTER
            needLazy = true
        }

        binding.fs2Vp.offscreenPageLimit = list.size
        binding.fs2Vp.adapter = adapter
    }

    override fun onBackPressed() {
        if (binding.fs2Vp.currentItem == 1) {
            val fragment = list[1]
            (fragment as? ZFileListFragment)?.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.fs2Vp.currentItem == 1) {
            (supportFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.showPermissionDialog()
        }
    }

    override fun onDestroy() {
        getZFileConfig().showBackIcon = true
        mListener = null
        super.onDestroy()
    }

    private var mListener: ZFragmentListener? = object : ZFragmentListener() {

        /**
         * 文件选择
         */
        override fun selectResult(selectList: MutableList<ZFileBean>?) {
            showToast("选中了${selectList?.size}个，具体信息查看log")
            Log.i("ZFileManager", "选中的值 ===>>> ")
            selectList?.forEach {
                Log.i("ZFileManager", it.toString())
            }
            finish()
        }

        /**
         * 获取 [Manifest.permission.WRITE_EXTERNAL_STORAGE] 权限失败
         * @param activity FragmentActivity
         */
        override fun onSDPermissionsFiled(activity: FragmentActivity) {
            showToast("OPS，没有SD卡权限")
        }

        /**
         * 获取 [Environment.isExternalStorageManager] (所有的文件管理) 权限 失败
         * 请注意：Android 11 及以上版本 才有
         */
        override fun onExternalStorageManagerFiled(activity: FragmentActivity) {
            showToast("Environment.isExternalStorageManager = false")
        }

    }

    private fun getZFragment(): ZFileListFragment {
        val fragment = ZFileListFragment.newInstance()
        fragment.zFragmentListener = mListener
        return fragment
    }

    private fun getFragmentTagByVP(vpAdapter: FragmentStateAdapter?, position: Int = 1): String {
        val fragmentId = vpAdapter?.getItemId(position)
        return "android:switcher:${binding.fs2Vp.id}:$fragmentId"
    }

}