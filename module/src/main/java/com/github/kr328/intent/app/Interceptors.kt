package com.github.kr328.intent.app

import android.app.Application
import android.content.Intent
import com.github.kr328.intent.compat.SystemService
import com.github.kr328.intent.shared.TLog

class Interceptors(private val application: Application) {
    private val interceptors: List<LoadedInterceptor> =
        SystemService.intercept.open(application.packageName).mapNotNull {
            TLog.i("Loading ${it.interceptor}")

            try {
                it.load(application)
            } catch (t: Throwable) {
                TLog.w("Load ${it.interceptor}: ${t.message}", t)

                null
            }
        }

    fun intercept(intent: Intent): Intent {
        return try {
            var result = intent

            interceptors.forEach {
                result = it(result)
            }

            result
        } catch (t: Throwable) {
            TLog.w("${application.packageName} intercept: ${t.message}", t)

            intent
        }
    }
}