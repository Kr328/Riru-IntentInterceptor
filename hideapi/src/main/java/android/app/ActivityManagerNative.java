package android.app;

import android.os.IBinder;

import utils.Utils;

public abstract class ActivityManagerNative implements IActivityManager {
    static public IActivityManager asInterface(IBinder obj) {
        return Utils.throwStub();
    }
}
