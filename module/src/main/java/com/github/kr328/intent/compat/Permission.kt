package com.github.kr328.intent.compat

import android.content.pm.PackageManager
import android.content.pm.PackageManagerHidden
import com.github.kr328.intent.util.unsafeCast

fun interface OnPermissionsChangedListener : PackageManagerHidden.OnPermissionsChangedListener {
    override fun onPermissionsChanged(uid: Int)
}

fun PackageManager.addOnPermissionsChangeListener(listener: OnPermissionsChangedListener) {
    unsafeCast<PackageManagerHidden>().addOnPermissionsChangeListener(listener)
}

fun PackageManager.removeOnPermissionsChangeListener(listener: OnPermissionsChangedListener) {
    unsafeCast<PackageManagerHidden>().removeOnPermissionsChangeListener(listener)
}
