package tech.zemn.mobile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import tech.zemn.mobile.databinding.ActivityMainBinding
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<SharedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window,false)
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            statusBarColor = Color.TRANSPARENT
        }
        var ok = true
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            if (!ok) return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // consider left/right insets in landscape orientation
            top = insets.top.toDp()
            bottom = insets.bottom.toDp()
            Timber.d("insets changed\ntop = $top, bottom = $bottom")
            ok = false
            WindowInsetsCompat.CONSUMED
        }
        if (!allPermissionsGranted()) requestPermission()

    }

    private fun Int.toDp(): Float {
        return this / (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT.toFloat())
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
        }
        return true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, requiredPermissions.toTypedArray(), requestCode)
    }

    companion object {
        val requiredPermissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
        const val requestCode = 10
        var top = 0f
            private set
        var bottom = 0f
            private set
    }
}