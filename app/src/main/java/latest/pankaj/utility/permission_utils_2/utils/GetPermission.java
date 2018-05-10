package latest.pankaj.utility.permission_utils_2.utils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.Settings;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import latest.pankaj.utility.permission_utils_2.utils.interfaces.PermissionDeniedDelegate;
import latest.pankaj.utility.permission_utils_2.utils.interfaces.PermissionRationalDelegate;
import latest.pankaj.utility.permission_utils_2.utils.interfaces.PermissionResultCallback;


public class GetPermission implements PermissionRationalDelegate, PermissionDeniedDelegate {
    Context context;
    ArrayList<PermissionRequest> permissionRequests = new ArrayList<>();
    int requestCode;

    public GetPermission(Context context, ArrayList<PermissionRequest> permissionRequests, int requestCode) {
        this.context = context;
        this.permissionRequests = permissionRequests;
        this.requestCode = requestCode;
    }

    public void enqueue(final PermissionResultCallback callback) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callback.onPermissionGranted(permissionRequests);
            return;
        }

        if (!GetPermissionUtils.hasPermissions(context, permissionRequests)) {

            PermissionRequestActivity.grantPermission(context, requestCode, permissionRequests, new ResultReceiver(new Handler(Looper.getMainLooper())) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    super.onReceiveResult(resultCode, resultData);

                    ArrayList<PermissionRequest> permissionRequests = (ArrayList<PermissionRequest>) resultData.getSerializable(PermissionConstant.Bundle.PERMISSIONS);
                    int[] grantResult = resultData.getIntArray(PermissionConstant.Bundle.PERMISSIONS_RESULT);
                    int requestCode = resultData.getInt(PermissionConstant.Bundle.REQUEST_CODE, 0);

                    ArrayList<PermissionRequest> grantedPermission = (ArrayList<PermissionRequest>) resultData.getSerializable(PermissionConstant.Bundle.GRANTED_PERMISSIONS);
                    ArrayList<PermissionRequest> deniedPermission = (ArrayList<PermissionRequest>) resultData.getSerializable(PermissionConstant.Bundle.DENIED_PERMISSIONS);
                    ArrayList<PermissionRequest> rationalPermission = (ArrayList<PermissionRequest>) resultData.getSerializable(PermissionConstant.Bundle.RATIONAL_PERMISSIONS);

                    if (!grantedPermission.isEmpty()) {
                        callback.onPermissionGranted(grantedPermission);
                    }

                    if (!deniedPermission.isEmpty()) {
                        callback.onPermissionDenied(deniedPermission, GetPermission.this);
                    }

                    if (!rationalPermission.isEmpty()) {
                        callback.onPermissionRationalShouldShow(rationalPermission, GetPermission.this);
                    }
                }
            });
        } else {
            callback.onPermissionGranted(permissionRequests);
        }
    }

    @Override
    public void requestPermission(PermissionRequest permissionRequest, int requestCode) {
        PermissionRequestActivity.grantPermissionAfterRational(context, requestCode, permissionRequest, new ResultReceiver(new Handler(Looper.getMainLooper())) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);

//                ArrayList<PermissionRequest> grantedPermission = (ArrayList<PermissionRequest>) resultData.getSerializable(PermissionConstant.Bundle.GRANTED_PERMISSIONS);
//                ArrayList<PermissionRequest> deniedPermission = (ArrayList<PermissionRequest>) resultData.getSerializable(PermissionConstant.Bundle.DENIED_PERMISSIONS);
//                ArrayList<PermissionRequest> rationalPermission = (ArrayList<PermissionRequest>) resultData.getSerializable(PermissionConstant.Bundle.RATIONAL_PERMISSIONS);
//
//                if (!grantedPermission.isEmpty()) {
//                    callback.onPermissionGranted(grantedPermission);
//                }
//
//                if (!deniedPermission.isEmpty()) {
//                    callback.onPermissionDenied(deniedPermission, GetPermission.this);
//                }
//
//                if (!rationalPermission.isEmpty()) {
//                    callback.onPermissionRationalShouldShow(rationalPermission, GetPermission.this);
//                }

            }
        });
    }


    @Override
    public void openSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    public static class Builder {
        Context context;
        ArrayList<PermissionRequest> permissionRequests = new ArrayList<>();
        int requestCode = 100;
//        String notificationTitle;
//        String notificationText;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setPermissions(String... permissions) {
            for (String permission : permissions) {
                permissionRequests.add(new PermissionRequest(permission));
            }
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

//        public Builder setNotificationTitle(String notificationTitle) {
//            this.notificationTitle = notificationTitle;
//            return this;
//        }
//
//        public Builder setNotificationText(String notificationText) {
//            this.notificationText = notificationText;
//            return this;
//        }

        public GetPermission build() {
            if (permissionRequests == null)
                throw new IllegalStateException("Permission array can't be null");

            if (requestCode == -1)
                throw new IllegalStateException("request code can't be null");

//            if (TextUtils.isEmpty(notificationTitle))
//                throw new IllegalStateException("notificationTitle can't be null or empty");
//
//            if (TextUtils.isEmpty(notificationText))
//                throw new IllegalStateException("notificationText can't be null or empty");

            return new GetPermission(context, permissionRequests, requestCode);
        }

        public void enqueue(PermissionResultCallback callback) {
            GetPermission getPermission = build();
            getPermission.enqueue(callback);
        }
    }
}