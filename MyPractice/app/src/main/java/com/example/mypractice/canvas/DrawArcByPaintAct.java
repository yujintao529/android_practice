package com.example.mypractice.canvas;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.example.mypractice.R;

public class DrawArcByPaintAct extends Activity {

    public static final String TAG = "APIDEMONS";
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawarcbypaint);
        frameLayout = (FrameLayout) findViewById(R.id.show);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.circle:
                frameLayout.removeAllViews();
                frameLayout.addView(new CircleLoading(this), 0);
                break;
            case R.id.sample:
                frameLayout.removeAllViews();
                frameLayout.addView(new DrawArc(this), 0);
                break;
            default:
                break;
        }
    }

    public static class CircleLoading extends View {
        private Paint mPaint;
        private int mStartAngle;
        private int mEndAngle;
        private RectF rectF;
        private int radius;
        private boolean flag;
        private boolean flag2;
        private Paint textPaint;
        private FontMetrics fontMetrics;

        public CircleLoading(Context context) {
            super(context);
            init();
        }

        public CircleLoading(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            init();
        }

        private void init() {
            mPaint = new Paint();
            textPaint = new Paint();
            mPaint.setStyle(Style.STROKE);
            textPaint.setColor(Color.GRAY);
            textPaint.setTextSize(14);
            textPaint.setTextAlign(Align.CENTER);
            fontMetrics = textPaint.getFontMetrics();
            mPaint.setStyle(Style.STROKE);
            mPaint.setStrokeCap(Cap.ROUND);
            mPaint.setStrokeWidth(8);

            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.RED);
            flag = true;
            LayoutParams layoutParams = getLayoutParams();
            if (layoutParams == null) {
                radius = 20;
            } else {
                final int heigth = layoutParams.height;
                final int width = layoutParams.width;
                radius = 20;
            }
            rectF = new RectF(100, 100, 200, 200);
        }

        public void setColor(int color) {
            mPaint.setColor(color);
        }

        /**

         *
         */
        @Override
        protected void onDraw(Canvas canvas) {
            final int width = canvas.getWidth();
            final int height = canvas.getHeight();
            Log.d(TAG, String.format("width %d height %d", width, height));
            canvas.drawArc(rectF, mStartAngle, mEndAngle, false, mPaint);
            canvas.drawText("loading", 150, 150 - (fontMetrics.descent + fontMetrics.ascent) / 2, textPaint);
            if (flag) {
                mStartAngle += 5;
                mEndAngle = 40;
                if (mStartAngle % 100 == 0) {
                    flag = false;
                    flag2 = true;
                }
            } else {
                if (mEndAngle < 180 && flag2) {
                    mEndAngle += 5;
                    mStartAngle += 5;
                } else {
                    flag2 = false;
                    mEndAngle -= 5;
                    mStartAngle += 10;
                    if (mEndAngle <= 40) {
                        flag = true;
                    }
                }
                if (mStartAngle >= 360) {
                    mStartAngle = 0;
                }
            }

            invalidate();
        }

        private void gurentee() {
            if (mStartAngle >= 360) {
                mStartAngle = 0;
            }
        }
    }


    public static class DrawArc extends View {

        private Boolean[] booleans;
        private Paint[] paints;
        private int bigIndex;
        private Paint rect;
        private float sweepAngle;
        private RectF[] rectFs;
        private RectF bigRectF;
        private float startTop;

        public DrawArc(Context context) {
            super(context);
            rect = new Paint();
            rect.setStyle(Style.STROKE);
            startTop = 10;
            bigRectF = new RectF(startTop, startTop, 200, 200);
            rectFs = new RectF[4];
            startTop += 210;
            for (int i = 0; i < 4; i++) {
                rectFs[i] = new RectF(10 * (i + 1) + 100 * i, startTop, 10 * (i + 1) + 100 * (i + 1), 100 + startTop);
            }

            booleans = new Boolean[4];
            paints = new Paint[4];
            paints[0] = new Paint();
            paints[0].setAntiAlias(true);
            paints[0].setColor(Color.RED);
            paints[0].setStyle(Style.FILL);
            booleans[0] = false;
            paints[1] = new Paint(paints[0]);
            paints[1].setColor(Color.BLUE);
            booleans[1] = true;
            paints[2] = new Paint(paints[0]);
            paints[2].setColor(Color.GREEN);
            paints[2].setStyle(Style.STROKE);
            paints[2].setStrokeWidth(2);
            booleans[2] = false;
            paints[3] = new Paint(paints[2]);
            paints[3].setColor(Color.YELLOW);
            booleans[3] = true;

        }

        private void onDrawArc(Canvas canvas, RectF rectF, Paint paint, boolean useCenter) {
            canvas.drawRect(rectF, rect);
            canvas.drawArc(rectF, 0, sweepAngle, useCenter, paint);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            onDrawArc(canvas, bigRectF, paints[bigIndex], booleans[bigIndex]);
            for (int i = 0; i < 4; i++) {
                onDrawArc(canvas, rectFs[i], paints[i], booleans[i]);
            }
            sweepAngle += 5;
            if (sweepAngle > 360) {
                sweepAngle -= 360;
                bigIndex = (++bigIndex) % 4;

            }
            invalidate();
        }

    }

    //画中心圆
    public static class MySample extends View {
        Paint circle;
        Paint text;
        float height;
        FontMetrics fontMetrics;

        public MySample(Context context) {
            super(context);
            circle = new Paint();
            circle.setColor(Color.RED);
            circle.setAntiAlias(true);
            text = new Paint();
            text.setTextAlign(Align.CENTER);
            text.setColor(Color.WHITE);
            text.setTextSize(20);
            Rect bounds = new Rect();
            text.getFontMetrics();
//			text.getTextBounds("圆形", 0, 2, bounds);
//			height=bounds.bottom-bounds.top;
            Log.d("MainActivity", bounds.toString());
            fontMetrics = text.getFontMetrics();
            height = fontMetrics.ascent + fontMetrics.descent;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.CYAN);
            canvas.drawCircle(100, 100, 50, circle);
            circle.setColor(Color.BLACK);
            circle.setStyle(Style.STROKE);
            circle.setStrokeWidth(2);
            canvas.drawPoints(new float[]{100, 100}, circle);
            canvas.drawText("圆形", 100, 100 - height / 2, text);
        }
    }
}
