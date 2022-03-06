package com.github.kr328.intent.util

import android.content.pm.PackageManager
import com.github.kr328.intent.compat.OnPermissionsChangedListener
import com.github.kr328.intent.compat.addOnPermissionsChangeListener
import com.github.kr328.intent.compat.removeOnPermissionsChangeListener
import com.github.kr328.intent.shared.debug
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun PackageManager.listenPermissionChanged(): Flow<Int> {
    return callbackFlow {
        val listener = OnPermissionsChangedListener {
            trySendBlocking(it)
        }

        "addOnPermissionsChangeListener($listener)".debug()

        addOnPermissionsChangeListener(listener)

        awaitClose {
            "removeOnPermissionsChangeListener($listener)".debug()

            removeOnPermissionsChangeListener(listener)
        }
    }
}
