# PermissionLib
自定义的android权限申请库


```
# 写法一：

String[] perArr = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION};
            
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

```

```
# 写法二：

// 获取权限
PermissionHelper.with(MainActivity.this)
                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "自定义提示标题", "自定义提示内容")
                        .permission(Manifest.permission.ACCESS_COARSE_LOCATION, "自定义提示标题", "自定义提示内容")
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
                        
```
