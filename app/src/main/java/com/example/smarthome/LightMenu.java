package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LightMenu extends AppCompatActivity {
    final String LOG_TAG = "myLogs";
    DBHelper dbHelper;
    String check;
    TextView condition;
    Button change_condition;

    Handler handler;

    public LightMenu() { handler = new Handler(); }

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

        change_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        final JSONObject LightJson = RemoteFetch.getPower(getApplication(), "192.168.0.224", "0");
                        if(LightJson == null){
                            handler.post(new Runnable(){
                                public void run(){
                                    Toast.makeText(getApplicationContext(),
                                            "ничё не работает ты думал всё так просто что ли",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            handler.post(new Runnable(){
                                public void run(){
                                    try {
                                        condition.setText(LightJson.getString("POWER"));
                                    }
                                    catch (Exception ex){
                                        Log.e("TAG", ex.getMessage());
                                    }
                                }
                            });
                        }
                    }
                }.start();
            }
        });
    }
}