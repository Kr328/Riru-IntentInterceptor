package com.github.kr328.intent.system

import android.content.Intent
import android.net.Uri
import com.github.kr328.intent.compat.withUserId
import com.github.kr328.intent.shared.Plugins
import com.github.kr328.intent.shared.debug
import com.github.kr328.intent.shared.error
import com.github.kr328.intent.util.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

object SystemProcess {
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

        withContext(System()) {
            "handleUsers()".debug()

            handleUsers()
        }
    }

    private suspend fun handleUsers(): Unit = coroutineScope {
        System.require().listenUsers().collectEach {
            "handleUser($it)".debug()

            handleUser(it)
        }
    }

    private suspend fun handleUser(userId: Int): Unit = coroutineScope {
        System.require().listenPackagesHoldingPermission(
            Plugins.PERMISSION_INTERCEPT_INTENT,
            userId
        ).collectEach {
            "handlePackage($userId, $it)".debug()

            handlePackage(it, userId)
        }
    }

    private suspend fun handlePackage(packageName: String, userId: Int): Unit = coroutineScope {
        whileHasPermission(userId, packageName) {
            System.require().listenIntentContentProviders(
                Intent(Plugins.ACTION_CONFIGURATION),
                packageName,
                userId
            ).collectEach(CollectEachMode.Replace) {
                "handleAuthority($userId, $it)".debug()

                handleAuthority(userId, it)
            }
        }
    }

    private suspend fun handleAuthority(userId: Int, authority: String): Unit = coroutineScope {
        val uri = Uri.parse(authority)

        val targets = System.require().context.contentResolver.observeQuery(
            uri.buildUpon().appendPath(Plugins.PROVIDER_PATH_TARGETS).build().withUserId(userId),
            arrayOf(Plugins.PROVIDER_COLUMN_PACKAGE_NAME),
        ) {
            sequence<String> {
                val packageName = getColumnIndex(Plugins.PROVIDER_COLUMN_PACKAGE_NAME)

                while (moveToNext()) {
                    yield(getString(packageName))
                }
            }.toSet()
        }
        val configs = System.require().context.contentResolver.observeQuery(
            uri.buildUpon().appendPath(Plugins.PROVIDER_PATH_CONFIGS).build().withUserId(userId),
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

    private suspend fun whileHasPermission(
        userId: Int,
        packageName: String,
        block: suspend () -> Unit,
    ) = coroutineScope {
        System.require().listenPermissionGranted(
            Plugins.PERMISSION_INTERCEPT_INTENT,
            packageName,
            userId
        ).map(::setOf).collectEach {
            if (it) {
                block()
            }
        }
    }
}