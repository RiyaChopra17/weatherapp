package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;

import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class WeatherActivity extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    Context mcontext;
    NetworkDetector networkDetector;
    private Boolean mRequestingLocationUpdates;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    TextView locationText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        mcontext=this;
        networkDetector = new NetworkDetector(mcontext);
        inits();
        currentLocation();
        checkRuntimePermission();




    }

    private void inits() {

        locationText=(TextView)findViewById(R.id.locationText);
        /*cityTxt=(TextView)findViewById(R.id.countryCodeTxt);
        search_dialog=(RelativeLayout)findViewById(R.id.search_dialog);
        search_country=(EditText)findViewById(R.id.search_country);
        listView=(ListView) findViewById(R.id.listview);
        cityTxt.setOnClickListener(this);*/
    }

    public void currentLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                Log.i("============check","=======check");
                mCurrentLocation = locationResult.getLastLocation();
                //updateLocationUI();
            }
        };

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();



    }
    private void startLocationUpdates() {

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        Log.e("kldjd","xjj");

                        currentLocation();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(WeatherActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("testing", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("testing", errorMessage);

                                Toast.makeText(WeatherActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }
    private void updateLocationUI() {
//        dialog.dismiss();

        if(mCurrentLocation!=null) {


            Log.i("latLong","Lat: " + mCurrentLocation.getLatitude() + ", " +
                    "Lng: " + mCurrentLocation.getLongitude()

            );


        }

        // toggleButtons();
    }






    private void checkRuntimePermission() {
        Log.i("printl========", "oooooooooo");
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.e("check=====","check1");
                        startLocationUpdates();

                        // permission is granted, open the camera
                       // mRequestingLocationUpdates = true;

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        showSettingsDialog();
                        /*if (response.isPermanentlyDenied()) {
                            // navigate user to app settings
                            showSettingsDialog();
                        }*/

                       /* new  UserPreferences(mcontext).setLatitude("28.6139");
                        new UserPreferences(mcontext).setLongitude("77.2090");
                        new UserPreferences(mcontext).setLastlocation("New Delhi");
                        locactiontxt.setText("New Delhi");
                        callApi();*/

                        //   customCityDialog("firsttime", false, null);
                        //Toast.makeText(getApplicationContext(),"app======",Toast.LENGTH_SHORT).show();

                    }



                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                       token.continuePermissionRequest();
                        Log.e("check=====","check1");
/*
                        if(dialog!=null && dialog.isShowing()){
                            dialog.dismiss();
                            dialog = null;
                        }

                        new  UserPreferences(mcontext).setLatitude("28.6139");
                        new UserPreferences(mcontext).setLongitude("77.2090");
                        new UserPreferences(mcontext).setLastlocation("New Delhi");
                       *//* mCurrentLocation.setLatitude(28.6139);
                        mCurrentLocation.setLatitude(77.2090);*//*
                        locactiontxt.setText("New Delhi");
                        callApi();*/


                        //Toast.makeText(getApplicationContext(),"tsting======",Toast.LENGTH_SHORT).show();
                        //    customCityDialog("s", false, null);

                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        /*if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
            dialog = null;

        }*/
    }




    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }




}