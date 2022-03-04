package com.github.kr328.intent.system

import com.github.kr328.intent.compat.requireSystemContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class System : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<System> {
        suspend fun require(): System {
            return coroutineContext[Key]!!
        }
    }

    override val key: CoroutineContext.Key<*>
        get() = Key

    val context = requireSystemContext()
}
