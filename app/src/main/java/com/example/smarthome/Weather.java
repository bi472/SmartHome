package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Weather extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, 100);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherFragment())
                    .commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_city:{
                showInputDialog();
                break;
                }
            case R.id.update_city:{
                saveLocation();
            }
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == RESULT_OK){
            saveLocation();
        }
        else{
            Toast.makeText(getApplicationContext(), "Пожалуйста, дайте разрешение.", Toast.LENGTH_LONG);
        }
    }

    public void saveLocation(){
        GPSTracker g = new GPSTracker(getApplicationContext()); //создаём трекер
        Location l = g.getLocation(); // получаем координаты
        if(l != null){
            Double lat = l.getLatitude();  // широта
            Double lon = l.getLongitude(); // долгота
            Log.i("-----Сообщение: ", lat.toString() + ", "+ lon.toString());
            changeCity(lat.toString(), lon.toString());
        }
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Изменить город");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity("30.5", "60.5");
            }
        });
        builder.show();
    }

    public void changeCity(String Lat, String Lon){
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.container);
        wf.changeCity(Lat, Lon);
        new CityPreference(this).setLat(Lat);
        new CityPreference(this).setLon(Lon);
    }
}