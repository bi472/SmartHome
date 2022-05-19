package com.example.smarthome;
import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // Значение по умолчанию если пользователь не указал город
    String getLat() { return prefs.getString("Latitude", "30,5");};
    String getLon() {return prefs.getString("Longitude", "60,5");};
    String getTemp() {return prefs.getString("Temperature", "0");}
    String getHum() {return prefs.getString("Humidity", "0");}
    String getTime() {return prefs.getString("Time", "0");}

    void setLat(String Lat){
        prefs.edit().putString("Latitude", Lat).commit();
    }

    void setLon(String Lon){
        prefs.edit().putString("Longitude", Lon).commit();
    }

    void setTemp(String Temp){
        prefs.edit().putString("Temperature", Temp).commit();
    }

    void setHum(String Hum){
        prefs.edit().putString("Humidity", Hum).commit();
    }

    void setTime(String Time){ prefs.edit().putString("Time", Time).commit(); };
}