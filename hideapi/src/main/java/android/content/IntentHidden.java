package android.content;

import dev.rikka.tools.refine.RefineAs;
import utils.Utils;

@RefineAs(Intent.class)
public class IntentHidden {
    public static String ACTION_USER_ADDED = Utils.throwStub();
    public static String ACTION_USER_REMOVED = Utils.throwStub();
    public static String EXTRA_USER_HANDLE = Utils.throwStub();
}
