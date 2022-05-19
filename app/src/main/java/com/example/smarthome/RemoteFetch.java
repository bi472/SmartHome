package com.example.smarthome;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class RemoteFetch {
    private static final String OPEN_WEATHER_MAP_API =
            "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=d69308a1f9c7b8e150503283dd7ff5d5&units=metric&lang=Ru&cnt=1";
    private static final String GEOCODER_API =
            "https://api.openweathermap.org/geo/1.0/reverse?lat=%s&lon=%s&appid=d885414d6862917fa6433df71c83d67f";
    private static final String LIGHT_API =
            "http://%s/cm?cmnd=Power%20off";

    public static JSONObject getWeatherJSON(Context context, String Lat, String Lon){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, Lat, Lon));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            Log.i("------------ Сообщение:" , url.toString());

            //Читаем данные
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(2048);
            String tmp="";
            //Записываем в String
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            Log.i("------------ Сообщение:" , json.toString());
            //Объявление json объекта
            JSONObject data = new JSONObject(json.toString());

            return data;
        }catch(Exception e){
            Log.i("-------------- Сообщение", e.getMessage());
            return null;
        }
    }

    public static JSONObject getCity(Context context, String Lat, String Lon){
        try {
            URL url = new URL(String.format(GEOCODER_API, Lat, Lon));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            Log.i("------------ Сообщение:" , url.toString());

            //Читаем данные
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(4096);
            String tmp="";
            //Записываем в String
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            //Объявление json объекта
            JSONObject data = new JSONObject(new JSONArray(json.toString()).getString(0));

            Log.i("Содержание: ", data.toString());

            return data;
        }catch(Exception e){
            Log.e("-------------- Сообщение ошибки", e.getMessage());
            return null;
        }
    }

    public static JSONObject getPower(Context context, String IP, String condition){
        try {
            URL url = new URL("http://" + IP +"/cm?cmnd=Power%20" + condition);
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            Log.e("------------ Сообщение:" , url.toString());

            //Читаем данные
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(4096);
            String tmp="";
            //Записываем в String
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            //Объявление json объекта
            JSONObject data = new JSONObject(json.toString());;

            Log.i("Содержание: ", data.toString());

            return data;
        }catch(Exception e){
            Log.e("-------------- Сообщение", e.getMessage());
            return null;
        }
    }
}
