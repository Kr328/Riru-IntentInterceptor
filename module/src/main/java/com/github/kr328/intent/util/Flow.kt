package com.github.kr328.intent.util

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class CollectLaunchMode {
    All, Changed
}

suspend fun <T> Flow<Set<T>>.collectWithLaunch(
    mode: CollectLaunchMode = CollectLaunchMode.Changed,
    block: suspend (T) -> Unit
) = coroutineScope {
    var jobs = mapOf<T, Job>()

    collectLatest { snapshot ->
        when (mode) {
            CollectLaunchMode.All -> {
                jobs.values.forEach {
                    it.cancel()
                }

                jobs = snapshot.associateWith {
                    launch { block(it) }
                }
            }
            CollectLaunchMode.Changed -> {
                (jobs.keys - snapshot).forEach {
                    jobs[it]?.cancel()
                }

                jobs = snapshot.associateWith {
                    jobs.getOrElse(it) {
                        launch { block(it) }
                    }
                }
            }
        }
    }

    jobs.values.forEach {
        it.cancel()
    }
}

suspend fun <T> Collection<Flow<T>>.collectAllWithLaunch(block: suspend (T) -> Unit) =
    coroutineScope {
        forEach {
            launch {
                it.collect {
                    block(it)
                }
            }
        }
    }