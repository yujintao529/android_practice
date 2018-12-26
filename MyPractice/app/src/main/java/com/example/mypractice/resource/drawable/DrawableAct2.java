package com.example.mypractice.resource.drawable;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.mypractice.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>
 * <p>Company: 浙江齐聚科技有限公司<a href="www.guagua.cn">www.guagua.cn</a></p>
 *
 * @author yujintao
 * @version 1.0.0
 * @description
 * @modify
 */

public class DrawableAct2 extends AppCompatActivity {

    @BindView(R.id.image1)
    ImageView imageView1;
    @BindView(R.id.image2)
    ImageView imageView2;
    @BindView(R.id.image3)
    ImageView imageView3;
    @BindView(R.id.image4)
    ImageView imageView4;
    @BindView(R.id.image5)
    ImageView imageView5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable_layout);
        ButterKnife.bind(this);

        Drawable origin = getResources().getDrawable(R.drawable.drawable_tint_base);
        imageView1.setImageDrawable(origin);
//
        Drawable drawable = getResources().getDrawable(R.drawable.drawable_tint_base).mutate();
        origin.setAlpha(150);
        imageView2.setImageDrawable(drawable);
//        //使用colorStateList
//        drawable=origin.getConstantState().newDrawable();
//        drawable=DrawableCompat.wrap(drawable);
//        DrawableCompat.setTintList(drawable,getResources().getColorStateList(R.color.drawable_color_state));
//        imageView3.setImageDrawable(drawable);
//        imageView3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        drawable=origin.getConstantState().newDrawable().mutate();
//        drawable=DrawableCompat.wrap(drawable);
//        ColorStateList colorStateList =ColorStateList.valueOf(getResources().getColor(R.color.color_CG));
//        DrawableCompat.setTintList(drawable,colorStateList);
//        imageView4.setImageDrawable(drawable);
//        imageView4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }
}
