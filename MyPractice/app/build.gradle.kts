import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}
apply(rootProject.file("gradle/deps.gradle.kts"))
apply(rootProject.file("add_export.gradle"))
apply(rootProject.file("gradle/base_custom_utils.gradle.kts"))
configure<com.android.build.gradle.internal.dsl.BaseAppModuleExtension> {
    defaultConfig {
        applicationId = "com.example.mypractice"
        minSdk = Versions.minSdkVersion
//        compileSdkVersion =  Versions.compileSdkVersion.toString()
        compileSdkVersion = Versions.compileSdkVersion
        targetSdk = Versions.targetSdkVersion
        multiDexEnabled = true
        multiDexKeepProguard = rootProject.file("maindexlist.txt")
        ndk {
            abiFilters.addAll(rootProject.extra["supportAbis"] as List<String>)
            resConfigs("cn", "xhdpi")
        }
    }
    signingConfigs {
        val properties = Properties()
        properties.load(FileInputStream(rootProject.file("release-jks.properties")))
        create("release") {
            storeFile = rootProject.file(properties.getProperty("storeFile"))
            storePassword = properties.getProperty("storePassword")
            keyAlias = properties.getProperty("keyAlias")
            keyPassword = properties.getProperty("keyPassword")
        }
    }
    sourceSets.getByName("main") {
        java.srcDirs("src/main/java", "src/main/kotlin")
        res.srcDirs("src/main/res")
        assets.srcDir("src/main/assets")
        jniLibs.srcDirs("libs")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    packagingOptions {
        jniLibs {
            pickFirsts.addAll((rootProject.extra["supportAbis"] as List<String>).map { "lib/$it/*" }.toList())
            excludes.addAll(listOf("lib/armeabi/*","lib/mips/*","META-INF/LICENSE.txt","META-INF/NOTICE.txt"))
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree("libs") {
        include("*.jar")
    })
    implementation(Deps.appcompat)
    implementation(Deps.viewModel)
    implementation(Deps.LiveData)
    implementation(Deps.lifecycle)
    implementation(Deps.fragment)
    implementation(Deps.fragment_ktx)
    implementation(Deps.annotation)
    implementation(Deps.recyclerview)
    implementation(Deps.cardview)
    implementation(Deps.gridLayout)
    implementation(Deps.constraintlayout)
    implementation(Deps.material)
    implementation(Deps.rxJavaDependency)
    implementation(Deps.rxAndroidDependency)
//    implementation(Deps.autoService)
    implementation(Deps.debugdb)
    implementation(Deps.glide)
    implementation(Deps.fresco)
    implementation(Deps.frescowebp)
    implementation(Deps.frescowebpanimated)
    implementation(Deps.wecahtOpenSdk)
    debugImplementation(Deps.leakcanary)
    releaseImplementation(Deps.leakcanary_noop)
    implementation(project(":libgaussia"))
    implementation(project(":libavd"))
    implementation(Deps.butterKnife)
    implementation(Deps.stetho)
    implementation(Deps.permissionDispatcher)
    implementation(Deps.kotlinCoroutines)
    implementation(Deps.androidKotlinCoroutines)
    kapt(Deps.butterKnifeCompiler)
    kapt(Deps.permissionDispatcherProcessor)
    testImplementation(Deps.kotlinCoroutinesTest)
    testImplementation(Deps.junit4)
}

