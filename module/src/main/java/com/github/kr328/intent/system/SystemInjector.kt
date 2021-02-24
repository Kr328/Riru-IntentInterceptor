package com.github.kr328.intent.system

import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.os.Process
import com.github.kr328.intent.compat.ServiceProxy
import com.github.kr328.intent.shared.TLog
import com.github.kr328.intent.util.HijackBinder

class SystemInjector : ServiceProxy() {
    object Service : Binder() {
        const val TRANSACTION_CODE =
            ('_'.toInt() shl 24) or ('I'.toInt() shl 16) or ('I'.toInt() shl 8) or 'S'.toInt()

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            when (code) {
                TRANSACTION_CODE -> {
                    val uid = getCallingUid()

                    TLog.i("Obtain interceptor service: $uid")

                    if (!IntentInterceptorManager.shouldSkipUid(uid)) {
                        reply?.writeStrongBinder(IntentInterceptorManager.service)

                        return true
                    } else {
                        TLog.i("Reject $uid")
                    }
                }
            }

            return false
        }
    }

    fun inject() {
        TLog.i("SystemServer pid = " + Process.myPid() + " uid = " + Process.myUid())

        install()

        IntentInterceptorManager //.init()
    }

    override fun onGetService(name: String, service: IBinder): IBinder {
        return when (name) {
            "package" -> {
                if (Thread.currentThread().stackTrace.any { it.methodName == "getCommonServicesLocked" }) {
                    HijackBinder(
                        service,
                        Service,
                        setOf(Service.TRANSACTION_CODE)
                    )
                } else {
                    service
                }
            }
            else -> service
        }
    }
}