package com.example.smarthome;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import android.os.Handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class WeatherFragment extends Fragment {
    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    TextView timeUpdated;
    TextView feelsLike;
    // нужен handler для потока
    Handler handler;

    MQTTHelperSubscribe mqttHelperSubscribe;

    TextView roomTemp;
    TextView roomHum;
    TextView roomUpdateTime;

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
        timeUpdated = (TextView)rootView.findViewById(R.id.updated_time);
        feelsLike = (TextView)rootView.findViewById(R.id.feels_like);
        roomTemp = (TextView) rootView.findViewById(R.id.temperature_room);
        roomHum = (TextView) rootView.findViewById(R.id.humidity_room);
        roomUpdateTime = (TextView) rootView.findViewById(R.id.updated_time_room);

        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "weathericons-regular-webfont.ttf");

        updateWeatherData(new CityPreference(getActivity()).getLat(), new  CityPreference(getActivity()).getLon());
        roomTemp.setText("Температура: " + new CityPreference(getActivity()).getTemp() + " ℃");
        roomHum.setText("Влажность: " + new CityPreference(getActivity()).getHum() + " %");
        roomUpdateTime.setText("Обновлено в " + new CityPreference(getActivity()).getTime());

        weatherIcon.setTypeface(weatherFont);


        startMqtt();


        return rootView;
    }

    private void startMqtt(){
        mqttHelperSubscribe = new MQTTHelperSubscribe(getActivity(), "tele/relay_with_temp/SENSOR", "temperature");
        mqttHelperSubscribe.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
                JSONObject roomInfoJson = new JSONObject(mqttMessage.toString());
                JSONObject localNamesJson = roomInfoJson.getJSONObject("AM2301");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat output = new SimpleDateFormat("HH:mm:ss");
                Date d = sdf.parse(roomInfoJson.getString("Time"));
                String time = output.format(d);
                new CityPreference(getActivity()).setTemp(localNamesJson.getString("Temperature"));
                new CityPreference(getActivity()).setHum(localNamesJson.getString("Humidity"));
                new CityPreference(getActivity()).setTime(time);
                roomTemp.setText("Температура: " + localNamesJson.getString("Temperature") + " ℃");
                roomHum.setText("Влажность: " + localNamesJson.getString("Humidity") + " %");
                roomUpdateTime.setText("Обновлено в " + time);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    // В updateWeatherData, мы запускаем новый поток и вызываем getJSON в классе RemoteFetch, нужен асинхронный поток а не фоновый,
    // вызов Toast или renderWeather прямо из фонового потока приведет к ошибке выполнения
    private void updateWeatherData(final String Lat, final String Lon){
        new Thread(){
            public void run(){
                final JSONObject weatherJson = RemoteFetch.getWeatherJSON(getActivity(), Lat, Lon);
                final JSONObject cityJson = RemoteFetch.getCity(getActivity(), Lat, Lon);
                if(weatherJson == null){
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
                            renderWeather(weatherJson,cityJson, Lat, Lon);
                        }
                    });
                }
            }
        }.start();
    }

    // Метод renderWeather использует данные JSON для обновления объектов TextView
    private void renderWeather(JSONObject json, JSONObject cityJson, String Lat, String Lon){
        try {
           if (cityJson.getJSONObject("local_names") != null) {
                JSONObject localNamesJson = cityJson.getJSONObject("local_names");
                cityField.setText(localNamesJson.getString("ru"));
            }
            else
                cityField.setText(cityJson.getString("name"));
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US));

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ "℃"
            );
            feelsLike.setText("Ощущается как " +
                    String.format("%.2f", main.getDouble("feels_like")) + "℃"
            );

            DateFormat df = DateFormat.getDateTimeInstance();
            //Перевод даты в соответствующий часовой пояс
            Calendar myCal = new GregorianCalendar();
            Date updatedOn = new Date(json.getLong("dt")*1000);
            myCal.setTime(updatedOn);

            updatedField.setText(myCal.get(Calendar.DAY_OF_MONTH) +
                    " " + myCal.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("ru")) +
                    ", " + myCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG_FORMAT, new Locale("ru")));
            timeUpdated.setText(myCal.get(Calendar.HOUR_OF_DAY) + ":" + myCal.get(Calendar.MINUTE));

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("Умный дом", e.getMessage());
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
