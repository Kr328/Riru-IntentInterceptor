package com.github.kr328.intent.compat

import android.app.IActivityManager
import android.content.pm.IPackageManager
import android.os.IUserManager
import android.os.Parcel
import android.os.ServiceManager
import android.permission.IPermissionManager
import com.github.kr328.intent.IIntentInterceptorService
import com.github.kr328.intent.system.SystemInjector

object SystemService {
    val activity: IActivityManager by lazy {
        requireNonNull(IActivityManager.Stub.asInterface(ServiceManager.getService("activity")))
    }

    val user: IUserManager by lazy {
        requireNonNull(IUserManager.Stub.asInterface(ServiceManager.getService("user")))
    }

    val packages: IPackageManager by lazy {
        requireNonNull(IPackageManager.Stub.asInterface(ServiceManager.getService("package")))
    }

    val permission: IPermissionManager by lazy {
        requireNonNull(IPermissionManager.Stub.asInterface(ServiceManager.getService("permission")))
    }

    val intercept: IIntentInterceptorService by lazy {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()

        try {
            val result = activity.asBinder()
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