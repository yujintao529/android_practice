package com.example.mypractice.matrix;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.mypractice.R;

import java.util.Arrays;

public class TranslateAndRotation extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_translate_and_rotation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static class MyView extends  View{

        private Matrix matrix;
        Paint paint;
        private Bitmap bitmap;
        private Shader shader;
        public MyView(Context context) {
            super(context);
            matrix=new Matrix();
            paint=new Paint();
            shader=new SweepGradient(50,50,Color.RED,Color.BLUE);
            shader=new RadialGradient(50,50,50,Color.RED,Color.BLUE, Shader.TileMode.CLAMP);
            paint.setColor(Color.RED);
            paint.setShader(shader);
            bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher);
        }

        private float[] src=new float[]{0,0};
        private float[] dst=new float[2];
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawRect(0, 0, 100,100, paint);
//            matrix.setRotate(90,bitmap.getWidth() / 2, bitmap.getHeight() / 2);
//            matrix.setTranslate(100, 100);
//            matrix.mapPoints(dst,src);
//            matrix.setScale(0.5f,0.5f);
//
//            Log.d("translate ", "dst " + Arrays.toString(dst));
////            canvas.setMatrix(matrix);
//            matrix.postTranslate(100,100);
//            canvas.drawBitmap(bitmap,matrix,paint);
            canvas.save();
//            canvas.scale(0.5f, 0.5f);

            canvas.translate(200, 200);




            canvas.rotate(45, 50, 50);


//            canvas.scale(1f, 1f);
            canvas.scale(.5f,.5f,50,50);

            canvas.drawRect(0, 0,100, 100, paint);

            canvas.restore();
//            matrix.postRotate()
//            matrix.postRotate(30, bitmap.getWidth() / 2 + 20, bitmap.getHeight() / 2 + 300);
//            canvas.setMatrix(matrix);
//            canvas.save();
//            canvas.rotate(20,25,25);
//            canvas.translate(100,100);
//            canvas.drawRect(0,0,50,50,paint);
//            canvas.restore();
//            canvas.save();
//            canvas.setMatrix(matrix);
//            canvas.setMatrix();
//            canvas.translate(20, 500);
//            canvas.drawRect(0, 0, 100, 100, paint);
//

//            canvas.drawRect(20,500,120,600,paint);
//            matrix.map
//            canvas.restore();
//canvas.drawRect(100,100,300,300,paint);
//            canvas.drawCircle(10,10,10,paint);
//            canvas.drawBitmap(bitmap,matrix,paint);
        }
    }
}
