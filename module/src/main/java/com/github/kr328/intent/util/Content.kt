package com.github.kr328.intent.util

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import com.github.kr328.intent.shared.debug
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun <T> ContentResolver.observeQuery(
    uri: Uri,
    projection: Array<String>? = null,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    sortOrder: String? = null,
    adapter: Cursor.() -> T
): Flow<T> {
    return callbackFlow {
        val observer = object : ContentObserver(Handler(moduleLooper)) {
            override fun onChange(selfChange: Boolean) {
                query(uri, projection, selection, selectionArgs, sortOrder)?.use {
                    trySendBlocking(it.adapter())
                }
            }
        }

        "registerContentObserver($uri, false, $observer)".debug()

        registerContentObserver(uri, false, observer)

        observer.onChange(false)

        awaitClose {
            "unregisterContentObserver($observer)".debug()

            unregisterContentObserver(observer)
        }
    }
}