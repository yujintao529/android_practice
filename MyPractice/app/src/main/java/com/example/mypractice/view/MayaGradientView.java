package com.example.mypractice.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.mypractice.R;


/**
 * 近用作背景使用
 */
public class MayaGradientView extends View {

    public MayaGradientView(Context context) {
        super(context);
        init(context, null);
    }

    public MayaGradientView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MayaGradientView(Context context, @Nullable AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        Drawable drawable = GradientDrawableUtils.createFromAttributeSet(context, attrs, R.styleable.MayaGradientView);
        setBackground(drawable);
    }

}
