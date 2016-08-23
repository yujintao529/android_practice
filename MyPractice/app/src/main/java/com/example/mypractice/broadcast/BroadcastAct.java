package com.example.mypractice.broadcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mypractice.R;

/**
 * <p>Copyright: Copyright (c) 2016</p>
 * <p/>
 * <p>Company: 浙江齐聚科技有限公司<a href="www.guagua.cn">www.guagua.cn</a></p>
 *
 * @author yujintao
 * @version 1.0.0
 * @description
 * @modify
 */
public class BroadcastAct extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_broadcast:
                sendBroadcast(new Intent(this, StanderBro.class));
                break;
            case R.id.send_receive_broadcast:
                Intent intent=new Intent();
                intent.setAction("com.example.action.TEST_RECEIVE_BRO");
                sendBroadcast(intent,"com.example.practice.RECEIVE_PRO");
                //发送带有com.example.practice.RECEIVE_PRO权限的广播，那么必须在manifest文件里声明相应的权限才可以接收到
                break;
            case R.id.send_send_broadcast:
                 intent=new Intent();
                intent.setAction("com.example.action.TEST_SNED_BRO");
                sendBroadcast(intent);
                //sendPermissionBro声明了com.example.practice.SEND_PRO这个权限，所以发送广播的app必须要声明这个权限，
                //才可以接收到他发送的广播。也就是谁能向我发送这个广播。同一个app好像这个权限不起作用。。。无论是否声明都可以发送
                break;
        }
    }
}
