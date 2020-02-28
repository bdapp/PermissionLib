package me.bello.testrequestpermission;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import me.bello.permissionlib.PermissionHelper;

public class MainActivity extends AppCompatActivity {

    final String[] perArr = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 获取权限
                PermissionHelper.with(MainActivity.this)
                        .permission(perArr)
                        .onAllow(new PermissionHelper.AllowCallback() {
                            @Override
                            public void onAllow(String permission) {

                            }
                        })
                        .onDeny(new PermissionHelper.DenyCallback() {
                            @Override
                            public void onDeny(String permission) {

                            }
                        })
                        .build().check();

            }
        });
    }


}
