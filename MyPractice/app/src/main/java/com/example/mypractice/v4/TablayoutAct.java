package com.example.mypractice.v4;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mypractice.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yujintao on 2017/3/16.
 */

public class TablayoutAct extends AppCompatActivity {
    @BindView(R.id.tab_layout_1)
    MfwTabLayout mfwTabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.set_tab_4)
    Button select4;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        ButterKnife.bind(this);
        MfwTabLayout.Tab tab=mfwTabLayout.newTab();
        tab.setTitle("我的title很长1");
        mfwTabLayout.addTab(tab);
        tab=mfwTabLayout.newTab();
        tab.setTitle("我的title很长2");
        mfwTabLayout.addTab(tab);
        tab=mfwTabLayout.newTab();
        tab.setTitle("我的title很长3");
        mfwTabLayout.addTab(tab);
        tab=mfwTabLayout.newTab();
        tab.setTitle("我的title很长4");
        mfwTabLayout.addTab(tab);
        final int[] images=new int[]{R.drawable.resource_1,R.drawable.resource_2,R.drawable.resource_3,R.drawable.resource_4};
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return images.length;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view=LayoutInflater.from(getBaseContext()).inflate(R.layout.tab_layout_view_pager_item,null);
                ImageView imageView= (ImageView) view.findViewById(R.id.image);
                imageView.setImageResource(images[position]);
                container.addView(view);
                return view;
            }
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "我的图片名字很长"+position;
            }
        });
        mfwTabLayout.setupViewPager(viewPager);
//        viewPager.getAdapter().notifyDataSetChanged();
//        tab=mfwTabLayout.newTab();
//        tab.setTitle("我的title很长5");
//        mfwTabLayout.addTab(tab);
//        tab=mfwTabLayout.newTab();
//        tab.setTitle("我的title很长6");
//        mfwTabLayout.addTab(tab);
//        tab=mfwTabLayout.newTab();
//        tab.setTitle("我的title很长7");
//        mfwTabLayout.addTab(tab);
    }

    @OnClick(R.id.set_tab_4)
    public void onSelect4(){
        viewPager.setCurrentItem(3);
    }
}
