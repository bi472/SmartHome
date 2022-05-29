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
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.ArrayList;

public class LightMenu extends AppCompatActivity {
    final String LOG_TAG = "myLogs";

    DBHelper dbHelper;
    SQLiteDatabase db;

    ArrayList<Switches> switches = new ArrayList<Switches>();
    RecyclerView recyclerView;
    SwitchesAdapter adapter;

    MQTTHelperPublish mqttHelperPublish;
    MQTTHelperSubscribe mqttHelperSubscribe;

    public boolean queryCheck(String name){
        String check = null;
        db = dbHelper.open();
        Cursor cursor = db.query(
                dbHelper.TABLE_NAME_SWITCHES,   // таблица
                new String[]{dbHelper.COLUMN_NAME_CONDITION},            // столбцы
                dbHelper.COLUMN_NAME_NAME_SWITCH + "=?",                  // столбцы для условия WHERE
                new String[]{name},                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки
        while (cursor.moveToNext()) {
            check = cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_NAME_CONDITION));
        }
        cursor.close();
        dbHelper.close();
        if (check.toLowerCase().contains("on")) {
            return true;
        }
        else
            return false;
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
        // создаем адаптер
        SwitchesAdapter.OnSwitchClickListener switchClickListener = new SwitchesAdapter.OnSwitchClickListener() {
            @Override
            public void onSwitchClick(Switches switches, int position) {
                startMqtt();
            }
        };

        adapter = new SwitchesAdapter(switchClickListener, this, switches);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    private void setInitialData() {
        switches.add(new Switches("Living", R.drawable.gostinaya, queryCheck("Living")));
        subscribe("Living");
        switches.add(new Switches("Lobby", R.drawable.prihozhaya, queryCheck("Lobby")));
        switches.add(new Switches("Bathroom", R.drawable.vannaya, queryCheck("Bathroom")));
    }

    private void startMqtt() {
        mqttHelperPublish = new MQTTHelperPublish(getApplicationContext(), "Toggle", "cmnd/relay_with_temp/POWER");
    }

    private void subscribe(final String room_name){
        mqttHelperSubscribe = new MQTTHelperSubscribe(getApplicationContext(), "stat/relay_with_temp/POWER", "power");
        mqttHelperPublish = new MQTTHelperPublish(getApplicationContext(), "", "cmnd/relay_with_temp/POWER");
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
                    queryChange(room_name, "on");
                }
                else if(mqttMessage.toString().contains("OFF")){
                    queryChange(room_name, "off");
                };
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

}