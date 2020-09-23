package com.example.mypractice.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.widget.ImageView;

import com.example.mypractice.R;

/**
 * Created by 郁金涛 on 2016/7/22 10:56
 * 邮箱：jintao@17guagua.com
 *
 * @description
 */
public class PicAndTextAct extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pic_text);
        String content="中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国";
        PicAndTextView2 picAndTextView2= (PicAndTextView2) findViewById(R.id.pic_txt);
        picAndTextView2.clean();
        picAndTextView2.addTextChild(content);
        ImageView imageView=new ImageView(this);
        imageView.setImageResource(R.drawable.share_icon_qq);
        picAndTextView2.addNewChild(imageView);
        picAndTextView2.addTextChild("郁金涛郁金涛郁金涛郁金涛郁金涛郁金涛");
        imageView=new ImageView(this);
        imageView.setImageResource(R.drawable.share_icon_qqzone);
        picAndTextView2.addNewChild(imageView);
        picAndTextView2.addTextChild("123456789101112113141516171819202122");
    }
}
