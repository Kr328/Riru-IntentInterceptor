package com.github.kr328.intent.system.observer

import com.github.kr328.intent.compat.PermissionListener
import com.github.kr328.intent.compat.userId

object PermissionObserver {
    private var onChanged: ((Int, Int) -> Unit)? = null

    private val listener: PermissionListener = PermissionListener {
        onChanged?.invoke(it.userId, it)
    }

    fun setChanged(callback: ((Int, Int) -> Unit)?) {
        onChanged = callback

        if (onChanged != null) {
            listener.register()
        } else {
            listener.unregister()
        }
    }
}