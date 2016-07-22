package com.example.mypractice.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.mypractice.R;

/**
 * Created by yujintao on 15/7/12.
 */
public class ColorMatrixView extends ImageView {

        private Paint myPaint = null;
        private Bitmap bitmap = null;
        private ColorMatrix myColorMatrix = null;
        private float[] colorArray = {1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0};


        public ColorMatrixView(Context context, AttributeSet attrs)
        {
            super(context, attrs);
//            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.resource_1);
            bitmap=Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
            Canvas canvas=new Canvas(bitmap);
            Paint paint=new Paint();
            paint.setColor(Color.BLACK);
            canvas.drawCircle(50,50,50,paint);
            invalidate();
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //新建画笔对象
            myPaint = new Paint();
            myPaint.setAlpha(100);
//            myPaint.setColor(Color.WHITE);
            //描画（原始图片）
//            canvas.drawBitmap(bitmap,0, 0, myPaint);
            //新建颜色矩阵对象
            myColorMatrix = new ColorMatrix();
            myColorMatrix.setSaturation(0.5f);
            //设置颜色矩阵的值
            myColorMatrix.set(colorArray);
            //设置画笔颜色过滤器
            myPaint.setColorFilter(new ColorMatrixColorFilter(myColorMatrix));
//            canvas.drawCircle(100,100,100,myPaint);
            //描画（处理后的图片）
            canvas.drawBitmap(bitmap,0,0,myPaint);
            invalidate();
        }
        //设置颜色数值
        public void setColorArray(float[] colorArray){
            this.colorArray = colorArray;
        }
        //设置图片
        public void setBitmap(Bitmap bitmap){
            this.bitmap = bitmap;
        }

}
