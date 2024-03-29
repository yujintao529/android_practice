// Top-level build file where you can add configuration options common to all sub-projects/modules.
//这个是整个项目的构建文件，他的作用阀是通过setttings.gradle来控制的。
buildscript {
    repositories {
        jcenter()
        mavenCentral()
        flatDir { dirs("libs") }
        google()
    }
    dependencies {
        classpath(Deps.androidToolsBuildGradle)
        classpath(Deps.kotlinGradlePlugin)
    }
}
allprojects {
    repositories {
        maven {
            setUrl("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        google()
        jcenter()
        mavenCentral()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}
//buildscript {
//    repositories {
//        jcenter()
//        mavenCentral()
//        flatDir {
//            dirs 'libs'
//        }
//        google()
//    }
//    dependencies {
//        classpath Deps.androidToolsBuildGradle
//                classpath Deps.kotlinGradlePlugin
//    }
//}
//
////定义依赖仓库
//allprojects {
//    repositories {
//        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
//        google()
//        jcenter()
//        mavenCentral()
//    }
//}

