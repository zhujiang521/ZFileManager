package com.zj.manager.dsl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zj.file.content.ZFileConfiguration
import com.zj.file.dsl.config
import com.zj.file.dsl.fileType
import com.zj.file.dsl.result
import com.zj.file.dsl.zfile
import com.zj.manager.content.Content
import com.zj.manager.databinding.FragmentDslBinding

class DslFragment : Fragment() {

    private var binding: FragmentDslBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDslBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.dslFragmentStartBtn?.setOnClickListener {
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
                binding?.dslFragInclude?.mainResultTxt?.text = sb.toString()
            }
        }
    }

}