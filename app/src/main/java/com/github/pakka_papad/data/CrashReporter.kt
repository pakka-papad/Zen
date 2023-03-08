package com.github.pakka_papad.data

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class ZenCrashReporter @Inject constructor(private val firebase: FirebaseCrashlytics) {

    fun logException(e: Exception?){
        if (e != null) {
            firebase.recordException(e)
        }
    }

    fun sendCrashData(reportData: Boolean){
        firebase.setCrashlyticsCollectionEnabled(reportData)
    }
}