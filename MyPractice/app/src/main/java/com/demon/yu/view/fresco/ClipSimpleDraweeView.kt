package com.demon.yu.view.fresco

import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.util.AttributeSet
import com.demon.yu.view.FrescoWebpViewAct
import com.demon.yu.view.recyclerview.IFakeLayoutView
import com.example.mypractice.Logger
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.listener.BaseRequestListener
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder

class ClipSimpleDraweeView(context: Context, attr: AttributeSet? = null) :
    SimpleDraweeView(context, attr), IFakeLayoutView, IViewDrawListener {

    init {
        hierarchy.fadeDuration = 0
    }

    private var ratio: Float = 1f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        ratio = measuredWidth / 160f
    }

    /**
     * 这个图片原始是200*200
     */
    fun initAvator() {
        val imageRequestBuilder =
            ImageRequestBuilder.newBuilderWithSource(Uri.parse("asset:///mock_header3.webp"))
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
            .setImageRequest(imageRequestBuilder.build())
            .setAutoPlayAnimations(true)
            .build()


        setController(controller)
    }

    fun startAnimation() {
        if (controller?.animatable != null && controller?.animatable?.isRunning == false) {
            controller?.animatable?.start()
        }
    }

    fun stopAnimation() {
        if (controller?.animatable != null && controller?.animatable?.isRunning == true) {
            controller?.animatable?.stop()
        }
    }

    /**
     * large_url:{
    pic_width:"160",
    pic_height:"146",
    circle_radius:"62",
    circle_px:"80",
    circle_py:"84"
    }
     * 特定的格式：
     * real:160*146 px (w*h)
     *
     */


    override fun getFakeHeight(): Int {//62 * 2
        return (124 * ratio).toInt()
    }

    override fun getFakeWidth(): Int { //62 * 2
        return (124 * ratio).toInt()
    }

    override fun getFakeTop(): Int { // 146 -124
        return (23 * ratio).toInt()
    }

    override fun getFakeLeft(): Int { // (160-124)/2
        return (18 * ratio).toInt()
    }


    fun getDestWidth(input: Int): Int {
        return (input * 160f / 124f).toInt()
    }

    fun getDestHeight(input: Int): Int {
        return (input * 146f / 124f).toInt()
    }

    override fun getCenterPoint(): Point {
        return Point(
            getFakeWidth() / 2 + getFakeLeft() + left,
            top + getFakeHeight() / 2 + getFakeTop()
        )
    }

    override fun getFakePivotX(): Int {
        return getFakeLeft() + getFakeWidth() / 2
    }

    override fun getFakePivotY(): Int {
        return getFakeTop() + getFakeHeight() / 2
    }

    override fun notifyDrawStatus(enableDraw: Boolean) {
//        if (enableDraw) {
//            start()
//        } else {
//            stop()
//        }
    }

//    fun stop() {
//        val animation = controller?.animatable
//        if (animation != null && animation.isRunning) {
//            animation.stop()
//        }
//    }
//
//    fun start() {
//        val animation = controller?.animatable
//        if (animation != null && animation.isRunning.not()) {
//            animation.start()
//        }
//    }
}