/**
 *  created by yujintao 2020-02-20
 *
 *  TODO 非常奇怪，**.gradle可以使用，但是没有提示
 */
object Deps {

    const val kotlin_version = "1.3.61"
    //lifecycle and  ViewModel and LiveData
    private const val lifecycle_version = "2.2.0"
    // ViewModel
    private const val viewModel="androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
    // LiveData
    private const val LiveData = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
    // Lifecycles only (without ViewModel or LiveData)
    private const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"
    // Saved state module for ViewModel
    private const val savedstate="androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version"
    // Annotation processor
    private const val lifecycle_compiler = "androidx.lifecycle:lifecycle-compiler:$lifecycle_version"
    // alternately - if using Java8, use the following instead of lifecycle-compiler
    private const val lifecycle_common_java8="androidx.lifecycle:lifecycle-common-java8:$lifecycle_version"
    // optional - helpers for implementing LifecycleOwner in a Service
    private const val lifecycle_service = "androidx.lifecycle:lifecycle-service:$lifecycle_version"
    // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
    private const val lifecycle_process = "androidx.lifecycle:lifecycle-process:$lifecycle_version"
    // optional - ReactiveStreams support for LiveData
    private const val lifecycle_reactivestreams_ktx="androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycle_version"

    //androidx
    private const val appcompat_version = "1.2.0"
    const val appcompat = "androidx.appcompat:appcompat:$appcompat_version"
    // For loading and tinting drawables on older versions of the platform
    const val appcompat_resources = "androidx.appcompat:appcompat-resources:$appcompat_version"

    private const val fragment_version = "1.2.5"

    // Java language implementation
    const val fragment= "androidx.fragment:fragment:$fragment_version"
    // Kotlin
    const val fragment_ktx= "androidx.fragment:fragment-ktx:$fragment_version"
    //annotation
    const val annotation = "androidx.annotation:annotation:1.1.0"


    const val recyclerview ="androidx.recyclerview:recyclerview:1.1.0"

    // For control over item selection of both touch and mouse driven selection
    const val recyclerview_selection_x= "androidx.recyclerview:recyclerview-selection:1.1.0-rc03"

    const val cardview= "androidx.cardview:cardview:1.0.0"
    const val gridLayout= "androidx.gridlayout:gridlayout:1.0.0"

    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.0"
    const val coordinatorlayout = "androidx.coordinatorlayout:coordinatorlayout:1.1.0"
    const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
    const val material = "com.google.android.material:material:1.2.1"

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
    const val butterKnife="com.jakewharton:butterknife:10.2.0"
    const val butterKnifeCompiler="com.jakewharton:butterknife-compiler:10.2.0"

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

    const val stetho = "com.facebook.stetho:stetho:1.5.1"


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
    const val minSdkVersion = 23
    const val targetSdkVersion = 29
    const val buildToolsVersion = "29.0.3"
}