package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class DatabaseEditActivity extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase db;

    public void QueryEdit(){

    }

    public void QueryAdd(String table_name, String column_name1,String column_name2, String value, String value2){
        ContentValues newValues = new ContentValues();
        newValues.put(column_name1, value);
        newValues.put(column_name2, value2);
        db.insert(table_name, null, newValues);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_edit);
        dbHelper = new DBHelper(this);
    }


    public void clicked(View view) {
        switch (view.getId()){
            case R.id.edit_switch_button:
                break;
            case R.id.add_user_button:
                break;
            case R.id.add_history_button:
                break;
            case R.id.edit_counter_button:
                break;
        }
    }
}