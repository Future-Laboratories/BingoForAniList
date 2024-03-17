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
    namespace = "io.future.laboratories.ui"
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
        freeCompilerArgs += listOf("-Xcontext-receivers")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    val composeCompiler: String by project
    composeOptions {
        kotlinCompilerExtensionVersion = composeCompiler
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

    api(project(":modules:anilistapi"))
    implementation(project(":modules:common"))

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}