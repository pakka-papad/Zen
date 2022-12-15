object Versions {
    const val kotlin = "1.6.10"
    const val coroutines = "1.6.1"

    const val androidxComposeUi = "1.2.1"
    const val androidxComposeCompiler = "1.1.1"
    const val androidxComposeMaterial = "1.2.1"
    const val androidxCore = "1.7.0"
    const val androidxLifecycle = "2.5.1"
    const val androidxLifecycleRuntimeCompose = "2.6.0-alpha01"
    const val androidxActivity = "1.5.1"
    const val androidxJunit = "1.1.3"
    const val androidxEspresso = "3.4.0"
    const val androidxComposeConstraintLayout = "1.0.1"
    const val androidxSplashScreen = "1.0.0-beta02"

    const val appCompat = "1.4.2"
    const val navigation = "2.4.2"

    const val room = "2.4.3"
    const val datastore = "1.0.0"
    const val kotlinLite = "3.18.0"

    const val material3 = "1.0.0-alpha14"
    const val accompanist = "0.25.1"

    const val junit = "4.13.2"

    const val leakcanary = "2.9.1"

    const val hilt = "2.38.1"

    const val timber = "5.0.1"

    const val exoPlayer = "2.18.1"
    const val media3 = "1.0.0-beta02"

    const val coilCompose = "2.2.2"
}

object Plugins {
    const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
    const val navSafeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object Libraries {
    const val androidxCore = "androidx.core:core-ktx:${Versions.androidxCore}"

    const val androidxLifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidxLifecycle}"
    const val androidxLifecycleRuntimeCompose = "androidx.lifecycle:lifecycle-runtime-compose:${Versions.androidxLifecycleRuntimeCompose}"

    const val androidxActivityCompose = "androidx.activity:activity-compose:${Versions.androidxActivity}"

    const val androidxComposeUi = "androidx.compose.ui:ui:${Versions.androidxComposeUi}"
    const val androidxComposeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview:${Versions.androidxComposeUi}"
    const val androidxComposeUiTooling = "androidx.compose.ui:ui-tooling:${Versions.androidxComposeUi}"
    const val androidxComposeUiTestManifest = "androidx.compose.ui:ui-test-manifest:${Versions.androidxComposeUi}"
    const val androidxComposeUiTestJunit4 = "androidx.compose.ui:ui-test-junit4:${Versions.androidxComposeUi}"
    const val androidxComposeConstraintLayout = "androidx.constraintlayout:constraintlayout-compose:${Versions.androidxComposeConstraintLayout}"
    const val androidxSplashScreen = "androidx.core:core-splashscreen:${Versions.androidxSplashScreen}"

    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val datastore = "androidx.datastore:datastore:${Versions.datastore}"
    const val kotlinLite = "com.google.protobuf:protobuf-kotlin-lite:${Versions.kotlinLite}"

    const val androidxComposeMaterial = "androidx.compose.material:material:${Versions.androidxComposeMaterial}"

    const val junit = "junit:junit:${Versions.junit}"
    const val androidxJunit = "androidx.test.ext:junit:${Versions.androidxJunit}"
    const val androidxEspresso = "androidx.test.espresso:espresso-core:${Versions.androidxEspresso}"

    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"

    const val material3 = "androidx.compose.material3:material3:${Versions.material3}"
    const val material3WindowSizeClass = "androidx.compose.material3:material3-window-size-class:${Versions.material3}"

    const val accompanistSystemUiController = "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}"
    const val accompanistPermissions = "com.google.accompanist:accompanist-permissions:${Versions.accompanist}"
    const val accompanistPager = "com.google.accompanist:accompanist-pager:${Versions.accompanist}"
    const val accompanistPagerIndicator = "com.google.accompanist:accompanist-pager-indicators:${Versions.accompanist}"

    const val leakcanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"

    const val hilt = "com.google.dagger:hilt-android:${Versions.hilt}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val exoPlayer = "com.google.android.exoplayer:exoplayer:${Versions.exoPlayer}"
    const val media3ExoPlayer = "androidx.media3:media3-exoplayer:${Versions.media3}"
    const val media3Transformer = "androidx.media3:media3-transformer:${Versions.media3}"
    const val exoPlayerUi = "com.google.android.exoplayer:exoplayer-ui:${Versions.exoPlayer}"

    const val coilCompose = "io.coil-kt:coil-compose:${Versions.coilCompose}"
}

object Api {
    const val compileSdk = 32
    const val minSdk = 23
    const val targetSdk = 32
}

object AnnotationProcessors {
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
}

