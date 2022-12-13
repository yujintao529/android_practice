plugins {
    id("com.android.library") apply false
}
//android { //also work but only for plugins{} not for apply way
//
//}
configure<com.android.build.gradle.LibraryExtension> { //work well with plugins{} and apply
    compileSdkVersion = Versions.compileSdkVersion
    defaultConfig {
        minSdk = Versions.minSdkVersion
        targetSdk = Versions.targetSdkVersion
        buildToolsVersion = Versions.buildToolsVersion
    }
    buildTypes {
        getByName("release") {
//            minifyEnabled(true)
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }
    sourceSets.getByName("main") {
        java.srcDirs("src/main/java", "src/main/kotlin")
        res.srcDirs("src/main/res")
        assets.srcDir("src/main/assets")
        jniLibs.srcDirs("libs")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}