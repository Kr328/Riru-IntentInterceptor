package android.app;

import android.content.pm.IPackageManager;

public class ActivityThread {
    static volatile IPackageManager sPackageManager;

    private ContextImpl mSystemContext;
    private ContextImpl mSystemUiContext;

    public static ActivityThread currentActivityThread() { throw new IllegalArgumentException("Stub!"); }
    public ContextImpl getSystemContext() { throw new IllegalArgumentException("Stub!"); }
    public static Application currentApplication() { throw new IllegalArgumentException("Stub!"); }
}
