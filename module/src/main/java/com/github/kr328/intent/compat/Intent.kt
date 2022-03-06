package com.github.kr328.intent.compat

import android.content.IntentHidden
import android.os.Bundle

var Bundle.userId: Int?
    get() = getInt(IntentHidden.EXTRA_USER_HANDLE, -1).takeUnless { it < 0 }
    set(value) {
        if (value != null) {
            putInt(IntentHidden.EXTRA_USER_HANDLE, value)
        } else {
            remove(IntentHidden.EXTRA_USER_HANDLE)
        }
    }

val ACTION_USER_ADDED: String
    get() = IntentHidden.ACTION_USER_ADDED

val ACTION_USER_REMOVED: String
    get() = IntentHidden.ACTION_USER_REMOVED

