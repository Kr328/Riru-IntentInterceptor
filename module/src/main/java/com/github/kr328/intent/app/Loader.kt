package com.github.kr328.intent.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.github.kr328.intent.compat.appMetaData
import com.github.kr328.intent.remote.Injection
import com.github.kr328.intent.shared.Constants
import dalvik.system.DexClassLoader

fun Injection.load(
    application: Application,
): LoadedInterceptor {
    val packageInfo =
        application.packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
    val resourceContext: Context = application.createPackageContext(packageName, 0)

    val classLoader = DexClassLoader(
        packageInfo.applicationInfo.sourceDir,
        application.codeCacheDir.absolutePath,
        packageInfo.applicationInfo.nativeLibraryDir,
        application.classLoader,
    )

    return with(
        classLoader.loadClass(
            packageInfo.appMetaData.getString(
                Constants.MODULE_METADATA_INTERCEPTOR,
                ""
            )
        )
    ) {
        val constructor = getConstructor(Context::class.java)
        val intercept = getMethod("intercept", Intent::class.java)

        require(intercept.returnType == Intent::class.java) {
            throw NoSuchMethodException("$intercept invalid")
        }

        LoadedInterceptor(
            constructor.newInstance(ModuleContext(classLoader, application, resourceContext)),
            intercept,
            packageName
        )
    }
}
