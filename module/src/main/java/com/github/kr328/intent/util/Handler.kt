package com.github.kr328.intent.util

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.github.kr328.intent.shared.TLog

fun startHandlerThread(name: String): HandlerThread {
    return HandlerThread(name).apply {
        setUncaughtExceptionHandler { _, e ->
            TLog.e("Inject thread crashed: ${e.message}", e)
        }

        start()
    }
}

open class DaemonHandler(private val name: String) : Handler(startHandlerThread(name).looper) {
    override fun dispatchMessage(msg: Message) {
        try {
            super.dispatchMessage(msg)
        } catch (e: Throwable) {
            TLog.e("$name crashed: ${e.message}", e)

            looper.quitSafely()
        }
    }

    fun finalize() {
        looper.quitSafely()
    }
}