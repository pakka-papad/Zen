object Versions {
    const val kotlin = "1.6.10"
    const val coroutines = "1.6.1"

    const val androidxComposeUi = "1.2.1"
    const val androidxComposeCompiler = "1.1.1"
    const val androidxComposeMaterial = "1.2.1"
    const val androidxCore = "1.7.0"
    const val androidxLifecycle = "2.5.1"
    const val androidxActivity = "1.5.1"
    const val androidxJunit = "1.1.3"
    const val androidxEspresso = "3.4.0"

    const val junit = "4.13.2"

    const val leakcanary = "2.9.1"

    const val hilt = "2.38.1"

    const val timber = "5.0.1"
}

object Plugins {
    const val hiltGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
}

object Libraries {
    const val androidxCore = "androidx.core:core-ktx:${Versions.androidxCore}"

    const val androidxLifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidxLifecycle}"

    const val androidxActivityCompose = "androidx.activity:activity-compose:${Versions.androidxActivity}"

    const val androidxComposeUi = "androidx.compose.ui:ui:${Versions.androidxComposeUi}"
    const val androidxComposeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview:${Versions.androidxComposeUi}"
    const val androidxComposeUiTooling = "androidx.compose.ui:ui-tooling:${Versions.androidxComposeUi}"
    const val androidxComposeUiTestManifest = "androidx.compose.ui:ui-test-manifest:${Versions.androidxComposeUi}"
    const val androidxComposeUiTestJunit4 = "androidx.compose.ui:ui-test-junit4:${Versions.androidxComposeUi}"

    const val androidxComposeMaterial = "androidx.compose.material:material:${Versions.androidxComposeMaterial}"

    const val junit = "junit:junit:${Versions.junit}"
    const val androidxJunit = "androidx.test.ext:junit:${Versions.androidxJunit}"
    const val androidxEspresso = "androidx.test.espresso:espresso-core:${Versions.androidxEspresso}"

    const val leakcanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"

    const val hilt = "com.google.dagger:hilt-android:${Versions.hilt}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
}

object Api {
    const val compileSdk = 32
    const val minSdk = 23
    const val targetSdk = 32
}

object AnnotationProcessors {
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
}

