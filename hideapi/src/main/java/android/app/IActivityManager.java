package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IActivityManager extends IInterface {
    abstract class Stub extends Binder implements IActivityManager {
        public static IActivityManager asInterface(IBinder binder) {
            throw new IllegalArgumentException("Unsupported");
        }

        @Override
        public IBinder asBinder() {
            throw new IllegalArgumentException("Unsupported");
        }
    }

    void forceStopPackage(String packageName, int userId) throws RemoteException;
}
