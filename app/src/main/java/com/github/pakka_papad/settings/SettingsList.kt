package com.github.pakka_papad.settings

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.components.OutlinedBox
import com.github.pakka_papad.data.UserPreferences
import com.github.pakka_papad.data.music.ScanStatus
import com.github.pakka_papad.ui.theme.ThemePreference

@Composable
fun SettingsList(
    paddingValues: PaddingValues,
    themePreference: ThemePreference,
    onThemePreferenceChanged: (ThemePreference) -> Unit,
    scanStatus: ScanStatus,
    onScanClicked: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            ThemeSettings(
                themePreference = themePreference, onPreferenceChanged = onThemePreferenceChanged
            )
        }
        item {
            MusicLibrarySettings(
                scanStatus = scanStatus,
                onScanClicked = onScanClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSettings(
    themePreference: ThemePreference,
    onPreferenceChanged: (ThemePreference) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            OutlinedBox(
                label = "App theme",
                contentPadding = PaddingValues(vertical = 13.dp, horizontal = 20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Use Material You Theme"
                    )
                    Switch(checked = themePreference.useMaterialYou, onCheckedChange = {
                        onPreferenceChanged(themePreference.copy(useMaterialYou = it))
                    })
                }
            }
        }
        OutlinedBox(
            label = "Theme mode",
            contentPadding = PaddingValues(vertical = 13.dp, horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.selectableGroup(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    RadioButton(
                        selected = (themePreference.theme == UserPreferences.Theme.LIGHT_MODE || themePreference.theme == UserPreferences.Theme.UNRECOGNIZED),
                        onClick = {
                            onPreferenceChanged(themePreference.copy(theme = UserPreferences.Theme.LIGHT_MODE))
                        },
                    )
                    Text(
                        text = "Light",
                        textAlign = TextAlign.Center,

                        )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                    RadioButton(
                        selected = (themePreference.theme == UserPreferences.Theme.DARK_MODE),
                        onClick = {
                            onPreferenceChanged(themePreference.copy(theme = UserPreferences.Theme.DARK_MODE))
                        },
                    )
                    Text(
                        text = "Dark",
                        textAlign = TextAlign.Center,

                        )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                    RadioButton(
                        selected = (themePreference.theme == UserPreferences.Theme.USE_SYSTEM_MODE),
                        onClick = {
                            onPreferenceChanged(themePreference.copy(theme = UserPreferences.Theme.USE_SYSTEM_MODE))
                        },
                    )
                    Text(
                        text = "Use System",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

    }
}

@Composable
private fun MusicLibrarySettings(
    scanStatus: ScanStatus,
    onScanClicked: () -> Unit,
) {
    OutlinedBox(
        label = "Music library",
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 13.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        when(scanStatus) {
            is ScanStatus.ScanNotRunning -> {
                Button(
                    onClick = onScanClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Scan for music",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            is ScanStatus.ScanComplete -> {
                Text(
                    text = "Scan Complete",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            is ScanStatus.ScanProgress -> {
                var totalSongs by remember { mutableStateOf(0) }
                var scanProgress by remember { mutableStateOf(0f) }
                scanProgress = (scanStatus.parsed.toFloat()) / (scanStatus.total.toFloat())
                totalSongs = scanStatus.total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "Found $totalSongs songs",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    CircularProgressIndicator(progress = scanProgress)
                }
            }
            else -> {  }
        }
    }
}