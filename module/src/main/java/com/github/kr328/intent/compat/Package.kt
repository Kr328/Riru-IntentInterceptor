package com.github.kr328.intent.compat

import android.content.Intent
import android.content.pm.*
import android.os.Bundle
import android.os.ServiceManager
import com.github.kr328.intent.util.unsafeCast

val PackageInfo.appMetaData: Bundle
    get() = applicationInfo.metaData ?: Bundle.EMPTY

val PackageInfo.uid: Int
    get() = applicationInfo.uid

fun IPackageManager.isPermissionGranted(uid: Int, permission: String): Boolean {
    return checkUidPermission(permission, uid) == PackageManager.PERMISSION_GRANTED
}

fun IPackageManager.getPackagesByPermission(
    permission: String,
    flags: Int,
    userId: Int
): List<PackageInfo> {
    return getPackagesHoldingPermissions(arrayOf(permission), flags, userId).list
}

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

fun PackageManager.getPackageInfoAsUser(packageName: String, flags: Int, userId: Int): PackageInfo {
    return unsafeCast<PackageManagerHidden>().getPackageInfoAsUser(packageName, flags, userId)
}

class DirectPackageManager {
    private val pm = IPackageManager.Stub.asInterface(
        ServiceManager.getService("package")
    )!!

    fun checkPermission(permName: String, pkgName: String, userId: Int): Int {
        return pm.checkPermission(permName, pkgName, userId)
    }

    fun getPackagesHoldingPermissions(
        permissions: Array<String>,
        flags: Int,
        userId: Int
    ): List<PackageInfo> {
        return pm.getPackagesHoldingPermissions(permissions, flags, userId).list
    }
}