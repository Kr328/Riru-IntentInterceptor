package com.github.kr328.intent.system

import com.github.kr328.intent.system.data.DataStore

class UserScope(userId: Int) {
    val data: DataStore = DataStore(userId)

    fun load() {
        data.load()
    }

    fun update(): Boolean {
        return data.update()
    }

    fun updatePackage(packageName: String): Set<String> {
        return data.updatePackage(packageName)
    }

    fun removePackage(packageName: String): Set<String> {
        return data.removePackage(packageName)
    }
}
