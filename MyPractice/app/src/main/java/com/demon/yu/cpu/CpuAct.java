package com.demon.yu.cpu;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mypractice.R;
import com.ksy.statlibrary.util.Cpu;

public class CpuAct extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu);
        TextView view= findViewById(R.id.tick);
        view.setText("cpu_tick : " + CpuManager.INSTANCE.getClkTck());
    }
}
