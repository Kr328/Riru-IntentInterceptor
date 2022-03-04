package com.github.kr328.intent.system

import android.os.UserManager
import com.github.kr328.intent.compat.*
import com.github.kr328.intent.util.receiveBroadcasts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun System.listenUsers(): Flow<Set<Int>> {
    return flow {
        var users: Set<Int> = context.getSystemService(UserManager::class.java).userIds.toSet()

        emit(users)

        context.receiveBroadcasts(UserHandleALL) {
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
