package com.github.kr328.intent.compat

import android.content.pm.IPackageManager
import android.os.Parcel
import android.os.ServiceManager
import com.github.kr328.intent.IIntentInterceptorService
import com.github.kr328.intent.system.SystemInjector

object SystemService {
    val packages: IPackageManager by lazy {
        requireNonNull(IPackageManager.Stub.asInterface(ServiceManager.getService("package")))
    }

    val intercept: IIntentInterceptorService by lazy {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()

        try {
            val result = packages.asBinder()
                .transact(SystemInjector.Service.TRANSACTION_CODE, data, reply, 0)

            requireNonNull(if (result) Unit else null)

            requireNonNull(IIntentInterceptorService.Stub.asInterface(reply.readStrongBinder()))
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    private fun <T> requireNonNull(v: T?): T {
        if (v == null) {
            throw IllegalStateException("invalid system status")
        }

        return v
    }
}