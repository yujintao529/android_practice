package com.example.mypractice.canvas;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public class PaintFlagsDrawFilterAct extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MySample(this));
    }

    public static class MySample extends View {
        private Bitmap bitmap;
        private Matrix matrix;
        private Paint paint;
        private Matrix mCanvasMatrix;
        private Paint mTextPaint;

        public MySample(Context context) {
            super(context);
            try {
                bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("resource_2.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCanvasMatrix = new Matrix();
            matrix = new Matrix();
            paint = new Paint();
//            1)mPaint.setAntiAlias();//此函数是用来防止边缘的锯齿，
//            2)mPaint.setBitmapFilter(true)。//此函数是用来对位图进行滤波处理。
            paint.setAntiAlias(false);
            paint.setFilterBitmap(false);
            mTextPaint = new Paint();
            mTextPaint.setColor(Color.RED);
            mTextPaint.setTextSize(40);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeWidth(2);

        }

        /**
         * @param
         *
         */
        @Override
        protected void onDraw(Canvas canvas) {
            int root = canvas.save();
//            canvas.setMatrix(mCanvasMatrix);
            canvas.translate(100, 100);
            canvas.drawBitmap(bitmap, matrix, paint);
            matrix.postRotate(45);
            matrix.postTranslate(250, 0);
            matrix.postScale(1.5f, 1.5f);
            canvas.drawBitmap(bitmap, matrix, paint);
            canvas.translate(0, 300);
            int temp=canvas.save();
            canvas.drawText("圆", 0, 0, mTextPaint);
            canvas.rotate(90, 0, 0);
            canvas.drawText("圆", 0, -40, mTextPaint);
            canvas.restoreToCount(temp);
            temp=canvas.save();
            canvas.rotate(-90);
            canvas.drawText("圆", 30, 0, mTextPaint);
            canvas.restoreToCount(temp);
            canvas.translate(0, 300);





            canvas.restoreToCount(root);
        }
    }
}
