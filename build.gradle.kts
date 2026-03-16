// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  repositories {
    google()
    mavenCentral()
  }
  dependencies {
    classpath(libs.android.gradle)
    classpath(libs.kotlin)
    classpath(libs.ksp)
    classpath(libs.wire)

    // NOTE: Do not place your application dependencies here; they belong
    // in the individual module build.gradle.kts files
  }
}

plugins {
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.dagger.hilt.android) apply false
  alias(libs.plugins.spotless)
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
  kotlin {
    target("*/**/*.kt")
    ktfmt().googleStyle()
  }
  kotlinGradle {
    target("*.kts", "*/**/*.kts")
    ktfmt().googleStyle()
  }
}

tasks.named("spotlessKotlinGradle") { dependsOn("spotlessKotlin") }
