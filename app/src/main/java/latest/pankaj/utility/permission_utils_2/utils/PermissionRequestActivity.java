package latest.pankaj.utility.permission_utils_2.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.reflect.Array;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by saurabh on 14/3/18.
 */

public class PermissionRequestActivity extends Activity {

    /**
     * Case 1: User doesn't have permission
     * Case 2: User has permission
     * <p>
     * Case 3: User has never seen the permission Dialog
     * Case 4: User has denied permission once but he din't clicked on "Never Show again" check box
     * Case 5: User denied the permission and also clicked on the "Never Show again" check box.
     * Case 6: User has allowed the permission
     */

    public static void grantPermission(Context context, int requestCode, ArrayList<PermissionRequest> permissionRequests, ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, PermissionRequestActivity.class);
        intent.putExtra(PermissionConstant.Bundle.PERMISSIONS, permissionRequests);
        intent.putExtra(PermissionConstant.Bundle.REQUEST_CODE, requestCode);
        intent.putExtra(PermissionConstant.Bundle.RESULT_RECEIVER, resultReceiver);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        context.startActivity(intent);
    }

    public static void grantPermissionAfterRational(Context context, int requestCode, PermissionRequest permissionRequest, ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, PermissionRequestActivity.class);
        intent.putExtra(PermissionConstant.Bundle.PERMISSION, permissionRequest);
        intent.putExtra(PermissionConstant.Bundle.REQUEST_CODE, requestCode);
        intent.putExtra(PermissionConstant.Bundle.RESULT_RECEIVER, resultReceiver);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        context.startActivity(intent);
    }

    private ArrayList<PermissionRequest> grantedPermission = new ArrayList<>();
    private ArrayList<PermissionRequest> deniedPermission = new ArrayList<>();
    private ArrayList<PermissionRequest> rationalPermission = new ArrayList<>();

    private ResultReceiver resultReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {

            resultReceiver = getIntent().getParcelableExtra(PermissionConstant.Bundle.RESULT_RECEIVER);

            int requestCode = getIntent().getIntExtra(PermissionConstant.Bundle.REQUEST_CODE, 0);

            if (getIntent().hasExtra(PermissionConstant.Bundle.PERMISSIONS)) {
                //Asking for multiple permission mostly
                ArrayList<PermissionRequest> permissionRequests = (ArrayList<PermissionRequest>) getIntent().getSerializableExtra(PermissionConstant.Bundle.PERMISSIONS);


                for (PermissionRequest permission : permissionRequests) {

                    if (ActivityCompat.checkSelfPermission(this, permission.getPermission()) == PackageManager.PERMISSION_GRANTED) {
                        // This is Case 2. You have permission now you can do anything related to it
                        grantedPermission.add(permission);
                    } else if (ActivityCompat.checkSelfPermission(this, permission.getPermission()) != PackageManager.PERMISSION_GRANTED) {
                        // This is Case 1. Now we need to check further if permission was shown before or not
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission.getPermission())) {
                            // This is Case 4.
                            rationalPermission.add(permission);
                        } else {
                            // This is Case 3.
                            deniedPermission.add(permission);
                        }
                    }

                }

                if (deniedPermission.size() > 0) {
                    String[] permissionArray = new String[deniedPermission.size()];
                    for (int i = 0; i < deniedPermission.size(); i++) {
                        permissionArray[i] = deniedPermission.get(i).getPermission();
                    }
                    ActivityCompat.requestPermissions(this, permissionArray, requestCode);
                } else {
                    finish();
                    onComplete(requestCode, permissionRequests, new int[]{PackageManager.PERMISSION_GRANTED});
                }

            } else {
                //Asking for single permission mostly after ration dialog
                PermissionRequest permissionRequest = (PermissionRequest) getIntent().getSerializableExtra(PermissionConstant.Bundle.PERMISSION);
                deniedPermission.add(permissionRequest);
                ActivityCompat.requestPermissions(this, new String[]{permissionRequest.getPermission()}, requestCode);
            }

        } else {
            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        ArrayList<PermissionRequest> tempDeniedList =  new ArrayList<>();

        for (int i = 0; grantResults.length > i; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                // This is Case 2 (Permission is now granted) and Case 6
                //Remove the permission from denied list and add it to granted list
                grantedPermission.add(deniedPermission.get(i));
            } else {
                // This is Case 1 again as Permission is not granted by user

                //Now further we check if used denied permanently or not
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, deniedPermission.get(i).getPermission())) {
                    // case 4 User has denied permission but not permanently
                    rationalPermission.add(deniedPermission.get(i));
                } else {
                    // case 5. Permission denied permanently.
                    // You can open Permission setting's page from here now.
                    // let me permission be in denied list and ask user if they want to open setting dialog
                    deniedPermission.get(i).setPermanentlyDenied(true);
                    tempDeniedList.add(deniedPermission.get(i));
                }
            }
        }

        deniedPermission.clear();
        deniedPermission.addAll(tempDeniedList);

        ArrayList<PermissionRequest> permissionRequests = new ArrayList<>();
        for (String permission : permissions) {
            permissionRequests.add(new PermissionRequest(permission));
        }

        onComplete(requestCode, permissionRequests, grantResults);
    }

    private void onComplete(int requestCode, ArrayList<PermissionRequest> permissionRequests, int[] grantResults) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(PermissionConstant.Bundle.PERMISSIONS, permissionRequests);
        bundle.putIntArray(PermissionConstant.Bundle.PERMISSIONS_RESULT, grantResults);
        bundle.putInt(PermissionConstant.Bundle.REQUEST_CODE, requestCode);

        bundle.putSerializable(PermissionConstant.Bundle.GRANTED_PERMISSIONS, grantedPermission);
        bundle.putSerializable(PermissionConstant.Bundle.DENIED_PERMISSIONS, deniedPermission);
        bundle.putSerializable(PermissionConstant.Bundle.RATIONAL_PERMISSIONS, rationalPermission);

        resultReceiver.send(requestCode, bundle);
        finish();
    }
}