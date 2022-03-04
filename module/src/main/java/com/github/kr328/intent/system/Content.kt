package com.github.kr328.intent.system

import android.content.Intent
import com.github.kr328.intent.compat.queryIntentContentProvidersAsUser
import com.github.kr328.intent.compat.userHandle
import com.github.kr328.intent.util.receiveBroadcasts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun System.listenIntentContentProviders(
    intent: Intent,
    packageName: String,
    userId: Int,
): Flow<Set<String>> {
    return flow {
        emit(Unit)

        context.receiveBroadcasts(userId.userHandle) {
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
            addDataAuthority(packageName, null)
        }.collectLatest {
            emit(Unit)
        }
    }.map {
        context.packageManager.queryIntentContentProvidersAsUser(
            intent.setPackage(packageName),
            0,
            userId,
        ).map { it.providerInfo.authority }.toSet()
    }
}
