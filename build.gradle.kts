buildscript {
    dependencies {
        classpath(Plugins.hilt)
        classpath(Plugins.kotlin)
        classpath(Plugins.navSafeArgs)
        //classpath(Plugins.googleServices)
        classpath(Plugins.crashlytics)
    }
}

plugins {
    id("com.android.application") version "7.4.2" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android") version Versions.kotlin apply false
}