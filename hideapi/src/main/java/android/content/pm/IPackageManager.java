package android.content.pm;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IPackageManager extends IInterface {
    ParceledListSlice<PackageInfo> getInstalledPackages(int flags, int userId) throws RemoteException;
    PackageInfo getPackageInfo(String packageName, int flags, int userId) throws RemoteException;
    ParceledListSlice<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags, int userId) throws RemoteException;
    String[] getPackagesForUid(int uid) throws RemoteException;
    int getPackageUid(String packageName, int flags, int userId) throws RemoteException;
    int checkUidPermission(String permName, int uid) throws RemoteException;
    void addOnPermissionsChangeListener(IOnPermissionsChangeListener listener) throws RemoteException;
    void removeOnPermissionsChangeListener(IOnPermissionsChangeListener listener) throws RemoteException;

    abstract class Stub extends Binder implements IPackageManager {
        @Override
        public IBinder asBinder() {
            throw new IllegalArgumentException("Stub!");
        }

        public static IPackageManager asInterface(IBinder binder) {
            throw new IllegalArgumentException("Stub!");
        }
    }
}
