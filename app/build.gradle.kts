import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
  id("com.android.application")
  alias(libs.plugins.ksp)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.dagger.hilt.android)
  alias(libs.plugins.wire)
  jacoco
}

kotlin {
  compilerOptions {
    languageVersion = KotlinVersion.KOTLIN_2_3
    jvmTarget = JvmTarget.JVM_17
  }
}

java { toolchain { languageVersion = JavaLanguageVersion.of(17) } }

android {
  compileSdk = 36

  defaultConfig {
    applicationId = "com.kainalu.wordle"
    minSdk = 23
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables { useSupportLibrary = true }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs["debug"]
    }
    debug {
      enableUnitTestCoverage = true
      enableAndroidTestCoverage = true
    }
  }
  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures { compose = true }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
  namespace = "com.kainalu.wordle"
}

ksp {
  arg("room.schemaLocation", "$projectDir/schemas")
  arg("room.generateKotlin", "true")
}

wire { kotlin {} }

tasks.withType<Test> {
  configure<JacocoTaskExtension> {
    isIncludeNoLocationClasses = true
    excludes = listOf("jdk.internal.*")
  }
}

tasks.register<JacocoReport>("unitTestDebugCoverageReport") {
  group = "Coverage"
  dependsOn("testDebugUnitTest")

  reports {
    html.required = true
    xml.required = true
  }

  // Execution data generated when running the tests against classes instrumented by the JaCoCo
  // agent. This is enabled with 'enableUnitTestCoverage' in the 'debug' build type.
  executionData.from(
    layout.buildDirectory.dir(
      "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
    )
  )

  val fileFilter =
    listOf(
      "**/R.class",
      "**/R$*.class",
      "**/BuildConfig.*",
      "**/Manifest*.*",
      "**/*Test*.*",
      "android/**/*.*",
      "androidx/**/*.*",
      "**/hilt_aggregated_deps/**",
      "**/dagger/hilt/internal/*.*",
      "**/*_HiltComponents*.*",
      "**/*_Factory.*",
      "**/*_MembersInjector.*",
      "**/composables/*$*.class",
    )
  val javaClasses =
    fileTree(
      layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")
    ) {
      exclude(fileFilter)
    }
  val kotlinClasses =
    fileTree(
      layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")
    ) {
      exclude(fileFilter)
    }

  classDirectories.from(files(javaClasses, kotlinClasses))

  // To produce an accurate report, the bytecode is mapped back to the original source code.
  sourceDirectories.from(files("src/main/java", "src/main/kotlin"))
}

afterEvaluate {
  tasks { getByName("testDebugUnitTest") { finalizedBy("unitTestDebugCoverageReport") } }
}

dependencies {
  coreLibraryDesugaring(libs.desugarJdkLibs)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.compose.ui.asProvider())
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.lifecycle.livedata.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.constraintlayout.compose)

  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)

  implementation(libs.wire.runtime)
  implementation(libs.androidx.datastore)
  implementation(libs.dagger.hilt.android)
  implementation(libs.androidx.hilt.navigation.compose)
  ksp(libs.dagger.compiler)
  ksp(libs.dagger.hilt.compiler)

  implementation(libs.timber)

  testImplementation(libs.junit)
  testImplementation(libs.kotlin.coroutines.test)
  testImplementation(libs.mockk)
  testImplementation(libs.junit.params)
  androidTestImplementation(libs.androidx.test.junit)
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.junit.ktx)
  debugImplementation(libs.androidx.compose.ui.tooling.asProvider())
}
