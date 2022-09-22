buildscript {
    dependencies {
        classpath(Plugins.hilt)
        classpath(Plugins.kotlin)
        classpath(Plugins.navSafeArgs)
    }
}

plugins {
    id("com.android.application") version "7.3.0" apply false
    id("com.android.library") version "7.3.0" apply false
    id("org.jetbrains.kotlin.android") version Versions.kotlin apply false
}