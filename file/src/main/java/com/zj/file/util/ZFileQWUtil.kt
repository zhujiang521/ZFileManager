package com.zj.file.util

import android.content.Context
import androidx.collection.ArrayMap
import com.zj.file.content.*
import com.zj.file.content.QQ_DOWLOAD1
import com.zj.file.content.QQ_DOWLOAD2
import com.zj.file.content.QQ_PIC
import com.zj.file.content.QQ_PIC_MOVIE
import com.zj.file.content.QW_SIZE
import com.zj.file.content.WECHAT_DOWLOAD
import com.zj.file.content.WECHAT_FILE_PATH
import com.zj.file.content.WECHAT_PHOTO_VIDEO
import com.zj.file.R
import com.zj.file.listener.ZFileQWFilter
import java.io.File

internal object ZFileQWUtil {

    /**
     * 显示的标题 [getQWTitle]
     */
    fun getQWTitle(context: Context): Array<String> {
        val titles = getZFileConfig().qwData.titles
        if (titles.isNullOrEmpty()) {
            return arrayOf(
                context getStringById R.string.zfile_pic,
                context getStringById R.string.zfile_video,
                context getStringById R.string.zfile_txt,
                context getStringById R.string.zfile_other
            )
        }
        if (titles.size != QW_SIZE) {
            throw ZFileException("ZFileQWData.titles size must be $QW_SIZE")
        }
        return titles
    }

    /**
     * 文件过滤规则的 Map
     */
    fun getQWFilterMap(): ArrayMap<Int, Array<String>> {
        val filterMap = getZFileConfig().qwData.filterArrayMap
        if (filterMap.isNullOrEmpty()) {
            val map = ArrayMap<Int, Array<String>>()
            map[ZFILE_QW_PIC] = arrayOf(PNG, JPEG, JPG, SVG, GIF)
            map[ZFILE_QW_MEDIA] = arrayOf(MP4, _3GP)
            map[ZFILE_QW_DOCUMENT] = arrayOf(TXT, JSON, XML, DOC, DOCX, XLS, XLSX, PPT, PPTX, PDF)
            map[ZFILE_QW_OTHER] = arrayOf("")
            return map
        }
        if (filterMap.size != QW_SIZE) {
            throw ZFileException("ZFileQWData.filterArrayMap size must be $QW_SIZE")
        }
        return filterMap
    }

    /**
     * QQ 保存至本地SD卡上路径的 Map
     */
    fun getQQFilePathMap(): ArrayMap<Int, MutableList<String>> {
        val qqMap = getZFileConfig().qwData.qqFilePathArrayMap
        if (qqMap.isNullOrEmpty()) {
            val map = ArrayMap<Int, MutableList<String>>()
            map[ZFILE_QW_PIC] =
                arrayListOf(QQ_PIC, QQ_PIC_MOVIE, QQ_DOWLOAD1, QQ_DOWLOAD2, QQ_DOWLOAD3)
            map[ZFILE_QW_MEDIA] = arrayListOf(QQ_PIC_MOVIE)
            map[ZFILE_QW_DOCUMENT] = arrayListOf(QQ_DOWLOAD1, QQ_DOWLOAD2, QQ_DOWLOAD3)
            map[ZFILE_QW_OTHER] = arrayListOf(QQ_DOWLOAD1, QQ_DOWLOAD2, QQ_DOWLOAD3)
            return map
        }
        return qqMap
    }

    /**
     * Wechat 保存至本地SD卡上路径的 Map
     */
    fun getWechatFilePathMap(): ArrayMap<Int, MutableList<String>> {
        val wechatMap = getZFileConfig().qwData.wechatFilePathArrayMap
        if (wechatMap.isNullOrEmpty()) {
            val map = ArrayMap<Int, MutableList<String>>()
            map[ZFILE_QW_PIC] =
                arrayListOf(
                    WECHAT_FILE_PATH + WECHAT_PHOTO_VIDEO,
                    WECHAT_NEW_PHOTO_VIDEO,
                    WECHAT_FILE_PATH + WECHAT_DOWLOAD,
                    WECHAT_NEW_DOWLOAD
                )
            map[ZFILE_QW_MEDIA] =
                arrayListOf(WECHAT_FILE_PATH + WECHAT_PHOTO_VIDEO, WECHAT_NEW_PHOTO_VIDEO)
            map[ZFILE_QW_DOCUMENT] =
                arrayListOf(WECHAT_FILE_PATH + WECHAT_DOWLOAD, WECHAT_NEW_DOWLOAD)
            map[ZFILE_QW_OTHER] = arrayListOf(WECHAT_FILE_PATH + WECHAT_DOWLOAD, WECHAT_NEW_DOWLOAD)
            return map
        }
        return wechatMap
    }

    /**
     * 获取QQ、Wechat文件
     * @param filePathArray     路径
     * @param filterArray       过滤规则
     */
    fun getQWFileData(
        filePathArray: MutableList<String>,
        filterArray: Array<String>
    ): MutableList<ZFileBean> {
        val listArrayList = arrayListOf<File>()
        filePathArray.forEach {
            val file = it.toFile()
            if (file.exists()) {
                val listFiles = file.listFiles(ZFileQWFilter(filterArray))
                if (listFiles != null && listFiles.isNotEmpty()) {
                    listArrayList.addAll(listFiles)
                }
            }
        }
        val list = ArrayList<ZFileBean>()
        listArrayList.forEach {
            if (!it.isHidden) {
                val bean = ZFileBean(
                    it.name,
                    it.isFile,
                    it.path,
                    ZFileOtherUtil.getFormatFileDate(it.lastModified()),
                    it.lastModified().toString(),
                    ZFileOtherUtil.getFileSize(it.length()),
                    it.length()
                )
                list.add(bean)
            }
        }
        if (!list.isNullOrEmpty()) {
            list.sortByDescending { it.originalDate }
        }
        return list
    }
}