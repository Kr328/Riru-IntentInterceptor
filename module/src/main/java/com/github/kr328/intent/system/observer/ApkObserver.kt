package com.github.kr328.intent.system.observer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.github.kr328.intent.compat.getUserIdExtra
import com.github.kr328.intent.compat.registerReceiverForAllUsers
import com.github.kr328.intent.compat.requireSystemContext
import com.github.kr328.intent.shared.TLog

object ApkObserver : BroadcastReceiver() {
    private var onAdded: ((Int, String) -> Unit)? = null
    private var onRemoved: ((Int, String) -> Unit)? = null
    private var onUpdated: ((Int, String) -> Unit)? = null

    private var registered: Boolean = false

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            when (intent?.action) {
                Intent.ACTION_PACKAGE_ADDED -> {
                    onAdded?.invoke(
                        intent.getUserIdExtra() ?: return,
                        intent.data!!.schemeSpecificPart!!
                    )
                }
                Intent.ACTION_PACKAGE_REMOVED -> {
                    onRemoved?.invoke(
                        intent.getUserIdExtra() ?: return,
                        intent.data!!.schemeSpecificPart!!
                    )
                }
                Intent.ACTION_PACKAGE_REPLACED -> {
                    onUpdated?.invoke(
                        intent.getUserIdExtra() ?: return,
                        intent.data!!.schemeSpecificPart!!
                    )
                }
            }
        } catch (e: Exception) {
            TLog.w("Send apk changed: $e", e)
        }
    }

    fun setAdded(callback: ((Int, String) -> Unit)?) {
        onAdded = callback

        registerOrUnregister()
    }

    fun setRemoved(callback: ((Int, String) -> Unit)?) {
        onRemoved = callback

        registerOrUnregister()
    }

    fun setUpdated(callback: ((Int, String) -> Unit)?) {
        onUpdated = callback

        registerOrUnregister()
    }

    private fun registerOrUnregister() {
        if (onAdded != null || onRemoved != null || onUpdated != null) {
            if (registered) return

            try {
                requireSystemContext().registerReceiverForAllUsers(
                    this,
                    IntentFilter().apply {
                        addAction(Intent.ACTION_PACKAGE_ADDED)
                        addAction(Intent.ACTION_PACKAGE_REMOVED)
                        addAction(Intent.ACTION_PACKAGE_REPLACED)
                        addDataScheme("package")
                    }
                )

                registered = true
            } catch (e: Exception) {
                TLog.w("Register apk observer: ${e.message}", e)
            }
        } else {
            if (!registered) return

            try {
                requireSystemContext().unregisterReceiver(this)

                registered = false
            } catch (e: Exception) {
                TLog.w("Unregister apk observer: ${e.message}", e)
            }
        }
    }
}