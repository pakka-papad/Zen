package com.github.pakka_papad.widgets

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.ImageProvider as UriImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.github.pakka_papad.Constants
import com.github.pakka_papad.R
import com.github.pakka_papad.player.ZenBroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

val imageUriKey = stringPreferencesKey("image_uri")
val albumKey = stringPreferencesKey("album")
val titleKey = stringPreferencesKey("title")
val artistKey = stringPreferencesKey("artist")
val isPlayingKey = booleanPreferencesKey("is_playing")

object MusicControlWidget : GlanceAppWidget() {

    private lateinit var pendingPausePlayIntent: PendingIntent
    private lateinit var pendingPreviousIntent: PendingIntent
    private lateinit var pendingNextIntent: PendingIntent

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        pendingPausePlayIntent = PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.PAUSE_PLAY_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_PAUSE_PLAY
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
        pendingPreviousIntent = PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.PREVIOUS_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_PREVIOUS
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
        pendingNextIntent = PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.NEXT_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_NEXT
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val imageUri = currentState(imageUriKey) ?: ""
        val title = currentState(titleKey) ?: ""
        val album = currentState(albumKey) ?: ""
        val artist = currentState(artistKey) ?: ""
        val isPlaying = currentState(isPlayingKey) ?: false
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .then(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                        GlanceModifier
                            .cornerRadius(28.dp)
                            .background(GlanceTheme.colors.secondaryContainer)
                            .padding(12.dp)
                    } else {
                        GlanceModifier
                            .background(ImageProvider(R.drawable.music_widget_background))
                    }
                ),
        ) {
            Image(
                provider = UriImageProvider(imageUri.toUri()),
                contentDescription = null,
                modifier = GlanceModifier
                    .wrapContentWidth()
                    .fillMaxHeight()
                    .then(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            GlanceModifier
                                .cornerRadius(16.dp)
                        } else {
                            GlanceModifier
                        }
                    ) ,
                contentScale = ContentScale.Fit
            )
            Spacer(GlanceModifier.width(12.dp))
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSecondaryContainer,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                )
                Text(
                    text = artist,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSecondaryContainer,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    maxLines = 1,
                )
                Row(
                    modifier = GlanceModifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_baseline_skip_previous_40),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondaryContainer),
                        modifier = GlanceModifier
                            .clickable {
                                pendingPreviousIntent.send()
                            }
                    )
                    Spacer(GlanceModifier.width(6.dp))
                    Image(
                        provider = ImageProvider(
                            if (isPlaying) {
                                R.drawable.ic_baseline_pause_40
                            } else {
                                R.drawable.ic_baseline_play_arrow_40
                            }
                        ),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondaryContainer),
                        modifier = GlanceModifier
                            .clickable {
                                pendingPausePlayIntent.send()
                            }
                    )
                    Spacer(GlanceModifier.width(6.dp))
                    Image(
                        provider = ImageProvider(R.drawable.ic_baseline_skip_next_40),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondaryContainer),
                        modifier = GlanceModifier
                            .clickable {
                                pendingNextIntent.send()
                            }
                    )
                }
            }
        }
    }
}

class MusicControlWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = MusicControlWidget

    private val scope = CoroutineScope(SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.extras?.getString(WidgetBroadcast.WIDGET_BROADCAST) ?: return
        scope.launch {
            GlanceAppWidgetManager(context)
                .getGlanceIds(MusicControlWidget.javaClass)
                .forEach { glanceId ->
                    when (action) {
                        WidgetBroadcast.SONG_CHANGED -> {
                            updateAppWidgetState(context, glanceId) { prefs ->
                                prefs[imageUriKey] = intent.getStringExtra("imageUri") ?: ""
                                prefs[albumKey] = intent.getStringExtra("album") ?: ""
                                prefs[titleKey] = intent.getStringExtra("title") ?: ""
                                prefs[artistKey] = intent.getStringExtra("artist") ?: ""
                            }
                        }

                        WidgetBroadcast.IS_PLAYING_CHANGED -> {
                            updateAppWidgetState(context, glanceId) { prefs ->
                                prefs[isPlayingKey] = intent.getBooleanExtra("isPlaying", false)
                            }
                        }

                        else -> {

                        }
                    }
                    glanceAppWidget.update(context, glanceId)
                }

        }
    }
}