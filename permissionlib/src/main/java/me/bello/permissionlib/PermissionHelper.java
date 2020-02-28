package me.bello.permissionlib;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

/**
 * @Info 请求权限工具类
 * @Auth Bello
 * @Time 2020-02-28 11:17
 * @Ver
 */
public class PermissionHelper {
    public static final String PERMISSION_FRAGMENT = "PERMISSION_FRAGMENT";
    public static final int PERMISSION_REQUEST_CODE = 1001;
    Builder builder;
    int requestIndex = 0;

    private PermissionHelper(Builder builder) {
        this.builder = builder;
    }

    public static Builder with(AppCompatActivity activity) {
        return new Builder(activity);
    }

    static class Config {
        public String permission;
        public String requestTitle;
        public String requestMessage;

        public Config(String permission, String title, String message) {
            this.permission = permission;
            this.requestTitle = title;
            this.requestMessage = message;
        }

        public Config(String permission) {
            this.permission = permission;
        }
    }

    public interface IBuilder {
        PermissionHelper build();
    }


    public static class Builder implements IBuilder {
        AppCompatActivity activity;
        ArrayList<Config> configs;

        Callback callback;
        AllowCallback allowCallback;
        DenyCallback denyCallback;

        private Builder(AppCompatActivity activity) {
            this.activity = activity;
            this.configs = new ArrayList<>();
        }

        //  添加单个权限，带自定义弹窗提示
        public Builder permission(String permission, String title, String message) {
            this.configs.add(new Config(permission, title, message));
            return this;
        }

        // 同时申请多个权限，无自定义弹窗
        public Builder permission(String[] permissions) {
            for (String s : permissions) {
                this.configs.add(new Config(s));
            }
            return this;
        }


        public SingleBuilder onAllow(AllowCallback callback) {
            allowCallback = callback;
            return new SingleBuilder(this);
        }

        public SingleBuilder onDeny(DenyCallback callback) {
            denyCallback = callback;
            return new SingleBuilder(this);
        }

        public PermissionHelper build() {
            return new PermissionHelper(this);
        }

        void notifyOnAllow(String permission) {
            if (allowCallback != null) {
                allowCallback.onAllow(permission);
            } else if (callback != null) {
                callback.onAllow(permission);
            }
        }

        void notifyOnDeny(String permission) {
            if (denyCallback != null) {
                denyCallback.onDeny(permission);
            } else if (callback != null) {
                callback.onDeny(permission);
            }
        }

    }



    public static class SingleBuilder implements IBuilder {
        Builder builder;

        SingleBuilder(Builder builder) {
            this.builder = builder;
        }

        @Override
        public PermissionHelper build() {
            return builder.build();
        }

        public SingleBuilder onAllow(AllowCallback callback) {
            builder.allowCallback = callback;
            return this;
        }

        public SingleBuilder onDeny(DenyCallback callback) {
            builder.denyCallback = callback;
            return this;
        }
    }


    public static abstract class Callback implements AllowCallback, DenyCallback {
    }

    public interface AllowCallback {
        void onAllow(String permission);
    }

    public interface DenyCallback {
        void onDeny(String permission);
    }

    public void check() {
        checkNext();
    }

    void checkNext() {
        int current = requestIndex++;
        if (current >= builder.configs.size()) {
            // 检查完了
            return;
        }
        Config config = builder.configs.get(current);
        if (!hasPermission(config)) {
            // 判断是否自定义弹窗
            if (null != config.requestTitle) {
                showDialogTipUserRequestPermission(config);
            } else {
                startRequestPermission(config);
            }
        } else {
            builder.notifyOnAllow(config.permission);
            checkNext();
        }
    }

    private boolean hasPermission(Config config) {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(builder.activity, config.permission);
            // 权限是否已经 授权 GRANTED---授权  DENIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                return false;
            }
        }
        return true;
    }

    private void showDialogTipUserRequestPermission(final Config config) {
        FragmentManager fm = builder.activity.getSupportFragmentManager();
        Log.e("fragment", "size+> " + fm.getFragments().size());
        PermissionDialogFragment fragment = new PermissionDialogFragment();
        fragment.setConfig(builder, config, this);
        fm.beginTransaction().add(fragment, "dialog").commitNow();

    }


    // 开始提交请求权限
    void startRequestPermission(Config config) {
        FragmentManager fm = builder.activity.getSupportFragmentManager();
        PermissionFragment permissionFragment = (PermissionFragment) fm.findFragmentByTag(PERMISSION_FRAGMENT);
        if (permissionFragment == null) {
            permissionFragment = new PermissionFragment();
            permissionFragment.setConfig(config, this);
            fm.beginTransaction().add(permissionFragment, PERMISSION_FRAGMENT).commitNow();
            permissionFragment.requestPermissions(new String[]{config.permission}, PERMISSION_REQUEST_CODE);
        } else {
            permissionFragment.setConfig(config, this);
            permissionFragment.requestPermissions(new String[]{config.permission}, PERMISSION_REQUEST_CODE);
        }
    }


    void onPermissionResult(PermissionResultEvent event) {

        Config config = builder.configs.get(requestIndex - 1);
        if (config == null) {
            return;
        }
        if (event.requestCode == PERMISSION_REQUEST_CODE && event.permissions[0].equals(config.permission)) {
            // 权限是否已经 授权 GRANTED---授权  DENIED---拒绝
            if (event.results[0] != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(builder.activity, config.permission)) {
//                    Toast.makeText(builder.activity, "用户勾选了禁止询问", Toast.LENGTH_SHORT).show();
                    Log.e("PermissionHelp", "用户勾选了禁止询问");
                    // 跳转到应用设置界面手动开启权限
                    showDialogTipUserGoToAppSetting();
                } else {

                    if (mAlertDialog != null && mAlertDialog.isShowing()) {
                        mAlertDialog.dismiss();
                    }
                    builder.notifyOnDeny(config.permission);
                    checkNext();
                }
            } else {
                if (mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
//                Toast.makeText(builder.activity, "权限获取成功", Toast.LENGTH_SHORT).show();
                Log.e("PermissionHelp", "权限获取成功");
                builder.notifyOnAllow(config.permission);
                checkNext();

            }
        }
    }

    AlertDialog mAlertDialog;

    // 提示用户去应用设置界面手动开启权限
    private void showDialogTipUserGoToAppSetting() {

        FragmentManager fm = builder.activity.getSupportFragmentManager();

        PermissionTipsFragment fragment = (PermissionTipsFragment) fm.findFragmentByTag("tips");
        if (null == fragment) {
            fragment = new PermissionTipsFragment();
            fm.beginTransaction().add(fragment, "tips").commitNow();
        } else {

        }

    }


}
