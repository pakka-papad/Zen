package com.github.pakka_papad

import android.graphics.Color
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.github.pakka_papad.data.UserPreferences
import com.github.pakka_papad.ui.theme.ThemePreference
import com.github.pakka_papad.ui.theme.ZenTheme

class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = CustomActivityOnCrash.getConfigFromIntent(intent) ?: run {
            finish()
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT

        setContent {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bug))
            ZenTheme(
                themePreference = ThemePreference(
                    useMaterialYou = true,
                    theme = UserPreferences.Theme.USE_SYSTEM_MODE,
                    accent = UserPreferences.Accent.Elm,
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 500.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .windowInsetsPadding(WindowInsets.systemBars)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LottieAnimation(
                        composition = composition,
                        iterations = 200,
                        modifier = Modifier
                            .weight(1f)
                            .sizeIn(maxWidth = 400.dp, maxHeight = 400.dp)
                    )
                    Text(
                        text = getString(cat.ereza.customactivityoncrash.R.string.customactivityoncrash_error_activity_error_occurred_explanation),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                CustomActivityOnCrash.restartApplication(this@CrashActivity, config)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Restart",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }
}