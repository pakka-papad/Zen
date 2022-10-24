package tech.zemn.mobile

import android.icu.text.SimpleDateFormat
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

val dateFormat = SimpleDateFormat("dd:MM:yyyy")

fun Long.formatToDate(): String {
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