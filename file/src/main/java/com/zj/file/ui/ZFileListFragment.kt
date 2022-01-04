package com.zj.file.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.zj.file.R
import com.zj.file.common.ZFileAdapter
import com.zj.file.common.ZFileViewHolder
import com.zj.file.content.*
import com.zj.file.databinding.ActivityZfileListBinding
import com.zj.file.dsl.ZFileDsl
import com.zj.file.listener.ZFragmentListener
import com.zj.file.ui.adapter.ZFileListAdapter
import com.zj.file.ui.dialog.ZFileSelectFolderDialog
import com.zj.file.ui.dialog.ZFileSortDialog
import com.zj.file.util.*
import java.io.File

/**
 * 文件管理 核心实现，可以 在 Activity、Fragment or ViewPager中使用
 *
 * 注意：使用 [FragmentManager] 或者 [ViewPager] 动态添加或直接嵌套使用时
 * 1. 无法通过 [ZFileDsl] 获取返回的数据，其他配置将不受影响
 * 2. 需在 Activity 中配置 [ZFragmentListener] 来接收选中的文件
 * 3. Activity onBackPressed 方法 需要 调用 [onBackPressed]
 * 4. Activity onResume 方法 需要调用 [showPermissionDialog]
 */
class ZFileListFragment : Fragment() {

    private lateinit var mActivity: FragmentActivity

    private var isFirstLoad = true

    private var toManagerPermissionPage = false

    private var barShow = false
    private lateinit var filePathAdapter: ZFileAdapter<ZFilePathBean>
    private var fileListAdapter: ZFileListAdapter? = null

    private var index = 0
    private var rootPath = "" // 根目录
    private var specifyPath: String? = "" // 指定目录
    private var nowPath: String? = "" // 当前目录

    private var hasPermission = false

    var zFragmentListener: ZFragmentListener? = null
    private var binding: ActivityZfileListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityZfileListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    private val titleArray by lazy {
        if (getZFileConfig().longClickOperateTitles.isNullOrEmpty()) {
            arrayOf(
                getString(R.string.zfile_menu_rename),
                getString(R.string.zfile_menu_copy),
                getString(R.string.zfile_menu_move),
                getString(R.string.zfile_menu_delete),
                getString(R.string.zfile_menu_detail),
            )
        } else getZFileConfig().longClickOperateTitles
    }

    private val backList by lazy {
        ArrayList<String>()
    }

    private var sortSelectId = R.id.zfile_sort_by_default // 排序方式选中的ID
    private var sequenceSelectId = R.id.zfile_sequence_asc // 顺序选中的ID

    /** 返回当前的路径 */
    private fun getThisFilePath() = if (backList.isEmpty()) null else backList[backList.size - 1]

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentActivity) {
            mActivity = context
        } else {
            throw ZFileException("activity must be FragmentActivity！！！")
        }
    }

    companion object {

        /**
         * 获取 [ZFileListFragment] 实例
         */
        @JvmStatic
        fun newInstance(): ZFileListFragment {
            val startPath = getZFileConfig().filePath
            if (startPath == ZFileConfiguration.QQ || startPath == ZFileConfiguration.WECHAT) {
                throw ZFileException(
                    "startPath must be real path or empty, if you want use \" qq \" or \" wechat \", " +
                            "please use \" getZFileHelp().start() \""
                )
            }
            val newPath = if (startPath.isNullOrEmpty()) SD_ROOT else startPath
            if (!newPath.toFile().exists()) {
                throw ZFileException("$newPath not exist")
            }
            return ZFileListFragment().apply {
                arguments = Bundle().run {
                    putString(FILE_START_PATH_KEY, newPath)
                    this
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!getZFileConfig().needLazy) {
            initAll()
        }
    }

    override fun onResume() {
        super.onResume()
        if (getZFileConfig().needLazy) {
            if (isFirstLoad) {
                initAll()
                isFirstLoad = false
            }
        }
    }

    /**
     * 可以有效避免 VP + Fragment 出现的问题
     * 请在 Activity 中的 onResume 中调用该方法
     */
    fun showPermissionDialog() {
        if (toManagerPermissionPage) {
            toManagerPermissionPage = false
            callPermission()
        }
    }

    private fun setMenuState() {
        binding?.zfileListToolBar?.menu?.apply {
            findItem(R.id.menu_zfile_down).isVisible = barShow
            findItem(R.id.menu_zfile_px).isVisible = !barShow
            findItem(R.id.menu_zfile_show).isVisible = !barShow
            findItem(R.id.menu_zfile_hidden).isVisible = !barShow
        }
    }

    private fun menuItemClick(menu: MenuItem?): Boolean {
        when (menu?.itemId) {
            R.id.menu_zfile_search -> {
                val search = Intent(context, SearchActivity::class.java)
                startActivity(search)
                activity?.overridePendingTransition(R.anim.search_push_in, R.anim.fake_anim)
            }
            R.id.menu_zfile_down -> {
                val list = fileListAdapter?.selectData
                if (list.isNullOrEmpty()) {
                    setBarTitle(mActivity getStringById R.string.zfile_title)
                    fileListAdapter?.isManage = false
                    barShow = false
                    setMenuState()
                } else {
                    if (zFragmentListener == null) {
                        mActivity.setResult(ZFILE_RESULT_CODE, Intent().apply {
                            putParcelableArrayListExtra(
                                ZFILE_SELECT_DATA_KEY,
                                list as java.util.ArrayList<out Parcelable>
                            )
                        })
                        mActivity.finish()
                    } else {
                        zFragmentListener?.selectResult(list)
                    }
                }
            }
            R.id.menu_zfile_px -> showSortDialog()
            R.id.menu_zfile_show -> {
                menu.isChecked = true
                getZFileConfig().showHiddenFile = true
                getData(nowPath)
            }
            R.id.menu_zfile_hidden -> {
                menu.isChecked = true
                getZFileConfig().showHiddenFile = false
                getData(nowPath)
            }
        }
        return true
    }

    private fun initAll() {
        setSortSelectId()
        specifyPath = arguments?.getString(FILE_START_PATH_KEY)
        getZFileConfig().filePath = specifyPath
        rootPath = specifyPath ?: ""
        backList.add(rootPath)
        nowPath = rootPath
        binding?.zfileListToolBar?.apply {
            if (getZFileConfig().showBackIcon) setNavigationIcon(R.drawable.zfile_back) else navigationIcon =
                null
            inflateMenu(R.menu.zfile_list_menu)
            setOnMenuItemClickListener { menu -> menuItemClick(menu) }
            setNavigationOnClickListener { back() }
        }
        setHiddenState()
        setBarTitle(mActivity getStringById R.string.zfile_title)
        binding?.zfileListAgainBtn?.setOnClickListener {
            callPermission()
        }
        callPermission()
    }

    private fun initRV() {
        hasPermission = true
        binding?.zfileListErrorLayout?.visibility = View.GONE
        binding?.zfileListRefreshLayout?.property {
            getData(nowPath)
        }
        initPathRecyclerView()
        initListRecyclerView()
    }

    private fun initPathRecyclerView() {
        filePathAdapter =
            object : ZFileAdapter<ZFilePathBean>(mActivity, R.layout.item_zfile_path) {
                override fun bindView(holder: ZFileViewHolder, item: ZFilePathBean, position: Int) {
                    holder.setText(R.id.item_zfile_path_title, item.fileName)
                }

                override fun addItem(position: Int, t: ZFilePathBean) {
                    var hasData = false
                    getDatas().forEach forEach@{
                        if (it.filePath == t.filePath) {
                            hasData = true
                            return@forEach
                        }
                    }
                    if (!(hasData || t.filePath == SD_ROOT)) {
                        super.addItem(position, t)
                    }
                }

            }
        binding?.zfileListPathRecyclerView?.apply {
            layoutManager = LinearLayoutManager(activity).run {
                orientation = LinearLayoutManager.HORIZONTAL
                this
            }
            adapter = filePathAdapter
        }
        getPathData()
    }

    private fun getPathData() {
        val filePath = getZFileConfig().filePath
        val pathList = ArrayList<ZFilePathBean>()
        if (filePath.isNullOrEmpty() || filePath == SD_ROOT) {
            pathList.add(ZFilePathBean(mActivity getStringById R.string.zfile_root_path, "root"))
        } else {
            pathList.add(
                ZFilePathBean(
                    "${mActivity getStringById R.string.zfile_path}${filePath.getFileName()}",
                    filePath
                )
            )
        }
        filePathAdapter.addAll(pathList)
    }

    private fun initListRecyclerView() {
        fileListAdapter = ZFileListAdapter(mActivity).run {
            itemClick = { v, _, item ->
                if (item.isFile) {
                    ZFileUtil.openFile(item.filePath, v)
                } else {
                    ZFileLog.i("进入 ${item.filePath}")
                    backList.add(item.filePath)
                    filePathAdapter.addItem(filePathAdapter.itemCount, item.toPathBean())
                    binding?.zfileListPathRecyclerView?.scrollToPosition(filePathAdapter.itemCount - 1)
                    getData(item.filePath)
                    nowPath = item.filePath
                }
            }
            itemLongClick = { _, index, item ->
                if (fileListAdapter?.isManage == true) {
                    false
                } else {
                    if (getZFileConfig().needLongClick) {
                        if (getZFileConfig().isOnlyFileHasLongClick) {
                            if (item.isFile) showSelectDialog(index, item)
                            else false
                        } else {
                            showSelectDialog(index, item)
                        }
                    } else {
                        false
                    }
                }
            }
            changeListener = { isManage, size ->
                if (isManage) {
                    if (barShow) {
                        setBarTitle(getString(R.string.zfile_selected_title, size))
                    } else {
                        barShow = true
                        setBarTitle(getString(R.string.zfile_selected_title, 0))
                        setMenuState()
                    }
                }
            }
            this
        }
        binding?.zfileListListRecyclerView?.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = fileListAdapter
        }
        getData(getZFileConfig().filePath)
        index++
    }

    private fun getData(filePath: String?) {
        if (!hasPermission) {
            ZFileLog.e("no permission")
            return
        }
        binding?.zfileListRefreshLayout?.isRefreshing = true
        val key = if (filePath.isNullOrEmpty()) SD_ROOT else filePath
        if (rootPath.isEmpty()) {
            rootPath = key
        }
        getZFileConfig().filePath = filePath
        if (index != 0) {
            filePathAdapter.addItem(filePathAdapter.itemCount, File(key).toPathBean())
            binding?.zfileListPathRecyclerView?.scrollToPosition(filePathAdapter.itemCount - 1)
        }
        ZFileUtil.getList(mActivity) {
            if (isNullOrEmpty()) {
                fileListAdapter?.clear()
                binding?.zfileListEmptyLayout?.visibility = View.VISIBLE
            } else {
                fileListAdapter?.setDatas(this)
                binding?.zfileListEmptyLayout?.visibility = View.GONE
            }
            binding?.zfileListRefreshLayout?.isRefreshing = false
        }
    }

    private fun showSelectDialog(index: Int, item: ZFileBean): Boolean {
        AlertDialog.Builder(mActivity, R.style.ZFile_Common_Dialog).apply {
            setTitle(getString(R.string.zfile_menu_selected))
            setItems(titleArray) { dialog, which ->
                jumpByWhich(item, which, index)
                dialog.dismiss()
            }
            setPositiveButton(R.string.zfile_cancel) { dialog, _ -> dialog.dismiss() }
            show()
        }
        return true
    }

    private val FRAGMENT_TAG by lazy {
        ZFileSelectFolderDialog::class.java.simpleName
    }

    private fun jumpByWhich(item: ZFileBean, which: Int, index: Int) {
        when (titleArray!![which]) {
            getString(R.string.zfile_menu_rename) -> {
                getZFileHelp().getFileOperateListener()
                    .renameFile(item.filePath, mActivity) { isSuccess, newName ->
                        if (isSuccess) {
                            val oldFile = item.filePath.toFile()
                            val oldFileType = oldFile.getFileType()
                            val oldPath =
                                oldFile.path.substring(0, oldFile.path.lastIndexOf("/") + 1)
                            val newFilePath = "$oldPath$newName.$oldFileType"
                            fileListAdapter?.getItem(index)?.apply {
                                filePath = newFilePath
                                fileName = "$newName.$oldFileType"
                            }
                            fileListAdapter?.notifyItemChanged(index)
                        }
                    }
            }
            getString(R.string.zfile_menu_copy), getString(R.string.zfile_menu_move) -> {
                mActivity.checkFragmentByTag(FRAGMENT_TAG)
                ZFileSelectFolderDialog.newInstance(titleArray!![which]).apply {
                    selectFolder = {
                        doSth(item, this, titleArray!![which], index)
                    }
                }.show(mActivity.supportFragmentManager, FRAGMENT_TAG)
            }
            getString(R.string.zfile_menu_delete) -> getZFileHelp().getFileOperateListener()
                .deleteFile(
                    item.filePath,
                    mActivity
                ) {
                    if (this) {
                        fileListAdapter?.remove(index)
                        ZFileLog.i("文件删除成功")
                    } else {
                        ZFileLog.i("文件删除失败")
                    }
                }
            getString(R.string.zfile_menu_detail) -> ZFileUtil.infoFile(item, mActivity)
            else -> throwError("longClickOperateTitles")
        }
    }

    private fun doSth(item: ZFileBean, targetPath: String, type: String, position: Int) {
        if (type == getString(R.string.zfile_menu_copy)) { // 复制文件
            getZFileHelp().getFileOperateListener().copyFile(item.filePath, targetPath, mActivity) {
                if (this) {
                    ZFileLog.i("文件复制成功")
                    observer(true)
                } else {
                    ZFileLog.e("文件复制失败")
                }
            }
        } else { // 移动文件
            getZFileHelp().getFileOperateListener().moveFile(item.filePath, targetPath, mActivity) {
                if (this) {
                    fileListAdapter?.remove(position)
                    ZFileLog.i("文件移动成功")
                } else {
                    ZFileLog.e("文件移动失败")
                }
            }
        }
    }

    private fun showSortDialog() {
        val tag = ZFileSortDialog::class.java.simpleName
        mActivity.checkFragmentByTag(tag)
        ZFileSortDialog.newInstance(sortSelectId, sequenceSelectId).apply {
            checkedChangedListener = { sortId, sequenceId ->
                sortSelectId = sortId
                sequenceSelectId = sequenceId
                val sortordByWhat = when (sortId) {
                    R.id.zfile_sort_by_default -> ZFileConfiguration.BY_DEFAULT
                    R.id.zfile_sort_by_name -> ZFileConfiguration.BY_NAME
                    R.id.zfile_sort_by_date -> ZFileConfiguration.BY_DATE
                    R.id.zfile_sort_by_size -> ZFileConfiguration.BY_SIZE
                    else -> ZFileConfiguration.BY_DEFAULT
                }
                val sortord = when (sequenceId) {
                    R.id.zfile_sequence_asc -> ZFileConfiguration.ASC
                    R.id.zfile_sequence_desc -> ZFileConfiguration.DESC
                    else -> ZFileConfiguration.ASC
                }
                getZFileConfig().apply {
                    sortordBy = sortordByWhat
                    this.sortord = sortord
                }
                getData(nowPath)
            }
        }.show(mActivity.supportFragmentManager, tag)
    }

    /**
     * 监听返回按钮
     * 请在 Activity 中的 onBackPressed 中调用该方法
     */
    fun onBackPressed() {
        back()
    }

    private fun back() {
        val path = getThisFilePath()
        if (path == rootPath || path.isNullOrEmpty()) { // 根目录
            if (barShow) {  // 存在编辑状态
                setBarTitle(mActivity getStringById R.string.zfile_title)
                fileListAdapter?.isManage = false
                barShow = false
                setMenuState()
            } else {
                if (zFragmentListener == null) {
                    mActivity.onBackPressed()
                } else {
                    zFragmentListener?.onActivityBackPressed(mActivity)
                }
            }
        } else { // 返回上一级
            // 先清除当前一级的数据
            backList.removeAt(backList.size - 1)
            val lastPath = getThisFilePath()
            getData(lastPath)
            nowPath = lastPath
            filePathAdapter.remove(filePathAdapter.itemCount - 1)
            binding?.zfileListPathRecyclerView?.scrollToPosition(filePathAdapter.itemCount - 1)
        }
    }

    private fun callPermission() {
        callStoragePermission(
            hasPermissionListener = {
                initRV()
                binding?.zfileListErrorLayout?.isVisible = !it
            }, noPermissionListener = {
                toManagerPermissionPage = true
            }, cancelPermissionListener = {
                if (zFragmentListener == null) {
                    mActivity.showToast(mActivity getStringById R.string.zfile_11_bad)
                    mActivity.finish()
                } else {
                    zFragmentListener?.onExternalStorageManagerFiled(mActivity)
                }
            })
    }

    private fun setSortSelectId() {
        sortSelectId = when (getZFileConfig().sortordBy) {
            ZFileConfiguration.BY_NAME -> R.id.zfile_sort_by_name
            ZFileConfiguration.BY_SIZE -> R.id.zfile_sort_by_size
            ZFileConfiguration.BY_DATE -> R.id.zfile_sort_by_date
            else -> R.id.zfile_sort_by_default
        }
        sequenceSelectId = when (getZFileConfig().sortord) {
            ZFileConfiguration.DESC -> R.id.zfile_sequence_desc
            else -> R.id.zfile_sequence_asc
        }
    }

    private fun setHiddenState() {
        binding?.zfileListToolBar?.post {
            val menu = binding?.zfileListToolBar?.menu
            val showMenuItem = menu?.findItem(R.id.menu_zfile_show)
            val hiddenMenuItem = menu?.findItem(R.id.menu_zfile_hidden)
            if (getZFileConfig().showHiddenFile) {
                showMenuItem?.isChecked = true
            } else {
                hiddenMenuItem?.isChecked = true
            }
        }
    }

    fun observer(isSuccess: Boolean) {
        if (isSuccess) getData(nowPath)
    }

    private fun setBarTitle(title: String) {
        when (getZFileConfig().titleGravity) {
            ZFileConfiguration.TITLE_LEFT -> {
                binding?.zfileListToolBar?.title = title
                binding?.zfileListCenterTitle?.visibility = View.GONE
            }
            else -> {
                binding?.zfileListToolBar?.title = ""
                binding?.zfileListCenterTitle?.visibility = View.VISIBLE
                binding?.zfileListCenterTitle?.text = title
            }
        }
    }

    override fun onDestroy() {
        ZFileUtil.resetAll()
        super.onDestroy()
        fileListAdapter?.reset()
        backList.clear()
        zFragmentListener = null
    }

}