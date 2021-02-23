package com.github.kr328.intent.system

import android.app.ActivityThread
import android.os.*
import com.github.kr328.intent.IIntentInterceptorService
import com.github.kr328.intent.compat.*
import com.github.kr328.intent.remote.Injection
import com.github.kr328.intent.shared.TLog
import com.github.kr328.intent.system.data.DataStore
import com.github.kr328.intent.system.observer.ApkObserver
import com.github.kr328.intent.system.observer.PermissionObserver
import com.github.kr328.intent.system.observer.UserObserver
import com.github.kr328.intent.util.DaemonHandler
import com.github.kr328.intent.util.useAs
import com.github.kr328.intent.util.withPrivilege
import java.io.File

object IntentInterceptorManager : DaemonHandler("intent_interceptor") {
    sealed class Event {
        object Boot : Event() {
            override fun toString() = "Boot"
        }

        object Started : Event() {
            override fun toString() = "Started"
        }

        data class ApkAdded(val userId: Int, val packageName: String) : Event()
        data class ApkRemoved(val userId: Int, val packageName: String) : Event()
        data class ApkUpdated(val userId: Int, val packageName: String) : Event()

        data class PermissionChanged(val userId: Int, val uid: Int) : Event()

        data class UserAdded(val userId: Int) : Event()
        data class UserRemoved(val userId: Int) : Event()
    }

    private val scopes: MutableMap<Int, ScopedContext> = mutableMapOf()

    val service = object : IIntentInterceptorService.Stub() {
        override fun open(packageName: String?): List<Injection> {
            packageName ?: return emptyList()

            val uid = Binder.getCallingUid()
            val userId = uid.userId
            val pkg = withPrivilege {
                SystemService.packages.getPackageInfo(packageName, 0, userId)
            } ?: return emptyList()

            if (pkg.applicationInfo?.uid != uid)
                return emptyList()

            val scope = scopes[userId] ?: return emptyList()

            val module = scope.data.targets[packageName] ?: return emptyList()

            return module.modules.mapNotNull {
                scope.data.modules[it]?.run {
                    Injection(
                        scope.apk[this.packageName]!!,
                        this.interceptor
                    )
                }
            }
        }
    }

    private fun Event.enqueue(delay: Long = 0) {
        val msg = Message.obtain(this@IntentInterceptorManager, 0).apply { this.obj = this@enqueue }

        sendMessageDelayed(msg, delay)
    }

    fun shouldSkipUid(uid: Int): Boolean {
        return withPrivilege { SystemService.packages.getPackagesForUid(uid) }?.none {
            scopes[uid.userId]?.data?.targets?.containsKey(it) == true
        } == true
    }

    private fun killApps(apps: Collection<String>, userId: Int) {
        apps.forEach {
            TLog.i("Killing $it/$userId")

            SystemService.activity.forceStopPackage(it, userId)
        }
    }

    override fun handleMessage(msg: Message) {
        when (val event = msg.obj as Event) {
            Event.Boot -> {
                if (ActivityThread.currentActivityThread() == null) {
                    TLog.i("System booting, wait 1s")

                    Event.Boot.enqueue(1000)
                } else {
                    ActivityThread.currentActivityThread()
                        .useAs(ActivityThreadDefinition::class.java, true).getHandler().post {
                            Event.Started.enqueue()
                        }
                }
            }
            Event.Started -> {
                ApkObserver.setAdded { userId, packageName ->
                    Event.ApkAdded(userId, packageName).enqueue()
                }
                ApkObserver.setUpdated { userId, packageName ->
                    Event.ApkUpdated(userId, packageName).enqueue()
                }
                ApkObserver.setRemoved { userId, packageName ->
                    Event.ApkRemoved(userId, packageName).enqueue()
                }
                PermissionObserver.setChanged { userId, uid ->
                    Event.PermissionChanged(userId, uid).enqueue()
                }
                UserObserver.setAdded {
                    Event.UserAdded(it).enqueue()
                }
                UserObserver.setRemoved {
                    Event.UserRemoved(it).enqueue()
                }

                scopes.forEach { (userId, scope) ->
                    if (scope.update()) {
                        killApps(scope.data.targets.keys, userId)
                    }
                }

                val users = SystemService.user.getUserIds().toSet()

                (scopes.keys - users).forEach {
                    Event.UserRemoved(it).enqueue()
                }

                (users - scopes.keys).forEach {
                    Event.UserAdded(it).enqueue()
                }
            }
            is Event.ApkAdded -> {
                killApps(
                    scopes[event.userId]?.updatePackage(event.packageName) ?: emptyList(),
                    event.userId
                )
            }
            is Event.ApkRemoved -> {
                killApps(
                    scopes[event.userId]?.removePackage(event.packageName) ?: emptyList(),
                    event.userId
                )
            }
            is Event.ApkUpdated -> {
                killApps(
                    scopes[event.userId]?.updatePackage(event.packageName) ?: emptyList(),
                    event.userId
                )
            }
            is Event.PermissionChanged -> {
                SystemService.packages.getPackagesForUid(event.uid)?.forEach {
                    killApps(
                        scopes[event.userId]?.updatePackage(it) ?: emptyList(),
                        event.userId,
                    )
                }
            }
            is Event.UserAdded -> {
                scopes.remove(event.userId)?.close()

                scopes[event.userId] = ScopedContext(event.userId).apply {
                    if (update()) {
                        killApps(data.targets.keys, event.userId)
                    }
                }
            }
            is Event.UserRemoved -> {
                scopes.remove(event.userId)?.apply {
                    close()

                    File(DataStore.DATA_PATH).resolve(event.userId.toString()).deleteRecursively()
                }
            }
        }

        TLog.i("Event ${msg.obj} processed")
    }

    init {
        try {
            File(DataStore.DATA_PATH).listFiles()?.forEach {
                val userId = it.name.toIntOrNull()

                if (userId != null) {
                    scopes[userId] = ScopedContext(userId).apply(ScopedContext::load)
                }
            }
        } catch (e: Exception) {
            TLog.w("Load configurations from cache: ${e.message}", e)
        }
    }

    init {
        Event.Boot.enqueue()
    }
}