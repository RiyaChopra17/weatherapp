package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {
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
    TextView locationText,temperatureText,humidityText,windDirectionText,pressureText,realFeelLabel;
    List<MyCity> list_arr=new ArrayList<>();
    MyAdpater myAdpater;
    ListView listView;
    EditText search_city;
    ImageView weatherIndicatorImage;
    RelativeLayout search_dialog;

    public static String BaseUrl = "http://api.openweathermap.org/";

    public static String AppId = "133a3b03ac4483d45ab7eec88090f6b1";
    public  String lat = "28.6139";
    public  String lon = "77.2090";
    public String city="Delhi";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        mcontext=this;
        networkDetector = new NetworkDetector(mcontext);
        inits();
        //currentLocation();
       // checkRuntimePermission();


        if (networkDetector.isConnectingToInternet()) {
            getApiData();
        } else {
            DialogService.customDialog(mcontext, getResources().getString(R.string.internet_connection), false, null);

        }

    }

    private void inits() {

        locationText=(TextView)findViewById(R.id.locationText);
        temperatureText=(TextView)findViewById(R.id.temperatureText);
        weatherIndicatorImage= (ImageView) findViewById(R.id.weatherIndicatorImage);
        humidityText=(TextView)findViewById(R.id.humidityText);
        pressureText=(TextView)findViewById(R.id.pressureText);
        windDirectionText=(TextView)findViewById(R.id.windDirectionText);
        realFeelLabel=(TextView)findViewById(R.id.realFeelLabel);
        search_dialog=(RelativeLayout)findViewById(R.id.search_dialog);
        search_city=(EditText)findViewById(R.id.search_city);
        listView=(ListView) findViewById(R.id.listview);
        locationText.setOnClickListener(this);
        search_city.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    String text = search_city.getText().toString().toLowerCase(Locale.getDefault());
                    myAdpater.filter(text);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // et_search.getFilter().filter(s.toString());
            }
        });
        try {
            JSONArray cities = new JSONArray(getJsonFromRaw(this, R.raw.city));

            list_arr.clear();
            for (int i = 0; i < cities.length(); i++) {
                try {

                    MyCity myCity=new MyCity();
                    JSONObject city = (JSONObject) cities.get(i);
                    myCity.setName(city.getString("name"));
                    myCity.setState(city.getString("state"));
                    myCity.setLat(city.getString("lat"));
                    myCity.setLon(city.getString("lon"));
                   /* myCity.setImageflag(this.getResources().getIdentifier("country_" +
                            country.getString("iso2").toLowerCase(), "drawable", this.getPackageName()));
*/
                    list_arr.add(myCity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setDataInAdpater();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    void getApiData() {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(lat, lon, AppId);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    progress.dismiss();
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    double a=weatherResponse.main.temp_min;
                    Log.e("============text",response.raw().toString());
                    Log.e("============text",(weatherResponse.main.temp_min)+"\u2109");
                    Log.e("============text",(weatherResponse.main.temp_min)+"\u2103");

                    float celsius = (weatherResponse.main.temp_min) - 273.15F;
                    String value=String.format("%.0f", celsius);

                    temperatureText.setText(value+"\u00B0");
                    humidityText.setText(weatherResponse.main.humidity+"%");
                    pressureText.setText(weatherResponse.main.pressure+"hpa");
                    realFeelLabel.setText(weatherResponse.weather.get(0).description+"");
                    windDirectionText.setText(weatherResponse.wind.speed+"km/h");
                    locationText.setText(city);

                    Log.e("============text",celsius+"\u2103");
                    //  temperatureValue.setText((result) + " \u2109");
                    String stringBuilder = "Country: " +
                            weatherResponse.sys.country +
                            "\n" +
                            "Temperature: " +
                            weatherResponse.main.temp +
                            "\n" +
                            "Temperature(Min): " +
                            weatherResponse.main.temp_min +
                            "\n" +
                            "Temperature(Max): " +
                            weatherResponse.main.temp_max +
                            "\n" +
                            "Humidity: " +
                            weatherResponse.main.humidity +
                            "\n" +
                            "Pressure: " +
                            weatherResponse.main.pressure;
                    String icon = weatherResponse.weather.get(0).icon;
                    weatherIndicatorImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_50n));
                  //  realFeelLabel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_50n, 0, 0, 0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
              //  weatherData.setText(t.getMessage());
                progress.dismiss();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

                case R.id.locationText:
                InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(locationText.getApplicationWindowToken(), 0);
                //setDataInAdpater();
                myAdpater.filter("");
                myAdpater.notifyDataSetChanged();


                runOnUiThread(new Runnable() {
                    public void run() {
                        // UI code goes here
                        listView.setSelection(0);
                    }
                });
                search_dialog.setVisibility(View.VISIBLE);

                break;
        }

    }

    private void setDataInAdpater() {
        myAdpater=new MyAdpater(this,list_arr);
        listView.setAdapter(myAdpater);
        listView.setTextFilterEnabled(true);

        myAdpater.onCallBackReturn(new MyAdpater.Callback() {
            @Override
            public void clickaction(int flagimage,String states,String citys,String lats,String lons) {
                String filename = states;   // full file name
                city=citys;
                locationText.setText(citys);
                lat=lats;
                lon=lons;

                search_dialog.setVisibility(View.GONE);
                search_city.setText("");
                myAdpater.notifyDataSetChanged();

                getApiData();
            }
        });
    }
    private static String getJsonFromRaw(Context context, int resource) {
        String json;
        try {
            InputStream inputStream = context.getResources().openRawResource(resource);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
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