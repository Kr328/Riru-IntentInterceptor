package $android.content;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.UserHandle;

import utils.Utils;

public class Context {
    public Intent registerReceiverAsUser(
            BroadcastReceiver receiver,
            UserHandle user,
            IntentFilter filter,
            String broadcastPermission,
            Handler scheduler
    ) {
        return Utils.throwStub();
    }
}
