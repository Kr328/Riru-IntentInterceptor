package com.github.kr328.intent.util

import android.os.Handler
import android.os.Looper
import com.github.kr328.intent.compat.currentActivityThread
import com.github.kr328.intent.shared.debug
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

suspend fun waitActivityThreadAvailable() {
    while (currentActivityThread() == null) {
        delay(TimeUnit.SECONDS.toMillis(1))

        "ActivityThread unavailable, wait 1s".debug()
    }

    "ActivityThread available".debug()
}

suspend fun waitMainThreadAvailable() {
    suspendCancellableCoroutine<Unit> { ctx ->
        val handler = Handler(Looper.getMainLooper())

        ctx.invokeOnCancellation {
            handler.removeMessages(0)
        }

        handler.post {
            ctx.resume(Unit)
        }
    }

    "MainThread available".debug()
}
