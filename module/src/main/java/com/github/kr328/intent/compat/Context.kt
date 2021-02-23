package com.github.kr328.intent.compat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import com.github.kr328.intent.util.useAs

interface ContextDefinition {
    fun registerReceiverAsUser(
        receiver: BroadcastReceiver,
        user: UserHandle,
        filter: IntentFilter,
        broadcastPermission: String?,
        scheduler: Handler
    ): Intent
}

fun Context.registerReceiverForAllUsers(
    receiver: BroadcastReceiver,
    filter: IntentFilter,
    scheduler: Handler = Handler(Looper.getMainLooper()),
) {
    this.useAs(ContextDefinition::class.java).registerReceiverAsUser(
        receiver,
        UserHandleConstants.ALL,
        filter,
        null,
        scheduler,
    )
}
