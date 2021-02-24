package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import utils.Utils;

public interface IPackageManager extends IInterface {
    ParceledListSlice<PackageInfo> getPackagesHoldingPermissions(String[] permissions, int flags, int userId) throws RemoteException;
    PackageInfo getPackageInfo(String packageName, int flags, int userId) throws RemoteException;
    String[] getPackagesForUid(int uid) throws RemoteException;
    int checkUidPermission(String permName, int uid) throws RemoteException;

    abstract class Stub extends Binder implements IPackageManager {
        @Override
        public IBinder asBinder() {
            return Utils.throwStub();
        }

        public static IPackageManager asInterface(IBinder binder) {
            return Utils.throwStub();
        }
    }
}
