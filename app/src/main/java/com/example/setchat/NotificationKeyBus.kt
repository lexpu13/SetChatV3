package com.example.setchat

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificationKeyBus {
    private val _keys = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val keys = _keys.asSharedFlow()

    fun emitFromUnicode(unicode: Int) {
        if (unicode == 0) return
        val char = unicode.toChar()
        if (!char.isLetterOrDigit()) return
        _keys.tryEmit(char.lowercaseChar().toString())
    }
}
