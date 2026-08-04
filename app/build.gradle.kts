plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.21-1.0.15"
}

android {
    namespace = "com.neonwhisper.combo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.neonwhisper.combo"
        minSdk = 26
        targetSdk = 34
        versionCode = 3
        versionName = "1.2.0-alpha"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Room Database (наш кэш!)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // OkHttp (HTTP клиент для API)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // Kotlin Coroutines (асинхронность)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    
    // JSON парсинг
    implementation("org.json:json:20240303")
}
