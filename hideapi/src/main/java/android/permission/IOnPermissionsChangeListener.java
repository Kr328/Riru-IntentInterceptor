package android.permission;

import android.annotation.TargetApi;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

@TargetApi(30)
public interface IOnPermissionsChangeListener extends IInterface {
    void onPermissionsChanged(int uid) throws RemoteException;

    abstract class Stub extends Binder implements android.content.pm.IOnPermissionsChangeListener, IBinder {

    }
}

