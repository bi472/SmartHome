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

    void setLat(String Lat){
        prefs.edit().putString("Latitude", Lat).commit();
    }

    void setLon(String Lon){
        prefs.edit().putString("Longitude", Lon).commit();
    }

}