package com.zj.file.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
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
import com.zj.file.ui.viewmodel.ZFileQWViewModel
import com.zj.file.util.ZFilePermissionUtil
import com.zj.file.util.ZFileUtil
import com.zj.file.util.callStoragePermission
import com.zj.file.util.showToast
import kotlinx.android.synthetic.main.activity_zfile_qw.*
import kotlin.collections.set

internal class ZFileQWActivity : ZFileActivity() {

    private val mViewModel by viewModels<ZFileQWViewModel>()

    private var toManagerPermissionPage = false
    private val list = ArrayList<Fragment>()
    private lateinit var adapter: FragmentStateAdapter

    private val selectArray by lazy {
        ArrayMap<String, ZFileBean>()
    }

    private var isManage = false

    override fun getContentView() = R.layout.activity_zfile_qw

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
        zfile_qw_toolBar.apply {
            if (getZFileConfig().showBackIcon) setNavigationIcon(R.drawable.zfile_back) else navigationIcon =
                null
            inflateMenu(R.menu.zfile_qw_menu)
            setOnMenuItemClickListener { menu -> menuItemClick(menu) }
            setNavigationOnClickListener { onBackPressed() }
        }

        zfile_qw_viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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
        val textArray = arrayOf(
            R.string.zfile_pic,
            R.string.zfile_video,
            R.string.zfile_txt,
            R.string.zfile_other
        )
        zfile_qw_viewPager.adapter = adapter
        TabLayoutMediator(
            zfile_qw_tabLayout, zfile_qw_viewPager, true, true
        ) { tab, position ->
            tab.setText(textArray[position])
        }.attach()
    }

    fun observer(bean: ZFileQWBean) {
        val item = bean.zFileBean!!
        if (bean.isSelected) {
            val size = selectArray.size
            if (size >= getZFileConfig().maxLength) {
                showToast(getZFileConfig().maxLengthStr)
                getVPFragment(zfile_qw_viewPager.currentItem)?.removeLastSelectData(bean.zFileBean)
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

    private fun getMenu() = zfile_qw_toolBar.menu.findItem(R.id.menu_zfile_qw_down)

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
                        if (getZFileConfig().filePath!! == ZFileConfiguration.QQ) getString(
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
        val tag = "android:switcher:${zfile_qw_viewPager.id}:$fragmentId"
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
                zfile_qw_toolBar.title = title
                zfile_qw_centerTitle.visibility = View.GONE
            }
            else -> {
                zfile_qw_toolBar.title = ""
                zfile_qw_centerTitle.visibility = View.VISIBLE
                zfile_qw_centerTitle.text = title
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
