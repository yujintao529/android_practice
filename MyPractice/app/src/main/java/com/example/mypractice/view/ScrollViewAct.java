package com.example.mypractice.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.mypractice.R;

import java.util.Random;

/**
 * Created by jintao on 2015/9/1.
 */
public class ScrollViewAct extends Activity{

    static int[] resources={R.drawable.lol_jinkesi,R.drawable.lol_mangseng,R.drawable.lol_mangseng2,R.drawable.lol_qinnv};

    ScrollViewGroup  scrollViewGroup=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
        scrollViewGroup= (ScrollViewGroup) findViewById(R.id.scroll_view);
        scrollViewGroup.addView(createImageView(randomID(resources)));
        scrollViewGroup.addView(createImageView(randomID(resources)));
        scrollViewGroup.addView(createImageView(randomID(resources)));
        scrollViewGroup.addView(createImageView(randomID(resources)));
        scrollViewGroup.addView(createImageView(randomID(resources)));
        scrollViewGroup.addView(createImageView(randomID(resources)));

    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.scroll:


                break;
        }
    }
    Random random=new Random();
    public int randomID(int[] resources){
        return resources[random.nextInt(resources.length)];
    }

    public ImageView createImageView(int id){
        ImageView imageView=new ImageView(this);
        imageView.setImageResource(id);
        return imageView;
    }


}
