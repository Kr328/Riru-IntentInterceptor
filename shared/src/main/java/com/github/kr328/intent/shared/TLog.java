package com.github.kr328.intent.shared;

import android.util.Log;

public class TLog {
    public static void i(String message) {
        Log.i(Constants.TAG, message);
    }

    public static void i(String message, Throwable throwable) {
        Log.i(Constants.TAG, message, throwable);
    }

    public static void w(String message) {
        Log.w(Constants.TAG, message);
    }

    public static void w(String message, Throwable throwable) {
        Log.w(Constants.TAG, message, throwable);
    }

    public static void e(String message) {
        Log.e(Constants.TAG, message);
    }

    public static void e(String message, Throwable throwable) {
        Log.e(Constants.TAG, message, throwable);
    }
}
