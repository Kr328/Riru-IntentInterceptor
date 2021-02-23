package android.permission;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IPermissionManager extends IInterface {
    void addOnPermissionsChangeListener(IOnPermissionsChangeListener listener) throws RemoteException;
    void removeOnPermissionsChangeListener(IOnPermissionsChangeListener listener) throws RemoteException;

    abstract class Stub extends Binder implements IPermissionManager, IBinder {
        public static IPermissionManager asInterface(IBinder service) {
            throw new IllegalArgumentException("Stub!");
        }
    }
}
