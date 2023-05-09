package com.github.pakka_papad.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.BuildConfig
import com.github.pakka_papad.R
import com.github.pakka_papad.components.OutlinedBox
import com.github.pakka_papad.data.UserPreferences
import com.github.pakka_papad.data.UserPreferences.Accent
import com.github.pakka_papad.data.music.ScanStatus
import com.github.pakka_papad.ui.theme.ThemePreference
import com.github.pakka_papad.ui.theme.getSeedColor
import timber.log.Timber

@Composable
fun SettingsList(
    paddingValues: PaddingValues,
    themePreference: ThemePreference,
    onThemePreferenceChanged: (ThemePreference) -> Unit,
    scanStatus: ScanStatus,
    onScanClicked: () -> Unit,
    onRestoreClicked: () -> Unit,
    disabledCrashlytics: Boolean,
    onAutoReportCrashClicked: (Boolean) -> Unit,
    onWhatsNewClicked: () -> Unit,
    onFolderToScanClicked: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            LookAndFeelSettings(
                themePreference = themePreference,
                onPreferenceChanged = onThemePreferenceChanged,
            )
        }
        item {
            MusicLibrarySettings(
                scanStatus = scanStatus,
                onScanClicked = onScanClicked,
                onRestoreClicked = onRestoreClicked,
                onFolderToScanClicked = onFolderToScanClicked
            )
        }
        item {
            ReportBug(
                disabledCrashlytics = disabledCrashlytics,
                onAutoReportCrashClicked = onAutoReportCrashClicked,
            )
        }
        item {
            MadeBy(
                onWhatsNewClicked = onWhatsNewClicked
            )
        }
    }
}

@Composable
private fun LookAndFeelSettings(
    themePreference: ThemePreference,
    onPreferenceChanged: (ThemePreference) -> Unit,
) {
    val spacerModifier = Modifier.height(10.dp)
    OutlinedBox(
        label = "Look and feel",
        contentPadding = PaddingValues(vertical = 13.dp, horizontal = 20.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Material You",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Switch(checked = themePreference.useMaterialYou, onCheckedChange = {
                        onPreferenceChanged(themePreference.copy(useMaterialYou = it))
                    })
                }
                Spacer(spacerModifier)
            }
            val seedColor by remember(themePreference.accent){
                derivedStateOf {
                    themePreference.accent.getSeedColor()
                }
            }
            var showAccentSelector by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (themePreference.useMaterialYou) 0.5f else 1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Accent color",
                    style = MaterialTheme.typography.titleMedium
                )
                Canvas(
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(
                            enabled = (!themePreference.useMaterialYou),
                            onClick = { showAccentSelector = true }
                        )
                ){
                    drawCircle(color = seedColor)
                }
            }
            if (showAccentSelector){
                AccentSelectorDialog(
                    themePreference = themePreference,
                    onPreferenceChanged = onPreferenceChanged,
                    onDismissRequest = { showAccentSelector = false }
                )
            }
            Spacer(spacerModifier)
            var showSelectorDialog by remember { mutableStateOf(false) }
            val buttonText = when (themePreference.theme) {
                UserPreferences.Theme.LIGHT_MODE, UserPreferences.Theme.UNRECOGNIZED -> "Light"
                UserPreferences.Theme.DARK_MODE -> "Dark"
                UserPreferences.Theme.USE_SYSTEM_MODE -> "System"
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "App theme",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(
                    onClick = { showSelectorDialog = true },
                    content = {
                        Text(
                            text = buttonText,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
            if (showSelectorDialog) {
               ThemeSelectorDialog(
                   themePreference = themePreference,
                   onPreferenceChanged = onPreferenceChanged,
                   onDismissRequest = { showSelectorDialog = false }
               )
            }
        }
    }
}

@Composable
private fun AccentSelectorDialog(
    themePreference: ThemePreference,
    onPreferenceChanged: (ThemePreference) -> Unit,
    onDismissRequest: () -> Unit,
){
    AlertDialog(
        title = {
            Text(
                text = "Accent color",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Canvas(Modifier.size(50.dp).clickable {
                        onPreferenceChanged(themePreference.copy(accent = Accent.Default))
                    }) {
                        drawCircle(Accent.Default.getSeedColor())
                    }
                    Canvas(Modifier.size(50.dp).clickable {
                        onPreferenceChanged(themePreference.copy(accent = Accent.Malibu))
                    }) {
                        drawCircle(Accent.Malibu.getSeedColor())
                    }
                    Canvas(Modifier.size(50.dp).clickable {
                        onPreferenceChanged(themePreference.copy(accent = Accent.Melrose))
                    }) {
                        drawCircle(Accent.Melrose.getSeedColor())
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Canvas(Modifier.size(50.dp).clickable {
                        onPreferenceChanged(themePreference.copy(accent = Accent.Elm))
                    }) {
                        drawCircle(Accent.Elm.getSeedColor())
                    }
                    Canvas(Modifier.size(50.dp).clickable {
                        onPreferenceChanged(themePreference.copy(accent = Accent.Magenta))
                    }) {
                        drawCircle(Accent.Magenta.getSeedColor())
                    }
                    Canvas(Modifier.size(50.dp).clickable {
                        onPreferenceChanged(themePreference.copy(accent = Accent.JacksonsPurple))
                    }) {
                        drawCircle(Accent.JacksonsPurple.getSeedColor())
                    }
                }
            }
        }
    )
}

@Composable
private fun ThemeSelectorDialog(
    themePreference: ThemePreference,
    onPreferenceChanged: (ThemePreference) -> Unit,
    onDismissRequest: () -> Unit,
){
    AlertDialog(
        title = {
            Text(
                text = "App theme",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = (themePreference.theme == UserPreferences.Theme.LIGHT_MODE || themePreference.theme == UserPreferences.Theme.UNRECOGNIZED),
                        onClick = {
                            onPreferenceChanged(themePreference.copy(theme = UserPreferences.Theme.LIGHT_MODE))
                        }
                    )
                    Text(
                        text = "Light mode",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = (themePreference.theme == UserPreferences.Theme.DARK_MODE),
                        onClick = {
                            onPreferenceChanged(themePreference.copy(theme = UserPreferences.Theme.DARK_MODE))
                        }
                    )
                    Text(
                        text = "Dark mode",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = (themePreference.theme == UserPreferences.Theme.USE_SYSTEM_MODE),
                        onClick = {
                            onPreferenceChanged(themePreference.copy(theme = UserPreferences.Theme.USE_SYSTEM_MODE))
                        }
                    )
                    Text(
                        text = "System mode",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    )
}

@Composable
private fun MusicLibrarySettings(
    scanStatus: ScanStatus,
    onScanClicked: () -> Unit,
    onRestoreClicked: () -> Unit,
    onFolderToScanClicked: () -> Unit,
) {
    OutlinedBox(
        label = "Music library",
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 13.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rescan for music",
                    style = MaterialTheme.typography.titleMedium,
                )
                Button(
                    onClick = {
                        if (scanStatus is ScanStatus.ScanNotRunning){
                            onScanClicked()
                        }
                    },
                    content = {
                        when (scanStatus) {
                            is ScanStatus.ScanNotRunning -> {
                                Text(
                                    text = "Scan",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                            is ScanStatus.ScanComplete -> {
                                Text(
                                    text = "Done",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            is ScanStatus.ScanProgress -> {
                                var totalSongs by remember { mutableStateOf(0) }
                                var scanProgress by remember { mutableStateOf(0f) }
                                scanProgress = (scanStatus.parsed.toFloat()) / (scanStatus.total.toFloat())
                                totalSongs = scanStatus.total
                                CircularProgressIndicator(
                                    progress = scanProgress,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                            else -> {}
                        }
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Restore blacklisted songs",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(
                    onClick = onRestoreClicked,
                    content = {
                        Text(
                            text = "Restore",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Folders to scan",
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowRight,
                    contentDescription = "right arrow",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(
                            onClick = onFolderToScanClicked
                        ),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

    }
}

private fun getSystemDetail(): String {
    return "Brand: ${Build.BRAND} \n" +
            "Model: ${Build.MODEL} \n" +
            "SDK: ${Build.VERSION.SDK_INT} \n" +
            "Manufacturer: ${Build.MANUFACTURER} \n" +
            "Version Code: ${Build.VERSION.RELEASE} \n" +
            "App Version Name: ${BuildConfig.VERSION_NAME}"
}

@Composable
private fun ReportBug(
    disabledCrashlytics: Boolean,
    onAutoReportCrashClicked: (Boolean) -> Unit,
){
    val context = LocalContext.current
    OutlinedBox(
        label = "Report",
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 13.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Auto crash reporting",
                    style = MaterialTheme.typography.titleMedium,
                )
                Switch(
                    checked = !disabledCrashlytics,
                    onCheckedChange = onAutoReportCrashClicked
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Report any bugs/crashes",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO)
                        intent.apply {
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("music.zen@outlook.com"))
                            putExtra(Intent.EXTRA_SUBJECT, "Zen Music | Bug Report")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                getSystemDetail() + "\n\n[Describe the bug or crash here]"
                            )
                            data = Uri.parse("mailto:")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    },
                    content = {
                        Text(
                            text = "Report",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MadeBy(
    onWhatsNewClicked: () -> Unit,
) {
    val githubUrl = "https://github.com/pakka-papad"
    val linkedinUrl = "https://www.linkedin.com/in/sumitzbera/"
    val context = LocalContext.current
    val appVersion = stringResource(id = R.string.app_version_name)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val semiTransparentSpanStyle = SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Text(
            text = buildAnnotatedString {
                withStyle(semiTransparentSpanStyle) {
                    append("App version ")
                }
                append(appVersion)
            },
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = "Check what's new!",
            modifier = Modifier
                .alpha(0.5f)
                .clickable(onClick = onWhatsNewClicked),
            style = MaterialTheme.typography.titleSmall.copy(textDecoration = TextDecoration.Underline)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(semiTransparentSpanStyle) {
                    append("Made by ")
                }
                append("Sumit Bera")
            },
            style = MaterialTheme.typography.titleMedium,
        )
        val iconModifier = Modifier
            .size(30.dp)
            .alpha(0.5f)
//            .clip(CircleShape)
//            .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.github_mark),
                contentDescription = "github",
                modifier = iconModifier
                    .combinedClickable(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(githubUrl)
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception){
                                Toast.makeText(context,"Error opening url. Long press to copy.",Toast.LENGTH_SHORT).show()
                            }
                        },
                        onLongClick = {
                            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                            if (clipboardManager == null){
                                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
                            } else {
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("Github url",githubUrl))
                                Toast.makeText(context,"Copied",Toast.LENGTH_SHORT).show()
                            }
                        }
                    ),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                contentScale = ContentScale.Inside,
            )
            Image(
                painter = painterResource(R.drawable.linkedin),
                contentDescription = "linkedin",
                modifier = iconModifier
                    .combinedClickable(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(linkedinUrl)
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception){
                                Toast.makeText(context,"Error opening url. Long press to copy.",Toast.LENGTH_SHORT).show()
                            }
                        },
                        onLongClick = {
                            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                            if (clipboardManager == null){
                                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
                            } else {
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("Linkedin url",linkedinUrl))
                                Toast.makeText(context,"Copied",Toast.LENGTH_SHORT).show()
                            }
                        }
                    ),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                contentScale = ContentScale.Inside,
            )
        }
        Spacer(Modifier.height(36.dp))
    }
}