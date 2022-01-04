package com.zj.manager;

import static com.zj.file.content.ZFileContentKt.ZFILE_DEFAULT;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zj.file.common.ZFileManageHelp;
import com.zj.file.content.ZFileBean;
import com.zj.file.content.ZFileConfiguration;
import com.zj.manager.content.Content;


public class JavaSampleActivity extends AppCompatActivity {

    private TextView resultTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_sample);
        // 图片显示自定义配置
        ZFileConfiguration.ZFileResources resources = new ZFileConfiguration.ZFileResources(R.drawable.ic_alipay, ZFILE_DEFAULT);
        // 操作自定义配置
        final ZFileConfiguration configuration = new ZFileConfiguration.Build()
                .resources(resources)
                .boxStyle(ZFileConfiguration.STYLE1)
                .sortordBy(ZFileConfiguration.BY_DEFAULT)
                .maxLength(3)
                .maxLengthStr("亲，最多选3个！")
                .authority(Content.AUTHORITY)
                .build();
        resultTxt = findViewById(R.id.main_resultTxt);

        findViewById(R.id.java_startBtn).setOnClickListener(v -> ZFileManageHelp.getInstance()
                .setConfiguration(configuration)
                .start(JavaSampleActivity.this, selectList -> {
                    if (selectList == null || selectList.size() <= 0) {
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (ZFileBean bean : selectList) {
                        sb.append(bean.toString()).append("\n\n");
                    }
                    resultTxt.setText(sb.toString());
                }));
    }

}
