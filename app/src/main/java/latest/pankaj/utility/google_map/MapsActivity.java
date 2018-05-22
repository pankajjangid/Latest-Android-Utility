package latest.pankaj.utility.google_map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import latest.pankaj.utility.R;
import latest.pankaj.utility.compaas_easy.Compass;
import latest.pankaj.utility.google_map.custom_winow_info.MapWrapperLayout;
import latest.pankaj.utility.google_map.custom_winow_info.OnInfoWindowElemTouchListener;
import latest.pankaj.utility.google_map.networking.APIClient;
import latest.pankaj.utility.google_map.networking.Example;
import latest.pankaj.utility.google_map.networking.RetrofitMaps;
import latest.pankaj.utility.google_map.pojo.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "MapsActivity";
    private static final String GOOGLE_KEY = "";
    double latitude;
    double longitude;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    boolean callNow = true;
    Button btnCompass;
    private GoogleMap mMap;
    private Context mContext;
    private Activity activity;
    // device sensor manager
    private int PROXIMITY_RADIUS = 500;
    //Custom infoview
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;
    //for map zooming
    //Initialize to a non-valid zoom value
    private float previousZoomLevel = 15f;
    private int rotatePoint;
    private boolean compaasEnabel = true;
    private Compass compass;
    private float currentAzimuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
        }

        initView();

        initSensors();
    }


    private void initView() {
        btnCompass = findViewById(R.id.btnCompass);

        btnCompass.setOnClickListener(this);
        mContext = this;
        activity = this;

        setupCompass();
    }

    private void initSensors() {
         boolean supportSensor;

        // initialize your android device sensor capabilities
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        Sensor sensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor sensorMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //check for sensor availability
        if (sensorAccelerometer != null && sensorMagneticField != null) {
            supportSensor = true;

            //code for apply permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
            //show error dialog if Google Play Services not available
            if (!MapUtils.CheckGooglePlayServices(activity)) {
                Log.d("onCreate", "Google Play Services not available. Ending Test case.");
                finish();
            } else {
                Log.d("onCreate", "Google Play Services available. Continuing.");
            }
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        } else {
            supportSensor = false;
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Support")
                    .setMessage("Device does not support magnetometer sensor")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    /*.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })*/
                    .show();
        }
    }

    private void initMap() {
        //Set Up GoogleApiClient
        buildGoogleApiClient();

        //Set All Map Settings
        setMapSettings();

        // Set Info Window That Apear After Marker Click
        setInfoWindow();

        // Set Dummy Marker In Case When Dont Want To Call Place Or Geocoding Api
        //  setDummyMarker();

        //mMap is an instance of GoogleMap


    }

    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                buildGoogleApiClient();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                initMap();
            }
        } else {
            initMap();
        }

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }



    @SuppressLint("MissingPermission")
    private void setMapSettings() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setBuildingsEnabled(true);

        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);


    }

    private void setDummyMarker() {

        // Let's add a couple of markers
        mMap.addMarker(new MarkerOptions()
                .title("Western Enterprises Global")
                .snippet("9/276, Malviya Nagar, Malviya Nagar, Jaipur")
                .position(new LatLng(26.8574129, 75.8157204)));

        mMap.addMarker(new MarkerOptions()
                .title("Weekender")
                .snippet("B 2/A Gaurav Tower, Malviya Nagar, Jaipur")
                .position(new LatLng(26.8571917, 75.8126972)));


    /*    mMap.addMarker(new MarkerOptions()
                .title("Paris")
                .snippet("France")
                .position(new LatLng(26.855, 75.815)));

        mMap.addMarker(new MarkerOptions()
                .title("London")
                .snippet("United Kingdom")
                .position(new LatLng(26.855, 75.815)));*/

    }

    private void setInfoWindow() {

        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        //final GoogleMap map = mapFragment.getMap();

        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20));

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        this.infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.info_widow_layout, null);
        this.infoTitle = (TextView) infoWindow.findViewById(R.id.nametv);
        this.infoSnippet = (TextView) infoWindow.findViewById(R.id.addressTv);
        this.infoButton = (Button) infoWindow.findViewById(R.id.clinicType);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
                getResources().getDrawable(R.drawable.round_but_green_sel), //btn_default_normal_holo_light
                getResources().getDrawable(R.drawable.round_but_red_sel)) //btn_default_pressed_holo_light
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                // Toast.makeText(MapsActivity.this, marker.getTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();


                String s = String.valueOf(marker.getTag());
                marker.setTag(s);
                String area = String.valueOf(marker.getTag());
//        String area = "JLT";
                String buildingName = String.valueOf(marker.getTitle());

            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);


        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info


                if (!marker.getTag().equals("CurrnetLocation")) {
                    infoTitle.setText(marker.getTitle());
                    infoSnippet.setText(marker.getSnippet());
                    infoButtonListener.setMarker(marker);
                    mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                    // mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                    return infoWindow;

                }


                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                return null;
            }
        });
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    private void setCurrentLocatonMarker() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }



    @Override
    protected void onResume() {

        compass.start();

        super.onResume();
    }


    @Override
    protected void onStart() {
        super.onStart();
        compass.stop();

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        compass.stop();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnCompass:
                if (compaasEnabel) {
                    compaasEnabel = false;
                    btnCompass.setText("Disable");
                } else {
                    btnCompass.setText("Enable");

                    compaasEnabel = true;
                }
                break;
        }
    }



    public void onZoomOut(View view) {

        if (previousZoomLevel >= 10) {
            previousZoomLevel--;
            updateCamera(rotatePoint);

        }
    }

    public void onZoomIn(View view) {
        if (previousZoomLevel < 25) {
            previousZoomLevel++;
            updateCamera(rotatePoint);


        }


    }

    public void updateCamera(double bearing) {

        try {
            if (mMap != null) {
                CameraPosition oldPos = mMap.getCameraPosition();

                CameraPosition currentPlace = new CameraPosition.Builder(oldPos)
                        .target(new LatLng(latitude, longitude))
                        .bearing((float) bearing)
                        .tilt(65.5f).zoom(previousZoomLevel).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
                mCurrLocationMarker.setRotation((float) bearing);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private void callGeoLocationAPI() {


        RetrofitMaps retrofitMaps = APIClient.getClient().create(RetrofitMaps.class);

        Call<Example> call = retrofitMaps.getGeoLocation(latitude + "," + longitude,GOOGLE_KEY, PROXIMITY_RADIUS,true);
        //   Call<Example> call = retrofitMaps.getNearbyPlaces(type, latitude + "," + longitude, PROXIMITY_RADIUS);

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                try {
                    mMap.clear();
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < response.body().getResults().size(); i++) {
                        Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                        Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
                        String placeName = response.body().getResults().get(i).getName();
                        String vicinity = response.body().getResults().get(i).getVicinity();
                        LatLng latLng = new LatLng(lat, lng);

                        mMap.addMarker(new MarkerOptions()
                                .title(placeName)
                                .snippet(vicinity)
                                .position(latLng));
                    }

                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.d("onFailure", t.toString());

            }
        });


    }

    private void setupCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = new Compass.CompassListener() {

            @Override
            public void onNewAzimuth(float azimuth) {
                // adjustArrow(azimuth);

                if (compaasEnabel) {
                    currentAzimuth = azimuth;
                    updateCamera(azimuth);


                }
            }
        };
        compass.setListener(cl);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }


        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        Log.d("onLocationChanged", "entered");
        //    temp = makeCall("https://maps.googleapis.com/maps/api/place/search/json?location=" + location.getLatitude() + "," + location.getLongitude() + "&radius=500&sensor=true&key=" + GOOGLE_KEY);


        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        if (callNow){
            callNow=false;
            callGeoLocationAPI();
        }



        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        setCurrentLocatonMarker();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.flat(true);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.arrows));
        markerOptions.title("Current Position");

        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mCurrLocationMarker.setTag("CurrnetLocation");

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", latitude, longitude));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 40);
        mMap.animateCamera(cameraUpdate);

    }
}

