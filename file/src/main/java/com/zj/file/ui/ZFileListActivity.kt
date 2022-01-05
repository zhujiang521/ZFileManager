package com.zj.file.ui

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.zj.file.R
import com.zj.file.common.ZFileActivity
import com.zj.file.content.*
import com.zj.file.databinding.ActivityZfileList2Binding
import com.zj.file.listener.ZFragmentListener

internal class ZFileListActivity : ZFileActivity() {

    private lateinit var binding: ActivityZfileList2Binding

    override fun getContentView(): View {
        binding = ActivityZfileList2Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun init(savedInstanceState: Bundle?) {
        getZFileConfig().apply {
            fragmentTag = ZFILE_FRAGMENT_TAG
            filePath = intent.getStringExtra(FILE_START_PATH_KEY)
        }
        supportFragmentManager
            .beginTransaction()
            .add(
                R.id.zfile_2rootLayout,
                ZFileListFragment.newInstance().apply {
                    zFragmentListener = mListener
                },
                ZFILE_FRAGMENT_TAG
            )
            .commit()
    }

    override fun onBackPressed() {
        (supportFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        (supportFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.showPermissionDialog()
    }

    private var mListener: ZFragmentListener? = object : ZFragmentListener() {

        override fun selectResult(selectList: MutableList<ZFileBean>?) {
            setResult(ZFILE_RESULT_CODE, Intent().apply {
                putParcelableArrayListExtra(
                    ZFILE_SELECT_DATA_KEY,
                    selectList as ArrayList<out Parcelable>
                )
            })
            finish()
        }

    }
}