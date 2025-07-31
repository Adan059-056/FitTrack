plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //id("com.android.application")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.proyectoe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.proyectoe"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation("com.google.firebase:firebase-storage-ktx")

    //Navegacion
    implementation(libs.androidx.navigation.compose)

    //foto
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.activity:activity-compose:1.9.0")

    //implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    //implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")
    //implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")

    // Para ViewModel con Compose
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose")

    // Para LiveData con Compose (incluye observeAsState)
    implementation ("androidx.compose.runtime:runtime-livedata")

    // Para LiveData y Transformations (parte de los KTX)
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx")

    // Para ViewModel KTX (si necesitas ViewModelProvider.Factory simplificado, etc.)
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    //implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    implementation("com.google.firebase:firebase-database")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation ("androidx.compose.material:material-icons-extended:1.6.7")
    implementation("androidx.compose.runtime:runtime-saveable")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout-compose-android:1.1.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-test-manifest")
    implementation ("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation ("com.google.firebase:firebase-core:21.1.1")
    implementation ("androidx.work:work-runtime-ktx:2.8.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")

}