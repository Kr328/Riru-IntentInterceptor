package com.github.kr328.intent.util

import android.os.Binder

fun <R> withPrivilege(block: () -> R): R {
    val token = Binder.clearCallingIdentity()

    return try {
        block()
    } finally {
        Binder.restoreCallingIdentity(token)
    }
}