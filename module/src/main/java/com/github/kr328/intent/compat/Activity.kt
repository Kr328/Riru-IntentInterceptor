package com.github.kr328.intent.compat

import android.app.ActivityManagerNative
import android.app.IActivityManager
import android.app.IActivityTaskManager
import android.os.Build
import android.os.IBinder

fun IBinder.asActivityManager(): IActivityManager {
    return if (Build.VERSION.SDK_INT >= 26)
        IActivityManager.Stub.asInterface(this)
    else
        ActivityManagerNative.asInterface(this)
}

fun IBinder.asActivityTaskManager(): IActivityTaskManager {
    return IActivityTaskManager.Stub.asInterface(this)
}