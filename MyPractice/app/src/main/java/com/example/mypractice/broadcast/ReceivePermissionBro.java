package com.example.mypractice.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mypractice.Logger;

/**
 * @author yujintao
 * @version 1.0.0
 * @description
 * @modify
 */
public class ReceivePermissionBro extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("onReceive "+intent.getAction());
    }
}
