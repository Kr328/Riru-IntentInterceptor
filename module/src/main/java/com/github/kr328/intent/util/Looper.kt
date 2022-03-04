package com.github.kr328.intent.util

import android.os.HandlerThread
import android.os.Looper

private val thread = HandlerThread("intent_interceptor").apply {
    start()
}

val moduleLooper: Looper
    get() = thread.looper
