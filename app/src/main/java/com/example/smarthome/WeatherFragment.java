package com.example.smarthome;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import android.graphics.Typeface;
import android.icu.text.Transliterator;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import android.os.Handler;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import retrofit2.http.Url;

public class WeatherFragment extends Fragment {
    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView timeUpdated;
    TextView feelsLike;
    ImageView weather_icon;
    TextView region_field;
    // нужен handler для потока
    Handler handler;
    ProgressBar progressBar;

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
        region_field = (TextView)rootView.findViewById(R.id.region_field);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weather_icon = (ImageView) rootView.findViewById(R.id.weather_icon);
        timeUpdated = (TextView)rootView.findViewById(R.id.updated_time);
        feelsLike = (TextView)rootView.findViewById(R.id.feels_like);
        roomTemp = (TextView) rootView.findViewById(R.id.temperature_room);
        roomHum = (TextView) rootView.findViewById(R.id.humidity_room);
        roomUpdateTime = (TextView) rootView.findViewById(R.id.updated_time_room);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);


        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "weathericons-regular-webfont.ttf");

        updateWeatherData(new CityPreference(getActivity()).getLat(), new  CityPreference(getActivity()).getLon());
        roomTemp.setText("Температура: " + new CityPreference(getActivity()).getTemp() + " ℃");
        roomHum.setText("Влажность: " + new CityPreference(getActivity()).getHum() + " %");
        roomUpdateTime.setText("Обновлено в " + new CityPreference(getActivity()).getTime());

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
                                renderWeather(weatherJson, Lat, Lon);
                            }
                        });
                    }
            }
        }.start();
    }

    // Метод renderWeather использует данные JSON для обновления объектов TextView
    private void renderWeather(JSONObject json, String Lat, String Lon){
        try {
            progressBar.setVisibility(View.GONE);
            Geocoder gc = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addressList = gc.getFromLocation(parseDouble(Lat), parseDouble(Lon), 1);
            region_field.setText(addressList.get(0).getLocality());

            final JSONObject locJson = json.getJSONObject("location");
            final JSONObject weatherJson = json.getJSONObject("current");
            final JSONObject conditionJson = weatherJson.getJSONObject("condition");

            String CYRILLIC_TO_LATIN = "Latin-Russian/BGN";
            String name = locJson.getString("name");

            Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
            String place_name = toLatinTrans.transliterate(name);

            cityField.setText(place_name);
            detailsField.setText(conditionJson.getString("text"));
            double temp = weatherJson.getDouble("temp_c");
            currentTemperatureField.setText((int) temp + " ℃");
            double feels = weatherJson.getDouble("feelslike_c");
            feelsLike.setText("Ощущается как " + (int) feels + " ℃");

            Picasso.get().load("https:" + conditionJson.getString("icon")).into(weather_icon);

            //Перевод даты в соответствующий часовой пояс
            Date date = new Date(weatherJson.getLong("last_updated_epoch")*1000L);
            Calendar myCal = new GregorianCalendar();
            myCal.setTime(date);

            updatedField.setText(myCal.get(Calendar.DAY_OF_MONTH) +
                    " " + myCal.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("ru")) +
                    ", " + myCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG_FORMAT, new Locale("ru")));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String time = simpleDateFormat.format(myCal.getTime());

            timeUpdated.setText(time);


        }catch(Exception e){
            Log.e("Умный дом", e.getMessage());
        }
    }

    public void changeCity(String Lat, String Lon){
        updateWeatherData(Lat, Lon);
    }
}
