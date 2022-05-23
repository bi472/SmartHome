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
    private static final String WEATHER_API =
            "http://api.weatherapi.com/v1/current.json?key=e58a49484fef404e808173715222005&q=%s, %s&lang=ru";

    public static JSONObject getWeatherJSON(Context context, String lat, String lon){
        try {
            URL url = new URL(String.format(WEATHER_API, lat, lon));
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
}
