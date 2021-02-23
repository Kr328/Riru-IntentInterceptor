package android.content.pm;

import android.annotation.TargetApi;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

@TargetApi(26)
public interface IOnPermissionsChangeListener {
    void onPermissionsChanged(int uid) throws RemoteException;

    abstract class Stub extends Binder implements IOnPermissionsChangeListener, IBinder {

    }
}
