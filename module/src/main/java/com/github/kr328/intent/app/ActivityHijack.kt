package com.github.kr328.intent.app

import android.content.Intent
import android.os.IInterface
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class ActivityHijack<T>(
    clazz: Class<T>,
    private val original: T,
    private val interceptor: (Intent) -> Intent,
) : InvocationHandler {
    val hijacked: IInterface = Proxy.newProxyInstance(
        clazz.classLoader,
        arrayOf(clazz),
        this
    ) as IInterface

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
