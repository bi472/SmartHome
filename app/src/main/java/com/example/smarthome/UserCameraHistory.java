package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class UserCameraHistory extends AppCompatActivity {

    TextView LastTime;
    TextView User;

    SQLiteDatabase db;
    DBHelper dbHelper;

    String historyCheck;

    public void HistoryCheck(String id){
        db = dbHelper.open();
        Cursor cursor = db.query(
                dbHelper.TABLE_NAME_CAMERA_HISTORY,   // таблица
                new String[]{"time_history"},            // столбцы
                "id_user" + "=?",                  // столбцы для условия WHERE
                new String[]{id},                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки
        while (cursor.moveToNext()) {
            historyCheck = cursor.getString(cursor.getColumnIndex("time_history"));
        }
        cursor.close();
        dbHelper.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_camera_history);

        dbHelper = new DBHelper(this);

        LastTime = (TextView) findViewById(R.id.last_time);
        User = (TextView) findViewById(R.id.user_name);

        Bundle arguments = getIntent().getExtras();
        String id = arguments.get("id_user").toString();
        String name_user = arguments.getString("name_user");

        HistoryCheck(id);

        User.setText(User.getText() + name_user);
        LastTime.setText(LastTime.getText() + historyCheck);
    }
}