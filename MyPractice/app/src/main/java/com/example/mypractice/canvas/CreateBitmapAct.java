package com.example.mypractice.canvas;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;

public class CreateBitmapAct extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new StaticView(this));
    }

    public static int randomColor() {

        return 0;
    }

    public static Bitmap createDefaultBitmap(int rectWidth) {
        Bitmap bitmap = Bitmap.createBitmap(rectWidth, rectWidth, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawColor(0xffCCFEBF);
        float rect = rectWidth / 3;
        RectF f = new RectF();
        f.bottom = rect;
        f.left = 0;
        f.top = 0;
        f.right = rect;
        canvas.save();
        paint.setColor(Color.BLACK);
        canvas.drawRect(f, paint);
        canvas.translate(2 * rect, 0);
        paint.setColor(Color.BLUE);
        canvas.drawRect(f, paint);
        canvas.restore();
        canvas.translate(0, 2 * rect);
        paint.setColor(Color.CYAN);
        canvas.drawRect(f, paint);
        canvas.translate(2 * rect, 0);
        paint.setColor(Color.GRAY);
        canvas.drawRect(f, paint);
        return bitmap;
    }

    public static void resetColor(int[] color){
        for(int i=0;i<color.length;i++){
            color[i]=-1232132;
        }
    }
    /*
           主要是getPixel方法的使用。几个参数很微妙
           getPixes方法，
           int[] pixels   这个是一个数组，就是把bitmap取出的像素数据放道理面
           int offset    这个就是往piexls数组放数据时的一个偏移位置。例如：如果想要把两个图片左右相连，只需要设置
                         为链接的图片的宽度就行了，但是如果想要上下相连，怎样要设置，连接图片的高度乘以宽度
           int stride    这个是代表图片的跨度，她必须大于等于所要扫瞄的宽度。也就是想要生成图片的大小。
           int x         图片的x像素位置开始读取
           int y         图片的y像素位置开始读取
           int width     读取的图片宽度
           int height    读取的图片的高度。
     */
    public static class StaticView extends View {

        private Bitmap def;
        private Bitmap one;
        private Bitmap two;
        private Bitmap three;
        private Bitmap four;
        private Bitmap sum;//1，2，3，4和在一起
        private Bitmap[] bs;
        public StaticView(Context context) {
            super(context);
            bs=new Bitmap[4];
            def = createDefaultBitmap(300);
            int[] color = new int[200 *200];
            resetColor(color);
            def.getPixels(color, 0, 100, 0, 0, 100, 100);
            one = Bitmap.createBitmap(color, 0, 100, 100, 100, Config.ARGB_8888);
            def.getPixels(color, 0, 100, 200, 0, 100, 100);
            two = Bitmap.createBitmap(color, 0, 100, 100, 100, Config.ARGB_8888);
            def.getPixels(color, 0, 100, 0, 200, 100, 100);
            three = Bitmap.createBitmap(color, 0, 100, 100, 100, Config.ARGB_8888);
            def.getPixels(color, 0, 100, 200, 200, 100, 100);
            four = Bitmap.createBitmap(color, 0, 100, 100, 100, Config.ARGB_8888);
            //获取部分图片，但是同时保留原来位置
            resetColor(color);
            def.getPixels(color, 0, 200, 0, 0, 100, 100);
            bs[0]=Bitmap.createBitmap(color,0,200,200,200,Config.ARGB_8888);
            resetColor(color);
            def.getPixels(color, 100, 200, 200, 0, 100, 100);
            bs[1]=Bitmap.createBitmap(color,0,200,200,200,Config.ARGB_8888);
            resetColor(color);
            def.getPixels(color, 100 * 200, 200, 0, 200, 100, 100);
            bs[2]=Bitmap.createBitmap(color,0,200,200,200,Config.ARGB_8888);
            resetColor(color);
            def.getPixels(color, 100 + 100 * 200, 200, 200, 200, 100, 100);
            bs[3]=Bitmap.createBitmap(color,0,200,200,200,Config.ARGB_8888);
            //将四个位置全部组合在一起。
            resetColor(color);
            def.getPixels(color,0,200,0,0,100,100);
            def.getPixels(color,100,200,200,0,100,100);
            def.getPixels(color,100*200,200,0,200,100,100);
            def.getPixels(color,100+100*200,200,200,200,100,100);
            sum=Bitmap.createBitmap(color,200,200,Config.ARGB_8888);


        }

        @Override
        protected void onDraw(Canvas canvas) {

            canvas.translate(20, 100);
            int source = canvas.save();
            canvas.drawBitmap(def, 0, 0, null);
            canvas.translate(320, 0);
            canvas.drawBitmap(one, 0, 0, null);
            canvas.translate(120, 0);
            canvas.drawBitmap(two, 0, 0, null);
            canvas.translate(120, 0);
            canvas.drawBitmap(three, 0, 0, null);
            canvas.translate(120, 0);
            canvas.drawBitmap(four, 0, 0, null);
            canvas.restoreToCount(source);

            canvas.translate(0,320);
            source=canvas.save();
            for(int i=0;i<bs.length;i++){
                if(bs[i]!=null){
                    final Bitmap b=bs[i];
                    canvas.drawBitmap(b,i*(200+20),0,null);
                }
            }
            canvas.restoreToCount(source);
            canvas.translate(0, 220);
            source=canvas.save();
            canvas.drawBitmap(sum,0,0,null);


        }
    }
}
