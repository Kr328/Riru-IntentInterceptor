package com.github.kr328.intent.compat

import android.content.pm.UserInfo
import android.os.UserHandle
import android.os.UserHandleHidden
import android.os.UserManager
import android.os.UserManagerHidden
import com.github.kr328.intent.util.unsafeCast

val UserHandleALL: UserHandle
    get() = UserHandleHidden.ALL

val Int.userId: Int
    get() = UserHandleHidden.getUserId(this)

val Int.userHandle: UserHandle
    get() = UserHandleHidden.of(userId)

val UserManager.userIds: List<Int>
    get() = users.map(UserInfo::id)

val UserManager.users: List<UserInfo>
    get() = unsafeCast<UserManagerHidden>().users
