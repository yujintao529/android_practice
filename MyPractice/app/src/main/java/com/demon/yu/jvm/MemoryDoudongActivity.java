package com.demon.yu.jvm;

import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mypractice.R;

import java.util.ArrayList;
import java.util.List;

public class MemoryDoudongActivity extends AppCompatActivity {
    private HandlerThread handlerThread;

    private List<BigObject> bigObjects = new ArrayList<>(20000);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_doudong);
        handlerThread = new HandlerThread("no ui");
        handlerThread.start();
//        final Handler handler=new Handler(handlerThread.getLooper());
        final Handler handler = new Handler();
        findViewById(R.id.doudong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 5000; i++) {
                            BigObject bigObject = new BigObject();
                            bigObjects.add(bigObject);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quit();
    }
}
