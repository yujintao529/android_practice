plugins {
    id("my-android-library")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    defaultConfig {
        consumerProguardFile("consumer-rules.pro")
        ndk {
//            abiFilters.add("x86")
            abiFilters.add("armeabi-v7a")
//            abiFilters.add("arm64-v8a")
        }
        //特别奇葩，必须得像下面配置那样
        externalNativeBuild {
            cmake {
                arguments("-DANDROID_TOOLCHAIN=clang ")
                cFlags("-std=gnu11")
            }
        }

    }
    externalNativeBuild {
        cmake {
            path = project.file("CMakeLists.txt")
        }
    }
}

