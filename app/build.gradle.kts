plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "tech.zemn.mobile"
    compileSdk = Api.compileSdk

    defaultConfig {
        applicationId = "tech.zemn.mobile"
        minSdk = Api.minSdk
        targetSdk = Api.targetSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.androidxComposeCompiler
    }

    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(Libraries.androidxCore)

    implementation(Libraries.androidxLifecycle)

    implementation(Libraries.androidxActivityCompose)

    implementation(Libraries.androidxComposeUi)
    implementation(Libraries.androidxComposeUiToolingPreview)
    debugImplementation(Libraries.androidxComposeUiTooling)
    debugImplementation(Libraries.androidxComposeUiTestManifest)
    androidTestImplementation(Libraries.androidxComposeUiTestJunit4)

    implementation(Libraries.androidxComposeMaterial)

    implementation(Libraries.appCompat)
    implementation(Libraries.navigationUi)
    implementation(Libraries.navigationFragment)

    testImplementation(Libraries.junit)
    androidTestImplementation(Libraries.androidxJunit)
    androidTestImplementation(Libraries.androidxEspresso)

    debugImplementation(Libraries.leakcanary)

    implementation(Libraries.hilt)
    kapt(AnnotationProcessors.hiltCompiler)

    implementation(Libraries.timber)

    implementation(Libraries.roomRuntime)
    implementation(Libraries.roomKtx)
    kapt(AnnotationProcessors.roomCompiler)

    implementation(Libraries.exoPlayer)
    implementation(Libraries.media3ExoPlayer)
    implementation(Libraries.media3Transformer)
    implementation(Libraries.exoPlayerUi)
}