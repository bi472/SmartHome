package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Counter extends AppCompatActivity {

    private List<String> readings = new ArrayList<>();
    private TextView time_tv;
    private TextView data_tv;
    RadioButton gas_radioButton;
    RadioButton electricity_radioButton;
    RadioButton water_radioButton;

    DBHelper dbHelper;
    SQLiteDatabase db;

    String data = "null";
    String time = "null";

    public void query(String name){
        SQLiteDatabase db = dbHelper.open();
        Cursor cursor = db.query(
                dbHelper.TABLE_NAME_COUNTERS,   // таблица
                new String[]{"data","time_counter"},            // столбцы
                "counter_name = ?",                  // столбцы для условия WHERE
                new String[]{name},                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки
        while (cursor.moveToNext()) {
            data = cursor.getString(cursor.getColumnIndex("data"));
            time = cursor.getString(cursor.getColumnIndex("time_counter"));
        }
        cursor.close();
        dbHelper.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        dbHelper = new DBHelper(this);

        time_tv = (TextView) findViewById(R.id.time_tv);
        data_tv = (TextView) findViewById(R.id.data_tv);

        gas_radioButton = (RadioButton) findViewById(R.id.radio_gas);
        gas_radioButton.setOnClickListener(radioButtonClickListener);

        electricity_radioButton = (RadioButton) findViewById(R.id.radio_electricity);
        electricity_radioButton.setOnClickListener(radioButtonClickListener);

        water_radioButton = (RadioButton) findViewById(R.id.radio_water);
        water_radioButton.setOnClickListener(radioButtonClickListener);
    }

    View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton)v;
            switch (rb.getId()) {
                case R.id.radio_gas: {
                    query("Gas");
                    time_tv.setText("Дата и время последнего сохранения: " + time.toString());
                    data_tv.setText("Данные счётчика: " + data.toString());
                }
                case R.id.radio_electricity: {
                    query("Electricity");
                    time_tv.setText("Дата и время последнего сохранения: " + time.toString());
                    data_tv.setText("Данные счётчика: " + data.toString());
                    break;
                }
                case R.id.radio_water: {
                    query("Water");
                    time_tv.setText("Дата и время последнего сохранения: " + time.toString());
                    data_tv.setText("Данные счётчика: " + data.toString());
                    break;
                }
                default:
                    break;
            }
        }
    };
}