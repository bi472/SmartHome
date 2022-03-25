package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LightMenu extends AppCompatActivity {
    ArrayList<Room> rooms = new ArrayList();
    final String LOG_TAG = "myLogs";
    ListView menuList;
    DBHelper dbHelper;
    String check;

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

        menuList = (ListView) findViewById(R.id.lightList);

        setInitialData(); //инициализация списка

        RoomAdapter foodAdapter = new RoomAdapter(this, R.layout.list_item, rooms); // создание адаптера
        menuList.setAdapter(foodAdapter); // устанавливаем адаптер
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Room selectedRoom = (Room) parent.getItemAtPosition(position);
                Intent intent = new Intent(LightMenu.this, LightActivity.class);
                intent.putExtra("condition", queryCheck(selectedRoom.getRoom_name()));
                intent.putExtra("room_name", selectedRoom.getRoom_name());
                startActivity(intent);
            }
        });
    }

    private void setInitialData() {
        rooms.add(new Room("Kitchen", R.drawable.kitchen));
        rooms.add(new Room("Living", R.drawable.gostinaya));
        rooms.add(new Room("Bathroom", R.drawable.vannaya));
        rooms.add(new Room("Lobby", R.drawable.prihozhaya));
        rooms.add(new Room("Toilet", R.drawable.tualet));
    }
}