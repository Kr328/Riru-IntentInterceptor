package com.github.kr328.intent.system.observer

import com.github.kr328.intent.compat.PermissionListener
import com.github.kr328.intent.compat.userId
import com.github.kr328.intent.shared.TLog

object PermissionObserver {
    private var onChanged: ((Int, Int) -> Unit)? = null

    private val listener: PermissionListener = PermissionListener {
        try {
            onChanged?.invoke(it.userId, it)
        } catch (e: Exception) {
            TLog.w("Send permission changed: $e", e)
        }
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