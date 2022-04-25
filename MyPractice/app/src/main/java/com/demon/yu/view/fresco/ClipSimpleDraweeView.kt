package com.demon.yu.view.fresco

import android.content.Context
import android.graphics.Canvas
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
    SimpleDraweeView(context, attr), IFakeLayoutView {


    private var ratio: Float = 1f

    override fun onDraw(canvas: Canvas) {
        canvas.save()
//        canvas.translate(((-10).dp2Px()).toFloat(), ((-10).dp2Px()).toFloat())
        super.onDraw(canvas)
        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        ratio = measuredWidth / 160f
    }

    /**
     * 这个图片原始是200*200
     */
    fun initAvator() {
        val imageRequestBuilder =
            ImageRequestBuilder.newBuilderWithSource(Uri.parse("asset:///avator.webp"))
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
        setController(controller)
    }

    /**
     * 特定的格式：
     * real:160*160
     * fake:120*120
     * top: 30
     * left:50
     */
    override fun getFakeHeight(): Int {
        return (120 * ratio).toInt()
    }

    override fun getFakeWidth(): Int {
        return (120 * ratio).toInt()
    }

    override fun getFakeTop(): Int {
        return (20 * ratio).toInt()
    }

    override fun getFakeLeft(): Int {
        return (20 * ratio).toInt()
    }

    override fun getCenterPoint(): Point {
        return Point(
            width / 2 + getFakeLeft() + left,
            top + height / 2 + getFakeTop()
        )
    }

    override fun getFakePivotX(): Int {
        return getFakeLeft() + getFakeWidth() / 2
    }

    override fun getFakePivotY(): Int {
        return getFakeTop() + getFakeHeight() / 2
    }


}