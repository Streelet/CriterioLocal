import java.util.Properties

fun String.asBuildConfigValue(): String {
    return "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

val googlePlacesApiKey = providers.environmentVariable("GOOGLE_PLACES_API_KEY").orNull
    ?.takeIf { it.isNotBlank() }
    ?: localProperties.getProperty("googlePlacesApiKey", "")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
<<<<<<< HEAD
=======
    alias(libs.plugins.ksp)
>>>>>>> feature/backend-Modelado-Datos-Arquitectura-Base
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.criteriolocal"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.criteriolocal"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "GOOGLE_PLACES_API_KEY", googlePlacesApiKey.asBuildConfigValue())
        buildConfigField("String", "GOOGLE_PLACES_BASE_URL", "https://maps.googleapis.com/".asBuildConfigValue())
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
<<<<<<< HEAD
=======
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
>>>>>>> feature/backend-Modelado-Datos-Arquitectura-Base
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
<<<<<<< HEAD
    implementation(libs.kotlinx.serialization.json)
=======
    implementation(libs.coil.network.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp.core)
    ksp(libs.androidx.room.compiler)
>>>>>>> feature/backend-Modelado-Datos-Arquitectura-Base

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
