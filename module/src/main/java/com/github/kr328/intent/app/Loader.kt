package com.github.kr328.intent.app

import android.app.Application
import android.content.Intent
import android.os.SharedMemory
import com.github.kr328.intent.remote.Injection
import dalvik.system.InMemoryDexClassLoader

fun Injection.load(
    application: Application,
): LoadedInterceptor {
    val loader = InMemoryDexClassLoader(
        classes.map(SharedMemory::mapReadOnly).toTypedArray(),
        application.classLoader
    )

    return with(loader.loadClass(interceptor)) {
        val constructor = getConstructor(Application::class.java)
        val intercept = getMethod("intercept", Intent::class.java)

        require(intercept.returnType == Intent::class.java) {
            throw NoSuchMethodException("$intercept invalid")
        }

        LoadedInterceptor(constructor.newInstance(application), intercept, interceptor)
    }
}