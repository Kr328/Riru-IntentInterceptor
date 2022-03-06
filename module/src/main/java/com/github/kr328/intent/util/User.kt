package com.github.kr328.intent.util

import android.content.Context
import android.os.UserManager
import com.github.kr328.intent.compat.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun Context.listenUsers(): Flow<Set<Int>> {
    return flow {
        var users: Set<Int> = getSystemService(UserManager::class.java).userIds.toSet()

        emit(users)

        listenBroadcasts(UserHandleALL) {
            addAction(ACTION_USER_ADDED)
            addAction(ACTION_USER_REMOVED)
        }.collect {
            val userId = it.extras?.userId
            if (userId != null) {
                when (it.action) {
                    ACTION_USER_ADDED -> {
                        users = users + userId
                    }
                    ACTION_USER_REMOVED -> {
                        users = users - userId
                    }
                }
            }

            emit(users)
        }
    }
}
