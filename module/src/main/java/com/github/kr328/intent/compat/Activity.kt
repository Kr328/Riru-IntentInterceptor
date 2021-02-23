package com.github.kr328.intent.compat

import android.app.IActivityManager

fun IActivityManager.forceStopApp(packageName: String, userId: Int = -1) {
    val users = if (userId >= 0) {
        listOf(userId)
    } else {
        SystemService.user.getUsersCompat().map { it.id }
    }

    users.forEach {
        forceStopPackage(packageName, it)
    }
}