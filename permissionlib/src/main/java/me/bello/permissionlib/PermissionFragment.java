package me.bello.permissionlib;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class PermissionFragment extends Fragment {

    PermissionHelper.Config config;
    PermissionHelper permissionHelper;

    public void setConfig(PermissionHelper.Config config, PermissionHelper permissionHelper) {
        this.config = config;
        this.permissionHelper = permissionHelper;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissionHelper != null && null != permissions && permissions.length>0) {
            permissionHelper.onPermissionResult(new PermissionResultEvent(requestCode, permissions, grantResults));
        }
    }

}
