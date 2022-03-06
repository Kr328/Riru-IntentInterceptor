package com.github.kr328.intent.compat

import android.app.ActivityThread
import android.app.Application
import android.content.Context

val currentApplication: Application?
    get() = ActivityThread.currentApplication()

val currentActivityThread: ActivityThread?
    get() = ActivityThread.currentActivityThread()

val currentSystemContext: Context?
    get() = currentActivityThread?.systemContext

val requiredSystemContext: Context
    get() = currentSystemContext!!
