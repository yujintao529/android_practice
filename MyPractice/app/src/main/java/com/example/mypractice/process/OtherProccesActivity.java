package com.example.mypractice.process;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.mypractice.Logger;
import com.example.mypractice.R;

public class OtherProccesActivity extends AppCompatActivity {

    private IBinder iBinder;

    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder);
        Intent intent = new Intent(this, BinderService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iBinder = service;
                IPlus iPlus = PlusStub.asInterface(service);
                iPlus.add(3, 4);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                iBinder = null;
            }
        };
        bindService(intent, serviceConnection, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
