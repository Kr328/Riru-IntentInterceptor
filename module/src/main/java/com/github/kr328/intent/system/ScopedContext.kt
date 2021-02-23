package com.github.kr328.intent.system

import com.github.kr328.intent.system.data.DataStore
import com.github.kr328.intent.system.module.ModuleManager

class ScopedContext(userId: Int) : AutoCloseable {
    val data: DataStore = DataStore(userId)
    val apk: ModuleManager = ModuleManager(userId)

    fun load() {
        data.load()
    }

    fun update(): Boolean {
        return data.update().apply {
            apk.setModules(data.modules.keys)
        }
    }

    fun updatePackage(packageName: String): Set<String> {
        return data.updatePackage(packageName).apply {
            apk.setModules(data.modules.keys)
        }
    }

    fun removePackage(packageName: String): Set<String> {
        return data.removePackage(packageName).apply {
            apk.setModules(data.modules.keys)
        }
    }

    override fun close() {
        apk.close()
    }
}
