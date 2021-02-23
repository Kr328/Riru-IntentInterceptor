package com.github.kr328.intent.compat

import android.content.pm.UserInfo
import android.os.Build
import android.os.IUserManager

fun IUserManager.getUsersCompat(): List<UserInfo> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getUsers(false, false, false)
    } else {
        getUsers(false)
    }
}

fun IUserManager.getUserIds(): List<Int> {
    return getUsersCompat().map(UserInfo::id)
}
