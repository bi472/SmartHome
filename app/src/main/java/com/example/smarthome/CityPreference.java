package com.example.smarthome;
import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // Значение по умолчанию если пользователь не указал город
    String getCity(){
        return prefs.getString("city", "Saint Petersburg");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }

}