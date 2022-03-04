package com.github.kr328.intent.compat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextHidden
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import com.github.kr328.intent.util.unsafeCast

fun Context.registerReceiverAsUser(
    receiver: BroadcastReceiver,
    user: UserHandle,
    filter: IntentFilter,
    broadcastPermission: String?,
    scheduler: Handler = Handler(Looper.getMainLooper()),
) {
    unsafeCast<ContextHidden>().registerReceiverAsUser(
        receiver,
        user,
        filter,
        broadcastPermission,
        scheduler,
    )
}
