package com.github.kr328.intent.compat

import android.content.pm.IPackageManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle

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
