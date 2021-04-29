package com.github.kr328.intent.app

import android.content.Intent
import com.github.kr328.intent.compat.currentApplication
import com.github.kr328.intent.shared.TLog
import com.github.kr328.intent.util.waitActivityThreadAvailable
import com.github.kr328.intent.util.waitMainThreadAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object InterceptorManager {
    private var interceptors: Interceptors? = null

    fun intercept(intent: Intent): Intent {
        return interceptors?.intercept(intent) ?: intent
    }

    private suspend fun run() {
        waitActivityThreadAvailable()
        waitMainThreadAvailable()

        interceptors = Interceptors(currentApplication()!!)
    }

    init {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                run()
            } catch (t: Throwable) {
                TLog.e("InterceptorManager crashed: $t`", t)
            }
        }
    }
}