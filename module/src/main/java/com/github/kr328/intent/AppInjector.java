package com.github.kr328.intent;

import android.os.Process;
import android.util.Log;

import static com.github.kr328.intent.shared.Constants.TAG;

public class AppInjector {
    public void inject() throws Exception {
        Log.i(TAG, "application pid = " + Process.myPid() + " uid = " + Process.myUid());
    }
}
