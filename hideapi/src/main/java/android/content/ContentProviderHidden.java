package android.content;

import android.net.Uri;
import android.os.UserHandle;

import dev.rikka.tools.refine.RefineAs;
import utils.Utils;

@RefineAs(ContentProvider.class)
public class ContentProviderHidden {
    public static Uri maybeAddUserId(Uri uri, int userId) {
        return Utils.throwStub();
    }
    public static Uri createContentUriForUser(Uri contentUri, UserHandle userHandle) {
        return Utils.throwStub();
    }
}
