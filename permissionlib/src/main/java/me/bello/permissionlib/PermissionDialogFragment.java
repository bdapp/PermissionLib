package me.bello.permissionlib;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * @Info 提示对话框
 * @Auth Bello
 * @Time 2020-02-28 11:17
 * @Ver
 */
public class PermissionDialogFragment extends DialogFragment {
    PermissionHelper.Builder builder;
    PermissionHelper.Config config;
    PermissionHelper permissionHelper;

    public void setConfig(PermissionHelper.Builder builder, PermissionHelper.Config config, PermissionHelper permissionHelper) {
        this.builder = builder;
        this.config = config;
        this.permissionHelper = permissionHelper;

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        setCancelable(false);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(config.requestTitle)
                .setMessage(config.requestMessage)
                .setCancelable(false)
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                        permissionHelper.startRequestPermission(config);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                        Log.e("permission", "用户取消授权");
                        builder.notifyOnDeny(config.permission);
                        permissionHelper.checkNext();
                    }
                }).create();

        return dialog;
    }

}
