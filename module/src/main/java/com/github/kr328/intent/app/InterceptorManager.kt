package com.github.kr328.intent.app

import android.content.Intent
import android.os.Message
import com.github.kr328.intent.compat.currentApplication
import com.github.kr328.intent.shared.TLog
import com.github.kr328.intent.util.DaemonHandler

object InterceptorManager : DaemonHandler("interceptors") {
    private var interceptors: Interceptors? = null

    private enum class Event {
        Start, Load
    }

    private fun sendUniqueEvent(event: Event, delay: Long = 0) {
        removeMessages(event.ordinal)
        sendMessageDelayed(Message.obtain(this, event.ordinal), delay)
    }

    override fun handleMessage(msg: Message) {
        when (Event.values()[msg.what]) {
            Event.Start -> {
                if (currentApplication == null) {
                    TLog.i("Application unavailable, wait 1s")

                    sendUniqueEvent(Event.Start, 1000)
                } else {
                    sendUniqueEvent(Event.Load)
                }
            }
            Event.Load -> {
                interceptors = Interceptors(currentApplication!!)
            }
        }
    }

    fun intercept(intent: Intent): Intent {
        return interceptors?.intercept(intent) ?: intent
    }

    init {
        sendUniqueEvent(Event.Start)
    }
}