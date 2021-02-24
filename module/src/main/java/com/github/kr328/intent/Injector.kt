package com.github.kr328.intent

import androidx.annotation.Keep
import com.github.kr328.intent.app.AppInjector
import com.github.kr328.intent.shared.TLog
import com.github.kr328.intent.system.SystemInjector

@Keep
fun main(args: Array<String>) {
    if (args.isEmpty()) return

    try {
        when (args[0]) {
            "system" -> SystemInjector().inject()
            "app" -> AppInjector().inject()
        }
    } catch (e: Throwable) {
        TLog.e("inject ${args[0]}: $e", e)
    }
}
