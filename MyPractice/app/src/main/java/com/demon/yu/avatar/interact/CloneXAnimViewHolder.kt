package com.demon.yu.avatar.interact

import android.view.View
import com.demon.yu.view.fresco.ClipSimpleDraweeView

/**
 * Created by yujintao.529 on 2022/7/15
 */
class CloneXAnimViewHolder(private val clipSimpleDraweeView: ClipSimpleDraweeView) :
    CloneXComposeViewHolder<CloneXStaticObj>(clipSimpleDraweeView) {
    override fun bindData(data: CloneXStaticObj, position: Int, payload: List<Any?>?) {
        clipSimpleDraweeView.initAvatar(data.assetAvatarUri)
    }

    override fun onHide(position: Int) {
        super.onHide(position)
        clipSimpleDraweeView.stopAnimation()
    }

    override fun onVisible(position: Int) {
        super.onVisible(position)
        clipSimpleDraweeView.startAnimation()
    }

    override fun release() {
        super.release()
        clipSimpleDraweeView.stopAnimation()
    }
}