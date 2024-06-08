import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinAndroid)

    alias(libs.plugins.ksp)
}

kotlin {
    explicitApi()

    explicitApi = ExplicitApiMode.Strict
}

android {
    namespace = "io.future.laboratories.anilistbingo"
    compileSdk = 34

    val major = 1
    val minor = 0
    val patch = 0

    defaultConfig {
        applicationId = "io.future.laboratories.anilistbingo"
        minSdk = 27
        targetSdk = 34
        versionCode = major * 100 * 100 + minor * 100 + patch
        versionName = "$major.$minor.$patch"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.create("release")
        }
    }

    signingConfigs {
        println("my message visible by default")

        getByName("release") {
            val tmpFilePath = System.getProperty("user.home") + "/work/_temp/keystore/"
            val allFilesFromDir = File(tmpFilePath).listFiles()

            if (allFilesFromDir != null) {
                val keystoreFile = allFilesFromDir.first()
                keystoreFile.copyTo(file("keystore/keystore.jks"), true)
            }

            storeFile = file("keystore/keystore.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
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
    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.material3)

    implementation(project(":modules:anilistapi"))
    implementation(project(":modules:common"))
    implementation(project(":modules:ui"))

    // Splashscreen
    implementation(libs.androidx.core.splashscreen)

    // testImplementations
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}