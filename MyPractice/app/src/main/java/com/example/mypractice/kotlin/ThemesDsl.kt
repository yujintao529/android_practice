package com.example.mypractice.kotlin

import android.graphics.drawable.Drawable

/**
 * Created by yujintao on 2018/1/30.
 */
class ThemeConfig {
    var enable: Boolean = false
    private var _searchSkin: SearchSkin? = null
    private var _categorySkin: CategorySkin? = null
    private var _bottomSkin: BottomSkin? = null

    fun enable(block: () -> Boolean) {
        enable = block()
    }

    fun searchSkin(config: SearchSkin.() -> Unit) {
        _searchSkin = SearchSkin("搜索皮肤").apply(config)
    }

    fun categorySkin(config: CategorySkin.() -> Unit) {
        _categorySkin = CategorySkin("分类大饼皮肤").apply(config)
    }

    fun bottomSkin(config: BottomSkin.() -> Unit) {
        _bottomSkin = BottomSkin()
        _bottomSkin?.config()
    }

    fun build() {
        _searchSkin?.build()
        _categorySkin?.build()
        _bottomSkin?.build()
    }
}

class CategorySkin(name: String) : Skin(name) {

    override fun build() {
        println("CategorySkin config")
    }

}

class SearchSkin(name: String) : Skin(name) {


    override fun build() {
        println("SearchSkin config")
    }

}

class HomeSkin() : Skin("首页底部主题") {
    override fun build() {
        println("   HomeSkin config")
    }
}

class MineSkin() : Skin("我的底部主题") {
    override fun build() {
        println("   MineSkin config")
    }
}

class BottomSkin() : Skin("底部导航栏") {


    private var _homeSkin: HomeSkin? = null
    private var _mineSkin: MineSkin? = null

    fun homeSkin(config: HomeSkin.() -> Unit) {
        _homeSkin = HomeSkin()
        _homeSkin?.config()
    }

    fun mineSkin(config: MineSkin.() -> Unit) {
        _mineSkin = MineSkin()
        _mineSkin?.config()
    }


    override fun build() {
        println("BottomSkin config")
        _homeSkin?.build()
        _mineSkin?.build()
    }

}

abstract class Skin(val name: String) {
    var imageUrl: String? = null
    var drawable: Drawable? = null
    abstract fun build()
}

fun themes(config: ThemeConfig.() -> Unit) {
    var themeConfig = ThemeConfig()
    themeConfig.config()
    themeConfig.build()
}


fun main(args: Array<String>) {
    themes {
        //enable { true }// 两种形式,目前语法上的限制不能省略大括号或者=号。以后应该会支持
        enable = true

        searchSkin {
            imageUrl = "http://mafengwo.cn/searchSkin?id=23123"
        }

        categorySkin {
            //            drawable = ColorDrawable(Color.RED)
        }
        bottomSkin {

            mineSkin {
                imageUrl = "http://mafengwo.cn/searchSkin?id=8888"
            }
            homeSkin {
                //                drawable = ColorDrawable(Color.RED)
            }
        }
    }
}


