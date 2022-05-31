package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class Weather extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

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
            case R.id.update_city:{
                checkPermisson();
                break;
            }
        }
        return false;
    }

    public void checkPermisson()
    {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Weather.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        else {
            getCoordinates();
        }
    }

    private void getCoordinates() {
        GPSTracker g = new GPSTracker(getApplicationContext()); //создаём трекер
        Location l = g.getLocation(); // получаем координаты
        if(l != null){
            double lat = l.getLatitude();  // широта
            double lon = l.getLongitude(); // долгота
            Log.i("Сообщение с координатами: ", String.valueOf(lat) + ", "+ String.valueOf(lon));
            changeCity(String.valueOf(lat), String.valueOf(lon));
        }
        else {
            Toast.makeText(getApplicationContext(), "Данные о местположении не изменились", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == RESULT_OK)
        {
            getCoordinates();
        }
        else if (requestCode == 100 && grantResults[0] == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Пожалуйста, предоставьте разрешение", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeCity(String Lat, String Lon){
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.container);
        wf.changeCity(Lat, Lon);
        new Preferences(this).setLat(Lat);
        new Preferences(this).setLon(Lon);
        Toast.makeText(getApplicationContext(), "Данные о местположении изменены", Toast.LENGTH_SHORT).show();
    }
}