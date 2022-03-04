package android.app;

import dev.rikka.tools.refine.RefineAs;
import utils.Utils;

@RefineAs(ActivityManager.class)
public class ActivityManagerHidden {
    public void forceStopPackageAsUser(String packageName, int userId) {
        Utils.throwStub();
    }
}
