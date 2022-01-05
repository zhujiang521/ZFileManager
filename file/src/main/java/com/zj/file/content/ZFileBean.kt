package com.zj.file.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ZFileBean(
        var fileName: String = "",      // 文件名
        var isFile: Boolean = true,     // true---文件；false---文件夹
        var filePath: String = "",      // 文件路径
        var date: String = "",          // 格式化后的时间
        var originalDate: String = "",  // 原始时间
        var size: String = "",          // 格式化后的大小
        var originaSize: Long = 0L,     // 原始大小
        var parent: String? = ""
) : Serializable, Parcelable

internal data class ZFileInfoBean(
        var duration: String = "",
        var width: String = "",
        var height: String = ""
)

data class ZFilePathBean(
        var fileName: String = "", // 路径名称
        var filePath: String = "" // 文件路径
)

internal data class ZFileQWBean(
        var zFileBean: ZFileBean? = null,
        var isSelected: Boolean = true
)



 