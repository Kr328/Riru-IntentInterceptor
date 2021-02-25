@file:Suppress("UNCHECKED_CAST")

package com.github.kr328.intent.compat

import android.app.ActivityManagerNative
import android.app.IActivityManager
import android.app.IActivityTaskManager
import android.content.Intent
import android.os.Build
import android.os.IBinder
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class ActivityHijack<T>(
    clazz: Class<T>,
    private val original: T,
    private val interceptor: (Intent) -> Intent,
) : InvocationHandler {
    val hijacked: T = Proxy.newProxyInstance(
        clazz.classLoader,
        arrayOf(clazz),
        this
    ) as T

    override fun invoke(proxy: Any?, method: Method, args: Array<Any>?): Any? {
        args ?: return method.invoke(original)

        if (method.name.startsWith("startActivity")) {
            if (args[2] is Intent) {
                args[2] = interceptor(args[2] as Intent)
            } else if (args[3] is Intent) {
                args[3] = interceptor(args[3] as Intent)
            }
        }

        return method.invoke(original, *args)
    }
}

inline fun IBinder.createActivityHijack(crossinline interceptor: (Intent) -> Intent): IActivityManager {
    return if (Build.VERSION.SDK_INT >= 26) {
        ActivityHijack(
            IActivityManager::class.java,
            IActivityManager.Stub.asInterface(this),
            { interceptor(it) }
        ).hijacked
    } else {
        ActivityHijack(
            IActivityManager::class.java,
            ActivityManagerNative.asInterface(this),
            { interceptor(it) }
        ).hijacked
    }
}

inline fun IBinder.createActivityTaskHijack(crossinline interceptor: (Intent) -> Intent): IActivityTaskManager {
    return ActivityHijack(
        IActivityTaskManager::class.java,
        IActivityTaskManager.Stub.asInterface(this),
        { interceptor(it) }
    ).hijacked
}

