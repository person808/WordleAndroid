dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }

  versionCatalogs {
    create("libs") {
      val compose = version("compose", "1.5.4")
      val dagger = version("dagger", "2.50")
      val lifecycle = version("lifecycle", "2.6.2")
      val room = version("room", "2.6.1")
      val protobuf = version("protobuf", "3.25.1")

      plugin("ksp", "com.google.devtools.ksp").version("1.9.20-1.0.14")
      plugin("dagger-hilt-android", "com.google.dagger.hilt.android").versionRef(dagger)
      plugin("protobuf", "com.google.protobuf").version("0.9.4")
      plugin("ktfmt", "com.ncorti.ktfmt.gradle").version("0.16.0")

      library("android-gradle", "com.android.tools.build:gradle:8.2.0")
      library("kotlin", "org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")

      library("desugarJdkLibs", "com.android.tools:desugar_jdk_libs:2.0.4")

      // Compose
      library("androidx-compose-ui", "androidx.compose.ui", "ui").versionRef(compose)
      library("androidx-compose-material", "androidx.compose.material", "material")
        .versionRef(compose)
      library("androidx-compose-foundation", "androidx.compose.foundation", "foundation")
        .versionRef(compose)
      library("androidx-compose-ui-tooling-preview", "androidx.compose.ui", "ui-tooling-preview")
        .versionRef(compose)
      library(
          "androidx-compose-material-icons-extended",
          "androidx.compose.material",
          "material-icons-extended"
        )
        .versionRef(compose)
      library("androidx-compose-ui-test-junit4", "androidx.compose.ui", "ui-test-junit4")
        .versionRef(compose)
      library("androidx-compose-ui-tooling", "androidx.compose.ui", "ui-tooling")
        .versionRef(compose)

      library("androidx-core-ktx", "androidx.core:core-ktx:1.12.0")
      library("androidx-appcompat", "androidx.appcompat:appcompat:1.6.1")
      library("material", "com.google.android.material:material:1.11.0")
      library("androidx-navigation-compose", "androidx.navigation:navigation-compose:2.7.6")
      library("androidx-activity-compose", "androidx.activity:activity-compose:1.8.2")
      library(
        "androidx-constraintlayout-compose",
        "androidx.constraintlayout:constraintlayout-compose:1.0.1"
      )

      // Lifecycle
      library("androidx-lifecycle-runtime-ktx", "androidx.lifecycle", "lifecycle-runtime-ktx")
        .versionRef(lifecycle)
      library("androidx-lifecycle-viewmodel-ktx", "androidx.lifecycle", "lifecycle-viewmodel-ktx")
        .versionRef(lifecycle)
      library(
          "androidx-lifecycle-viewmodel-compose",
          "androidx.lifecycle",
          "lifecycle-viewmodel-compose"
        )
        .versionRef(lifecycle)
      library("androidx-lifecycle-livedata-ktx", "androidx.lifecycle", "lifecycle-livedata-ktx")
        .versionRef(lifecycle)

      // Room
      library("androidx-room-runtime", "androidx.room", "room-runtime").versionRef(room)
      library("androidx-room-ktx", "androidx.room", "room-ktx").versionRef(room)
      library("androidx-room-compiler", "androidx.room", "room-compiler").versionRef(room)

      library("androidx-datastore", "androidx.datastore:datastore:1.0.0")

      // Protobuf
      library("protobuf-javalite", "com.google.protobuf", "protobuf-javalite").versionRef(protobuf)
      library("protobuf-protoc", "com.google.protobuf", "protoc").versionRef(protobuf)

      // Dagger/Hilt
      library("dagger-compiler", "com.google.dagger", "dagger-compiler").versionRef(dagger)
      library("dagger-hilt-compiler", "com.google.dagger", "hilt-compiler").versionRef(dagger)
      library("dagger-hilt-android", "com.google.dagger", "hilt-android").versionRef(dagger)
      library("androidx-hilt-navigation-compose", "androidx.hilt:hilt-navigation-compose:1.1.0")

      library("timber", "com.jakewharton.timber:timber:5.0.1")

      library("junit", "junit:junit:4.13.2")
      library("androidx-test-junit", "androidx.test.ext:junit:1.1.5")
      library("androidx-test-espresso-core", "androidx.test.espresso:espresso-core:3.5.1")
    }
  }
}

rootProject.name = "WordleApp"

include(":app")
