package com.example.mypractice.process;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.example.mypractice.ApiLaunch;
import com.example.mypractice.Logger;

/**
 * <p>Copyright: Copyright (c) 2016</p>
 * <p/>
 * <p>Company: 浙江齐聚科技有限公司<a href="www.guagua.cn">www.guagua.cn</a></p>
 *
 * @author yujintao
 * @version 1.0.0
 * @description 启动前台的foreground的service，同时去掉通知栏
 * @modify
 */
public class ProcessService extends Service implements ServiceConnection{
    public static final int ID=3;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("onStartCommand "+intent.getAction());
        bindService(new Intent(this,InnerService.class),this, Context.BIND_AUTO_CREATE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(ID,new Notification());

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("ProcessService onDestroy");
        sendBroadcast(new Intent(this,WakeUpBroadCast.class));
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Logger.d("onServiceConnected "+name+" "+service);
        /**
         * 此处说明：就是如果相同进程的service方返回的就是InnerService.localService的对象如果不是
         * 的话，则是一个代理对象了
         */
        startForeground(ID,getMyNotification());
        InnerService.LocalService localService= (InnerService.LocalService) service;
        localService.innerService.startForeground(ID,getMyNotification());
        localService.innerService.stopForeground(true);
    }
    private Notification getMyNotification() {
        // 定义一个notification
        Notification.Builder builder = new Notification.Builder(getBaseContext());
        Intent notificationIntent = new Intent(this, ApiLaunch.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        builder.setContentIntent(pendingIntent).setContentText("content").setContentTitle("title");
        return builder.getNotification();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }



    public static class InnerService extends Service{

        public LocalService localService;
        public InnerService(){
            localService=new LocalService(this);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            Logger.d("InnerService onBind "+intent.getAction());
            return localService;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Logger.d("InnerService onStartCommand "+intent.getAction());
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public void onDestroy() {
            Logger.d("InnerService onDestroy");
            super.onDestroy();
        }

        @Override
        public boolean onUnbind(Intent intent) {
            Logger.d("InnerService onUnbind");
            return super.onUnbind(intent);
        }

        private class  LocalService extends Binder{
            InnerService innerService;
            public LocalService(InnerService innerService){
                this.innerService=innerService;
            }

        }
    }
}
