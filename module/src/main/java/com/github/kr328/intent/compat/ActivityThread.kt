package com.github.kr328.intent.compat

import android.app.ActivityThread
import android.app.Application
import android.content.Context

fun currentApplication(): Application? {
    return ActivityThread.currentApplication()
}

fun requireSystemContext(): Context {
    return ActivityThread.currentActivityThread()?.systemContext!!
}
