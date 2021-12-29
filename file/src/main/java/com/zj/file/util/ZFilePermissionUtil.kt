package com.zj.file.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zj.file.R


internal object ZFilePermissionUtil {

    /** 读写SD卡权限  */
    const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    const val WRITE_EXTERNAL_CODE = 0x1001

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
     * 请求权限
     * @param code  请求码
     * @param requestPermission 权限
     */
    fun requestPermission(fragmentOrActivity: Any, code: Int, vararg requestPermission: String) {
        when (fragmentOrActivity) {
            is Activity -> ActivityCompat.requestPermissions(
                fragmentOrActivity,
                requestPermission,
                code
            )
            is Fragment -> fragmentOrActivity.requestPermissions(requestPermission, code)
        }

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
                this,
                ZFilePermissionUtil.WRITE_EXTERNAL_CODE,
                ZFilePermissionUtil.WRITE_EXTERNAL_STORAGE
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
fun Activity.callStoragePermission(
    hasPermissionListener: () -> Unit,
    noPermissionListener: () -> Unit,
    cancelPermissionListener: () -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
        val hasPermission =
            ZFilePermissionUtil.hasPermission(this, ZFilePermissionUtil.WRITE_EXTERNAL_STORAGE)
        if (hasPermission) {
            ZFilePermissionUtil.requestPermission(
                this,
                ZFilePermissionUtil.WRITE_EXTERNAL_CODE,
                ZFilePermissionUtil.WRITE_EXTERNAL_STORAGE
            )
        } else {
            hasPermissionListener()
        }
    } else {
        noStoragePermissionDialog(noPermissionListener, cancelPermissionListener)
    }
}