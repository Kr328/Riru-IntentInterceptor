package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;

import utils.Utils;

public interface IActivityTaskManager {
    abstract class Stub extends Binder implements IActivityTaskManager, IInterface {
        public static IActivityTaskManager asInterface(IBinder binder) {
            return Utils.throwStub();
        }
    }
}
