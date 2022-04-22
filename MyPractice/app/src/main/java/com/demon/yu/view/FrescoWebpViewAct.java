package com.demon.yu.view;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demon.yu.view.fresco.ClipSimpleDraweeView;
import com.example.mypractice.Logger;
import com.example.mypractice.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.listener.BaseRequestListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class FrescoWebpViewAct extends AppCompatActivity {
    public static final String TAG = "FrescoWebpViewAct";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresco_webp_view);
        ClipSimpleDraweeView simpleDraweeView = findViewById(R.id.simpleDraweeView);
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse("asset:///water.webp")).setRequestListener(new BaseRequestListener() {
            @Override
            public void onRequestFailure(ImageRequest request, String requestId, Throwable throwable, boolean isPrefetch) {
                super.onRequestFailure(request, requestId, throwable, isPrefetch);
                Logger.error(TAG, "onRequestFailure ", throwable);
            }
        });
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .setImageRequest(imageRequestBuilder.build())
                .build();
        simpleDraweeView.setController(controller);
    }

}
