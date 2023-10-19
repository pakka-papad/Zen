package com.github.pakka_papad.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

class MusicControlWidget: GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent(){
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.secondaryContainer)
                .padding(12.dp)
        ) {
            Text(
                text = "Hello World",
                style = TextStyle(color = GlanceTheme.colors.onSecondaryContainer)
            )
        }
    }
}

class MusicControlWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = MusicControlWidget()
}