package android.content;

import android.os.Handler;
import android.os.UserHandle;

import dev.rikka.tools.refine.RefineAs;

import utils.Utils;

@RefineAs(Context.class)
public class ContextHidden {
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
