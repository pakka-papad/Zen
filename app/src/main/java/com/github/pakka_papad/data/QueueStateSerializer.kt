package com.github.pakka_papad.data

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

object QueueStateSerializer: Serializer<QueueState> {
    override val defaultValue: QueueState
        get() = QueueState.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): QueueState =
        try {
            QueueState.parseFrom(input)
        } catch (_: Exception) {
            defaultValue
        }

    override suspend fun writeTo(t: QueueState, output: OutputStream) = t.writeTo(output)
}