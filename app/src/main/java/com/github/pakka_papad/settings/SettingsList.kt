package com.github.pakka_papad.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.github.pakka_papad.Screens
import com.github.pakka_papad.data.UserPreferences
import com.github.pakka_papad.data.UserPreferences.Accent
import com.github.pakka_papad.data.music.ScanStatus
import com.github.pakka_papad.nowplaying.DraggableItem
import com.github.pakka_papad.nowplaying.dragContainer
import com.github.pakka_papad.nowplaying.rememberDragDropState
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
    onRestoreFoldersClicked: () -> Unit,
    tabsSelection: List<Pair<Screens,Boolean>>,
    onTabsSelectChange: (Screens, Boolean) -> Unit,
    onTabsOrderChanged: (fromIdx: Int, toIdx: Int) -> Unit,
    onTabsOrderConfirmed: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            GroupTitle(title = "Look and feel")
        }
        item {
            LookAndFeelSettings(
                themePreference = themePreference,
                onPreferenceChanged = onThemePreferenceChanged,
                tabsSelection = tabsSelection,
                onTabsSelectChange = onTabsSelectChange,
                onTabsOrderChanged = onTabsOrderChanged,
                onTabsOrderConfirmed = onTabsOrderConfirmed,
            )
        }
        item {
            GroupTitle(title = "Music library")
        }
        item {
            MusicLibrarySettings(
                scanStatus = scanStatus,
                onScanClicked = onScanClicked,
                onRestoreClicked = onRestoreClicked,
                onRestoreFoldersClicked = onRestoreFoldersClicked
            )
        }
        item {
            GroupTitle(title = "Report bug")
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
fun GroupTitle(
    title: String,
){
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .padding(start = 8.dp, top = 8.dp)
    )
}

@Composable
private fun LookAndFeelSettings(
    themePreference: ThemePreference,
    onPreferenceChanged: (ThemePreference) -> Unit,
    tabsSelection: List<Pair<Screens, Boolean>>,
    onTabsSelectChange: (Screens, Boolean) -> Unit,
    onTabsOrderChanged: (fromIdx: Int, toIdx: Int) -> Unit,
    onTabsOrderConfirmed: () -> Unit,
) {
    val seedColor by remember(themePreference.accent){
        derivedStateOf {
            themePreference.accent.getSeedColor()
        }
    }
    var showAccentSelector by remember { mutableStateOf(false) }
    var showSelectorDialog by remember { mutableStateOf(false) }
    val isSystemInDarkMode = isSystemInDarkTheme()
    val icon by remember(themePreference.theme) { derivedStateOf {
        when (themePreference.theme) {
            UserPreferences.Theme.LIGHT_MODE, UserPreferences.Theme.UNRECOGNIZED -> R.drawable.baseline_light_mode_40
            UserPreferences.Theme.DARK_MODE -> R.drawable.baseline_dark_mode_40
            UserPreferences.Theme.USE_SYSTEM_MODE -> {
                if (isSystemInDarkMode){
                    R.drawable.baseline_dark_mode_40
                } else {
                    R.drawable.baseline_light_mode_40
                }
            }
        }
    } }
    var showRearrangeTabsDialog by remember{ mutableStateOf(false) }

    Column(
        modifier = Modifier.group()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Setting(
                title = "Material You",
                icon = R.drawable.baseline_palette_40,
                description = "Use a theme generated from your device wallpaper",
                isChecked = themePreference.useMaterialYou,
                onCheckedChanged = {
                    onPreferenceChanged(themePreference.copy(useMaterialYou = it))
                },
            )
        }
        AccentSetting(
            onClick = {
                if (!themePreference.useMaterialYou) {
                    showAccentSelector = true
                }
            },
            seedColor = seedColor,
            modifier = Modifier
                .alpha(if (themePreference.useMaterialYou) 0.5f else 1f),
        )
        Setting(
            title = "Theme mode",
            icon = icon,
            onClick = { showSelectorDialog = true },
            description = "Choose a theme mode"
        )
        Setting(
            title = "Tabs arrangement",
            icon = R.drawable.ic_baseline_library_music_40,
            description = "Select and reorder the tabs shown",
            onClick = { showRearrangeTabsDialog = true }
        )
    }
    if (showAccentSelector){
        AccentSelectorDialog(
            themePreference = themePreference,
            onPreferenceChanged = onPreferenceChanged,
            onDismissRequest = { showAccentSelector = false }
        )
    }
    if (showSelectorDialog) {
        ThemeSelectorDialog(
            themePreference = themePreference,
            onPreferenceChanged = onPreferenceChanged,
            onDismissRequest = { showSelectorDialog = false }
        )
    }
    if (showRearrangeTabsDialog){
        RearrangeTabsDialog(
            tabsSelection = tabsSelection,
            onDismissRequest = { showRearrangeTabsDialog = false },
            onSelectChange = onTabsSelectChange,
            onTabsOrderChanged = onTabsOrderChanged,
            onTabsOrderConfirmed = {
                showRearrangeTabsDialog = false
                onTabsOrderConfirmed()
            }
        )
    }
}

@Composable
private fun AccentSelectorDialog(
    themePreference: ThemePreference,
    onPreferenceChanged: (ThemePreference) -> Unit,
    onDismissRequest: () -> Unit,
){
    val sizeModifier = Modifier.size(50.dp)
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
                    Canvas(
                        modifier = sizeModifier
                            .clickable {
                                onPreferenceChanged(themePreference.copy(accent = Accent.Default))
                            }
                    ) {
                        drawCircle(Accent.Default.getSeedColor())
                    }
                    Canvas(
                        modifier = sizeModifier
                            .clickable {
                                onPreferenceChanged(themePreference.copy(accent = Accent.Malibu))
                            }
                    ) {
                        drawCircle(Accent.Malibu.getSeedColor())
                    }
                    Canvas(
                        modifier = sizeModifier
                            .clickable {
                                onPreferenceChanged(themePreference.copy(accent = Accent.Melrose))
                            }
                    ) {
                        drawCircle(Accent.Melrose.getSeedColor())
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Canvas(
                        modifier = sizeModifier
                            .clickable {
                                onPreferenceChanged(themePreference.copy(accent = Accent.Elm))
                            }
                    ) {
                        drawCircle(Accent.Elm.getSeedColor())
                    }
                    Canvas(
                        modifier = sizeModifier
                            .clickable {
                                onPreferenceChanged(themePreference.copy(accent = Accent.Magenta))
                            }
                    ) {
                        drawCircle(Accent.Magenta.getSeedColor())
                    }
                    Canvas(
                        modifier = sizeModifier
                            .clickable {
                                onPreferenceChanged(themePreference.copy(accent = Accent.JacksonsPurple))
                            }
                    ) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RearrangeTabsDialog(
    tabsSelection: List<Pair<Screens,Boolean>>,
    onDismissRequest: () -> Unit,
    onSelectChange: (Screens, Boolean) -> Unit,
    onTabsOrderChanged: (fromIdx: Int, toIdx: Int) -> Unit,
    onTabsOrderConfirmed: () -> Unit,
){
    AlertDialog(
        title = {
            Text(
                text = "App tabs",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onTabsOrderConfirmed
            ) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        text = {
            val listState = rememberLazyListState()
            val dragDropState = rememberDragDropState(
                lazyListState = listState,
                onMove = onTabsOrderChanged,
            )
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .dragContainer(dragDropState)
            ){
                itemsIndexed(
                    items = tabsSelection,
                    key = { index, screenChoice -> screenChoice.first.ordinal }
                ){ index, screenChoice ->
                    DraggableItem(dragDropState, index) {
                        SelectableMovableScreen(
                            screen = screenChoice.first,
                            isSelected = screenChoice.second,
                            onSelectChange = { isSelected -> onSelectChange(screenChoice.first, isSelected) },
                        )
                    }
                }
            }
        },

    )
}


@Composable
private fun SelectableMovableScreen(
    screen: Screens,
    isSelected: Boolean,
    onSelectChange: (Boolean) -> Unit,
){
    val spaceModifier = Modifier.width(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectChange,
        )
        Icon(
            painter = painterResource(id = screen.filledIcon),
            contentDescription = "${screen.name} screen icon",
            modifier = Modifier.size(35.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Spacer(spaceModifier)
        Text(
            text = screen.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.weight(1f)
        )
        Spacer(spaceModifier)
        Icon(
            painter = painterResource(id = R.drawable.baseline_drag_indicator_40),
            contentDescription = "move icon",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun MusicLibrarySettings(
    scanStatus: ScanStatus,
    onScanClicked: () -> Unit,
    onRestoreClicked: () -> Unit,
    onRestoreFoldersClicked: () -> Unit,
) {
    var progress by remember { mutableStateOf(0f) }
    LaunchedEffect(key1 = scanStatus){
        if (scanStatus is ScanStatus.ScanComplete) {
            progress = 1f
        } else if (scanStatus is ScanStatus.ScanProgress){
            progress = if (scanStatus.total == 0){
                1f
            } else {
                scanStatus.parsed.toFloat()/scanStatus.total.toFloat()
            }
        }
    }
    Column(
        modifier = Modifier
            .group()
            .animateContentSize(),
    ) {
        Setting(
            title = "Rescan for music",
            icon = Icons.Outlined.Search,
            description = "Search for all the songs on this device and update the library",
            onClick = {
                if (scanStatus is ScanStatus.ScanNotRunning){
                    onScanClicked()
                }
            },
        )
        if (scanStatus is ScanStatus.ScanProgress){
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = progress
            )
        } else if (scanStatus is ScanStatus.ScanComplete){
            Text(
                text = "Done",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Setting(
            title = "Restore blacklisted songs",
            icon = R.drawable.baseline_settings_backup_restore_40,
            onClick = onRestoreClicked
        )
        Setting(
            title = "Restore blacklisted folders",
            icon = R.drawable.baseline_settings_backup_restore_40,
            onClick = onRestoreFoldersClicked
        )
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
    Column(
        modifier = Modifier.group()
    ) {
        Setting(
            title = "Auto crash reporting",
            icon = R.drawable.baseline_send_40,
            description = "Enable this to automatically send crash reports to the developer",
            isChecked = !disabledCrashlytics,
            onCheckedChanged = onAutoReportCrashClicked,
        )
        Setting(
            title = "Report any bugs/crashes",
            icon = R.drawable.baseline_bug_report_40,
            description = "Manually report any bugs or crashes you faced",
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
            }
        )
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

private fun Modifier.group() = composed {
    this
        .fillMaxWidth()
        .padding(8.dp)
        .clip(MaterialTheme.shapes.large)
        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
}


@Composable
private fun Setting(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    isChecked: Boolean? = null,
    onCheckedChanged: ((Boolean) -> Unit)? = null,
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(6.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ){
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            description?.let {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }
        isChecked?.let {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChanged,
            )
        }
    }
}


@Composable
private fun Setting(
    title: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    isChecked: Boolean? = null,
    onCheckedChanged: ((Boolean) -> Unit)? = null,
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(6.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ){
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            description?.let {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }
        isChecked?.let {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChanged,
            )
        }
    }
}

@Composable
private fun AccentSetting(
    onClick: () -> Unit,
    seedColor: Color,
    modifier: Modifier = Modifier,
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_colorize_40),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(6.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ){
            Text(
                text = "Accent color",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Choose a theme color",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
        Box(
            modifier = Modifier
                .height(32.dp)
                .width(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = seedColor)
        )
    }
}