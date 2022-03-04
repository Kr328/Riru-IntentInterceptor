package android.os;

import android.content.pm.UserInfo;

import java.util.List;

import dev.rikka.tools.refine.RefineAs;
import utils.Utils;

@RefineAs(UserManager.class)
public class UserManagerHidden {
    public List<UserInfo> getUsers() {
        return Utils.throwStub();
    }
}
