package com.example.mypractice.v4;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mypractice.Logger;
import com.example.mypractice.R;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
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


    @BindView(R.id.tab_layout_2)
    MfwTabLayout tabLayout2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        ButterKnife.bind(this);
//        MfwTabLayout.Tab tab=mfwTabLayout.newTab();
//        tab.setTitle("我的title很长1");
//        mfwTabLayout.addTab(tab);
//        tab=mfwTabLayout.newTab();
//        tab.setTitle("我的title很长2");
//        mfwTabLayout.addTab(tab);
//        tab=mfwTabLayout.newTab();
//        tab.setTitle("我的title很长3");
//        mfwTabLayout.addTab(tab);
//        tab=mfwTabLayout.newTab();
//        tab.setTitle("我的title很长4");
//        mfwTabLayout.addTab(tab);
        final int[] images = new int[]{R.drawable.resource_1, R.drawable.resource_2, R.drawable.resource_3, R.drawable.resource_4};
//        final int[] images=new int[]{R.drawable.resource_1,R.drawable.resource_2};
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return images.length;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.tab_layout_view_pager_item, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.image);
                imageView.setImageResource(images[position]);
                container.addView(view);
                return view;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "我的图片名字很长" + position;
            }
        });
        mfwTabLayout.setupViewPager(viewPager);
//        viewPager.getAdapter().notifyDataSetChanged();
        MfwTabLayout.Tab tab = tabLayout2.newTab();
        tab.setTitle("我的我的123");
        tabLayout2.addTab(tab);
        tab = tabLayout2.newTab();
        tab.setTitle("我的title123");
        tabLayout2.addTab(tab);
        tab = tabLayout2.newTab();
        tab.setTitle("我的我的");
        tabLayout2.addTab(tab);

        tabLayout2.addTabSelectListener(new MfwTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(MfwTabLayout.Tab tab) {
                Logger.d("tab seleonTabSelectedcted " + tab);
            }

            @Override
            public void onTabUnselected(MfwTabLayout.Tab tab) {
                Logger.d("tab onTabUnselected " + tab);
            }
        });

    }


    @OnClick(R.id.add)
    public void onAdd() {
        MfwTabLayout.Tab tab = tabLayout2.newTab().setTitle("我的名称");
        tabLayout2.addTab(tab);
    }

    @OnClick(R.id.remove)
    public void onDelete(){
        tabLayout2.removeIndex(0);
    }

    @OnClick(R.id.set_tab_4)
    public void onSelect4() {
        viewPager.setCurrentItem(3);
    }
}
