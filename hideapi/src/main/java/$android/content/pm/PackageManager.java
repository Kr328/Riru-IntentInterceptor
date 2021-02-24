package $android.content.pm;

public abstract class PackageManager {
    public interface OnPermissionsChangedListener {
        void onPermissionsChanged(int uid);
    }

    public abstract void addOnPermissionsChangeListener(OnPermissionsChangedListener listener);
    public abstract void removeOnPermissionsChangeListener(OnPermissionsChangedListener listener);
}
