package com.github.kr328.intent;

import android.os.Process;
import android.util.Log;

import static com.github.kr328.intent.shared.Constants.TAG;

public class SystemInjector {
    public void inject() throws Exception {
        Log.i(TAG, "system pid = " + Process.myPid() + " uid = " + Process.myUid());
    }
}
