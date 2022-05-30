package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.ArrayList;

public class LightMenu extends AppCompatActivity {
    final String LOG_TAG = "myLogs";

    DBHelper dbHelper;
    SQLiteDatabase db;

    ArrayList<Switches> switchesArrayList = new ArrayList<Switches>();
    SwitchesAdapter.OnSwitchClickListener switchClickListener;
    RecyclerView recyclerView;
    SwitchesAdapter adapter;

    MQTTHelperPublish mqttHelperPublish;
    MQTTHelperSubscribe mqttHelperSubscribe;

    public void setConditionAdapter(String room_name, int position, boolean condition){
        switchesArrayList.get(position).setCondition(condition);
        adapter = new SwitchesAdapter(switchClickListener, getApplicationContext(), switchesArrayList);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
        queryChange(room_name, "on");
    }

    public String queryCheck(String name, String column){
        String check = null;
        try {
            db = dbHelper.open();
            Cursor cursor = db.query(
                    dbHelper.TABLE_NAME_SWITCHES,   // таблица
                    new String[]{column},            // столбцы
                    "name_switch=?",                  // столбцы для условия WHERE
                    new String[]{name},                  // значения для условия WHERE
                    null,                  // Don't group the rows
                    null,                  // Don't filter by row groups
                    null);                   // порядок сортировки
            while (cursor.moveToNext()) {
                check = cursor.getString(cursor.getColumnIndex(column));
            }
            cursor.close();
            dbHelper.close();
            Log.i("Сообщение с датчика", check);
            return check;
        }catch (Exception exception){
            Log.i("Запрос к БД" + " " + name, exception.getMessage());
        }
        return check;
    }

    public void queryChange(String room_name, String condition){
        db = dbHelper.open();
        ContentValues cv = new ContentValues();
        cv.put(dbHelper.COLUMN_NAME_NAME_SWITCH, room_name);
        cv.put(dbHelper.COLUMN_NAME_CONDITION, condition);
        db.update(dbHelper.TABLE_NAME_SWITCHES,
                cv,
                dbHelper.COLUMN_NAME_NAME_SWITCH + "=?",
                new String[]{room_name}
        );

        Log.d(LOG_TAG, room_name + " is" + condition);

        dbHelper.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_menu);

        dbHelper = new DBHelper(this);

        setInitialData();
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        switchClickListener = (switches, position) -> startMqtt(queryCheck(switches.getName(), "subscriptionTopic"), switches.getName() + "_relay_publish");

        adapter = new SwitchesAdapter(switchClickListener, this, switchesArrayList);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
    }

    private void setInitialData() {
        switchesArrayList.add(new Switches("Living", R.drawable.gostinaya, false));
        subscribe("Living", queryCheck("Living", "subscriptionTopic"), "living_relay_subscribe", 0);
        switchesArrayList.add(new Switches("Monitors", R.drawable.prihozhaya, false));
        subscribe("Monitors", queryCheck("Monitors", "subscriptionTopic"), "monitors_relay_subscribe", 1);
    }

    private void startMqtt(String subscriptionTopic, String clientID) {
        mqttHelperPublish = new MQTTHelperPublish(getApplicationContext(), "Toggle", "cmnd/"+ subscriptionTopic+"/POWER", clientID);
    }

    private void subscribe(final String room_name, String subscriptionTopic, String clientID, int position){
        Log.i("Топик для подписки " + room_name, subscriptionTopic);
        mqttHelperSubscribe = new MQTTHelperSubscribe(getApplicationContext(), "stat/"+ subscriptionTopic + "/POWER", clientID);
        mqttHelperPublish = new MQTTHelperPublish(getApplicationContext(), "", "cmnd/"+ subscriptionTopic+"/POWER", clientID + "_power_publish");
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
                if (mqttMessage.toString().contains("ON")) {
                    setConditionAdapter(room_name, position, true);
                }
                else if(mqttMessage.toString().contains("OFF")){
                    setConditionAdapter(room_name, position, false);
                };
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mqttHelperSubscribe.mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}