package com.github.pakka_papad

import android.app.Application
import android.graphics.Bitmap
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import cat.ereza.customactivityoncrash.config.CaocConfig
import cat.ereza.customactivityoncrash.config.CaocConfig.BACKGROUND_MODE_SILENT
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.github.pakka_papad.workers.ThumbnailWorker
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ZenApp: Application(), ImageLoaderFactory, Configuration.Provider {

    @Inject lateinit var hiltWorkerFactory: HiltWorkerFactory

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

        WorkManager.getInstance(this)
            .enqueue(OneTimeWorkRequestBuilder<ThumbnailWorker>().build())
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this).apply {
            allowRgb565(true)
            bitmapConfig(Bitmap.Config.RGB_565)
            error(R.drawable.error)
        }.build()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
    }
}