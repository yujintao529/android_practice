package com.example.mypractice.source;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.example.mypractice.R;

public class DrawActivity extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_draw);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.drawParent);
        viewGroup.setDrawingCacheEnabled(true);
        viewGroup.setAlwaysDrawnWithCacheEnabled(true);
        viewGroup.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        Animation animation = new ScaleAnimation(0.5f, 1, 0.5f, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        animation.setDuration(2000);
        viewGroup.setLayoutAnimation(new LayoutAnimationController(animation, 3000));
        viewGroup.startLayoutAnimation();
    }

    /**
     * 删除view添加animation，animation执行完才不再显示
     * @param v
     */
    public void onDeleteClick(View v) {
        View view = findViewById(R.id.deleteView);
        if (view != null) {
            Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 2, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            animation.setDuration(2000);
            view.startAnimation(animation);
            ((ViewGroup) (view.getParent())).removeView(view);
        }

    }


    @Override
    public void onClick(View v) {

    }
}
