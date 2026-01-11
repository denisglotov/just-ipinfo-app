plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.compose")
}

android {
  namespace = "org.glotov.justipinfo"
  compileSdk = 36

  defaultConfig {
    applicationId = "org.glotov.justipinfo"
    minSdk = 26
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
    ndkVersion = "26.1.10909125"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables { useSupportLibrary = true }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
  buildFeatures { compose = true }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
  implementation("androidx.core:core-ktx:1.17.0-alpha01")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0-alpha01")
  implementation("androidx.activity:activity-compose:1.10.0-alpha03")
  implementation(platform("androidx.compose:compose-bom:2025.01.00"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3")

  // Networking
  implementation("com.squareup.okhttp3:okhttp:4.12.0")

  // Viewmodel
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0-alpha01")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.3.0-alpha01")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0-alpha01")
  androidTestImplementation(platform("androidx.compose:compose-bom:2025.01.00"))
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}
