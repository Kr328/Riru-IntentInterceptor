package com.github.kr328.intent.util

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class CollectEachMode {
    Replace, Difference
}

suspend fun <T> Flow<Set<T>>.collectEach(
    mode: CollectEachMode = CollectEachMode.Difference,
    block: suspend (T) -> Unit
) = coroutineScope {
    var jobs = mapOf<T, Job>()

    collectLatest { snapshot ->
        when (mode) {
            CollectEachMode.Replace -> {
                jobs.values.forEach {
                    it.cancel()
                }

                jobs = snapshot.associateWith {
                    launch { block(it) }
                }
            }
            CollectEachMode.Difference -> {
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
