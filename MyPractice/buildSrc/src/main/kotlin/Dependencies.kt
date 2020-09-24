/**
 *  created by yujintao 2020-02-20
 *
 *  TODO 非常奇怪，**.gradle可以使用，但是没有提示
 */
object Deps {

    const val lifecycle_version = "1.1.1"
    const val supportLibVersion = "27.1.1"
    const val kotlin_version = "1.3.61"
    // ViewModel and LiveData
    const val lifecycleExtensions = "android.arch.lifecycle:extensions:$lifecycle_version"
    // 官方支持库
    const val appcompatv7 = "com.android.support:appcompat-v7:${supportLibVersion}"
    const val supportv4 = "com.android.support:support-v4:${supportLibVersion}"
    const val design = "com.android.support:design:${supportLibVersion}"
    const val cardview = "com.android.support:cardview-v7:${supportLibVersion}"
    const val recyclerview = "com.android.support:recyclerview-v7:${supportLibVersion}"
    const val palette = "com.android.support:palette-v7:${supportLibVersion}"
    const val annotation = "com.android.support:support-annotations:${supportLibVersion}"
    const val multidex = "com.android.support:multidex:1.0.1"



    // 知名项目
    const val frescoVersion = "0.11.0"
    const val fresco = "com.facebook.fresco:fresco:${frescoVersion}"
    const val frescogif = "com.facebook.fresco:animated-gif:${frescoVersion}"
    const val frescowebp = "com.facebook.fresco:webpsupport:${frescoVersion}"
    const val frescowebpanimated = "com.facebook.fresco:animated-webp:${frescoVersion}"
    const val picasso = "com.squareup.picasso:picasso:2.5.2"
    const val eventbus = "de.greenrobot:eventbus:2.4.0"
    const val andfix = "com.alipay.euler:andfix:0.3.1@aar"
    const val exoplayer = "com.google.android.exoplayer:exoplayer:r1.5.10"
    const val rxjava = "io.reactivex:rxjava:1.1.6"
    const val rxandroid = "io.reactivex:rxandroid:1.2.1"
    const val htmltextview = "org.sufficientlysecure:html-textview:1.8"

    const val leakcanary = "com.squareup.leakcanary:leakcanary-android:1.5.4"
    const val leakcanary_noop = "com.squareup.leakcanary:leakcanary-android-no-op:1.5.4"
    const val blockcanary = "com.github.moduth:blockcanary-android:1.2.1"
    const val blockcanary_noop = "com.github.moduth:blockcanary-no-op:1.2.1"
    const val dbinspector = "im.dino:dbinspector:3.3.0@aar"
    const val bugly_sdk = "com.tencent.bugly:crashreport:2.1.9"
    const val bugly_ndk = "com.tencent.bugly:nativecrashreport:2.2.0"
    const val rxJavaDependency = "io.reactivex:rxjava:1.1.6"
    const val rxAndroidDependency="io.reactivex:rxandroid:1.2.1"
    const val autoService= "com.google.auto.service:auto-service:1.0-rc2"
    const val javapoet= "com.squareup:javapoet:1.7.0"
    const val javawrite="com.squareup:javawriter:2.5.0"

    const val glide="com.github.bumptech.glide:glide:4.8.0"
    const val wecahtOpenSdk="com.tencent.mm.opensdk:wechat-sdk-android-without-mta:+"
    const val butterKnife="com.jakewharton:butterknife:8.4.0"
    const val butterKnifeCompiler="com.jakewharton:butterknife-compiler:8.4.0"

    // Other
    const val swipelayout = "com.daimajia.swipelayout:library:1.2.0@aar"
    const val ratiolayout = "net.soulwolf.widget:ratiolayout:1.0.0"
    const val fragment_animation = "com.desarrollodroide:fragmenttransactionextended:1"
    const val circleindicator = "me.relex:circleindicator:1.1.8@aar"
    const val  stickyheader_recyclerview = "com.timehop.stickyheadersrecyclerview:library:0.4.3@aar"
    const val tagflow = "com.zhy:flowlayout-lib:1.0.0"
    const val autofittextview = "me.grantland:autofittextview:0.2.+"
    const val recycler_viewpager = "com.github.lsjwzh.RecyclerViewPager:lib:v1.1.0"
    const val spanny = "com.binaryfork:spanny:1.0.4"
    const val viewanimator = "com.github.florent37:viewanimator:1.0.4"


    //permission
    const val permissionDispatcher = "org.permissionsdispatcher:permissionsdispatcher:4.8.0"
    const val permissionDispatcherProcessor = "org.permissionsdispatcher:permissionsdispatcher-processor:4.8.0"
    //new other
    const val debugdb="com.amitshekhar.android:debug-db:1.0.3"
    //kotlin
    const val kotlinstdjdk7= "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    const val kotlinreflect= "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"
}

object Versions {
    const val compileSdkVersion = 29
    const val minSdkVersion = 21
    const val targetSdkVersion = 29
    const val buildToolsVersion = "29.0.3"
}