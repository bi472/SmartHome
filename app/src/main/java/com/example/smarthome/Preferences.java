package com.example.smarthome;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    SharedPreferences prefs;

    public Preferences(Activity activity){
        prefs = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    // Значение по умолчанию если пользователь не указал город
    String getLat() { return prefs.getString("Latitude", "55.37");};
    String getLon() {return prefs.getString("Longitude", "37.36");};
    String getTemp() {return prefs.getString("Temperature", "0");}
    String getHum() {return prefs.getString("Humidity", "0");}
    String getTime() {return prefs.getString("Time", "0");}

    String getMQTTServer() {return  prefs.getString("MQTTServer", "m9.wqtt.ru");}
    String getPort(){return  prefs.getString("Port", "12488");}
    String getUsername(){return prefs.getString("User", "u_Q8U3S8");}
    String getPassword(){return prefs.getString("Password", "JPreADjI");}

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

    void setMQTTServer(String server) {prefs. edit().putString("MQTTServer", server).commit();}
    void setPort(String port) {prefs.edit().putString("Port", port).commit();}
    void setUsername(String user) {prefs.edit().putString("User", user).commit();}
    void setPassword(String password) {prefs. edit().putString("Password", password).commit();}
}