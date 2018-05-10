package latest.pankaj.utility.permission_utils_2.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import latest.pankaj.utility.R;
import latest.pankaj.utility.permission_utils_2.utils.interfaces.PermissionDeniedDelegate;
import latest.pankaj.utility.permission_utils_2.utils.interfaces.PermissionRationalDelegate;
import latest.pankaj.utility.permission_utils_2.utils.interfaces.PermissionResultCallback;

public class PermissionExample2Activity extends AppCompatActivity {
    Context context;

    Button locationBtn, contactBtn, audioBtn, bothBtn;
    TextView locationTv, contactTv, audioTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_example2);

        context = this;

        contactBtn = (Button) findViewById(R.id.contactBtn);
        locationBtn = (Button) findViewById(R.id.locationBtn);
        audioBtn = (Button) findViewById(R.id.audioBtn);
        bothBtn = (Button) findViewById(R.id.bothBtn);

        contactTv = (TextView) findViewById(R.id.contactTv);
        locationTv = (TextView) findViewById(R.id.locationTv);
        audioTv = (TextView) findViewById(R.id.audioTv);


        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetPermission.Builder(context).setPermissions(Manifest.permission.READ_CONTACTS).enqueue(new PermissionResultCallback() {
                    @Override
                    public void onPermissionGranted(ArrayList<PermissionRequest> grantedPermission) {
                        PermissionRequest permissionRequest = grantedPermission.get(0);
                        Log.d("Get Permission", "Granted - " + permissionRequest.getPermission());

                        contactTv.setText("Granted");
                        contactTv.setTextColor(Color.GREEN);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<PermissionRequest> deniedPermission, PermissionDeniedDelegate permissionDeniedDelegate) {
                        PermissionRequest permissionRequest = deniedPermission.get(0);
                        Log.d("Get Permission", "Denied - " + permissionRequest.getPermission());

                        contactTv.setText("Denied");
                        contactTv.setTextColor(Color.RED);
                    }

                    @Override
                    public void onPermissionRationalShouldShow(final ArrayList<PermissionRequest> rationalPermission, final PermissionRationalDelegate permissionRationalDelegate) {
                        PermissionRequest permissionRequest = rationalPermission.get(0);
                        Log.d("Get Permission", "Rational - " + permissionRequest.getPermission());

                        contactTv.setText("Rational");
                        contactTv.setTextColor(Color.RED);
                    }
                });
            }
        });
        bothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetPermission.Builder(context).setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO).enqueue(new PermissionResultCallback() {
                    @Override
                    public void onPermissionGranted(ArrayList<PermissionRequest> grantedPermission) {
                        for (PermissionRequest permissionRequest : grantedPermission) {
                            Log.d("Get Permission", "Granted - " + permissionRequest.getPermission());
                            if (permissionRequest.getPermission().equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                locationTv.setText("Granted");
                                locationTv.setTextColor(Color.GREEN);
                            }

                            if (permissionRequest.getPermission().equalsIgnoreCase(Manifest.permission.RECORD_AUDIO)) {
                                audioTv.setText("Granted");
                                audioTv.setTextColor(Color.GREEN);
                            }
                        }
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<PermissionRequest> deniedPermission, final PermissionDeniedDelegate permissionDeniedDelegate) {
                        for (final PermissionRequest permissionRequest : deniedPermission) {
                            Log.d("Get Permission", "Denied - " + permissionRequest.getPermission());

                            if (permissionRequest.getPermission().equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                locationTv.setText("Denied");
                                locationTv.setTextColor(Color.RED);
                            }

                            if (permissionRequest.getPermission().equalsIgnoreCase(Manifest.permission.RECORD_AUDIO)) {
                                audioTv.setText("Denied");
                                audioTv.setTextColor(Color.RED);
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationalShouldShow(final ArrayList<PermissionRequest> rationalPermission, final PermissionRationalDelegate permissionRationalDelegate) {
                        for (final PermissionRequest permissionRequest : rationalPermission) {
                            Log.d("Get Permission", "Rational - " + permissionRequest.getPermission());

                            if (permissionRequest.getPermission().equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                locationTv.setText("Rational");
                                locationTv.setTextColor(Color.RED);
                            }

                            if (permissionRequest.getPermission().equalsIgnoreCase(Manifest.permission.RECORD_AUDIO)) {
                                audioTv.setText("Rational");
                                audioTv.setTextColor(Color.RED);
                            }
                        }
                    }
                });
            }
        });


        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetPermission.Builder(context).setPermissions(Manifest.permission.ACCESS_FINE_LOCATION).enqueue(new PermissionResultCallback() {
                    @Override
                    public void onPermissionGranted(ArrayList<PermissionRequest> grantedPermission) {
                        for (PermissionRequest permissionRequest : grantedPermission) {
                            Log.d("Get Permission", "Granted - " + permissionRequest.getPermission());
                            if (permissionRequest.getPermission().equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                locationTv.setText("Granted");
                                locationTv.setTextColor(Color.GREEN);
                            }

                            if (permissionRequest.getPermission().equalsIgnoreCase(Manifest.permission.RECORD_AUDIO)) {
                                audioTv.setText("Granted");
                                audioTv.setTextColor(Color.GREEN);
                            }
                        }
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<PermissionRequest> deniedPermission, final PermissionDeniedDelegate permissionDeniedDelegate) {
                        for (final PermissionRequest permissionRequest : deniedPermission) {
                            Log.d("Get Permission", "Denied - " + permissionRequest.getPermission());

                            if (permissionRequest.getPermission().equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                AlertDialog alertDialog = new AlertDialog.Builder(context)
                                        .setTitle("Need Location Permission")
                                        .setMessage("Waring Message to Show user")
                                        .setCancelable(true)
                                        .setPositiveButton("Open Setting", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                if (permissionRequest.isPermanentlyDenied())
                                                    permissionDeniedDelegate.openSetting();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        })
                                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialogInterface) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .show();
                            }

                            if (permissionRequest.getPermission().equalsIgnoreCase(Manifest.permission.RECORD_AUDIO)) {
                                audioTv.setText("Denied");
                                audioTv.setTextColor(Color.RED);
                            } else {

                            }
                        }
                    }

                    @Override
                    public void onPermissionRationalShouldShow(final ArrayList<PermissionRequest> rationalPermission, final PermissionRationalDelegate permissionRationalDelegate) {
                        for (final PermissionRequest permissionRequest : rationalPermission) {
                            Log.d("Get Permission", "Rational - " + permissionRequest.getPermission());

                            if (permissionRequest.getPermission().equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {

                                AlertDialog alertDialog = new AlertDialog.Builder(context)
                                        .setTitle("Need Location Permission")
                                        .setMessage("Rational Message to Show user")
                                        .setCancelable(true)
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                permissionRationalDelegate.requestPermission(permissionRequest, 110);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        })
                                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialogInterface) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .show();
                            }

                            if (permissionRequest.getPermission().equalsIgnoreCase(Manifest.permission.RECORD_AUDIO)) {
                                audioTv.setText("Rational");
                                audioTv.setTextColor(Color.RED);
                            } else {

                            }


                        }
                    }
                });
            }
        });
    }
}
