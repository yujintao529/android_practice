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
    implementation("com.android.tools.build:gradle:4.1.0")
    gradleApi()
}



