package com.github.kr328.intent.compat

val Int.userId: Int
    get() = this / 100000
