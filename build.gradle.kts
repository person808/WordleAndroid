// Top-level build file where you can add configuration options common to all sub-projects/modules.

val compose_version by extra("1.5.4")
val daggerVersion by extra("2.50")

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

plugins {
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
}

tasks.register("clean") {
    delete(rootProject.buildDir)
}
