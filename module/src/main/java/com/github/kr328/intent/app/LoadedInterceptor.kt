package com.github.kr328.intent.app

import android.content.Intent
import com.github.kr328.intent.shared.TLog
import java.lang.reflect.Method

class LoadedInterceptor(
    private val obj: Any,
    private val intercept: Method,
    private val className: String,
) {
    operator fun invoke(intent: Intent): Intent {
        return try {
            (intercept.invoke(obj, intent) as Intent?) ?: intent
        } catch (e: Exception) {
            TLog.w("$className: ${e.message}", e)

            intent
        }
    }
}
