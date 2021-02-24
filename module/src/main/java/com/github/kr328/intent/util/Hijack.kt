package com.github.kr328.intent.util

import android.os.Binder
import android.os.IBinder
import android.os.IBinder.DeathRecipient
import android.os.IInterface
import android.os.Parcel

class HijackIBinder<T : IInterface>(private val original: IBinder, private val replaced: T) :
    IBinder by original {
    override fun queryLocalInterface(descriptor: String): IInterface? {
        if (descriptor == interfaceDescriptor) {
            return replaced
        }

        return original.queryLocalInterface(descriptor)
    }
}

class HijackBinder(
    private val original: IBinder,
    private val replaced: Binder,
    private val codes: Set<Int>
) : Binder() {
    override fun queryLocalInterface(descriptor: String): IInterface? {
        return null
    }

    override fun attachInterface(owner: IInterface?, descriptor: String?) {

    }

    override fun getInterfaceDescriptor(): String? {
        return original.interfaceDescriptor
    }

    override fun pingBinder(): Boolean {
        return original.pingBinder()
    }

    override fun isBinderAlive(): Boolean {
        return original.isBinderAlive
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        return if (codes.contains(code)) {
            replaced.transact(
                code,
                data,
                reply,
                flags
            )
        } else {
            original.transact(
                code,
                data,
                reply,
                flags
            )
        }
    }

    override fun linkToDeath(deathRecipient: DeathRecipient, i: Int) {
        original.linkToDeath(deathRecipient, i)
    }

    override fun unlinkToDeath(deathRecipient: DeathRecipient, i: Int): Boolean {
        return original.unlinkToDeath(deathRecipient, i)
    }
}