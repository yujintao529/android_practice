package com.example.mypractice.process;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BinderService extends Service {

    private PlusStub plusStub;

    @Override
    public void onCreate() {
        super.onCreate();
        plusStub = new PlusStub(){
            @Override
            public int add(int a, int b) {
                return 0;
            }
        };

    }


    @Override
    public IBinder onBind(Intent intent) {
        return plusStub;
    }
}
