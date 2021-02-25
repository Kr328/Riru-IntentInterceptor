package com.github.kr328.intent.app

import android.app.Application
import android.content.Context
import android.content.ContextWrapper

class ModuleContext(
    private val classLoader: ClassLoader,
    private val application: Application,
    resourceContext: Context,
) : ContextWrapper(resourceContext) {
    override fun getApplicationContext(): Context {
        return application
    }

    override fun getClassLoader(): ClassLoader {
        return classLoader
    }
}