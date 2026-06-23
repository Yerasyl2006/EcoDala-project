plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

fun secret(name: String): String? {
    return localProperties.getProperty(name)
        ?: keystoreProperties.getProperty(name)
        ?: System.getenv(name)
}

val releaseSigningReady = listOf(
    "ECODALA_RELEASE_STORE_FILE",
    "ECODALA_RELEASE_STORE_PASSWORD",
    "ECODALA_RELEASE_KEY_ALIAS",
    "ECODALA_RELEASE_KEY_PASSWORD"
).all { !secret(it).isNullOrBlank() }

android {
    namespace = "com.ecodala"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ecodala"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = secret("GOOGLE_MAPS_API_KEY").orEmpty()
        manifestPlaceholders["USES_CLEARTEXT_TRAFFIC"] = "true"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    signingConfigs {
        if (releaseSigningReady) {
            create("release") {
                storeFile = file(secret("ECODALA_RELEASE_STORE_FILE").orEmpty())
                storePassword = secret("ECODALA_RELEASE_STORE_PASSWORD")
                keyAlias = secret("ECODALA_RELEASE_KEY_ALIAS")
                keyPassword = secret("ECODALA_RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            manifestPlaceholders["USES_CLEARTEXT_TRAFFIC"] = "true"
            buildConfigField("String", "ECODALA_API_BASE_URL", "\"${secret("ECODALA_DEBUG_API_BASE_URL") ?: "http://127.0.0.1:8000/api/"}\"")
            buildConfigField("Boolean", "ECODALA_LOGGING_ENABLED", "true")
        }
        release {
            manifestPlaceholders["USES_CLEARTEXT_TRAFFIC"] = "false"
            buildConfigField("String", "ECODALA_API_BASE_URL", "\"${secret("ECODALA_RELEASE_API_BASE_URL") ?: "https://api.ecodala.kz/api/"}\"")
            buildConfigField("Boolean", "ECODALA_LOGGING_ENABLED", "false")
            if (releaseSigningReady) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("org.osmdroid:osmdroid-android:6.1.20")
    implementation("androidx.camera:camera-core:1.4.0")
    implementation("androidx.camera:camera-camera2:1.4.0")
    implementation("androidx.camera:camera-lifecycle:1.4.0")
    implementation("androidx.camera:camera-view:1.4.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
