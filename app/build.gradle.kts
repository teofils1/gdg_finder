plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
    id("kotlin-kapt") // Add this line for kapt
}

// Updated library versions
val versionRoom = "2.6.1"
val versionCore = "1.12.0"
val versionConstraintLayout = "2.1.4"
val versionGlide = "4.15.1"
val versionKotlin = "1.9.10"
val versionKotlinCoroutines = "1.7.3"
val versionLifecycleExtensions = "2.6.2"
val versionNavigation = "2.7.3"
val versionMoshi = "1.15.0"
val versionRetrofit = "2.9.0"
val versionRecyclerView = "1.3.1"

android {
    namespace = "com.example.dmd_project"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dmd_project"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
    }

    kapt {
        correctErrorTypes = true // Useful for libraries like Dagger
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$versionKotlin")
    implementation("androidx.core:core-ktx:1.12.0")

    // Constraint Layout
    implementation("androidx.constraintlayout:constraintlayout:$versionConstraintLayout")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$versionLifecycleExtensions")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$versionLifecycleExtensions")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$versionNavigation")
    implementation("androidx.navigation:navigation-ui-ktx:$versionNavigation")

    // Core with Ktx
    implementation("androidx.core:core-ktx:$versionCore")

    // Moshi
    implementation("com.squareup.moshi:moshi:$versionMoshi")
    implementation("com.squareup.moshi:moshi-kotlin:$versionMoshi")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:$versionRetrofit")
    implementation("com.squareup.retrofit2:converter-moshi:$versionRetrofit")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$versionKotlinCoroutines")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$versionKotlinCoroutines")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    // Glide
    implementation("com.github.bumptech.glide:glide:$versionGlide")
    annotationProcessor("com.github.bumptech.glide:compiler:$versionGlide")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:$versionRecyclerView")

    // Material design components
    implementation("com.google.android.material:material:1.9.0")

    // Play Services Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("androidx.databinding:databinding-runtime:8.1.0")

//    // Room Database
    implementation("androidx.room:room-runtime:2.5.0") // Replace with your Room version
    kapt("androidx.room:room-compiler:2.5.0") // Annotation processor for Room

    implementation("androidx.core:core-ktx:1.12.0") // Use the latest version

//    ksp("androidx.room:room-compiler:$versionRoom")
//    implementation("androidx.room:room-ktx:$versionRoom")
}
