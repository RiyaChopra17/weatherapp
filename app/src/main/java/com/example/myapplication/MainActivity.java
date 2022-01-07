package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String BaseUrl = "http://api.openweathermap.org/";
    //public static String AppId = "2e65127e909e178d0af311a81f39948c";
    public static String AppId = "133a3b03ac4483d45ab7eec88090f6b1";
    public static String lat = "28.6139";
    public static String lon = "77.2090";
    private TextView weatherData;
    private android.widget.Spinner spinner;
    List<MyCity> list_arr=new ArrayList<>();
    MyAdpater myAdpater;
    ListView listView;
    TextView cityTxt;
    EditText search_country;
    RelativeLayout search_dialog;
    String state;
    String city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherData = findViewById(R.id.textView);
        spinner = (Spinner) findViewById(R.id.spinner);
        cityTxt=(TextView)findViewById(R.id.countryCodeTxt);
        search_dialog=(RelativeLayout)findViewById(R.id.search_dialog);
        search_country=(EditText)findViewById(R.id.search_country);
        listView=(ListView) findViewById(R.id.listview);
        cityTxt.setOnClickListener(this);
        //  Typeface typeface = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
      /*  FontUtils fontUtils = new FontUtils();
        fontUtils.applyFontToView(weatherData, typeface);*/
        search_country.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    String text = search_country.getText().toString().toLowerCase(Locale.getDefault());
                    myAdpater.filter(text);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // et_search.getFilter().filter(s.toString());
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  getCurrentData();
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
    private void setDataInAdpater() {
        myAdpater=new MyAdpater(this,list_arr);
        listView.setAdapter(myAdpater);
        listView.setTextFilterEnabled(true);

        myAdpater.onCallBackReturn(new MyAdpater.Callback() {
            @Override
            public void clickaction(int flagimage,String states,String citys) {
                String filename = states;   // full file name
              //  String[] parts = filename.split("\\("); // String array, each element is text between dots


                cityTxt.setText("+"+states+"");
                Log.i("==========code",citys);
                Log.i("==========code",filename);
              //  city= parts[0];
              //  state=String.valueOf(dialcodes);
                search_dialog.setVisibility(View.GONE);
                search_country.setText("");
                myAdpater.notifyDataSetChanged();
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

    void getCurrentData() {
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
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                   double a=weatherResponse.main.temp_min;
                    Log.e("============text",response.raw().toString());
                     Log.e("============text",(weatherResponse.main.temp_min)+"\u2109");
                     Log.e("============text",(weatherResponse.main.temp_min)+"\u2103");

                    float celsius = (weatherResponse.main.temp_min) - 273.15F;
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

                    weatherData.setText(stringBuilder);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                weatherData.setText(t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.countryCodeTxt:
                InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(cityTxt.getApplicationWindowToken(), 0);
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


   /* Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    List<Address> addresses = geocoder.getFromLocation(MyLat, MyLong, 1);
    String cityName = addresses.get(0).getAddressLine(0);
    String stateName = addresses.get(0).getAddressLine(1);
    String countryName = addresses.get(0).getAddressLine(2);*/
}