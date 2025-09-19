import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

android {
    namespace = "com.jointag.proximity.examples.empty"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jointag.proximity.examples.empty"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    flavorDimensions.add("provider")
    // Different product flavour based on which service platform you want to use
    // for location and advertising
    productFlavors {
        create("gms") {
            dimension = "provider"
        }
        create("huawei") {
            dimension = "provider"
            applicationIdSuffix = ".hms"
            versionNameSuffix = "-HMS"
        }
    }
}

val gmsImplementation by configurations
val huaweiImplementation by configurations

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // For Google Play Services
    gmsImplementation(libs.proximitysdk)

    // For Huawei Mobile Services
    huaweiImplementation(libs.hms.ads.identifier)
    huaweiImplementation(libs.hms.location)
    huaweiImplementation(libs.proximitysdk) {
        exclude(group = "com.google.android.gms")
    }

    implementation(libs.cmp.sdk)
}