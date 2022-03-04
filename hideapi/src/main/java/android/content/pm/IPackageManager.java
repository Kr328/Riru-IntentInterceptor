package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import utils.Utils;

public interface IPackageManager extends IInterface {
    ParceledListSlice<PackageInfo> getPackagesHoldingPermissions(
            String[] permissions,
            int flags,
            int userId
    ) throws RemoteException;

    PackageInfo getPackageInfo(
            String packageName,
            int flags,
            int userId
    ) throws RemoteException;

    int checkUidPermission(
            String permName,
            int uid
    ) throws RemoteException;

    int checkPermission(
            String permName,
            String pkgName,
            int userId
    ) throws RemoteException;

    abstract class Stub extends Binder implements IPackageManager {
        public static IPackageManager asInterface(IBinder binder) {
            return Utils.throwStub();
        }

        @Override
        public IBinder asBinder() {
            return Utils.throwStub();
        }
    }
}
