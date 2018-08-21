package com.example.mypractice.kotlin

class Gradle {

    //采用成员变量进行dsl
    val dependencies = Dependencies()

    //采用函数dsl，Android不包含invoke函数
    private val _android = Android()

    fun android(block: Android.() -> Unit) {
        _android.block()
    }

    //invoke约束杉树
    operator fun invoke(block: Gradle.() -> Unit) {
        block()
    }

}

//android对象配置
class Android {

    //两种方式
    var compileSdkVersion = -1

    fun compileSdkVersion(sdk: Int) {
        compileSdkVersion = sdk
    }

    var targetSdkVersion = 12
    //defaultConfig对象配置
    val defaultConfig = DefaultConfig()

}

class DefaultConfig {
    var applicationId = ""
        get() {
            println("get applicationId")
            return field
        }
        set(value) {
            println("set applicationId")
            field = value
        }

    operator fun invoke(block: DefaultConfig.() -> Unit) {
        block()
    }
}

class Dependencies {

    fun compile(str: String) {
        println("compile $str")
    }

    fun annotationProcessor(str: String) {
        println("annotationProcessor $str")
    }

    operator fun invoke(block: Dependencies.() -> Unit) {
        block()
    }


}


class DownloadManager {


    fun download(url: String) {
        println("downManager down $url ")
    }

    operator fun invoke(url: String) {
        println("invoke  $url ")
        download(url)
    }
}


fun main(args: Array<String>) {


    val block = { x: Int, y: Int -> x + y }

    var gradle = Gradle()
    gradle {
        android {
            compileSdkVersion(2)
            compileSdkVersion = 2
            targetSdkVersion = 27
            defaultConfig {
                applicationId = "com.google.android.internal"

            }
        }
        dependencies {
            compile("com.android.support:support-v4:23.1.1")
            annotationProcessor("com.jakewharton:butterknife-compiler:8.4.0")
        }
    }

    val downloadManager = DownloadManager()

    //正常调用方法
    downloadManager.download("www.demon-yu.com/file")
    //高级调用方法
    downloadManager("www.demon-yu.com/file1")
    downloadManager("www.demon-yu.com/file2")
    downloadManager("www.demon-yu.com/file3")

    val theme = global {
        icon = "柯南皮肤"
        color = "蓝色"
    }
    println(theme)

}


class Theme {
    var icon: String? = null
    var color: String? = null

    override fun toString(): String {
        return "$icon $color"
    }
}

fun global(block: Theme.() -> Unit): Theme {
    val theme = Theme()
    theme.block()
    return theme
}

