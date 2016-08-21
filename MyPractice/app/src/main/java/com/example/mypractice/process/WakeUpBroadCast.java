package com.example.mypractice.process;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mypractice.Logger;

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
public class WakeUpBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("WakeUpBroadCast context "+context);
        context.startService(new Intent(context,ProcessService.class));
    }
}
