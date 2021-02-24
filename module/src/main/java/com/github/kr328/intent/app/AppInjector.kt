package com.github.kr328.intent.app

import android.app.IActivityManager
import android.app.IActivityTaskManager
import android.os.IBinder
import android.os.Process
import com.github.kr328.intent.compat.ServiceProxy
import com.github.kr328.intent.compat.asActivityManager
import com.github.kr328.intent.compat.asActivityTaskManager
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
                        ActivityHijack(
                            IActivityManager::class.java,
                            service.asActivityManager(),
                            InterceptorManager::intercept
                        ).hijacked
                    )
                }
                "activity_task" -> {
                    HijackIBinder(
                        service,
                        ActivityHijack(
                            IActivityTaskManager::class.java,
                            service.asActivityTaskManager(),
                            InterceptorManager::intercept
                        ).hijacked
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