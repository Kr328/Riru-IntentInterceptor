package com.github.kr328.intent.util

import android.content.pm.PackageManager
import com.github.kr328.intent.compat.*
import com.github.kr328.intent.shared.debug
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*

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
