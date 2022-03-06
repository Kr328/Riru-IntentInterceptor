package com.github.kr328.intent.system

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PatternMatcher
import com.github.kr328.intent.compat.requiredSystemContext
import com.github.kr328.intent.compat.userHandle
import com.github.kr328.intent.compat.withUserId
import com.github.kr328.intent.shared.Plugins
import com.github.kr328.intent.shared.debug
import com.github.kr328.intent.shared.error
import com.github.kr328.intent.shared.warn
import com.github.kr328.intent.util.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

object System {
    fun main() {
        thread {
            try {
                runBlocking {
                    run()
                }
            } catch (throwable: Throwable) {
                "SystemProcess.run: $throwable".error(throwable)
            }
        }
    }

    private suspend fun run(): Unit = coroutineScope {
        waitActivityThreadAvailable()
        waitMainThreadAvailable()
        waitSystemContextAvailable()

        "handleUsers()".debug()

        requiredSystemContext.handleUsers()
    }

    private suspend fun Context.handleUsers(): Unit = coroutineScope {
        listenUsers().collectWithLaunch {
            "handleUser($it)".debug()

            handleUser(it)
        }
    }

    private suspend fun Context.handleUser(userId: Int): Unit = coroutineScope {
        listenPackagesHasIntentContentProvider(
            Intent(Plugins.ACTION_CONFIGURATION),
            userId
        ).collectWithLaunch {
            "handlePackage($it, $userId)".debug()

            handlePackage(it, userId)
        }
    }

    private suspend fun Context.handlePackage(packageName: String, userId: Int): Unit =
        coroutineScope {
            whileHasPermission(packageName, userId) {
                whileNotBroken(packageName, userId) {
                    listenIntentContentProviderAuthorities(
                        Intent(Plugins.ACTION_CONFIGURATION).setPackage(packageName),
                        userId
                    ).collectWithLaunch(CollectLaunchMode.All) {
                        "handleAuthority($userId, $it)".debug()

                        handleAuthority(userId, it)
                    }
                }
            }
        }

    private suspend fun Context.handleAuthority(userId: Int, authority: String): Unit =
        coroutineScope {
            val uri = Uri.parse("content://$authority")

            val targets = contentResolver.listenContentProviderWithQuery(
                uri.buildUpon().appendPath(Plugins.PROVIDER_PATH_TARGETS).build()
                    .withUserId(userId),
                arrayOf(Plugins.PROVIDER_COLUMN_PACKAGE_NAME),
            ) {
                sequence<String> {
                    val packageName = getColumnIndex(Plugins.PROVIDER_COLUMN_PACKAGE_NAME)

                    while (moveToNext()) {
                        yield(getString(packageName))
                    }
                }.toSet()
            }
            val configs = contentResolver.listenContentProviderWithQuery(
                uri.buildUpon().appendPath(Plugins.PROVIDER_PATH_CONFIGS).build()
                    .withUserId(userId),
                arrayOf(Plugins.PROVIDER_COLUMN_KEY, Plugins.PROVIDER_COLUMN_VALUE),
            ) {
                sequence<Pair<String, String>> {
                    val key = getColumnIndex(Plugins.PROVIDER_COLUMN_KEY)
                    val value = getColumnIndex(Plugins.PROVIDER_COLUMN_VALUE)

                    while (moveToNext()) {
                        yield(getString(key) to getString(value))
                    }
                }.toMap()
            }
            combine(targets, configs) { target, config ->
                target to config
            }.collectLatest { (target, config) ->
                "targets = $target, configs = $config".debug()
            }
        }

    private suspend fun Context.whileHasPermission(
        packageName: String,
        userId: Int,
        block: suspend () -> Unit,
    ) = coroutineScope {
        listenPermissionGranted(
            Plugins.PERMISSION_INTERCEPT_INTENT,
            packageName,
            userId
        ).map(::setOf).collectWithLaunch {
            if (it) {
                block()
            }
        }
    }

    private suspend fun Context.whileNotBroken(
        packageName: String,
        userId: Int,
        block: suspend () -> Unit
    ) = coroutineScope {
        flow {
            emit(Unit)

            listenBroadcasts(userId.userHandle) {
                addAction(Intent.ACTION_PACKAGE_REPLACED)
                addDataScheme("package")
                addDataSchemeSpecificPart(packageName, PatternMatcher.PATTERN_LITERAL)
            }.collect {
                emit(Unit)
            }
        }.collectLatest {
            try {
                block()
            } catch (e: Exception) {
                "Plugin $packageName/$userId broken: $e".warn(e)
            }
        }
    }
}