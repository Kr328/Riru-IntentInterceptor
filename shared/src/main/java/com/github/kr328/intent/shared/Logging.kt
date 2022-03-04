package com.github.kr328.intent.shared

import android.util.Log

private const val TAG = "IntentInterceptor"

fun String.warn(throwable: Throwable? = null) {
    Log.w(TAG, this, throwable)
}

fun String.info(throwable: Throwable? = null) {
    Log.i(TAG, this, throwable)
}

fun String.error(throwable: Throwable? = null) {
    Log.e(TAG, this, throwable)
}

fun String.debug(throwable: Throwable? = null) {
    if (BuildConfig.DEBUG) {
        Log.d(TAG, this, throwable)
    }
}

fun String.verbose(throwable: Throwable? = null) {
    Log.v(TAG, this, throwable)
}
