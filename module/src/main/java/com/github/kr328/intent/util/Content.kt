package com.github.kr328.intent.util

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.PatternMatcher
import com.github.kr328.intent.compat.queryIntentContentProvidersAsUser
import com.github.kr328.intent.compat.userHandle
import com.github.kr328.intent.shared.debug
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*

fun <T> ContentResolver.listenContentProviderWithQuery(
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

fun Context.listenIntentContentProviderAuthorities(
    intent: Intent,
    userId: Int,
): Flow<Set<String>> {
    return flow {
        emit(Unit)

        listenBroadcasts(userId.userHandle) {
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")

            val packageName = intent.`package`
            if (packageName != null) {
                addDataSchemeSpecificPart(packageName, PatternMatcher.PATTERN_LITERAL)
            }
        }.collect {
            emit(Unit)
        }
    }.map {
        packageManager.queryIntentContentProvidersAsUser(
            intent,
            0,
            userId,
        ).map { it.providerInfo.authority }.toSet()
    }.onEach {
        "Providers matches $intent: $it".debug()
    }
}

fun Context.listenPackagesHasIntentContentProvider(
    intent: Intent,
    userId: Int,
): Flow<Set<String>> {
    return channelFlow {
        val unlock = listenBroadcasts(userId.userHandle) {
            addAction(Intent.ACTION_USER_UNLOCKED)
        }.buffer(Channel.CONFLATED).map { }
        val packages = listenBroadcasts(userId.userHandle) {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            addDataScheme("package")
        }.buffer(Channel.CONFLATED).map { }

        listOf(flowOf(Unit), unlock, packages).collectAllWithLaunch {
            val r = packageManager.queryIntentContentProvidersAsUser(intent, 0, userId)
                .map { it.providerInfo.packageName }.toSet()

            "Queried packages: $r".debug()

            send(r)
        }
    }
}
