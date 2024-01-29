buildscript {
    dependencies {
        classpath(Plugins.hilt)
        classpath(Plugins.kotlin)
        classpath(Plugins.navSafeArgs)
        classpath(Plugins.googleServices)
        classpath(Plugins.crashlytics)
    }
}

plugins {
    id("com.android.application") version "8.2.1" apply false
    id("com.android.library") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version Versions.kotlin apply false
}