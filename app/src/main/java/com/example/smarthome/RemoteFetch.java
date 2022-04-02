package com.example.smarthome;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class RemoteFetch {
    private static final String OPEN_WEATHER_MAP_API =
            "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=d69308a1f9c7b8e150503283dd7ff5d5&units=metric&lang=Ru&cnt=1";

    public static JSONObject getJSON(Context context, String Lat, String Lon){
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
}
