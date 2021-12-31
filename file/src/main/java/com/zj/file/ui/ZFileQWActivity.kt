package com.zj.file.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.zj.file.R
import com.zj.file.common.ZFileActivity
import com.zj.file.content.*
import com.zj.file.databinding.ActivityZfileQwBinding
import com.zj.file.ui.viewmodel.ZFileQWViewModel
import com.zj.file.util.*
import com.zj.file.util.ZFilePermissionUtil
import com.zj.file.util.ZFileQWUtil
import com.zj.file.util.ZFileUtil
import kotlin.collections.set

internal class ZFileQWActivity : ZFileActivity() {

    private lateinit var binding: ActivityZfileQwBinding
    private val mViewModel by viewModels<ZFileQWViewModel>()

    private var toManagerPermissionPage = false
    private val list = ArrayList<Fragment>()
    private lateinit var adapter: FragmentStateAdapter

    private val selectArray by lazy {
        ArrayMap<String, ZFileBean>()
    }

    private var isManage = false

    override fun getContentView(): View {
        binding = ActivityZfileQwBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun init(savedInstanceState: Bundle?) {
        if (mViewModel.type == null) {
            mViewModel.type = getZFileConfig().filePath ?: ZFileConfiguration.QQ
        }
        setBarTitle(
            if (mViewModel.type == ZFileConfiguration.QQ) getString(R.string.zfile_qq_title)
            else getString(R.string.zfile_we_chart_title)
        )
        callPermission()
    }

    private fun initAll() {
        binding.zfileQwToolBar.apply {
            if (getZFileConfig().showBackIcon) setNavigationIcon(R.drawable.zfile_back) else navigationIcon =
                null
            inflateMenu(R.menu.zfile_qw_menu)
            setOnMenuItemClickListener { menu -> menuItemClick(menu) }
            setNavigationOnClickListener { onBackPressed() }
        }

        binding.zfileQwViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                getVPFragment(position)?.setManager(isManage)
            }
        })
        val type = mViewModel.type ?: ZFileConfiguration.QQ
        if (list.isNullOrEmpty()) {
            list.add(ZFileQWFragment.newInstance(type, ZFILE_QW_PIC, isManage))
            list.add(ZFileQWFragment.newInstance(type, ZFILE_QW_MEDIA, isManage))
            list.add(ZFileQWFragment.newInstance(type, ZFILE_QW_DOCUMENT, isManage))
            list.add(ZFileQWFragment.newInstance(type, ZFILE_QW_OTHER, isManage))
        }
        adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = list.size

            override fun createFragment(position: Int): Fragment = list[position]
        }
        val textArray = ZFileQWUtil.getQWTitle(this)
        binding.zfileQwViewPager.adapter = adapter
        TabLayoutMediator(
            binding.zfileQwTabLayout, binding.zfileQwViewPager, true, true
        ) { tab, position ->
            tab.text = textArray[position]
        }.attach()
    }

    fun observer(bean: ZFileQWBean) {
        val item = bean.zFileBean!!
        if (bean.isSelected) {
            val size = selectArray.size
            if (size >= getZFileConfig().maxLength) {
                showToast(getZFileConfig().maxLengthStr)
                getVPFragment(binding.zfileQwViewPager.currentItem)?.removeLastSelectData(bean.zFileBean)
            } else {
                selectArray[item.filePath] = item
            }
        } else {
            if (selectArray.contains(item.filePath)) {
                selectArray.remove(item.filePath)
            }
        }
        setBarTitle(getString(R.string.zfile_selected_title, selectArray.size))
        isManage = true
        getMenu().isVisible = true
    }

    private fun getMenu() = binding.zfileQwToolBar.menu.findItem(R.id.menu_zfile_qw_down)

    private fun menuItemClick(menu: MenuItem?): Boolean {
        when (menu?.itemId) {
            R.id.menu_zfile_qw_down -> {
                if (selectArray.isNullOrEmpty()) {
                    list.indices.forEach {
                        getVPFragment(it)?.apply {
                            resetAll()
                        }
                    }
                    isManage = false
                    getMenu().isVisible = false
                    setBarTitle(
                        if (getZFileConfig().filePath == ZFileConfiguration.QQ) getString(
                            R.string.zfile_qq_title
                        ) else getString(R.string.zfile_we_chart_title)
                    )
                } else {
                    setResult(ZFILE_RESULT_CODE, Intent().apply {
                        putParcelableArrayListExtra(
                            ZFILE_SELECT_DATA_KEY,
                            selectArray.toFileList() as java.util.ArrayList<out Parcelable>
                        )
                    })
                    finish()
                }
            }
        }
        return true
    }

    private fun getVPFragment(currentItem: Int): ZFileQWFragment? {
        val fragmentId = adapter.getItemId(currentItem)
        val tag = "android:switcher:${binding.zfileQwViewPager.id}:$fragmentId"
        return supportFragmentManager.findFragmentByTag(tag) as? ZFileQWFragment
    }

    override fun onResume() {
        super.onResume()
        if (toManagerPermissionPage) {
            toManagerPermissionPage = false
            callPermission()
        }
    }

    private fun callPermission() {
        callStoragePermission(
            hasPermissionListener = {
                initAll()
            }, noPermissionListener = {
                toManagerPermissionPage = true
            }, cancelPermissionListener = {
                finish()
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ZFilePermissionUtil.WRITE_EXTERNAL_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) initAll()
            else {
                showToast(getStringById(R.string.zfile_permission_bad))
                finish()
            }
        }
    }

    private fun setBarTitle(title: String) {
        when (getZFileConfig().titleGravity) {
            ZFileConfiguration.TITLE_LEFT -> {
                binding.zfileQwToolBar.title = title
                binding.zfileQwCenterTitle.visibility = View.GONE
            }
            else -> {
                binding.zfileQwToolBar.title = ""
                binding.zfileQwCenterTitle.visibility = View.VISIBLE
                binding.zfileQwCenterTitle.text = title
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isManage = false
        selectArray.clear()
        ZFileUtil.resetAll()
    }

}
