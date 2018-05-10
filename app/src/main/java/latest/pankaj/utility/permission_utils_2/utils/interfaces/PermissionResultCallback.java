package latest.pankaj.utility.permission_utils_2.utils.interfaces;

import java.util.ArrayList;

import latest.pankaj.utility.permission_utils_2.utils.PermissionRequest;

public interface PermissionResultCallback {
    void onPermissionGranted(ArrayList<PermissionRequest> grantedPermission);

    void onPermissionDenied(ArrayList<PermissionRequest> deniedPermission, PermissionDeniedDelegate permissionDeniedDelegate);

    void onPermissionRationalShouldShow(ArrayList<PermissionRequest> rationalPermission,PermissionRationalDelegate permissionRationalDelegate);
}