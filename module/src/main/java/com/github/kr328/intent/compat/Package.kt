package com.github.kr328.intent.compat

import android.content.pm.IPackageManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import java.io.FileNotFoundException

val PackageInfo.appMetaData: Bundle
    get() = applicationInfo.metaData ?: Bundle.EMPTY

val PackageInfo.uid: Int
    get() = applicationInfo.uid

fun IPackageManager.loadInstalledApps(flag: Int, userId: Int): List<PackageInfo> {
    return getInstalledPackages(flag, userId).list
}

fun IPackageManager.isPermissionGranted(uid: Int, permission: String): Boolean {
    return checkUidPermission(permission, uid) == PackageManager.PERMISSION_GRANTED
}

fun IPackageManager.getUpdatedTime(packageName: String, userId: Int): Long {
    return getPackageInfo(packageName, 0, userId)?.lastUpdateTime
        ?: throw FileNotFoundException("$packageName not found")
}

fun Sequence<PackageInfo>.filterHasPermission(permission: String): Sequence<PackageInfo> {
    return filter { it.requestedPermissions?.contains(permission) == true }
}

fun Sequence<PackageInfo>.filterHasAppMetadata(key: String): Sequence<PackageInfo> {
    return filter { it.appMetaData.containsKey(key) }
}

fun Sequence<PackageInfo>.filterGrantedPermission(permission: String): Sequence<PackageInfo> {
    return filter { SystemService.packages.isPermissionGranted(it.applicationInfo.uid, permission) }
}