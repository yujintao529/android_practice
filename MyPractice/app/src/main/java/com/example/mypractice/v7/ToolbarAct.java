package com.example.mypractice.v7;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypractice.Logger;
import com.example.mypractice.R;

import java.util.ArrayList;

/**
 * Created by jintao on 2015/9/23.
 */
public class ToolbarAct extends AppCompatActivity implements View.OnClickListener {
    private float translateX = 0.3f;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private RecyclerView recyclerView;
    private ListView mListView;
    private ViewGroup mContent;
    private ViewPager mViewPager;
    private PagerTitleStrip mPagerTitleStrip;
    private static ArrayList<String> lists = new ArrayList<>();



    static{
        lists.add("item1");
        lists.add("item2");
        lists.add("item3");
        lists.add("item4");
        lists.add("item5");
    }

    /**
     * viewpager数据相关
     */

    private MyViewPagerAdapter mMyViewPagerAdapter;


    public static final PictureBean[] pictures=new PictureBean[5];
    {
        pictures[0]=createPictureBean(R.drawable.lol_jinkesi,"金克斯");
        pictures[1]=createPictureBean(R.drawable.lol_mangseng,"盲僧1");
        pictures[2]=createPictureBean(R.drawable.lol_mangseng2,"盲僧2");
        pictures[3]=createPictureBean(R.drawable.lol_qinnv,"琴女");
        pictures[4]=createPictureBean(R.drawable.lol_mangseng,"盲僧1");
    }

    public static final PictureBean createPictureBean(int drawable,String title) {
        PictureBean pictureBean=new PictureBean();
        pictureBean.drwaable=drawable;
        pictureBean.title=title;
        return pictureBean;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContent = (ViewGroup) findViewById(R.id.content);
        mViewPager= (ViewPager) findViewById(R.id.viewPager);
        mPagerTitleStrip= (PagerTitleStrip) findViewById(R.id.pagerTitleStrip);
        mPagerTitleStrip.setGravity(Gravity.CENTER);
        mMyViewPagerAdapter=new MyViewPagerAdapter(this,pictures);
        mViewPager.setAdapter(mMyViewPagerAdapter);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(new MyListViewAdapter(this));
        toolbar.setTitle("toolbartitle");
        toolbar.setSubtitle("subtitle");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                Logger.d("slide drawer %f ", slideOffset);
                final int width = mContent.getWidth();

                final float temp = slideOffset * translateX;
                float exeValue = Math.min(Math.max(0, temp), 1);
                mContent.setTranslationX(exeValue*width);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Logger.d("slide drawer open");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Logger.d("slide drawer close");
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                switch (newState) {
                    case DrawerLayout.STATE_DRAGGING:
                        Logger.d("slide drawer STATE_DRAGGING");
                        break;
                    case DrawerLayout.STATE_IDLE:
                        Logger.d("slide drawer STATE_IDLE");
                        break;
                    case DrawerLayout.STATE_SETTLING:
                        Logger.d("slide drawer STATE_SETTLING");
                        break;
                }
            }
        };
        mActionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onClick(View v) {

    }


    public static class MyListViewAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public MyListViewAdapter(Context context) {
            this.mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder myViewHolder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.recycler_item_toolbar, parent, false);
                myViewHolder = new ToolbarAct.MyViewHolder(convertView);
                myViewHolder.mItem = (TextView) convertView.findViewById(R.id.drawer_list);
                convertView.setTag(myViewHolder);
            } else {
                myViewHolder = (MyViewHolder) convertView.getTag();
            }
            final String info = (String) getItem(position);
            myViewHolder.mItem.setText(info);

            myViewHolder.mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();
                }
            });


            return convertView;
        }
    }


    public static class MyRecyclerViewAdapter<MyViewHolder extends RecyclerView.Adapter> extends RecyclerView.Adapter {

        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public MyRecyclerViewAdapter(Context context) {
            this.mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = mLayoutInflater.inflate(R.layout.recycler_item_toolbar, parent, false);
            ToolbarAct.MyViewHolder myViewHolder = new ToolbarAct.MyViewHolder(view);
            myViewHolder.mItem = (TextView) view.findViewById(R.id.drawer_list);
            return myViewHolder;
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ToolbarAct.MyViewHolder myViewHolder = (ToolbarAct.MyViewHolder) holder;

        }


        @Override
        public int getItemCount() {
            return lists.size();
        }


    }

    /**
     * recyclerview和list通用的adapter
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mItem;

        public MyViewHolder(View itemView) {
            super(itemView);
        }

    }


    /**
     * myViewPagerAdapter 适配器
     */
    public static class MyViewPagerAdapter extends PagerAdapter{
        private Context mContext;
        private PictureBean[] mDrawabls;
        private SparseArray<View> mLists;
        public MyViewPagerAdapter(Context context,PictureBean[] drawabls){
            mContext=context;
            mDrawabls=drawabls;
            mLists=new SparseArray<>();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mLists.valueAt(position));
        }
        private View createImage(int drawable){
            LayoutInflater layoutInflater=LayoutInflater.from(mContext);
            View view=layoutInflater.inflate(R.layout.activity_viewpager_image_item, null);
            ImageView mImageView= (ImageView) view.findViewById(R.id.image);
            mImageView.setImageResource(drawable);
            return view;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view=mLists.get(position);
            if(view == null ){
                view = createImage(mDrawabls[position].drwaable);
                mLists.setValueAt(position,view);
            }
            container.addView(view);
            return view;
        }

        public int getCount() {
            return mDrawabls.length;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mDrawabls[position].title;
        }
    }


    public static class PictureBean{
        public int drwaable;
        public String title;
    }
}
