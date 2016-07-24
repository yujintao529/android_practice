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
public class ScrollerStudyAct extends Activity{

    static int[] resources={R.drawable.lol_jinkesi,R.drawable.lol_mangseng,R.drawable.lol_mangseng2,R.drawable.lol_qinnv};

    ScrollerLinearLayout myScrollerLinearLayout=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroller);
        myScrollerLinearLayout= (ScrollerLinearLayout) findViewById(R.id.scrollerlinearlayout);
        myScrollerLinearLayout.addView(createImageView(randomID(resources)));
        myScrollerLinearLayout.addView(createImageView(randomID(resources)));
        myScrollerLinearLayout.addView(createImageView(randomID(resources)));
        myScrollerLinearLayout.addView(createImageView(randomID(resources)));
        myScrollerLinearLayout.addView(createImageView(randomID(resources)));
        myScrollerLinearLayout.addView(createImageView(randomID(resources)));


    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.scroll:
                myScrollerLinearLayout.smoothScrollBy(100,0);

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
