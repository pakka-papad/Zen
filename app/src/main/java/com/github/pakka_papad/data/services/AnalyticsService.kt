package com.github.pakka_papad.data.services

import com.github.pakka_papad.data.analytics.PlayHistoryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface AnalyticsService {
    fun logSongPlay(songLocation: String, playDuration: Long)
}

class AnalyticsServiceImpl(
    private val playHistoryDao: PlayHistoryDao,
    private val scope: CoroutineScope,
): AnalyticsService {
    override fun logSongPlay(songLocation: String, playDuration: Long) {
        scope.launch {
            playHistoryDao.addRecord(songLocation, playDuration)
        }
    }
}