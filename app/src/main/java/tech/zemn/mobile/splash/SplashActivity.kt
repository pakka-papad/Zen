package tech.zemn.mobile.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import tech.zemn.mobile.MainActivity
import tech.zemn.mobile.R
import tech.zemn.mobile.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            statusBarColor = getColor(R.color.surface)
            navigationBarColor = getColor(R.color.surface)
        }

        lifecycleScope.launch {
            delay(2500)
            withContext(Dispatchers.Main) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}