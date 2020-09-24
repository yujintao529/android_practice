package com.example.mypractice.v7;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mypractice.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by jintao on 2015/9/1.
 */
public class RecycleViewPagerAct extends Activity {
    RecyclerViewPager recyclerView;
    RecycleAdapter recycleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v7_recycleviewpager);
        recyclerView=(RecyclerViewPager)findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager manager= new GridLayoutManager(this,1, LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recycleAdapter=new RecycleAdapter();
        recyclerView.setAdapter(recycleAdapter);
        final Paint paint=new Paint();
        paint.setColor(Color.RED);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                c.drawCircle(20,20,10,paint);
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent) {
                super.onDraw(c, parent);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent) {
                super.onDrawOver(c, parent);
            }

            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                super.getItemOffsets(outRect, itemPosition, parent);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });


//        recyclerView.offsetLeftAndRight(2);
//        recyclerView.setItemAnimator();
//        recyclerView.smoothScrollToPosition(2);
//        recyclerView.
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
//            ImageView imageView=new ImageView(RecycleViewPagerAct.this);
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ImageView imageView = (ImageView) LayoutInflater.from(RecycleViewPagerAct.this).inflate(R.layout.activity_imageview,null);
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
