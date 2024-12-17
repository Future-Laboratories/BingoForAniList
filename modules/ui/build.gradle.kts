import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinAndroid)

    alias(libs.plugins.ksp)
}

kotlin {
    explicitApi()

    explicitApi = ExplicitApiMode.Strict
}

android {
    namespace = "io.future.laboratories.ui"
    compileSdk = 35

    defaultConfig {
        minSdk = 27

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    val javaVersion = JavaVersion.VERSION_21
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions {
        jvmTarget = javaVersion.toString()
        freeCompilerArgs += listOf("-Xcontext-receivers")
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.activity.compose)
    implementation(libs.coil.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material.icons)
    implementation(libs.material3)
    implementation(libs.colorpicker.compose)
    implementation(libs.compose.charts)
    implementation(libs.textflow.material3)

    api(project(":modules:anilistapi"))
    implementation(project(":modules:common"))

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}