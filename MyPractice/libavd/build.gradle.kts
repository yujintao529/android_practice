plugins {
    id("my-android-library")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}
apply(rootProject.file("gradle/base_custom_utils.gradle.kts"))
android {
    defaultConfig {
        //特别奇葩，必须得像下面配置那样
        ndk {
            abiFilters.addAll(rootProject.extra["supportAbis"] as List<String>)
        }
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
dependencies {
    implementation(Deps.appcompat)
    implementation(Deps.permissionDispatcher)
}