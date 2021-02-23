@file:Suppress("DEPRECATION")

package com.github.kr328.intent.compat

import android.os.Build
import android.os.FileObserver
import java.io.File

fun createFileObserver(
    file: File,
    mask: Int = FileObserver.ALL_EVENTS,
    onEvent: (event: Int, file: File) -> Unit
): FileObserver {
    return if (Build.VERSION.SDK_INT >= 29) {
        object : FileObserver(file, mask) {
            override fun onEvent(event: Int, path: String?) {
                onEvent(event, if (path == null) file else file.resolve(path))
            }
        }
    } else {
        object : FileObserver(file.absolutePath, mask) {
            override fun onEvent(event: Int, path: String?) {
                onEvent(event, if (path == null) file else file.resolve(path))
            }
        }
    }
}
