package com.github.pakka_papad

import android.app.Application
import android.graphics.Bitmap
import cat.ereza.customactivityoncrash.config.CaocConfig
import cat.ereza.customactivityoncrash.config.CaocConfig.BACKGROUND_MODE_SILENT
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ZenApp: Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        CaocConfig.Builder.create().apply {
            restartActivity(MainActivity::class.java)
            errorActivity(CrashActivity::class.java)
            backgroundMode(BACKGROUND_MODE_SILENT)
            apply()
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this).apply {
            allowRgb565(true)
            bitmapConfig(Bitmap.Config.RGB_565)
            error(R.drawable.error)
        }.build()
    }
}