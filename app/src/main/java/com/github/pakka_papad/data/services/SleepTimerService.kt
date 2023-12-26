package com.github.pakka_papad.data.services

import android.app.PendingIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

interface SleepTimerService {
    val isRunning: StateFlow<Boolean>
    val timeLeft: StateFlow<Int>

    fun cancel()
    fun begin(duration: Int)
}

class SleepTimerServiceImpl(
    private val scope: CoroutineScope,
    private val closeIntent: PendingIntent,
): SleepTimerService {

    private var timerJob: Job? = null

    @VisibleForTesting
    internal val _isRunning = MutableStateFlow(false)
    override val isRunning: StateFlow<Boolean>
        = _isRunning.asStateFlow()

    @VisibleForTesting
    internal val _timeLeft = MutableStateFlow(0)
    override val timeLeft: StateFlow<Int>
        = _timeLeft.asStateFlow()

    override fun cancel() {
        timerJob?.cancel()
        timerJob = null
        _isRunning.update { false }
    }

    override fun begin(duration: Int) {
        if (duration == 0) return
        if (timerJob != null) return
        timerJob = scope.launch {
            _isRunning.update { true }
            var left = duration
            _timeLeft.update { left }
            while (left >= 0){
                delay(1000L)
                left--
                _timeLeft.update { left }
            }
            closeIntent.send()
            _isRunning.update { false }
        }
    }
}