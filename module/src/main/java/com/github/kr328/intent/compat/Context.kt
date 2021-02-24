package com.github.kr328.intent.compat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import com.github.kr328.intent.util.unsafeCast

fun Context.registerReceiverForAllUsers(
    receiver: BroadcastReceiver,
    filter: IntentFilter,
    scheduler: Handler = Handler(Looper.getMainLooper()),
) {
    this.unsafeCast<`$android`.content.Context>().registerReceiverAsUser(
        receiver,
        `$android`.os.UserHandle.ALL.unsafeCast(),
        filter,
        null,
        scheduler,
    )
}
