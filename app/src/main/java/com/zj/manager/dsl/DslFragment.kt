package com.zj.manager.dsl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zj.manager.R
import com.zj.manager.content.Content
import com.zj.file.content.ZFileConfiguration
import com.zj.file.dsl.config
import com.zj.file.dsl.fileType
import com.zj.file.dsl.result
import com.zj.file.dsl.zfile
import kotlinx.android.synthetic.main.fragment_dsl.*
import kotlinx.android.synthetic.main.layout_result_txt.*

class DslFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dsl, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dsl_fragmentStartBtn.setOnClickListener {
            dsl()
        }
    }

    private fun dsl() {
        zfile {
            config {
                Content.CONFIG.run {
                    filePath = ZFileConfiguration.WECHAT
                    this
                }
            }
            fileType {
                MyDslFileTypeListener()
            }
            result {
                val sb = StringBuilder()
                this?.forEach {
                    sb.append(it).append("\n\n")
                }
                main_resultTxt.text = sb.toString()
            }
        }
    }

}