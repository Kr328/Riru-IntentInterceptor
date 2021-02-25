package com.github.kr328.intent.util

inline fun <reified T> Any?.unsafeCast(): T {
    return this as T
}
