package com.github.kr328.intent.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.github.kr328.intent.compat.appMetaData
import com.github.kr328.intent.remote.Injection
import com.github.kr328.intent.shared.Constants

fun Injection.load(
    application: Application,
): LoadedInterceptor {
    val info = application.packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
    val context: Context = application.createPackageContext(
        packageName,
        Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
    )

    return with(
        context.classLoader.loadClass(
            info.appMetaData.getString(
                Constants.MODULE_METADATA_INTERCEPTOR,
                ""
            )
        )
    ) {
        val constructor = getConstructor(Application::class.java, Context::class.java)
        val intercept = getMethod("intercept", Intent::class.java)

        require(intercept.returnType == Intent::class.java) {
            throw NoSuchMethodException("$intercept invalid")
        }

        LoadedInterceptor(constructor.newInstance(application, context), intercept, packageName)
    }
}
