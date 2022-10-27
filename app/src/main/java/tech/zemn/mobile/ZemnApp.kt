package tech.zemn.mobile

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ZemnApp: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        calculateNavBarHeight(this)
        calculateStatusBarHeight(this)
    }

    companion object {
        var navBarHeight: Int = 0
        private fun calculateNavBarHeight(context: Context) {
            val resName = if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                "navigation_bar_height"
            } else {
                "navigation_bar_height_landscape"
            }
            val id: Int = context.resources.getIdentifier(resName, "dimen", "android")
            navBarHeight = if (id > 0) {
                context.resources.getDimensionPixelSize(id)
            } else {
                0
            }
        }
        var statusBarHeight: Int = 0
        private fun calculateStatusBarHeight(context: Context) {
            val resName = if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                "status_bar_height"
            } else {
                "status_bar_height_landscape"
            }
            val id: Int = context.resources.getIdentifier(resName, "dimen", "android")
            statusBarHeight = if (id > 0) {
                context.resources.getDimensionPixelSize(id)
            } else {
                0
            }
        }
    }
}