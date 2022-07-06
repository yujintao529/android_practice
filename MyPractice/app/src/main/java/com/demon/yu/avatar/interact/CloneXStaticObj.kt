package com.demon.yu.avatar.interact

import android.net.Uri
import com.demon.yu.view.recyclerview.ColorUtils

class CloneXStaticObj(var color: Int = ColorUtils.getRandomColor(), var viewType: Int = 0) {
    var assetAvatarUri: Uri? = null
}