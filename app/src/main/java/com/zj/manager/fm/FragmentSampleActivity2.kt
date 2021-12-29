package com.zj.manager.fm

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.zj.file.content.ZFileBean
import com.zj.file.content.ZFileConfiguration.Companion.TITLE_CENTER
import com.zj.file.content.getZFileConfig
import com.zj.file.listener.ZFragmentListener
import com.zj.file.ui.ZFileListFragment
import com.zj.file.util.showToast
import com.zj.manager.R
import com.zj.manager.databinding.ActivityFragmentSample2Binding

class FragmentSampleActivity2 : AppCompatActivity() {

    companion object {
        fun jump(context: Context, type: Int) {
            context.startActivity(Intent(context, FragmentSampleActivity2::class.java).apply {
                putExtra("type", type)
            })
        }
    }

    private lateinit var binding: ActivityFragmentSample2Binding
    private var type = 1
    private var vpAdapter: VPAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentSample2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getIntExtra("type", 1)
        when (type) {
            1 -> init1()
            2 -> init2()
            3 -> init3()
        }
    }

    private fun init1() {
        binding.fs2.visibility = View.VISIBLE
        binding.fs2Vp.visibility = View.GONE
        val TAG = "ZFileListFragmentTag"
        getZFileConfig().fragmentTag = TAG
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fs2, getZFragment(), TAG)
            .commit()
    }

    private fun init2() {
        binding.fs2.visibility = View.GONE
        binding.fs2Vp.visibility = View.VISIBLE
        val list = arrayListOf<Fragment>()
        list.add(BlankFragment())
        list.add(getZFragment())
        list.add(BlankFragment())
        vpAdapter = VPAdapter(supportFragmentManager, list)
        getZFileConfig().apply {
            fragmentTag = getFragmentTagByVP(vpAdapter)
            showBackIcon = false
            titleGravity = TITLE_CENTER
            needLazy = true
        }

        binding.fs2Vp.offscreenPageLimit = list.size
        binding.fs2Vp.adapter = vpAdapter
    }

    private fun init3() {
        binding.fs2.visibility = View.VISIBLE
        binding.fs2Vp.visibility = View.GONE
        val TAG = "BlankFragment2"
        val fragment = BlankFragment2()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fs2, fragment, TAG)
            .commit()
    }

    override fun onBackPressed() {
        if (type == 3) {
            (supportFragmentManager.findFragmentByTag("BlankFragment2") as? BlankFragment2)?.onBackPressed()
        } else {
            (supportFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (type == 3) {
            (supportFragmentManager.findFragmentByTag("BlankFragment2") as? BlankFragment2)?.showPermissionDialog()
        } else {
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
         * 直接调用 [Activity.finish] 即可
         */
//        override fun onActivityBackPressed() {
//            finish()
//        }

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

    private fun getFragmentTagByVP(vpAdapter: FragmentPagerAdapter?, position: Int = 1): String {
        val fragmentId = vpAdapter?.getItemId(position)
        return "android:switcher:${binding.fs2Vp.id}:$fragmentId"
    }

    private class VPAdapter(
        fragmentManager: FragmentManager,
        private var fragments: MutableList<Fragment>
    ) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount() = fragments.size
        override fun getItem(position: Int) = fragments[position]
        override fun getItemPosition(any: Any) = PagerAdapter.POSITION_NONE
        override fun getPageTitle(position: Int) = ""
    }

}