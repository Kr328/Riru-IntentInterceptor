package com.github.kr328.intent.system.observer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.github.kr328.intent.compat.*
import com.github.kr328.intent.shared.TLog

object UserObserver : BroadcastReceiver() {
    private var onAdded: ((Int) -> Unit)? = null
    private var onRemoved: ((Int) -> Unit)? = null

    private var registered: Boolean = false

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            when (intent?.action) {
                ACTION_USER_ADDED -> {
                    val userId = intent.getUserIdExtra()

                    onAdded?.invoke(userId ?: return)
                }
                ACTION_USER_REMOVED -> {
                    val userId = intent.getUserIdExtra()

                    onRemoved?.invoke(userId ?: return)
                }
            }
        } catch (e: Exception) {
            TLog.w("Send user changed: $e", e)
        }
    }

    fun setAdded(callback: ((Int) -> Unit)?) {
        onAdded = callback

        registerOrUnregister()
    }

    fun setRemoved(callback: ((Int) -> Unit)?) {
        onRemoved = callback

        registerOrUnregister()
    }

    private fun registerOrUnregister() {
        if (onAdded != null || onRemoved != null) {
            if (registered) return

            try {
                requireSystemContext().registerReceiverForAllUsers(
                    this,
                    IntentFilter().apply {
                        addAction(ACTION_USER_ADDED)
                        addAction(ACTION_USER_REMOVED)
                    }
                )

                registered = true
            } catch (e: Exception) {
                TLog.w("Register user observer: ${e.message}", e)
            }
        } else {
            if (!registered) return

            try {
                requireSystemContext().unregisterReceiver(this)

                registered = false
            } catch (e: Exception) {
                TLog.w("Unregister user observer: ${e.message}", e)
            }
        }
    }
}