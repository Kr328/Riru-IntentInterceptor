package $android.content.pm;

import android.content.pm.PackageManager$OnPermissionsChangedListener;

public abstract class PackageManager {
    public abstract void addOnPermissionsChangeListener(PackageManager$OnPermissionsChangedListener listener);

    public abstract void removeOnPermissionsChangeListener(PackageManager$OnPermissionsChangedListener listener);
}
