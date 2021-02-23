package com.github.kr328.intent.compat

import android.os.Binder
import android.os.Build

class PermissionListener(private val onChanged: (Int) -> Unit) {
    private val receiver: Binder = if (Build.VERSION.SDK_INT >= 30) {
        object : android.permission.IOnPermissionsChangeListener.Stub() {
            override fun onPermissionsChanged(uid: Int) {
                onChanged(uid)
            }
        }
    } else {
        object : android.content.pm.IOnPermissionsChangeListener.Stub() {
            override fun onPermissionsChanged(uid: Int) {
                onChanged(uid)
            }
        }
    }

    fun register() {
        if (Build.VERSION.SDK_INT >= 30) {
            SystemService.permission.addOnPermissionsChangeListener(receiver as android.permission.IOnPermissionsChangeListener)
        } else {
            SystemService.packages.addOnPermissionsChangeListener(receiver as android.content.pm.IOnPermissionsChangeListener)
        }
    }

    fun unregister() {
        if (Build.VERSION.SDK_INT >= 30) {
            SystemService.permission.removeOnPermissionsChangeListener(receiver as android.permission.IOnPermissionsChangeListener)
        } else {
            SystemService.packages.removeOnPermissionsChangeListener(receiver as android.content.pm.IOnPermissionsChangeListener)
        }
    }
}