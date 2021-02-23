package com.github.kr328.intent.compat

import android.os.UserHandle
import com.github.kr328.intent.util.Field
import com.github.kr328.intent.util.useAs

interface UserHandleDefinition {
    val all: UserHandle
        @Field("ALL") get
}

object UserHandleConstants {
    val ALL = UserHandle::class.java.useAs(UserHandleDefinition::class.java).all
}

val Int.userId: Int
    get() = this / 100000
