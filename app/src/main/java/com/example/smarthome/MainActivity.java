package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.database.Cursor;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        // создаем базу данных
        dbHelper.create_db();
    }

    public void click(View view) {
        switch (view.getId()){
            case R.id.lightButton:
                Intent intent = new Intent(this, LightMenu.class);
                startActivity(intent);
                break;
            case R.id.WeatherButton:
                Intent intent2 = new Intent(this, Weather.class);
                startActivity(intent2);
                break;
            case R.id.CameraButton:
                Intent intent3 = new Intent(this, VideoCamera.class);
                startActivity(intent3);
                break;
        }
    }
}