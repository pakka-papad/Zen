package com.github.pakka_papad.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.data.music.ScanStatus
import com.github.pakka_papad.round

@Composable
fun ScanningPage(
    scanStatus: ScanStatus,
    onStartScanClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (scanStatus is ScanStatus.ScanNotRunning) {
            Button(
                onClick = onStartScanClicked
            ) {
                Text(
                    text = "Scan Storage to find all the awesome songs \uD83D\uDE80"
                )
            }
        } else if (scanStatus is ScanStatus.ScanComplete) {
            Text(
                text = "Scan Complete",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        } else {
            var totalSongs by remember { mutableStateOf(0) }
            var scanProgress by remember { mutableStateOf(0f) }
            when (scanStatus) {
                is ScanStatus.ScanProgress -> {
                    scanProgress = (scanStatus.parsed.toFloat())/(scanStatus.total.toFloat())
                    totalSongs = scanStatus.total
                }
                else -> {}
            }
            Text(
                text = "Found $totalSongs awesome songs",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            LinearProgressIndicator(
                progress = scanProgress,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
            Text(
                text = "${(scanProgress*100).round(2)}%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}