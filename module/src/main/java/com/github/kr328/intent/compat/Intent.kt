package com.github.kr328.intent.compat

import android.content.Intent

object IntentConstants {
    const val ACTION_USER_ADDED = "android.intent.action.USER_ADDED"
    const val ACTION_USER_REMOVED = "android.intent.action.USER_REMOVED"
    const val EXTRA_USER_HANDLE = "android.intent.extra.user_handle"
}

fun Intent.getUserIdExtra(): Int? {
    val uid = getIntExtra(Intent.EXTRA_UID, -1)

    if (uid < 0)
        return null

    return uid.userId
}