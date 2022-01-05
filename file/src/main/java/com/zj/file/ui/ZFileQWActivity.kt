package com.zj.file.ui

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.zj.file.R
import com.zj.file.common.ZFileActivity
import com.zj.file.content.*
import com.zj.file.databinding.ActivityZfileQwBinding
import com.zj.file.ui.viewmodel.ZFileQWViewModel
import com.zj.file.util.ZFileQWUtil
import com.zj.file.util.callStoragePermission
import com.zj.file.util.showToast
import kotlin.collections.set

internal class ZFileQWActivity : ZFileActivity() {

    private lateinit var binding: ActivityZfileQwBinding
    private val mViewModel by viewModels<ZFileQWViewModel>()

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

        binding.zfileQwViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                getVPFragment(position)?.setManager(mViewModel.isManage)
            }
        })
        val type = mViewModel.type ?: ZFileConfiguration.QQ
        if (mViewModel.list.isNullOrEmpty()) {
            mViewModel.list.add(
                ZFileQWFragment.newInstance(
                    type,
                    ZFILE_QW_PIC,
                    mViewModel.isManage
                )
            )
            mViewModel.list.add(
                ZFileQWFragment.newInstance(
                    type,
                    ZFILE_QW_MEDIA,
                    mViewModel.isManage
                )
            )
            mViewModel.list.add(
                ZFileQWFragment.newInstance(
                    type,
                    ZFILE_QW_DOCUMENT,
                    mViewModel.isManage
                )
            )
            mViewModel.list.add(
                ZFileQWFragment.newInstance(
                    type,
                    ZFILE_QW_OTHER,
                    mViewModel.isManage
                )
            )
        }
        mViewModel.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = mViewModel.list.size

            override fun createFragment(position: Int): Fragment = mViewModel.list[position]
        }
        val textArray = ZFileQWUtil.getQWTitle(this)
        binding.zfileQwViewPager.adapter = mViewModel.adapter
        TabLayoutMediator(
            binding.zfileQwTabLayout, binding.zfileQwViewPager, true, true
        ) { tab, position ->
            tab.text = textArray[position]
        }.attach()
    }

    fun observer(bean: ZFileQWBean) {
        val item = bean.zFileBean!!
        if (bean.isSelected) {
            val size = mViewModel.selectArray.size
            if (size >= getZFileConfig().maxLength) {
                showToast(getZFileConfig().maxLengthStr)
                getVPFragment(binding.zfileQwViewPager.currentItem)?.removeLastSelectData(bean.zFileBean)
            } else {
                mViewModel.selectArray[item.filePath] = item
            }
        } else {
            if (mViewModel.selectArray.contains(item.filePath)) {
                mViewModel.selectArray.remove(item.filePath)
            }
        }
        setBarTitle(getString(R.string.zfile_selected_title, mViewModel.selectArray.size))
        mViewModel.isManage = true
        getMenu().isVisible = true
    }

    private fun getMenu() = binding.zfileQwToolBar.menu.findItem(R.id.menu_zfile_qw_down)

    private fun menuItemClick(menu: MenuItem?): Boolean {
        when (menu?.itemId) {
            R.id.menu_zfile_qw_down -> {
                if (mViewModel.selectArray.isNullOrEmpty()) {
                    mViewModel.list.indices.forEach {
                        getVPFragment(it)?.apply {
                            resetAll()
                        }
                    }
                    mViewModel.isManage = false
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
                            mViewModel.selectArray.toFileList() as java.util.ArrayList<out Parcelable>
                        )
                    })
                    finish()
                }
            }
        }
        return true
    }

    private fun getVPFragment(currentItem: Int): ZFileQWFragment? {
        val fragmentId = mViewModel.adapter.getItemId(currentItem)
        val tag = "android:switcher:${binding.zfileQwViewPager.id}:$fragmentId"
        return supportFragmentManager.findFragmentByTag(tag) as? ZFileQWFragment
    }

    override fun onResume() {
        super.onResume()
        if (mViewModel.toManagerPermissionPage) {
            mViewModel.toManagerPermissionPage = false
            callPermission()
        }
    }

    private fun callPermission() {
        callStoragePermission(
            hasPermissionListener = {
                initAll()
            }, noPermissionListener = {
                mViewModel.toManagerPermissionPage = true
            }, cancelPermissionListener = {
                finish()
            })
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

}
