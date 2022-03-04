package android.os;

import dev.rikka.tools.refine.RefineAs;
import utils.Utils;

@RefineAs(UserHandle.class)
public class UserHandleHidden {
    public static UserHandle ALL = Utils.throwStub();

    public static int getUserId(int uid) {
        return Utils.throwStub();
    }

    public static UserHandle of(int userId) {
        return Utils.throwStub();
    }
}

