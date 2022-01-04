package com.zj.file.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zj.file.R

/**
 * 权限工具类
 *
 * [requestPermissions] 获取多个权限
 * [requestPermission] 获取单个权限
 */
internal object ZFilePermissionUtil {

    /** 读写SD卡权限  */
    const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

    /**
     * 判断是否申请过权限
     * @param permissions   权限
     * @return true表示没有申请过
     */
    fun hasPermission(context: Context, vararg permissions: String) =
        permissions.any {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }

    /**
     * 请求多个权限
     * @param hasPermissionListener  成功获取权限的回调
     * @param noPermissionListener 没有获取权限的回调
     * @param requestPermission 权限
     */
    fun requestPermissions(
        fragmentOrActivity: Any,
        hasPermissionListener: () -> Unit,
        noPermissionListener: () -> Unit,
        vararg requestPermission: String
    ) {
        val launcher: ActivityResultLauncher<Array<String>>? = when (fragmentOrActivity) {
            is AppCompatActivity -> {
                fragmentOrActivity.registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { result ->
                    var hasPermission = true
                    requestPermission.forEach {
                        if (result[it] == false) {
                            hasPermission = false
                        }
                    }
                    if (hasPermission) {
                        //权限全部获取到之后的动作
                        hasPermissionListener()
                    } else {
                        //有权限没有获取到的动作
                        noPermissionListener()
                    }
                }
            }
            is Fragment -> {
                fragmentOrActivity.registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { result ->
                    var hasPermission = true
                    requestPermission.forEach {
                        if (result[it] == false) {
                            hasPermission = false
                        }
                    }
                    if (hasPermission) {
                        //权限全部获取到之后的动作
                        hasPermissionListener()
                    } else {
                        //有权限没有获取到的动作
                        noPermissionListener()
                    }
                }
            }
            else -> null
        }
        launcher?.launch(requestPermission.asList().toTypedArray())
    }

    /**
     * 请求单个权限
     * @param hasPermissionListener  成功获取权限的回调
     * @param noPermissionListener 没有获取权限的回调
     * @param requestPermission 权限
     */
    fun requestPermission(
        fragmentOrActivity: Any,
        hasPermissionListener: () -> Unit,
        noPermissionListener: () -> Unit,
        requestPermission: String
    ) {
        val launcher: ActivityResultLauncher<String>? = when (fragmentOrActivity) {
            is AppCompatActivity -> {
                fragmentOrActivity.registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { result ->
                    if (result.equals(true)) {
                        //权限获取到之后的动作
                        hasPermissionListener()
                    } else {
                        //权限没有获取到的动作
                        noPermissionListener()
                    }
                }
            }
            is Fragment -> {
                fragmentOrActivity.registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { result ->
                    if (result.equals(true)) {
                        //权限获取到之后的动作
                        hasPermissionListener()
                    } else {
                        //权限没有获取到的动作
                        noPermissionListener()
                    }
                }
            }
            else -> null
        }
        launcher?.launch(requestPermission)
    }

}

/**
 * 简化Fragment获取权限步骤
 *
 * @param hasPermissionListener 有权限的操作
 * @param noPermissionListener 没有权限，但点对话框中同意的操作
 * @param cancelPermissionListener 没有权限，但点对话框中取消的操作
 */
fun Fragment.callStoragePermission(
    hasPermissionListener: (Boolean) -> Unit,
    noPermissionListener: () -> Unit,
    cancelPermissionListener: () -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
        val hasPermission =
            ZFilePermissionUtil.hasPermission(
                requireContext(),
                ZFilePermissionUtil.WRITE_EXTERNAL_STORAGE
            )
        if (hasPermission) {
            ZFilePermissionUtil.requestPermission(
                fragmentOrActivity = this,
                hasPermissionListener = { hasPermissionListener(false) },
                noPermissionListener = noPermissionListener,
                requestPermission = ZFilePermissionUtil.WRITE_EXTERNAL_STORAGE
            )
        } else {
            hasPermissionListener(true)
        }
    } else {
        hasPermissionListener(false)
        requireContext().noStoragePermissionDialog(noPermissionListener, cancelPermissionListener)
    }
}

/**
 * 没有权限弹出的对话框
 *
 * @param noPermissionListener 没有权限，但点对话框中同意的操作
 * @param cancelPermissionListener 没有权限，但点对话框中取消的操作
 */
@RequiresApi(Build.VERSION_CODES.R)
private fun Context.noStoragePermissionDialog(
    noPermissionListener: () -> Unit,
    cancelPermissionListener: () -> Unit
) {
    commonDialog(
        title = R.string.zfile_11_title,
        content = R.string.zfile_11_content,
        cancelListener = {
            cancelPermissionListener()
            showToast(R.string.zfile_11_bad)
        },
        finishListener = {
            noPermissionListener()
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
        })
}

/**
 * 简化Actvity获取权限步骤
 *
 * @param hasPermissionListener 有权限的操作
 * @param noPermissionListener 没有权限，但点对话框中同意的操作
 * @param cancelPermissionListener 没有权限，但点对话框中取消的操作
 */
fun AppCompatActivity.callStoragePermission(
    hasPermissionListener: () -> Unit,
    noPermissionListener: () -> Unit,
    cancelPermissionListener: () -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
        val hasPermission =
            ZFilePermissionUtil.hasPermission(this, ZFilePermissionUtil.WRITE_EXTERNAL_STORAGE)
        if (hasPermission) {
            ZFilePermissionUtil.requestPermission(
                fragmentOrActivity = this,
                hasPermissionListener = hasPermissionListener,
                noPermissionListener = noPermissionListener,
                requestPermission = ZFilePermissionUtil.WRITE_EXTERNAL_STORAGE
            )
        } else {
            hasPermissionListener()
        }
    } else {
        noStoragePermissionDialog(noPermissionListener, cancelPermissionListener)
    }
}