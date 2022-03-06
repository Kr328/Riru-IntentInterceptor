package android.content;

import android.net.Uri;

import dev.rikka.tools.refine.RefineAs;
import utils.Utils;

@RefineAs(ContentProvider.class)
public class ContentProviderHidden {
    public static Uri maybeAddUserId(Uri uri, int userId) {
        return Utils.throwStub();
    }
}
