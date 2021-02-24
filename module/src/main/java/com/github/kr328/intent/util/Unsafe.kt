package com.github.kr328.intent.util

@Suppress("UNCHECKED_CAST")
fun <T> Any?.unsafeCast(): T {
    return this as T
}
