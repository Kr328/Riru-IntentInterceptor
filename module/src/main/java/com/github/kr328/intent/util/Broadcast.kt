package com.github.kr328.intent.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.UserHandle
import com.github.kr328.intent.compat.registerReceiverAsUser
import com.github.kr328.intent.shared.debug
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Context.listenBroadcasts(
    user: UserHandle,
    broadcastPermission: String? = null,
    filterBuilder: IntentFilter.() -> Unit,
): Flow<Intent> {
    return callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                "$intent received".debug()

                trySendBlocking(intent ?: return)
            }
        }
        val filter = IntentFilter().apply(filterBuilder)
        val handler = Handler(moduleLooper)

        "registerReceiverAsUser(@{${receiver.hashCode()}}, $user, ${
            filter.actionsIterator().asSequence().toList()
        }, $broadcastPermission, $handler)".debug()

        registerReceiverAsUser(receiver, user, filter, broadcastPermission, handler)

        awaitClose {
            "unregisterReceiver(@{${receiver.hashCode()}})".debug()

            unregisterReceiver(receiver)
        }
    }
}

