package com.github.kr328.intent.util

import android.app.ActivityThread
import android.os.Handler
import android.os.Looper
import com.github.kr328.intent.shared.TLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun waitActivityThreadAvailable() {
    while (true) {
        if (ActivityThread.currentActivityThread() != null)
            break

        delay(1000)

        TLog.i("ActivityThread unavailable, wait 1s")
    }
}

suspend fun waitMainThreadAvailable() {
    return suspendCancellableCoroutine { ctx ->
        val handler = Handler(Looper.getMainLooper())

        ctx.invokeOnCancellation {
            handler.removeMessages(0)
        }

        handler.post {
            ctx.resume(Unit)
        }
    }
}