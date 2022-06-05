package com.example.mypractice.resource.drawable;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;

import com.demon.yu.view.fresco.FrescoAvatarUtils;
import com.example.mypractice.R;
import com.facebook.drawee.view.SimpleDraweeView;

import androidx.core.graphics.drawable.DrawableCompat;

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

public class DrawableAct extends AppCompatActivity {

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
        Drawable origin = getResources().getDrawable(R.drawable.emotion_black);
        imageView1.setImageDrawable(origin);

        //生成一个新的drawable
//        Drawable drawable = DrawableCompat.wrap(origin.getConstantState().newDrawable().mutate());
//        DrawableCompat.setTint(drawable, 0xFFCECFCF);
//
//        imageView2.setImageDrawable(drawable);
        //使用colorStateList
//        drawable = origin.getConstantState().newDrawable();
//        drawable = DrawableCompat.wrap(drawable);
//        DrawableCompat.setTintList(drawable, getResources().getColorStateList(R.color.drawable_color_state));

        Drawable dest = getResources().getDrawable(R.drawable.emotion_white);
        imageView3.setImageDrawable(dest);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        SimpleDraweeView simpleDraweeView=findViewById(R.id.simpleDraweeView);
        FrescoAvatarUtils.INSTANCE.bindIconWithTint(simpleDraweeView,R.drawable.emotion_black);
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
