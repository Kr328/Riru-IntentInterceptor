package com.github.kr328.intent.compat

import `$android`.os.UserHandle
import android.content.pm.UserInfo
import android.os.UserManager
import com.github.kr328.intent.util.unsafeCast

val Int.userId: Int
    get() = UserHandle.getUserId(this)

fun UserManager.getUserIds(): List<Int> {
    return getUsers().map(UserInfo::id)
}

fun UserManager.getUsers(): List<UserInfo> {
    return this.unsafeCast<`$android`.os.UserManager>().users
}