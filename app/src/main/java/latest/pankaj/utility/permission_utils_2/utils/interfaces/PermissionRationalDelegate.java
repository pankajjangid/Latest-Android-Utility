package latest.pankaj.utility.permission_utils_2.utils.interfaces;


import latest.pankaj.utility.permission_utils_2.utils.PermissionRequest;

public interface PermissionRationalDelegate {
    void requestPermission(PermissionRequest permissionRequest, int requestCode);
}