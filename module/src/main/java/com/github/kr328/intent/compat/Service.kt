package com.github.kr328.intent.compat

import android.content.pm.IPackageManager
import android.os.ServiceManager
import com.github.kr328.intent.remote.IIntentInterceptor

object SystemService {
    val packages: IPackageManager by lazy {
        requireNonNull(IPackageManager.Stub.asInterface(ServiceManager.getService("package")))
    }

    val intercept: IIntentInterceptor
        get() {
            TODO()
        }

    private fun <T> requireNonNull(v: T?): T {
        if (v == null) {
            throw IllegalStateException("invalid system status")
        }

        return v
    }
}