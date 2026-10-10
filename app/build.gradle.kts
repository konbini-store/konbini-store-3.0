plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.github.konbini.store3"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "io.github.konbini.store3"
        minSdk = 14
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    testImplementation(libs.junit)
    implementation("com.loopj.android:android-async-http:1.4.9")
    implementation("com.android.support:support-v4:13.0.0")
}