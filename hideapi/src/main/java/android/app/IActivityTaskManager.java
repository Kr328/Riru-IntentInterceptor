package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;

import utils.Utils;

public interface IActivityTaskManager extends IInterface {
    abstract class Stub extends Binder implements IActivityTaskManager {
        public static IActivityTaskManager asInterface(IBinder binder) {
            return Utils.throwStub();
        }
    }
}
