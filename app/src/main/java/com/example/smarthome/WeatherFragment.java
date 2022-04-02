package com.example.smarthome;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;
import android.os.Handler;

import java.text.DateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherFragment extends Fragment {
    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    // нужен handler для потока
    Handler handler;

    Geocoder geocoder;

    public WeatherFragment(){
        handler = new Handler();
    }


    // Инициализация элементов
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);

        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "weathericons-regular-webfont.ttf");
        updateWeatherData(new CityPreference(getActivity()).getLat(), new  CityPreference(getActivity()).getLon());

        weatherIcon.setTypeface(weatherFont);



        return rootView;
    }

    // В updateWeatherData, мы запускаем новый поток и вызываем getJSON в классе RemoteFetch, нужен асинхронный поток а не фоновый,
    // вызов Toast или renderWeather прямо из фонового потока приведет к ошибке выполнения
    private void updateWeatherData(final String Lat, final String Lon){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(getActivity(), Lat, Lon);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json, Lat, Lon);
                        }
                    });
                }
            }
        }.start();
    }

    // Метод renderWeather использует данные JSON для обновления объектов TextView
    private void renderWeather(JSONObject json, String Lat, String Lon){
        try {
            geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(Lat), Double.parseDouble(Lon), 1);
            cityField.setText(addresses.get(0).getAddressLine(0));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Влажность: " + main.getString("humidity") + "%" +
                            "\n" + "Давление: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            //Перевод даты в соответствующий часовой пояс
            String updatedOn = df.format(new Date(json.getLong("dt")*1000 + json.getLong("timezone")*1000));
            updatedField.setText("Последнее обновление: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("Умный дом", "Нет данных в JSON.");
        }
    }

    // Изменение иконки в соответствии с погодой
    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String Lat, String Lon){
        updateWeatherData(Lat, Lon);
    }
}
