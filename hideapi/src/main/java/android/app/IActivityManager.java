package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;

import utils.Utils;

public interface IActivityManager extends IInterface {
    abstract class Stub extends Binder implements IActivityManager {
        public static IActivityManager asInterface(IBinder binder) {
            return Utils.throwStub();
        }

        @Override
        public IBinder asBinder() {
            return Utils.throwStub();
        }
    }
}
