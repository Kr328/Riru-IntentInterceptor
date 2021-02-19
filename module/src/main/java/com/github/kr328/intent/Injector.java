package com.github.kr328.intent;

import android.util.Log;

import com.github.kr328.intent.shared.Constants;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class Injector {
    public static void inject(String process) {
        try {
            switch (process) {
                case "system_server":
                    new SystemInjector().inject();
                    break;
                case "app":
                    new AppInjector().inject();
                    break;
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, "inject " + process + ": " + e, e);
        }
    }
}
