package com.example.mypractice.v7;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.example.mypractice.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jintao on 2015/9/1.
 */
public class RecycleViewAct extends Activity {
    RecyclerView recyclerView;
    RecycleAdapter recycleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v7_recycleview);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager manager= new GridLayoutManager(this,1, LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(manager);
        recycleAdapter=new RecycleAdapter();
        recyclerView.setAdapter(recycleAdapter);
        HorizontalScrollView horizontalScrollView=new HorizontalScrollView(getApplication());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                Logger.d("onscrolled recycleview");
                int childCount = recyclerView.getChildCount();
                int width = recyclerView.getChildAt(0).getWidth();
                int padding = (recyclerView.getWidth() - width) / 2;

                for (int j = 0; j < childCount; j++) {
                    View v = recyclerView.getChildAt(j);
                    float rate = 0;
                    if (v.getLeft() <= padding) {
                        if (v.getLeft() >= padding - v.getWidth()) {
                            rate = (padding - v.getLeft()) * 1f / v.getWidth();
                        } else {
                            rate = 1;
                        }

                        v.setScaleY(1 - rate * 0.1f);
                        v.setScaleX(1 - rate * 0.1f);

                    } else {
                        if (v.getLeft() <= recyclerView.getWidth() - padding) {
                            rate = (recyclerView.getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
                        }
                        v.setScaleY(0.9f + rate * 0.1f);
                        v.setScaleX(0.9f + rate * 0.1f);

                    }
                }
            }
        });
//        recyclerView.fling(20,10);

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.fling:
                recyclerView.fling(100,0);
                break;
        }

    }

    public  class RecycleAdapter extends RecyclerView.Adapter<MyViewHolder>{

        private List<Integer> imageID;

        RecycleAdapter(){
            imageID=new ArrayList<Integer>();
            imageID.add(R.drawable.lol_jinkesi);
            imageID.add(R.drawable.lol_mangseng);
            imageID.add(R.drawable.lol_mangseng2);
            imageID.add(R.drawable.lol_qinnv);

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            ImageView imageView=new ImageView(RecycleViewAct.this);
//            imageView.setPadding(20,20,20,20);
//            imageView.setCropToPadding();
//            imageView.setAdjustViewBounds(true);
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ImageView imageView = (ImageView) LayoutInflater.from(RecycleViewAct.this).inflate(R.layout.activity_imageview,null);
            return new MyViewHolder(imageView);
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.imageView.setImageResource(imageID.get(position));
        }

        @Override
        public int getItemCount() {
            return imageID.size();
        }
    }



    public class MyViewHolder extends  RecyclerView.ViewHolder{
        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView= (ImageView) itemView;
        }
    }
}
