package com.example.mypractice.process;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mypractice.R;
import com.example.mypractice.R2;

import javax.crypto.spec.RC2ParameterSpec;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <p>Copyright: Copyright (c) 2016</p>
 * <p/>
 * <p>Company: 浙江齐聚科技有限公司<a href="www.guagua.cn">www.guagua.cn</a></p>
 *
 * @author yujintao
 * @version 1.0.0
 * @description
 *
 * 通过adbshell ，在proc/pid/oom_adj查看oom_adj的值
 *
 * @modify
 */
public class ProcessAct extends AppCompatActivity {

    @BindView(R.id.start_forground_service)
    View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        ButterKnife.bind(this);

    }
    @OnClick({R.id.start_forground_service})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.start_forground_service:
                startService(new Intent(this,ProcessService.class));
                break;
            case R.id.stop_service:
                stopService(new Intent(this,ProcessService.class));
                break;
        }
    }
}
