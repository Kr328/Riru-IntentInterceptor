package com.github.kr328.intent.system.observer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.github.kr328.intent.compat.IntentConstants
import com.github.kr328.intent.compat.registerReceiverForAllUsers
import com.github.kr328.intent.compat.requireSystemContext
import com.github.kr328.intent.shared.TLog

object UserObserver : BroadcastReceiver() {
    private var onAdded: ((Int) -> Unit)? = null
    private var onRemoved: ((Int) -> Unit)? = null

    private var registered: Boolean = false

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            IntentConstants.ACTION_USER_ADDED -> {
                val userId = intent.getIntExtra(IntentConstants.EXTRA_USER_HANDLE, -1)

                if (userId >= 0)
                    onAdded?.invoke(userId)
            }
            IntentConstants.ACTION_USER_REMOVED -> {
                val userId = intent.getIntExtra(IntentConstants.EXTRA_USER_HANDLE, -1)

                if (userId >= 0)
                    onRemoved?.invoke(userId)
            }
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
                        addAction(IntentConstants.ACTION_USER_ADDED)
                        addAction(IntentConstants.ACTION_USER_REMOVED)
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