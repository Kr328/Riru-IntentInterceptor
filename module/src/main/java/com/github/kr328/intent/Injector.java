package com.github.kr328.intent;

import android.content.Context;
import android.content.IClipboard;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import static com.github.kr328.intent.shared.Constants.TAG;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class Injector extends ServiceProxy {
    public static void inject(String packageName) {
        Log.i(TAG, String.format("Uid = %d Pid = %d", Process.myUid(), Process.myPid()));

        Injector injector = new Injector();

        try {
            injector.install();

            Log.i(TAG, "Inject successfully");
        } catch (Exception e) {
            Log.e(TAG, "Inject failure", e);
        }
    }

    @Override
    protected IBinder onAddService(String name, IBinder service) {
        return service;
    }
}
