package com.github.pakka_papad.util

import android.content.Context
import androidx.annotation.StringRes
import javax.inject.Inject

interface MessageStore {
    fun getString(@StringRes id: Int): String
    fun getString(@StringRes id: Int, vararg formatArgs: Any): String
}

class MessageStoreImpl @Inject constructor(
    private val context: Context
) : MessageStore {
    override fun getString(id: Int): String {
        return context.getString(id)
    }

    override fun getString(id: Int, vararg formatArgs: Any): String {
        return context.getString(id, *formatArgs)
    }
}