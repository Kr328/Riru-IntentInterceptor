package com.github.kr328.intent.system

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.github.kr328.intent.compat.DirectPackageManager
import com.github.kr328.intent.compat.getPackageInfoAsUser
import com.github.kr328.intent.compat.getPackageUidAsUser
import com.github.kr328.intent.compat.userHandle
import com.github.kr328.intent.util.listenPermissionChanged
import com.github.kr328.intent.util.receiveBroadcasts
import kotlinx.coroutines.flow.*

fun System.listenPackagesHoldingPermission(
    permission: String,
    userId: Int,
): Flow<Set<String>> {
    val pm = DirectPackageManager()

    return flow {
        emit(Unit)

        context.receiveBroadcasts(userId.userHandle) {
            addAction(Intent.ACTION_USER_UNLOCKED)
        }.collectLatest {
            emit(Unit)
        }
    }.map {
        pm.getPackagesHoldingPermissions(arrayOf(permission), 0, userId)
            .map(PackageInfo::packageName)
            .toSet()
    }.transform {
        var packages: Set<String> = it

        emit(packages)

        context.receiveBroadcasts(userId.userHandle) {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            addDataScheme("package")
        }.collect { intent ->
            val packageName = intent.data?.authority

            if (packageName != null) {
                when (intent.action) {
                    Intent.ACTION_PACKAGE_ADDED -> {
                        val info = context.packageManager.getPackageInfoAsUser(
                            packageName,
                            PackageManager.GET_PERMISSIONS,
                            userId
                        )
                        if (info.requestedPermissions?.contains(permission) == true) {
                            packages = packages + packageName
                        }
                    }
                    Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                        packages = packages - packageName
                    }
                }

                emit(packages)
            }
        }
    }
}

fun System.listenPermissionGranted(
    permission: String,
    packageName: String,
    userId: Int,
): Flow<Boolean> {
    val uid = context.packageManager.getPackageUidAsUser(packageName, userId)

    val pm = DirectPackageManager()

    return flow {
        emit(Unit)

        context.packageManager.listenPermissionChanged().filter { it == uid }.collectLatest {
            emit(Unit)
        }
    }.map {
        pm.checkPermission(permission, packageName, userId) == PackageManager.PERMISSION_GRANTED
    }
}
