package com.github.kr328.intent.system.module

import android.os.SharedMemory
import com.github.kr328.intent.compat.SystemService
import com.github.kr328.intent.compat.getUpdatedTime

class ModuleLoader(private val packageName: String, private val userId: Int) : AutoCloseable {
    private var updatedAt: Long = SystemService.packages.getUpdatedTime(packageName, 0)
    private var apk: LoadedApk = loadApk(packageName, userId)

    val classes: List<SharedMemory>
        get() = apk.classesDex

    fun reload(): Boolean {
        if (SystemService.packages.getUpdatedTime(packageName, userId) <= updatedAt) {
            return false
        }

        apk.close()

        apk = loadApk(packageName, 0)

        return true
    }

    override fun close() {
        apk.close()
    }
}