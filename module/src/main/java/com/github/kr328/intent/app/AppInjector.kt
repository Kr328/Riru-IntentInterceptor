package com.github.kr328.intent.app

import android.os.IBinder
import android.os.Process
import com.github.kr328.intent.compat.ServiceProxy
import com.github.kr328.intent.compat.createActivityHijack
import com.github.kr328.intent.compat.createActivityTaskHijack
import com.github.kr328.intent.shared.TLog
import com.github.kr328.intent.util.HijackIBinder

class AppInjector : ServiceProxy() {
    fun inject() {
        TLog.i("Application pid = ${Process.myPid()} uid = ${Process.myUid()}")

        install()

        InterceptorManager //.init()
    }

    override fun onGetService(name: String, service: IBinder): IBinder {
        return try {
            when (name) {
                "activity" -> {
                    HijackIBinder(
                        service,
                        service.createActivityHijack(InterceptorManager::intercept)
                    )
                }
                "activity_task" -> {
                    HijackIBinder(
                        service,
                        service.createActivityTaskHijack(InterceptorManager::intercept)
                    )
                }
                else -> {
                    service
                }
            }
        } catch (t: Throwable) {
            TLog.e("Proxy $name failure", t)

            service
        }
    }
}