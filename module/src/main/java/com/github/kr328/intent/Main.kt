package com.github.kr328.intent

import com.github.kr328.intent.shared.info
import com.github.kr328.intent.system.launchSystem
import com.github.kr328.zloader.ZygoteLoader

fun main() {
    "ProcessName = ${ZygoteLoader.getPackageName()}".info()

    if (ZygoteLoader.getPackageName() == ZygoteLoader.PACKAGE_SYSTEM_SERVER) {
        launchSystem()
    }
}
