package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LightMenu extends AppCompatActivity {
    
    DBHelper dbHelper;
    SQLiteDatabase db;

    ArrayList<Switches> switchesArrayList = new ArrayList<Switches>();
    Map<String, String> switchesMap = new HashMap<>();
    SwitchesAdapter.OnSwitchClickListener switchClickListener;
    RecyclerView recyclerView;
    SwitchesAdapter adapter;

    MQTTHelperPublish mqttHelperPublish;
    MQTTHelperSubscribe mqttHelperSubscribe;
    String serverUri;
    String username;
    String password;

    public void setConditionAdapter(int position, boolean condition){
        switchesArrayList.get(position).setCondition(condition);
        adapter = new SwitchesAdapter(switchClickListener, getApplicationContext(), switchesArrayList);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
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

    public void queryCheck(){
        try {
            db = dbHelper.open();
            Cursor cursor = db.query(
                    dbHelper.TABLE_NAME_SWITCHES,   // таблица
                    new String[]{"name_switch", "subscriptionTopic"},            // столбцы
                    null,                  // столбцы для условия WHERE
                    null,                  // значения для условия WHERE
                    null,                  // Don't group the rows
                    null,                  // Don't filter by row groups
                    null);                   // порядок сортировк
            int i = 0;
            Log.i("Количество строк", String.valueOf(cursor.getCount()));
            while (cursor.moveToNext()) {
                switchesMap.put(cursor.getString(cursor.getColumnIndex("name_switch")), cursor.getString(cursor.getColumnIndex("subscriptionTopic")));
                Log.i("Записи в БД", cursor.getString(cursor.getColumnIndex("name_switch")) +": "+ cursor.getString(cursor.getColumnIndex("subscriptionTopic")));
                i++;
            }
            cursor.close();
            dbHelper.close();
        }catch (Exception exception){
            Log.i("Запрос к БД" , exception.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_menu);

        serverUri = "tcp://" + new Preferences(LightMenu.this).getMQTTServer() + ":" + new Preferences(LightMenu.this).getPort();
        username = new Preferences(LightMenu.this).getUsername();
        password = new Preferences(LightMenu.this).getPassword();

        dbHelper = new DBHelper(this);

        queryCheck();

        setInitialData();
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        switchClickListener = (switches, position) -> startMqtt(queryCheck(switches.getName(), "subscriptionTopic"), switches.getName() + "_relay_publish_" + new Random().nextInt());

        adapter = new SwitchesAdapter(switchClickListener, this, switchesArrayList);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
    }

    private void setInitialData() {
        int i =0;
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);;
        for(Map.Entry<String, String> entry: switchesMap.entrySet()) {
            // get key
            String key = entry.getKey();
            // get value
            String value = entry.getValue();
            switchesArrayList.add(new Switches(key, R.drawable.gostinaya, false));
            subscribe(key, queryCheck(key, "subscriptionTopic"), value + "_subscribe_" + new Random().nextInt(), i);
            i++;
        }
    }

    private void startMqtt(String subscriptionTopic, String clientID) {
        mqttHelperPublish = new MQTTHelperPublish(getApplicationContext(), "Toggle", "cmnd/"+ subscriptionTopic+"/POWER", clientID,
                serverUri, username, password);
        Log.i("MSG", clientID);
    }

    private void subscribe(final String room_name, String subscriptionTopic, String clientID, int position){
        Log.i("MSG", clientID);
        mqttHelperSubscribe = new MQTTHelperSubscribe(getApplicationContext(), "stat/"+ subscriptionTopic + "/POWER", clientID,
                serverUri, username, password);
        mqttHelperPublish = new MQTTHelperPublish(getApplicationContext(), "", "cmnd/"+ subscriptionTopic+"/POWER", clientID + "_power_publish",
                serverUri, username, password);
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
                    setConditionAdapter(position, true);
                }
                else if(mqttMessage.toString().contains("OFF")){
                    setConditionAdapter(position, false);
                };
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}