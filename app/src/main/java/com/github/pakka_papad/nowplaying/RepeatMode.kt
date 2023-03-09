package com.github.pakka_papad.nowplaying

import androidx.annotation.DrawableRes
import com.github.pakka_papad.R

enum class RepeatMode(@DrawableRes val iconResource: Int) {
    NO_REPEAT(R.drawable.baseline_arrow_forward_40),
    REPEAT_ALL(R.drawable.baseline_repeat_40),
    REPEAT_ONE(R.drawable.baseline_repeat_one_40);

    fun next(): RepeatMode {
        return when(this){
            NO_REPEAT -> REPEAT_ALL
            REPEAT_ALL -> REPEAT_ONE
            REPEAT_ONE -> NO_REPEAT
        }
    }
}