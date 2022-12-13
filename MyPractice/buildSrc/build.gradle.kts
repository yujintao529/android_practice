plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    jcenter()
    google()
    mavenCentral()
}
dependencies {
    implementation("com.android.tools.build:gradle:7.0.0")
    gradleApi()
}



