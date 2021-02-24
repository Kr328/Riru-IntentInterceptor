package $android.content.pm;

public abstract class PackageManager {
    public abstract void addOnPermissionsChangeListener(OnPermissionsChangedListener listener);

    public abstract void removeOnPermissionsChangeListener(OnPermissionsChangedListener listener);

    public interface OnPermissionsChangedListener {
        void onPermissionsChanged(int uid);
    }
}
