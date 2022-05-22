package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LightMenu extends AppCompatActivity {
    final String LOG_TAG = "myLogs";
    DBHelper dbHelper;
    String check;
    TextView condition;
    Button change_condition;
    MQTTHelperPublish mqttHelperPublish;
    MQTTHelperSubscribe mqttHelperSubscribe;

    public boolean queryCheck(String name){
        SQLiteDatabase db = dbHelper.open();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_menu);

        dbHelper = new DBHelper(this);

        condition = findViewById(R.id.light_condition);

        change_condition = findViewById(R.id.find_light);
        subscribe();

        change_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMqtt();
            }
        });

    }

    private void startMqtt() {
        mqttHelperPublish = new MQTTHelperPublish(getApplicationContext());
    }

    private void subscribe(){
        mqttHelperSubscribe = new MQTTHelperSubscribe(getApplicationContext(), "stat/relay_with_temp/POWER", "power");
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
                JSONObject relayInfoJson = new JSONObject(mqttMessage.toString());
                String conditionString = relayInfoJson.getString("POWER");
                condition.setText(conditionString);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}