package com.github.kr328.intent.compat

import com.github.kr328.intent.util.unsafeCast

class PermissionListener(private val onChanged: (Int) -> Unit) {
    private val receiver = `$android`.content.pm.PackageManager.OnPermissionsChangedListener {
        onChanged(it)
    }

    fun register() {
        requireSystemContext().packageManager.unsafeCast<`$android`.content.pm.PackageManager>()
            .addOnPermissionsChangeListener(receiver)
    }

    fun unregister() {
        requireSystemContext().packageManager.unsafeCast<`$android`.content.pm.PackageManager>()
            .removeOnPermissionsChangeListener(receiver)
    }
}