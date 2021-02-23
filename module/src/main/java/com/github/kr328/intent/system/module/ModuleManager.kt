package com.github.kr328.intent.system.module

import android.os.SharedMemory

class ModuleManager(private val userId: Int) : AutoCloseable {
    private val modules: MutableMap<String, ModuleLoader> = mutableMapOf()

    fun setModules(packageNames: Set<String>) {
        val removed = modules.keys - packageNames
        val added = packageNames - modules.keys

        removed.forEach {
            modules.remove(it)?.close()
        }

        // update
        modules.forEach { (_, loader) ->
            loader.reload()
        }

        added.forEach {
            modules[it] = ModuleLoader(it, userId)
        }
    }

    operator fun get(packageName: String): List<SharedMemory>? {
        return modules[packageName]?.classes
    }

    override fun close() {
        modules.forEach {
            it.value.close()
        }
    }
}
