plugins {
    id("com.android.library") apply false
}
configure<com.android.build.gradle.LibraryExtension> {
    compileSdkVersion(Versions.compileSdkVersion)
    defaultConfig {
        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
    }
    buildTypes {
        getByName("release") {
            minifyEnabled(true)
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}