package com.github.kr328.intent.compat

import android.os.IBinder
import android.os.IServiceManager
import android.os.ServiceManager
import com.github.kr328.intent.util.Field
import com.github.kr328.intent.util.useAs
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

interface ServiceManagerDefinition {
    fun getIServiceManager(): IServiceManager

    var sIServiceManager: IServiceManager
        @Field("sServiceManager") set
}

abstract class ServiceProxy : InvocationHandler {
    private var original: IServiceManager? = null

    @Synchronized
    fun install() {
        if (original != null) return

        ServiceManager::class.java.useAs(ServiceManagerDefinition::class.java, true).apply {
            original = getIServiceManager()

            sIServiceManager = Proxy.newProxyInstance(
                ServiceProxy::class.java.classLoader,
                arrayOf<Class<*>>(IServiceManager::class.java),
                this@ServiceProxy
            ) as IServiceManager
        }
    }

    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        when (method.name) {
            "addService" -> {
                if (args.size < 2 || args[0] !is String || args[1] !is IBinder) {
                    return method.invoke(original, *args)
                }

                val name = args[0] as String
                val service = args[1] as IBinder

                args[1] = onAddService(name, service)

                return method.invoke(original, *args)
            }
            "getService" -> {
                if (args.isEmpty() || args[0] !is String) return method.invoke(original, *args)

                val n = args[0] as String
                val s = method.invoke(original, *args)

                return if (s !is IBinder) s else onGetService(n, s)
            }
        }
        return method.invoke(original, *args)
    }

    protected open fun onAddService(name: String, service: IBinder): IBinder {
        return service
    }

    protected open fun onGetService(name: String, service: IBinder): IBinder {
        return service
    }
}