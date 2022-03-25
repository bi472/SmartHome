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
            "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=d69308a1f9c7b8e150503283dd7ff5d5&units=metric&lang=RU";

    public static JSONObject getJSON(Context context, String city){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();
            //Читаем данные
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            //Записываем в String
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            //Объявление json объекта
            JSONObject data = new JSONObject(json.toString());

            // Если код равен 200, то возврощается null
            if(data.getInt("cod") != 200){
                return null;
            }
            return data;
        }catch(Exception e){
            return null;
        }
    }
}
