package com.github.kr328.intent.compat

import android.content.pm.UserInfo
import android.os.UserManager
import com.github.kr328.intent.util.unsafeCast

fun UserManager.getUserIds(): List<Int> {
    return getUsers().map(UserInfo::id)
}

fun UserManager.getUsers(): List<UserInfo> {
    return this.unsafeCast<`$android`.os.UserManager>().users
}