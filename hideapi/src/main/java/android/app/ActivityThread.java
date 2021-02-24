package android.app;

import utils.Utils;

public class ActivityThread {
    public static ActivityThread currentActivityThread() {
        return Utils.throwStub();
    }

    public static Application currentApplication() {
        return Utils.throwStub();
    }

    public ContextImpl getSystemContext() {
        return Utils.throwStub();
    }
}
