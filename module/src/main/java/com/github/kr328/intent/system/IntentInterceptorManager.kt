package com.github.kr328.intent.system

import `$android`.app.ActivityManager
import android.os.Binder
import android.os.UserManager
import com.github.kr328.intent.IIntentInterceptorService
import com.github.kr328.intent.compat.SystemService
import com.github.kr328.intent.compat.getUserIds
import com.github.kr328.intent.compat.requireSystemContext
import com.github.kr328.intent.compat.userId
import com.github.kr328.intent.remote.Injection
import com.github.kr328.intent.shared.TLog
import com.github.kr328.intent.system.data.DataStore
import com.github.kr328.intent.system.observer.ApkObserver
import com.github.kr328.intent.system.observer.PermissionObserver
import com.github.kr328.intent.system.observer.UserObserver
import com.github.kr328.intent.util.waitActivityThreadAvailable
import com.github.kr328.intent.util.waitMainThreadAvailable
import com.github.kr328.intent.util.withPrivilege
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File
import java.io.PrintStream

object IntentInterceptorManager {
    sealed class Event {
        data class ApkAdded(val userId: Int, val packageName: String) : Event()
        data class ApkRemoved(val userId: Int, val packageName: String) : Event()
        data class ApkUpdated(val userId: Int, val packageName: String) : Event()

        data class PermissionChanged(val userId: Int, val uid: Int) : Event()

        data class UserAdded(val userId: Int) : Event()
        data class UserRemoved(val userId: Int) : Event()
    }

    private val users: MutableMap<Int, UserScope> = mutableMapOf()

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

            val scope = users[userId] ?: return emptyList()

            val module = scope.data.targets[packageName] ?: return emptyList()

            return module.modules.map(::Injection)
        }
    }

    fun shouldSkipUid(uid: Int): Boolean {
        return withPrivilege { SystemService.packages.getPackagesForUid(uid) }?.none {
            users[uid.userId]?.data?.targets?.containsKey(it) == true
        } == true
    }

    private fun killApps(apps: Collection<String>, userId: Int) {
        val am = requireSystemContext().getSystemService(ActivityManager::class.java)

        apps.forEach {
            TLog.i("Killing $it/$userId")

            am.forceStopPackageAsUser(it, userId)
        }
    }

    private suspend fun run() {
        val events = Channel<Event>(Channel.UNLIMITED)

        waitActivityThreadAvailable()
        waitMainThreadAvailable()

        ApkObserver.setAdded { userId, packageName ->
            events.offer(Event.ApkAdded(userId, packageName))
        }
        ApkObserver.setUpdated { userId, packageName ->
            events.offer(Event.ApkUpdated(userId, packageName))
        }
        ApkObserver.setRemoved { userId, packageName ->
            events.offer(Event.ApkRemoved(userId, packageName))
        }
        PermissionObserver.setChanged { userId, uid ->
            events.offer(Event.PermissionChanged(userId, uid))
        }
        UserObserver.setAdded {
            events.offer(Event.UserAdded(it))
        }
        UserObserver.setRemoved {
            events.offer(Event.UserRemoved(it))
        }

        users.forEach { (userId, scope) ->
            if (scope.update()) {
                killApps(scope.data.targets.keys, userId)
            }
        }

        val users = requireSystemContext()
            .getSystemService(UserManager::class.java).getUserIds()

        (this.users.keys - users).forEach {
            events.offer(Event.UserRemoved(it))
        }

        (users - this.users.keys).forEach {
            events.offer(Event.UserAdded(it))
        }

        try {
            while (true) {
                val event = events.receive()

                when (event) {
                    is Event.ApkAdded -> {
                        killApps(
                            IntentInterceptorManager.users[event.userId]?.updatePackage(event.packageName)
                                ?: emptyList(),
                            event.userId
                        )
                    }
                    is Event.ApkRemoved -> {
                        killApps(
                            IntentInterceptorManager.users[event.userId]?.removePackage(event.packageName)
                                ?: emptyList(),
                            event.userId
                        )
                    }
                    is Event.ApkUpdated -> {
                        killApps(
                            IntentInterceptorManager.users[event.userId]?.updatePackage(event.packageName)
                                ?: emptyList(),
                            event.userId
                        )
                    }
                    is Event.PermissionChanged -> {
                        SystemService.packages.getPackagesForUid(event.uid)?.forEach {
                            killApps(
                                IntentInterceptorManager.users[event.userId]?.updatePackage(it)
                                    ?: emptyList(),
                                event.userId,
                            )
                        }
                    }
                    is Event.UserAdded -> {
                        IntentInterceptorManager.users[event.userId] = UserScope(event.userId).apply {
                            if (update()) {
                                killApps(data.targets.keys, event.userId)
                            }
                        }
                    }
                    is Event.UserRemoved -> {
                        IntentInterceptorManager.users.remove(event.userId)?.apply {
                            File(DataStore.DATA_PATH).resolve(event.userId.toString())
                                .deleteRecursively()
                        }
                    }
                }

                TLog.i("Event $event processed")
            }
        } finally {
            events.close()
        }
    }

    init {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                File(DataStore.DATA_PATH).listFiles()?.forEach {
                    val userId = it.name.toIntOrNull()

                    if (userId != null) {
                        users[userId] = UserScope(userId).apply(UserScope::load)
                    }
                }
            } catch (e: Exception) {
                TLog.w("Load configurations from cache: ${e.message}", e)
            }

            try {
                run()
            } catch (e: Throwable) {
                TLog.e("IntentInterceptorService crashed: $e", e)

                try {
                    DataStore.openCrashedLog().use {
                        e.printStackTrace(PrintStream(it))
                    }
                } catch (e: Exception) {
                    TLog.w("Save crashed log: $e", e)
                }
            }
        }
    }
}