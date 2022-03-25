package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VideoCameraHistory extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase db;

    EditText username;
    EditText password;

    int id;

    public boolean PasswordCheck(String user, String pass) {
        SQLiteDatabase db = dbHelper.open();
        String checkUser = "null";
        String checkPass = "null";
        Cursor cursor = db.query(
                dbHelper.TABLE_NAME_USERS,   // таблица
                new String[]{"id_user","name_user", "password"},            // столбцы
                "name_user" + "=? and " + "password" + "=?",                  // столбцы для условия WHERE
                new String[]{user, pass},                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex("id_user"));
            checkUser = cursor.getString(cursor.getColumnIndex("name_user"));
            checkPass = cursor.getString(cursor.getColumnIndex("password"));
        }
        cursor.close();
        dbHelper.close();
        if (checkUser.contains(user)
                && checkPass.contains(pass))
            return true;
        else
            return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_camera_history);

        dbHelper = new DBHelper(this);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
    }

    public void goToHistoryView(View view) {
        if (PasswordCheck(
                username.getText().toString(),
                password.getText().toString())
                &&
                username.getText().toString() != ""
                &&
                password.getText().toString() != ""
            )
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Вы вошли!",
                    Toast.LENGTH_SHORT);
            toast.show();

            Intent intent = new Intent(this, UserCameraHistory.class);
            intent.putExtra("id_user", id);
            intent.putExtra("name_user", username.getText().toString());
            startActivity(intent);
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Неправильный логин или пароль.",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}