import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)

    alias(libs.plugins.ksp)
}

kotlin {
    explicitApi()

    explicitApi = ExplicitApiMode.Strict
}

android {
    namespace = "io.future.laboratories.anilistapi"
    compileSdk = 34

    defaultConfig {
        minSdk = 27

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    val javaVersion = JavaVersion.VERSION_21
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }
}

dependencies {
    // Moshi & Retrofit
    api(libs.retrofit)

    implementation(libs.retrofit2.converter.moshi)
    implementation(libs.moshi.kotlin)

    ksp(libs.moshi.kotlin.codegen)
}