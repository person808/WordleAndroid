import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.android.application")
  id("kotlin-android")
  alias(libs.plugins.ksp)
  alias(libs.plugins.dagger.hilt.android)
  alias(libs.plugins.protobuf)
  alias(libs.plugins.ktfmt)
}

android {
  compileSdk = 34

  defaultConfig {
    applicationId = "com.kainalu.wordle"
    minSdk = 23
    targetSdk = 34
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
  }
  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions { jvmTarget = "1.8" }
  buildFeatures { compose = true }
  composeOptions { kotlinCompilerExtensionVersion = libs.versions.compose.get() }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
  namespace = "com.kainalu.wordle"
}

ktfmt { googleStyle() }

ksp {
  arg("room.schemaLocation", "$projectDir/schemas")
  arg("room.generateKotlin", "true")
}

dependencies {
  coreLibraryDesugaring(libs.desugarJdkLibs)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.compose.ui.asProvider())
  implementation(libs.androidx.compose.material.asProvider())
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

  implementation(libs.androidx.datastore)
  implementation(libs.protobuf.javalite)
  implementation(libs.dagger.hilt.android)
  implementation(libs.androidx.hilt.navigation.compose)
  ksp(libs.dagger.compiler)
  ksp(libs.dagger.hilt.compiler)

  implementation(libs.timber)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.junit)
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling.asProvider())
}

protobuf {
  protoc { artifact = libs.protobuf.protoc.get().toString() }

  // Generates the java Protobuf-lite code for the Protobufs in this project. See
  // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
  // for more information.
  generateProtoTasks {
    all().forEach { task -> task.builtins { create("java") { option("lite") } } }
  }
}

androidComponents {
  // workaround for https://github.com/google/ksp/issues/1590
  onVariants(selector().all()) { variant ->
    afterEvaluate {
      val capName = variant.name.capitalize()
      tasks.getByName<KotlinCompile>("ksp${capName}Kotlin") {
        setSource(tasks.getByName("generate${capName}Proto").outputs)
      }
    }
  }
}
