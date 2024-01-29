package com.github.pakka_papad.data

import androidx.datastore.core.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueStateProvider @Inject constructor(
    private val queueState: DataStore<QueueState>,
    private val coroutineScope: CoroutineScope,
) {
    val state: Flow<QueueState>
        get() = queueState.data

    fun saveState(queue: List<String>, startIndex: Int, startPosition: Long) {
        coroutineScope.launch {
            queueState.updateData {
                it.copy {
                    this.locations.apply {
                        clear()
                        addAll(queue)
                    }
                    this.startIndex = startIndex
                    this.startPositionMs = startPosition
                }
            }
        }
    }
}