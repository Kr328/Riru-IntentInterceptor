package android.content.pm;

import android.content.Intent;

import java.util.List;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(PackageManager.class)
public abstract class PackageManagerHidden {
    public interface OnPermissionsChangedListener {
        void onPermissionsChanged(int uid);
    }

    public abstract void addOnPermissionsChangeListener(OnPermissionsChangedListener listener);
    public abstract void removeOnPermissionsChangeListener(OnPermissionsChangedListener listener);
    public abstract List<ResolveInfo> queryIntentContentProvidersAsUser(Intent intent, int flags, int userId);
    public abstract int getPackageUidAsUser(String packageName, int userId) throws PackageManager.NameNotFoundException;
    public abstract PackageInfo getPackageInfoAsUser(String packageName, int flags, int userId) throws PackageManager.NameNotFoundException;
}
