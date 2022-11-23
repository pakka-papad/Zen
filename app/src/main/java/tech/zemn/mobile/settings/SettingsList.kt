package tech.zemn.mobile.settings

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tech.zemn.mobile.components.OutlinedBox
import tech.zemn.mobile.data.UserPreferences
import tech.zemn.mobile.ui.theme.ThemePreference

@Composable
fun SettingsList(
    paddingValues: PaddingValues,
    themePreference: ThemePreference,
    onThemePreferenceChanged: (ThemePreference) -> Unit,
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