plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.skillboost"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.skillboost"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    // Core dependencies
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.coil.compose)
    implementation(libs.cardview)
    implementation(libs.recyclerview)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.ui.text.android)
    //debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)


    // Unit test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.compose.ui.test.junit4)

    // Instrumentation test dependencies
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.runner)
    implementation("androidx.compose.material3:material3:1.2.1") // or latest version
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.activity:activity-compose:1.8.0")
    // if you're using MaterialComponents
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.1")
}