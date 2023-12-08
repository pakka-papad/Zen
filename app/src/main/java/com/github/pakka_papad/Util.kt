package com.github.pakka_papad

import androidx.media3.common.PlaybackParameters
import com.github.pakka_papad.data.UserPreferences.PlaybackParams
import com.github.pakka_papad.data.copy
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

fun Float.round(decimals: Int): Float {
    var multiplier = 1f
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

fun Float.toMBfromB(): String{
    val mb = this/(1024*1024)
    return "${mb.round(2)} MB"
}

val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

fun Long.formatToDate(): String {
    if (this < 0) return ""
    val calender = Calendar.getInstance().apply {
        timeInMillis = this@formatToDate
    }
    return dateFormat.format(calender.time)
}

fun Long.toMinutesAndSeconds(): String {
    val totalSeconds = this/1000
    val minutes = totalSeconds/60
    val seconds = totalSeconds%60
    return if (minutes == 0L) "$seconds secs"
    else if (seconds == 0L) "$minutes mins"
    else "$minutes mins $seconds secs"
}

fun Long.toMS(): String {
    val totalSeconds = this/1000
    val minutes = totalSeconds/60
    val seconds = totalSeconds%60
    return "${if(minutes < 10) "0" else ""}${minutes}:${if (seconds < 10) "0"  else ""}${seconds}"
}

fun PlaybackParams.toCorrectedParams(): PlaybackParams {
    val correctedSpeed = if (this.playbackSpeed < 1 || this.playbackSpeed > 200) 100 else this.playbackSpeed
    val correctedPitch = if (this.playbackPitch < 1 || this.playbackPitch > 200) 100 else this.playbackPitch
    return PlaybackParams.getDefaultInstance().copy {
        playbackSpeed = correctedSpeed
        playbackPitch = correctedPitch
    }
}

/**
 * Call on corrected PlaybackParams
 */
fun PlaybackParams.toExoPlayerPlaybackParameters(): PlaybackParameters {
    return PlaybackParameters(
        this.playbackSpeed.toFloat()/100,
        this.playbackPitch.toFloat()/100
    )
}