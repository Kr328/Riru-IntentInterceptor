package com.github.kr328.intent.compat

import android.content.Intent
import android.content.pm.*
import android.os.Bundle
import android.os.ServiceManager
import com.github.kr328.intent.util.unsafeCast

val PackageInfo.appMetaData: Bundle
    get() = applicationInfo.metaData ?: Bundle.EMPTY

fun PackageManager.queryIntentContentProvidersAsUser(
    intent: Intent,
    flags: Int,
    userId: Int
): List<ResolveInfo> {
    return unsafeCast<PackageManagerHidden>().queryIntentContentProvidersAsUser(
        intent,
        flags,
        userId
    )
}

fun PackageManager.getPackageUidAsUser(packageName: String, userId: Int): Int {
    return unsafeCast<PackageManagerHidden>().getPackageUidAsUser(packageName, userId)
}

class DirectPackageManager {
    private val pm = IPackageManager.Stub.asInterface(
        ServiceManager.getService("package")
    )!!

    fun checkPermission(permName: String, pkgName: String, userId: Int): Int {
        return pm.checkPermission(permName, pkgName, userId)
    }
}