package com.github.kr328.intent.compat

import android.content.Intent

fun Intent.getUserIdExtra(): Int? {
    val userId = getIntExtra(`$android`.content.Intent.EXTRA_USER_HANDLE, -1)

    if (userId < 0) {
        return null
    }

    return userId
}

val ACTION_USER_ADDED: String
    get() = `$android`.content.Intent.ACTION_USER_ADDED

val ACTION_USER_REMOVED: String
    get() = `$android`.content.Intent.ACTION_USER_REMOVED
