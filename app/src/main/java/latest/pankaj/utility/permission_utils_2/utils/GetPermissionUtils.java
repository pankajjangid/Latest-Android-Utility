package latest.pankaj.utility.permission_utils_2.utils;



import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;


public class GetPermissionUtils {

//    public static boolean checkPermission(final Activity context, final int requestCode, final String... permissions) {
//        int currentAPIVersion = Build.VERSION.SDK_INT;
//        if (currentAPIVersion >= Build.VERSION_CODES.M) {
//            if (!hasPermissions(context, permissions)) {
//                ActivityCompat.requestPermissions(context, permissions, requestCode);
//                return false;
//            } else {
//                return true;
//            }
//        } else {
//            return true;
//        }
//    }


    public static boolean hasPermissions(Context context, ArrayList<PermissionRequest> permissionRequests ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissionRequests != null) {
            for (PermissionRequest permissionRequest : permissionRequests) {
                if (ActivityCompat.checkSelfPermission(context, permissionRequest.getPermission()) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}