package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

public class LightActivity extends AppCompatActivity {

    Switch aSwitch;
    SQLiteDatabase db;
    DBHelper dbHelper;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        aSwitch = (Switch) findViewById(R.id.set_condition_switch);

        Bundle arguments = getIntent().getExtras();
        String room_name = arguments.get("room_name").toString();
        boolean condition = Boolean.parseBoolean(arguments.get("condition").toString());
        aSwitch.setChecked(condition);

        dbHelper = new DBHelper(this);


        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((Switch) v).isChecked();
                db = dbHelper.open();
                if (checked){
                    Toast toast = Toast.makeText(getApplicationContext(), "Вы включили свет в комнате: " + room_name, Toast.LENGTH_SHORT);
                    toast.show();

                    ContentValues cv = new ContentValues();
                    cv.put(dbHelper.COLUMN_NAME_NAME_SWITCH, room_name);
                    cv.put(dbHelper.COLUMN_NAME_CONDITION, "on");
                    db.update(dbHelper.TABLE_NAME_SWITCHES,
                            cv,
                            dbHelper.COLUMN_NAME_NAME_SWITCH + "=?",
                            new String[]{room_name}
                            );

                    Log.d(LOG_TAG, room_name + " is on");
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Вы выключили свет в комнате: " + room_name, Toast.LENGTH_SHORT);
                    toast.show();

                    ContentValues cv = new ContentValues();
                    cv.put(dbHelper.COLUMN_NAME_NAME_SWITCH, room_name);
                    cv.put(dbHelper.COLUMN_NAME_CONDITION, "off");
                    db.update(dbHelper.TABLE_NAME_SWITCHES,
                            cv,
                            dbHelper.COLUMN_NAME_NAME_SWITCH + "=?",
                            new String[]{room_name}
                    );
                    Log.d(LOG_TAG, room_name + " is off");
                }
                dbHelper.close();
            }
        });
    }

}