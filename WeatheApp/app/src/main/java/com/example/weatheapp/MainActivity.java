package com.example.weatheapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarException;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRl;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private final int PERMISSIOON_CODE =1;
    // private static int isDay;
    private String cityName;
    private double lon;
    private double lat;
    private TextView tb0;
    private TextView tb1;
    private TextView tb2;
    private TextView tb3;
    private TextView tb4;
    private TextView tb5;
    private TextView tb6;

    private ArrayList<String> textViewTable;
    private ArrayList<String> imageViewTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        homeRl = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(weatherRVModalArrayList,this );
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSIOON_CODE );
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location == null){
            cityName = getCityName(-71.0598, 42.3584);
        }
        else{
            cityName = getCityName(location.getLongitude(), location.getLatitude());
        }
        if(cityName.isEmpty())
        {
            Toast.makeText(MainActivity.this, "could not find Current Location", Toast.LENGTH_SHORT).show();
        }
        else{
            cityNameTV.setText(cityName.toUpperCase());
            getWeatherInfo(cityName);
            //setBackgound(isDay);
        }
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                if(cityName.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Enter the city name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    cityNameTV.setText(city.toUpperCase());
                    getWeatherInfo(city);
                    // setBackgound(isDay);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIOON_CODE)
        {
            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED )
            {
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();

            }
            else
            {
                Toast.makeText(this, "please provide the information", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    private String getCityName(double lon, double lat) {
        String cityName = "City not found.";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(lat,lon,10);
            String city = addresses.get(0).getLocality();
            if (city != null && !city.equals("")) {
                cityName = city;
            }
            else{
                Log.d("Tag", "city not found");

            }

        }catch(IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1500);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public void setBackgound( int isDay){
        Runnable background = new Runnable() {
            @Override
            public void run() {
                String str;
                if(isDay==1) {
                    /// day time
                   // str = "//www.worldatlas.com/r/w960-q80/upload/a5/e5/43/shutterstock-520412536.jpg";
                    temperatureTV.setTextColor(Color.WHITE);
                    str = "//images.theconversation.com/files/349332/original/file-20200724-37-bc1uu3.jpg?ixlib=rb-1.1.0&q=45&auto=format&w=754&fit=clip";
                }
                else {
                    temperatureTV.setTextColor(Color.WHITE);
                    str = "//images.unsplash.com/photo-1528722828814-77b9b83aafb2?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80";
                }
                setImg(str, R.id.idIVBack);
            }
        };
        new Thread(background).start();
    }

    public Handler th = new Handler(){
        public void handleMessage(android.os.Message message){
            if(message.what == 0){
                loadingPB.setVisibility(View.GONE);
                homeRl.setVisibility(View.VISIBLE);
                weatherRVAdapter.notifyDataSetChanged();
            }
            else if(message.what == -1){
                loadingPB.setVisibility(View.VISIBLE);
                homeRl.setVisibility(View.GONE);
                weatherRVAdapter.notifyDataSetChanged();
            }
            else if (message.what== 1){
                tb0.setText(textViewTable.get(0));
                tb1.setText(textViewTable.get(1));
                tb2.setText(textViewTable.get(2));
                tb3.setText(textViewTable.get(3));
                tb4.setText(textViewTable.get(4));
                tb5.setText(textViewTable.get(5));
                tb6.setText(textViewTable.get(6));
            }
            else if(message.what ==2){
                setImg(imageViewTable.get(0), R.id.daily_img1);
                setImg(imageViewTable.get(1), R.id.daily_img2);
                setImg(imageViewTable.get(2), R.id.daily_img3);
                setImg(imageViewTable.get(3), R.id.daily_img4);
                setImg(imageViewTable.get(4), R.id.daily_img5);
                setImg(imageViewTable.get(5), R.id.daily_img6);
                setImg(imageViewTable.get(6), R.id.daily_img7);
            }

        }
    };
    public void getWeatherInfo(String cityName) {
        Runnable getCurrentWeather = new Runnable() {
            @Override
            public void run() {
                String strUrl ="https://api.weatherapi.com/v1/forecast.json?key=dca892fc2d534382a1461857211207&q="+cityName+"&days=1&aqi=no&alerts=no";
                try {
                    URL url = new URL(strUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    // Starts the query
                    try {
                        conn.connect();
                    }catch(Exception e){
                        th.sendEmptyMessage(-1);
                    }
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String data = readStream(in);
                    JSONObject response = new JSONObject(data);
                    String temp = response.getJSONObject("current").getString("temp_f");
                    temperatureTV.setText(temp + "° F");
                    //need to set lat and lon for getDailyweather
                    String lat1 = response.getJSONObject("location").getString("lat");
                    String lon1 = response.getJSONObject("location").getString("lon");
                    lat = Double.parseDouble(lat1);
                    lon = Double.parseDouble(lon1);
                    String icon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    String cond = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    setImg(icon, R.id.idIVIcon);
                    conditionTV.setText(cond);
                    int  isDay = response.getJSONObject("current").getInt("is_day");
                    setBackgound(isDay);
                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hoursArray = forecast0.getJSONArray("hour");

                    for(int i =0; i < hoursArray.length(); i++) {
                        JSONObject hourObj = hoursArray.getJSONObject(i);
                        String time= hourObj.getString("time");
                        String temp_c= hourObj.getString("temp_f");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVModalArrayList.add(new WeatherRVModal(time, temp_c,img, wind));
                    }
                    if(response != null ){
                        th.sendEmptyMessage(0);
                    }
                    Log.i("test3", Double.toString(lat));
                    Log.i("test3", Double.toString(lon));
                    getDailyinfo(lat, lon);
                    //weatherRVAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(getCurrentWeather).start();
    }
    public void setImg(String icon,int id ){
        Runnable imageThread = new Runnable() {
            @Override
            public void run() {
                try {
                    String imgUrl = "https:"+icon;
                    Bitmap bitmap=downloadImg(imgUrl);
                    iconIV=(ImageView) findViewById(id);
                    iconIV.post(new Runnable() {
                        @Override
                        public void run() {
                            if(bitmap!=null) iconIV.setImageBitmap(bitmap);
                            else{
                                Log.i("BITMAP", "returned bitmap is null");}
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(imageThread).start();
    }

    private Bitmap downloadImg(String myUrl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            // Log.i(DEBUG_TAG, "The response is: " + response);

            is = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        }catch(Exception e) {
            Log.i( "downloading image", e.toString());
        }finally {
            if (is != null) {
                is.close();
            }
        }

        return null;
    }


    private  void getDailyinfo(double lat, double lon){
        Runnable getDailyWeather  = new Runnable() {
            @Override
            public void run() {
                String strUrl ="https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&exclude={part}&appid=9346a38f762ab46c8bc1e91a5df7c7ba";
                try {
                    URL url = new URL(strUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    // Starts the query
                    conn.connect();
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String data = readStream(in);
                    JSONObject response = new JSONObject(data);
                    JSONArray forecastObj = response.getJSONArray("daily");
                    textViewTable = new ArrayList<>();
                    imageViewTable = new ArrayList<>();
                    for(int i =0; i < forecastObj.length(); i++) {
                        JSONObject dayObj = forecastObj.getJSONObject(i);
                        String time= Integer.toString(i);//hourObj.getString("time");
                        String temp_c = dayObj.getJSONObject("temp").getString("day");
                        Double nel = Double.parseDouble(temp_c);
                        String check = "textId" +i;

                        int temp_f = ((int) (nel - 273.15) * (9 / 5)) + 32;
                        String icon = dayObj.getJSONArray("weather").getJSONObject(0).getString("icon");
                        String iconUrl  = "//openweathermap.org/img/wn/"+icon+".png";
                        String str = Integer.toString(temp_f) + "°F";
                        textViewTable.add(str);
                        imageViewTable.add(iconUrl);
                        Log.i("test4", str);
                    }
                    settingTable();
                    th.sendEmptyMessage(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(getDailyWeather).start();
    }
//    private void settingImg()

    private void settingTable() {
        Runnable tableRun = new Runnable() {
            @Override
            public void run() {
                //try to make this dynamic
                tb0 = (TextView) findViewById(R.id.textId1);
                tb1 = (TextView) findViewById(R.id.textId2);
                tb2 = (TextView) findViewById(R.id.textId3);
                tb3 = (TextView) findViewById(R.id.textId4);
                tb4 = (TextView) findViewById(R.id.textId5);
                tb5 = (TextView) findViewById(R.id.textId6);
                tb6 = (TextView) findViewById(R.id.textId7);
                th.sendEmptyMessage(1);
            }
        };
        new Thread(tableRun).start();

    }

}