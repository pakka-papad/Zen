package com.github.pakka_papad.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.github.pakka_papad.data.music.ScanStatus
import com.github.pakka_papad.R

@Composable
fun ScanningPage(
    scanStatus: ScanStatus,
    onStartScanClicked: () -> Unit,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.database_search))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            contentAlignment = Alignment.Center,
        ){
            Text(
                text = "Storage scan",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        LottieAnimation(
            composition = composition,
            iterations = 200,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            contentAlignment = Alignment.Center,
        ){
            Text(
                text = "Scan Storage to find all the songs",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }

        Box(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(
                    enabled = scanStatus is ScanStatus.ScanNotRunning,
                    onClick = onStartScanClicked
                ),
        ){
            when(scanStatus){
                is ScanStatus.ScanNotRunning -> {
                    Text(
                        text = "Scan storage",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                is ScanStatus.ScanComplete -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.CenterStart)
                            .background(MaterialTheme.colorScheme.inversePrimary)
                    )
                    Text(
                        text = "Scan complete",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                is ScanStatus.ScanProgress -> {
                    var totalSongs by remember { mutableStateOf(0) }
                    var scanProgress by remember { mutableStateOf(0f) }
                    scanProgress = (scanStatus.parsed.toFloat())/(scanStatus.total.toFloat())
                    totalSongs = scanStatus.total

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(scanProgress)
                            .align(Alignment.CenterStart)
                            .background(MaterialTheme.colorScheme.inversePrimary)
                    )
                    Text(
                        text = "Found $totalSongs songs",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                else -> {  }
            }
        }
    }
}