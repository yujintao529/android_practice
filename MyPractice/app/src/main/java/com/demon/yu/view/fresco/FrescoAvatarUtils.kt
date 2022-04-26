package com.demon.yu.view.fresco

import android.net.Uri
import androidx.annotation.DrawableRes
import com.demon.yu.view.FrescoWebpViewAct
import com.example.mypractice.Logger
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.listener.BaseRequestListener
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder


object FrescoAvatarUtils {
    fun bindAvatar(simpleDraweeView: SimpleDraweeView, @DrawableRes resourceID: Int) {
        val imageRequestBuilder =
            ImageRequestBuilder.newBuilderWithResourceId(resourceID)
                .setRequestListener(object : BaseRequestListener() {
                    override fun onRequestFailure(
                        request: ImageRequest,
                        requestId: String,
                        throwable: Throwable,
                        isPrefetch: Boolean
                    ) {
                        super.onRequestFailure(request, requestId, throwable, isPrefetch)
                        Logger.error(FrescoWebpViewAct.TAG, "onRequestFailure ", throwable)
                    }
                })
        val controller: DraweeController = Fresco.newDraweeControllerBuilder()
            .setAutoPlayAnimations(true)
            .setImageRequest(imageRequestBuilder.build())
            .build()
        simpleDraweeView.controller = controller
    }

    fun bindAvatar(simpleDraweeView: SimpleDraweeView, uri: String, width: Int, height: Int) {
        val imageDecodeOptions = ImageDecodeOptionsBuilder()
            .setForceStaticImage(true)
            .build()
        val imageRequestBuilder =
            ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                .setRequestListener(object : BaseRequestListener() {
                    override fun onRequestFailure(
                        request: ImageRequest,
                        requestId: String,
                        throwable: Throwable,
                        isPrefetch: Boolean
                    ) {
                        super.onRequestFailure(request, requestId, throwable, isPrefetch)
                        Logger.error(FrescoWebpViewAct.TAG, "onRequestFailure ", throwable)
                    }
                }).setResizeOptions(ResizeOptions(width, height))
                .setCacheChoice(ImageRequest.CacheChoice.SMALL)
                .setImageDecodeOptions(imageDecodeOptions)

        val controller: DraweeController = Fresco.newDraweeControllerBuilder()
            .setAutoPlayAnimations(true)
            .setOldController(simpleDraweeView.controller)
            .setImageRequest(imageRequestBuilder.build())
            .build()
        simpleDraweeView.controller = controller
    }

    fun asCircle(simpleDraweeView: SimpleDraweeView) {
        val roundingParams = RoundingParams()
        roundingParams.roundAsCircle = true
        simpleDraweeView.hierarchy.roundingParams = roundingParams
    }

}