package com.zj.manager.dsl

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.zj.file.content.ZFileBean
import com.zj.file.dsl.config
import com.zj.file.dsl.result
import com.zj.file.dsl.zfile
import com.zj.manager.content.Content
import com.zj.manager.databinding.ActivityDslBinding

class DslActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDslBinding
    private var anim: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDslBinding.inflate(layoutInflater)
        setContentView(binding.root)
        anim = getAnim()
        anim?.start()
        binding.dslStartBtn.setOnClickListener {
            dsl()
        }
        binding.dslFragmentBtn.setOnClickListener {
            startActivity(Intent(this, DslFragmentActivity::class.java))
        }
    }

    private fun dsl() {
        zfile {
            config {
                Content.CONFIG
            }
            result {
                setResultData(this)
            }
        }
    }

    private fun setResultData(selectList: MutableList<ZFileBean>?) {
        val sb = StringBuilder()
        selectList?.forEach {
            sb.append(it).append("\n\n")
        }
        binding.dslInclude.mainResultTxt.text = sb.toString()
    }

    override fun onDestroy() {
        anim?.apply {
            end()
            cancel()
            removeAllListeners()
            removeAllUpdateListeners()
        }
        anim = null
        super.onDestroy()
    }

    private fun getAnim() = ObjectAnimator.ofFloat(binding.dslDslTxt, "rotation", 0f, 360f).run {
        duration = 5000L
        repeatCount = -1
        interpolator = LinearInterpolator()
        this
    }
}