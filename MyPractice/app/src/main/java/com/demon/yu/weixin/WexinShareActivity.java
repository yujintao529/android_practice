package com.demon.yu.weixin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mypractice.R;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class WexinShareActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_weixin_share);
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appId = "wx280b69d6c0938cd7"; // 填应用AppId
                IWXAPI api = WXAPIFactory.createWXAPI(getBaseContext(), appId);
                api.registerApp(appId);


                WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
                miniProgramObj.webpageUrl = "http://www.qq.com"; // 兼容低版本的网页链接
                miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;// 正式版:0，测试版:1，体验版:2
                miniProgramObj.userName = "gh_d05a44429654";     // 小程序原始id
                miniProgramObj.path = "/pages/index/index";            //小程序页面路径
                WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
                msg.title = "小程序消息Title";                    // 小程序消息title
                msg.description = "小程序消息Desc";               // 小程序消息desc
                msg.thumbData = getThumb();                      // 小程序消息封面图片，小于128k

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction();
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前支持会话
                api.sendReq(req);
            }


        });
    }

    private String buildTransaction() {
        return String.valueOf(System.currentTimeMillis());
    }

    private byte[] getThumb() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lol_mangseng2, options);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

}
