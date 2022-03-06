package com.github.kr328.intent.util

import android.content.Context
import android.content.pm.PackageManager
import com.github.kr328.intent.compat.DirectPackageManager
import com.github.kr328.intent.compat.getPackageUidAsUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun Context.listenPermissionGranted(
    permission: String,
    packageName: String,
    userId: Int,
): Flow<Boolean> {
    val uid = packageManager.getPackageUidAsUser(packageName, userId)

    val pm = DirectPackageManager()

    return flow {
        emit(Unit)

        packageManager.listenPermissionChanged().filter { it == uid }.collect {
            emit(Unit)
        }
    }.map {
        pm.checkPermission(permission, packageName, userId) == PackageManager.PERMISSION_GRANTED
    }
}
